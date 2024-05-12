package components.plugins;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import ast.base.Base;
import ast.cont.DCont;
import ast.cont.ECont;
import ast.cont.FCont;
import ast.dirs.Dirs;
import ast.query.Query;
import components.cvm.CVM;
import components.ports.lookup.Lookup_Connector;
import components.ports.lookup.Lookup_OutboundPort;
import components.ports.requestResult.RequestResult_InboundPort;
import components.ports.requesting.Requesting_Connector;
import components.ports.requesting.Requesting_OutboundPort;
import connexion.EndPointDescriptor;
import connexion.NodeInfo;
import connexion.requests.Request;
import connexion.requests.RequestContinuation;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.EndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestResultCI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;

public class Plugin_Client extends AbstractPlugin {

	private static final long serialVersionUID = 1L;

	String inp_uri;
	Requesting_OutboundPort outc; //le port sortant agit comme un RequestingCI
    Lookup_OutboundPort outcreg; //le port pour le registre
    
    RequestResult_InboundPort inAsynchrone;
    
    private Map<String, QueryResultI> results = new HashMap<>();
    List<RequestI> requests;
    QueryResultI result;
    
    // additional request parameters
    private int nbRequests = 1; // par defaut
 	private String nodeId; // in case we want to send the request with a node id 
 	private GeographicalZoneI geo; // in case we want to send the request inside a geographical zone
    
    protected ClocksServerOutboundPort clockOP;
    protected AcceleratedClock ac;
    
    /* Delay after which the Client will merge and print the results */
    private final long DELAY = 2; 
    
    protected final String PLUGIN_URI;

	public Plugin_Client(List<RequestI> requests,
			            int nbRequests,
			            String nodeId,
			            String in_uri,
	                    GeographicalZoneI geo,
	                    String plugin_uri,
	                    ConnectionInfoI co
	                    ) throws Exception {
		
		super();
		
		this.requests = requests;
		this.nbRequests = nbRequests;
		this.nodeId = nodeId;
		this.geo = geo;
		this.PLUGIN_URI = plugin_uri;
		this.inp_uri = in_uri;
		
		// met à jour la connection info de la requête
		for (RequestI request : requests)
			((Request)request).setConnectionInfo(co);
	}
	
	
	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#installOn(fr.sorbonne_u.components.ComponentI)
	 */
	@Override
	public void	installOn(ComponentI owner) throws Exception {
		super.installOn(owner);
		this.addRequiredInterface(RequestingCI.class);
		this.addRequiredInterface(LookupCI.class);
		this.addOfferedInterface(RequestResultCI.class);
		
		this.outc = new Requesting_OutboundPort(this.getOwner());
        outc.publishPort();
        
        this.outcreg = new Lookup_OutboundPort(this.getOwner());
        outcreg.publishPort();
        
        this.inAsynchrone = new RequestResult_InboundPort(this.getOwner(), inp_uri, PLUGIN_URI);
        inAsynchrone.publishPort();
	}
	
	 @Override
    public void finalise() throws Exception {
    	this.logMessage("Finalising");
        try {
			if (outc.connected())
				this.getOwner().doPortDisconnection(outc.getPortURI());
			if (outcreg.connected())
				this.getOwner().doPortDisconnection(outcreg.getPortURI());
		} catch (Exception e) {
			throw new Exception();
		}
        super.finalise();
    }
    
    @Override
    public void uninstall() throws ComponentShutdownException {
		this.logMessage("Shutting down");
    	try {
			outc.unpublishPort();
			outcreg.unpublishPort();
			inAsynchrone.unpublishPort();
			
		} catch (Exception e) {
			throw new ComponentShutdownException();
		}
		
    	this.removeRequiredInterface(RequestingCI.class);
		this.removeRequiredInterface(LookupCI.class);
		this.removeOfferedInterface(RequestResultCI.class);
    }


    /**
     * Envoie une requête 
     * Ce processus implique la localisation d'un noeud cible par identifiant ou zone géographique,
     * l'établissement de la connexion, et l'envoi de la requête, de manière synchrone ou asynchrone
     *
     * @throws
     */
    public void sendRequest() throws Exception {
    	this.logMessage("executing client component.") ;
    	/* Connexion au registre */
    	this.getOwner().logMessage("Connecting to the register");
    	this.getOwner().doPortConnection(
    			outcreg.getPortURI(),
    			CVM.registerClInURI,
    			Lookup_Connector.class.getCanonicalName());
    	
    	this.getOwner().logMessage("Looking for a node");
    	
    	NodeInfo uriNode;
    	if (this.nodeId != "") {
    		uriNode =(NodeInfo) outcreg.findByIdentifier(nodeId);
    	} else if (this.geo != null) {
	        Set<ConnectionInfoI> nodesInZone = outcreg.findByZone(geo);
	        
	        //On prend le premier noeud trouvé
	        if (!nodesInZone.isEmpty()) {
	        	Iterator<ConnectionInfoI> iterator = nodesInZone.iterator();
	            uriNode = (NodeInfo) iterator.next(); 
	        } else {
	        	throw new Exception("Le HashSet est vide.");
	        }
    	} else throw new Exception("No way! to locate the Node ");

        this.getOwner().logMessage("Node trouvé: "+uriNode.nodeIdentifier());
        
        this.getOwner().logMessage("Connecting to its port");
        // connexion au noeud pour l'envoie de la requete
		this.getOwner().doPortConnection(
                outc.getPortURI(),
                uriNode.requestingEndPointInfo().toString(), 
                Requesting_Connector.class.getCanonicalName());
		
		
		this.getOwner().logMessage("Sending request");
		for (RequestI request : requests) {
	        if (request.isAsynchronous()) {
	        	if (nbRequests > 1)
	        		createAndSendMultipleRequests(request);
	        	else outc.executeAsync(request);
	        	printResults();
	        }
	        else {
	        	result = outc.execute(request);
	            printResult();
	        	this.getOwner().logMessage("Result received:\n"+result.toString());
	        }
		} 
    }
	    
	/**
     * Crée une requête avec continuation
     * @param query la requête
     * @param isDCont si vraie, avec continuation directionnelle 
     * @param isFCont si vraie, avec continuation flood
     * @param dirs la/les direction(s) de la continuation 
     * @param maxDist si isDCont, la distance maximale, sinon, la valeur n'a pas d'importance
     * @param base si isDCont, la base de la continuation, sinon, la valeur n'a pas d'importance
     * @param maxJumps si FCont, le nombre de sauts maximal, sinon, la valeur n'a pas d'importance
     * @param isAsynchronous si la requête se fait en mode asynchrone
     * @param requestURI l'URI de la requête
     * @return la requête continuation
     * @throws Exception 
     */
    public RequestContinuationI createRequestContinuation(Query query, boolean isDCont, boolean isFCont, Dirs dirs, double maxDist, Base base, int maxJumps, boolean isAsynchronous, String requestURI) throws Exception {
    	
		if (isDCont) {
			DCont dcont = new DCont(dirs, maxJumps);
			query.setCont(dcont);
		}
		else if (isFCont) {
			FCont fcont = new FCont(base, maxDist);
			query.setCont(fcont);
		}
		else {
			throw new Exception("Continuation during create request is not of type ICont");
		}
		
		/* Modifier le query en parametre de la requete selon le test */
		EndPointDescriptorI endpoint = new EndPointDescriptor(inAsynchrone.getPortURI());
		RequestContinuationI request = new RequestContinuation(isAsynchronous,requestURI, (QueryI) query, (ConnectionInfoI) endpoint, null);
		return request;
    }
    
    /**
     * Crée une requête sans continuation
     * @param query la requête
     * @param isAsynchronous si la requête se fait en mode asynchrone
     * @param requestURI l'URI de la requête
     * @return la requête sans continuation
     */
    public RequestI createRequest(Query query, boolean isAsynchronous, String requestURI) {
    	// Modifie la continuation de la query
    	query.setCont(new ECont());
		
		/* Modifier le query en parametre de la requete selon le test */
		RequestI request = new Request(isAsynchronous,requestURI, (QueryI) query, null);
		return request;
    }
    
    /**
     * Accepte le résultat de la requête et ajoute les résultats de la requête spécifiée par l'URI dans la HashMap,
     * on aura donc les résultats locaux de la requête envoyés par chaque noeud stockée dans la clé URI de la requête. 
     * @param Uri l'URI de la requête envoyée
     * @param qr le résultat de la requête spécifiée par l'URI en paramètre
     */
    public void acceptRequestResult(String Uri, QueryResultI qr) {
        
    	//System.out.println(" ==================================>  resultat recu: " + qr + " uri :" +Uri);
    	
    	if (results.containsKey(Uri)) {
            // There's already a result for this URI, merge the new result with the existing one
            QueryResultI res = results.get(Uri);
            res.positiveSensorNodes().addAll(qr.positiveSensorNodes());
            res.gatheredSensorsValues().addAll(qr.gatheredSensorsValues());
        } else {
            // No result for this URI yet, put the new result in the map
            results.put(Uri, qr);
        }
    }
    
    /**
     * Print the results that were sent by the nodes and were merged beforehand, this method is only used for the asynchronous request handling
     * This method is called after DELAY seconds
     */
    private void printResults() {
    	this.getOwner().scheduleTask(o -> {
			this.getOwner().runTask(new AbstractComponent.AbstractTask() {
				@Override
				public void run() {
					try {
						
						System.out.println("PRINTING");
						
				    	if (results.isEmpty()) {
				            System.out.println("No data to merge Exiting");
				            return;
				        } else {
				        	System.out.println("not empty, print hashmap\n");
				        }
				    	
				    	/* Affichage */
				    	for (Map.Entry<String, QueryResultI> entry : results.entrySet()) {
	                        String uri = entry.getKey();
	                        QueryResultI result = entry.getValue();

	                        System.out.println("============== Result [" + uri + "] ===============");
	                        System.out.println(result);
	                        System.out.println("=================================================");

	                        this.getTaskOwner().logMessage("============== Result [" + uri + "] ===============");
	                        this.getTaskOwner().logMessage(result.toString());
	                        this.getTaskOwner().logMessage("=================================================");
	                    }
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}, ac.nanoDelayUntilInstant(ac.currentInstant().plusSeconds(DELAY*100)), TimeUnit.NANOSECONDS);
    }
    
    /**
     * Print the result, this method is only used for the synchronous request handling
     */
    public void printResult() {
    	System.out.println(result);
    }
    
    
    /**
     * Renvoie la même requête avec une uri différente plusieurs fois sur le même port
     * 
     * @param numberOfRequests Le nombre de requêtes à créer et envoyer.
     * @param isAsynchronous Détermine si les requêtes doivent être asynchrones.
     * @param query La requête de base à envoyer. (Notez que si chaque requête doit être différente, vous devrez ajuster cette méthode.)
  
     */
    public void createAndSendMultipleRequests(RequestI request) throws Exception {
    	
        for (int i = 0; i < nbRequests; i++) {
            String requestURI = request.requestURI() + i;
            RequestI req = new RequestContinuation(request.isAsynchronous(), requestURI, request.getQueryCode(), request.clientConnectionInfo(), null);
            outc.executeAsync(req);
        }
    }
    
    public void setClock(AcceleratedClock clock) {
    	ac = clock;
    }
}
