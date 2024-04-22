package components;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestResultCI;
import fr.sorbonne_u.cps.sensor_network.interfaces.EndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import connexion.EndPointDescriptor;
import connexion.NodeInfo;
import connexion.QueryResult;
import connexion.registre.GeographicalZone;
import connexion.requests.Request;
import connexion.requests.RequestContinuation;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import ast.base.ABase;
import ast.base.Base;
import ast.bexp.AndBExp;
import ast.bexp.CExpBExp;
import ast.cexp.GEqExp;
import ast.cont.DCont;
import ast.cont.ECont;
import ast.cont.FCont;
import ast.cont.ICont;
import ast.dirs.Dirs;
import ast.dirs.FDirs;
import ast.dirs.RDirs;
import ast.gather.FGather;
import ast.gather.Gather;
import ast.position.Position;
import ast.query.BQuery;
import ast.query.GQuery;
import ast.query.Query;
import ast.rand.CRand;
import ast.rand.SRand;
import components.client_node.*;
import components.client_node.asynchrone.*;
import components.client_register.LookupConnector;
import components.client_register.OutboundPortClientRegister;
import components.cvm.CVM;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;

@RequiredInterfaces(required={RequestingCI.class, LookupCI.class})
@OfferedInterfaces(offered= {RequestingCI.class, RequestResultCI.class})

/**
 * Class for the Client Component
 */
public class Client extends AbstractComponent {
    OutboundPortClient outc; //le port sortant agit comme un RequestingCI
    OutboundPortClientRegister outcreg; //le port pour le registre
    
    InboundPortClientNode inAsynchrone;
    
    private Map<String, ArrayList<QueryResultI>> resultHashMap;
    volatile RequestI request;
    QueryResultI result;
    private int nbRequests = 1; // par defaut

    protected ClocksServerOutboundPort clockOP;
    
    // temporisateur pour fusionner les resultats
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> mergeTask;
    private final long DELAY = 3;
    
    protected Client(int nbThreads, int nbSchedulableThreads,
                     String uriClient,
                     String uriOutPort,
                     String uriOutPortClientRegister,
                     String uriInPortAsynchrone,
                     RequestI request,
                     int nbRequests
                     ) throws Exception{

        super(uriClient, nbThreads, nbSchedulableThreads);
        this.outc = new OutboundPortClient( this, uriOutPort);
        outc.publishPort();
        
        this.outcreg = new OutboundPortClientRegister(this, uriOutPortClientRegister);
        outcreg.publishPort();
        
        this.inAsynchrone = new InboundPortClientNode(this,uriInPortAsynchrone);
        inAsynchrone.publishPort();
        
       	resultHashMap = new ConcurrentHashMap<>();
        this.request = request;
        
        this.nbRequests = nbRequests;
        
        this.addOfferedInterface(RequestingCI.class);
        this.addRequiredInterface(RequestingCI.class);
        this.addRequiredInterface(LookupCI.class);
        this.addOfferedInterface(RequestResultCI.class);

        this.getTracer().setTitle("Client Component") ;
        this.getTracer().setRelativePosition(2,2);
        this.toggleTracing();
        
    }

    @Override
    public void start() throws ComponentStartException {
        this.logMessage("starting client component.") ;

        // Horloge accélérée
        try {
            clockOP = new ClocksServerOutboundPort(this);
            clockOP.publishPort();
            this.doPortConnection(
                    clockOP.getPortURI(),
                    ClocksServer.STANDARD_INBOUNDPORT_URI,
                    ClocksServerConnector.class.getCanonicalName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.start() ;
    }
	
    @Override
    public synchronized void execute() throws Exception {
        super.execute();

        // Horloge accélérée
        try {

            AcceleratedClock ac = clockOP.getClock(CVM.TEST_CLOCK_URI);
            this.doPortDisconnection(clockOP.getPortURI());
            clockOP.unpublishPort();
            clockOP.destroyPort();
            // toujours faire waitUntilStart avant d’utiliser l’horloge pour
            // calculer des moments et instants
            ac.waitUntilStart();

            Instant instant = CVM.START_INSTANT.plusSeconds(CVM.nbNodes * 60);
            long d = ac.nanoDelayUntilInstant(instant); // délai en nanosecondes

            this.scheduleTask(
                    o -> { 
                        this.logMessage("executing client component.") ;
                        this.runTask(
                                new AbstractComponent.AbstractTask() {
                                    @Override
                                    public void run() {
                                        try {
                                        	/* Connexion au registre */
                                        	((Client)this.getTaskOwner()).logMessage("Connecting to the register");
                                        	((Client)this.getTaskOwner()).doPortConnection(
                                        			outcreg.getPortURI(),
                                        			CVM.registerClInURI, //peut être mettre en global pour que tous puisse le récupérer au lieu attribut?
                                        			LookupConnector.class.getCanonicalName());
                                        	
                                        	((Client)this.getTaskOwner()).logMessage("Looking for a node");
	                                        NodeInfo uriNode =(NodeInfo) outcreg.findByIdentifier("URI_Node0");
                                        	//GeographicalZoneI geo = new  GeographicalZone(-0.1, -0.1, 0.1, 0.1); //node 1 
                                        	//GeographicalZoneI zone = new GeographicalZone(-1, 1, -1, 1); // node 3
	                                        //Set<ConnectionInfoI> nodesInZone = outcreg.findByZone(zone);
	                                        //NodeInfoI firstNode = null;
	                                        
	                                        //On prend le premier noeud
	                                        /*if (!nodesInZone.isEmpty()) {
									        	Iterator<ConnectionInfoI> iterator = nodesInZone.iterator();
									            firstNode = (NodeInfoI) iterator.next(); 
									        } else {
									        	throw new Exception("Le HashSet est vide.");
									        }*/
									     
	                                        //NodeInfo uriNode = (NodeInfo) firstNode;
	                                        ((Client)this.getTaskOwner()).logMessage("Node trouvé: "+uriNode.nodeIdentifier());
	                                        
	                                        
	                                        ((Client)this.getTaskOwner()).logMessage("Connecting to its port");
											((Client)this.getTaskOwner()).doPortConnection( //requête
                                                    outc.getPortURI(),
                                                    uriNode.requestingEndPointInfo().toString(), 
                                                    RequestConnector.class.getCanonicalName());
											
											
                                    		((Client)this.getTaskOwner()).logMessage("Sending request");
                                            if (request.isAsynchronous())
                                            	createAndSendMultipleRequests(request);
                                            	//outc.executeAsync(request);
                                            else {
                                            	result = outc.execute(request);
	                                            printResult();
	                                        	((Client)this.getTaskOwner()).logMessage("Result received:\n"+result.toString());
                                            }
                                            System.out.println("======================result====================");
                                            if (request.isAsynchronous()) { // RAJOUTER UN DELAI POUR PRINT LE RESULTAT FINAL
                                            	System.out.println("test");
                                            	
                                            }
                                             
                                            System.out.println("===================================================");
                                            mergeTask = scheduler.schedule(((Client)this.getTaskOwner())::printResults, DELAY, TimeUnit.SECONDS);	
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }) ;
                        

                    }, d, TimeUnit.NANOSECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void finalise() throws Exception {
    	this.logMessage("Finalising");
        try {
			if (outc.connected())
				this.doPortDisconnection(outc.getPortURI());
			if (outcreg.connected())
				this.doPortDisconnection(outcreg.getPortURI());
		} catch (Exception e) {
			e.printStackTrace();
		}
        super.finalise();
    }
    
    @Override
    public void shutdown() throws ComponentShutdownException {
		this.logMessage("Shutting down");
    	try {
			outc.unpublishPort();
			outcreg.unpublishPort();
			inAsynchrone.unpublishPort();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		super.shutdown();
    }
    
    
    public void printResult() {
    	System.out.println(result);
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
			ICont dcont = new DCont(dirs, maxJumps);
			query.setCont(dcont);
		}
		else if (isFCont) {
			ICont fcont = new FCont(base, maxDist);
			query.setCont(fcont);
		}
		else {
			throw new Exception("Continuation during create request is not of type ICont");
		}
		
		/* Modifier le query en parametre de la requete selon le test */
		EndPointDescriptorI endpoint = new EndPointDescriptor(inAsynchrone.getPortURI(), RequestResultCI.class);
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
        
    	// renvoie la liste de la hashmap qui contient les resultats ou cree une nouvelle
    	ArrayList<QueryResultI> results = resultHashMap.getOrDefault(Uri, new ArrayList<>());
    	results.add(qr);
    	resultHashMap.put(Uri, results);
        
        // reset à chaque nouvelle reception d'un resultat
        /*
        if (mergeTask != null && !mergeTask.isDone()) {
        	System.out.println("reset");
            mergeTask.cancel(false);
        }*/

        
        //mergeTask = scheduler.schedule(this::mergeResults, DELAY, TimeUnit.SECONDS);
    }
    
    /*
     * Appelée par le temporiseur
     */
    private void printResults() {
    	System.out.println("-----merge time------");
    	
    	if (resultHashMap.isEmpty()) {
            System.out.println("No data to merge Exiting");
            return;
        } else {
        	System.out.println("not empty, print hashmap\n");
        }
    	
    	//merge();
    	//System.out.println(((RequestContinuationI)request).getExecutionState().getCurrentResult());   	
    	debugPrintHashMap();
    }
    
	/**
	 * Méthode de débogage pour afficher la taille de la HashMap resultHashMap
	 */
    public void debugPrintHashMap() {
    	// Affiche la taille de la HashMap
        System.out.println("Taille actuelle de resultHashMap: " + this.resultHashMap.size());

        // Itérer sur chaque entrée dans la HashMap
        for (Map.Entry<String, ArrayList<QueryResultI>> entry : this.resultHashMap.entrySet()) {
            String key = entry.getKey();
            ArrayList<QueryResultI> values = entry.getValue();

            StringBuilder results = new StringBuilder();
            for (QueryResultI result : values) {
                results.append(result.toString()).append("\n");
            }

            System.out.println("Requete URI: " + key + ", Nombre de résultats: " + values.size());
            System.out.println(results.toString());
            
            this.logMessage("Requete URI: " + key + ", Nombre de résultats: " + values.size());
            this.logMessage(results.toString());
        }

    }
    
    public void merge () {
    	QueryResultI mergedResult = new QueryResult(true);
    	for (ArrayList<QueryResultI> resultList : resultHashMap.values()) {
            for (QueryResultI result : resultList) {
            	System.out.println("TEST " + result);
            	mergedResult.positiveSensorNodes().addAll(((QueryResultI) resultList).positiveSensorNodes());
            	System.out.println("apres");
                mergedResult.gatheredSensorsValues().addAll(((QueryResultI) resultList).gatheredSensorsValues());
            }
        }
    	System.out.println("final: " + mergedResult);
    }
    
    /**
     * Set la request à celle en paramètre
     * @param request la requete
     */
    public void setRequest(RequestI request) {
    	this.request = request;
    }
    
    
    /**
     * Crée et envoie un nombre spécifié de requêtes avec des URIs uniques.
     * 
     * @param numberOfRequests Le nombre de requêtes à créer et envoyer.
     * @param isAsynchronous Détermine si les requêtes doivent être asynchrones.
     * @param query La requête de base à envoyer. (Notez que si chaque requête doit être différente, vous devrez ajuster cette méthode.)
  
     */
    public void createAndSendMultipleRequests(RequestI request) throws Exception {
    	
        for (int i = 0; i < nbRequests; i++) {
            String requestURI = "RequeteURI-" + i;
            RequestI req = new RequestContinuation(request.isAsynchronous(), requestURI, request.getQueryCode(), request.clientConnectionInfo(), null);
            outc.executeAsync(req);
        }
    }

   
}
