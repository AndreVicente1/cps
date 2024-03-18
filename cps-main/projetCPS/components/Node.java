package components;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import interpreter.Interpreter;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import connexion.BCM4JavaEndPointDescriptor;
import connexion.EndPointDescriptor;
import connexion.ExecutionState;
import connexion.ProcessingNode;
import connexion.Request;
import connexion.RequestContinuation;
import connexion.SensorData;
import connexion.NodeInfo;
import ast.query.BQuery;
import ast.query.GQuery;
import ast.query.Query;
import components.client_node.InboundPortProvider;
import components.client_node.asynchrone.OutboundPortNodeClient;
import components.client_node.asynchrone.RequestResultConnector;
import components.cvm.CVM;
import components.node_node.InboundPortNodeNode;
import components.node_node.NeighborConnector;
import components.node_node.OutboundPortProvider;
import components.node_register.OutboundPortNodeRegister;
import components.node_register.RegisterConnector;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.EndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;

@RequiredInterfaces(required= {SensorNodeP2PCI.class, RegistrationCI.class})
@OfferedInterfaces(offered={RequestingCI.class, SensorNodeP2PCI.class})

/**
 * Class for the Node Component
 */
public class Node extends AbstractComponent  {
	// Node attributes
	private NodeInfoI nodeInfo;
    private ArrayList<SensorDataI> sensors;
    private Interpreter interpreter;
	private ExecutionStateI exec;

    private Set<NodeInfoI> neighbours = new HashSet<NodeInfoI>();
    
    // Component attributes
    /* Inbound Port Client-Node */
    InboundPortProvider inp;
    
    /* Outbound Port Asynchronous Node-Client */
    OutboundPortNodeClient outp;
	
	/* Inbound Port Node-Node */
	InboundPortNodeNode inpn;
	
	/* Outbound Ports Node-Node */
	HashMap<Direction, OutboundPortProvider> portsMap = new HashMap<>();
    OutboundPortProvider outpNE;
    OutboundPortProvider outpNW;
    OutboundPortProvider outpSE;
    OutboundPortProvider outpSW;
    
    /* Outbound Port Node-Register */
    OutboundPortNodeRegister outpr;

    protected ClocksServerOutboundPort clockOP;

    //rajouter nodeinfo, neighbors, senseurs
    public Node(int nbThreads, int nbSchedulableThreads,
                       String uri,
                       String uriInPort,
                       String uriInPortNode,
                       String uriOutPortNodeClient,
                       String uriOutPortNE, String uriOutPortNW, String uriOutPortSE, String uriOutPortSW,
                       String uriOutPortNodeRegister,
                       PositionI p,
                       double range,
                       ArrayList<SensorDataI> sensors) throws Exception {
        super(uri, nbThreads, nbSchedulableThreads);
        
        this.nodeInfo = new NodeInfo(uri,new BCM4JavaEndPointDescriptor(uriInPortNode,SensorNodeP2PCI.class),new BCM4JavaEndPointDescriptor(uriInPort,RequestingCI.class),p,range);
        this.sensors = sensors;
        
        this.inp = new InboundPortProvider(this, uriInPort);
        inp.publishPort();
        
        this.inpn = new InboundPortNodeNode(this, uriInPortNode);
        inpn.publishPort();
        
        this.outpNE = new OutboundPortProvider(this, uriOutPortNE);
        outpNE.publishPort();
        this.outpNW = new OutboundPortProvider(this, uriOutPortNW);
        outpNW.publishPort();
        this.outpSE = new OutboundPortProvider(this, uriOutPortSE);
        outpSE.publishPort();
        this.outpSW = new OutboundPortProvider(this, uriOutPortSW);
        outpSW.publishPort();
   
        portsMap.put(Direction.SE, outpSE);
        portsMap.put(Direction.SW, outpSW);
        portsMap.put(Direction.NE, outpNE);
        portsMap.put(Direction.NW, outpNW);
        
        this.outpr = new OutboundPortNodeRegister(this,uriOutPortNodeRegister);
        outpr.publishPort();
        
        this.outp = new OutboundPortNodeClient(this,uriOutPortNodeClient);
        outp.publishPort();
        interpreter = new Interpreter();
        
        this.addOfferedInterface(SensorNodeP2PCI.class);
        this.addOfferedInterface(RequestingCI.class);
        
        this.addRequiredInterface(SensorNodeP2PCI.class);
        this.addRequiredInterface(RegistrationCI.class);
        
        this.getTracer().setTitle("Node Component") ;
        this.getTracer().setRelativePosition(2, 1);
        this.toggleTracing();
    }
    
    @Override
    public void start() throws ComponentStartException {
        this.logMessage("starting node component " + nodeInfo.nodeIdentifier());

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

        super.start();
    }
    
  //méthode auxiliaire pour rechercher un senseur
    private SensorDataI searchSensor(String sensorIdentifier) {
    	for (SensorDataI s : sensors) {
    		if (s.getSensorIdentifier().equals(sensorIdentifier)) {
    			//System.out.println("Trouvé et ajouté " + sensorIdentifier);
    			return s;
    		}
    	}
    	System.out.println("SENSOR NULL");
    	return null;
    }
    
    /**
     * Retourne les informations du noeud NodeInfoI courrant
     * @return le NodeInfoI du noeud
     */
    public NodeInfoI getNodeInfo() {
        return nodeInfo;
    }

    /**
     * Renvoie l'ensemble des voisins du noeud, donné par le registre lorsqu'il se connecte
     * @return l'ensemble des voisins du noeud
     */
    public Set<NodeInfoI> getNeighbours() {
        return neighbours;
    }

    /**
     * Renvoie la valeur du senseur par son identifiant en paramètre
     * @param l'identifiant du noeud dont on cherche la valeur
     * @return la valeur du senseur
     */
    public SensorDataI getSensorData(String sensorIdentifier) {
        return searchSensor(sensorIdentifier);
    }
    
    /**
     * Cette méthode a été rajouté pour récupérer tous les noeuds
     * @return la liste de touts les senseurs du noeud courrant
     */
    public ArrayList<SensorDataI> getAllSensors(){
    	return sensors;
    }
    
    /**
     * Method used by the register to set the node's neighbors
     * @param the neighbors set to be given to the node
     */
    public void setNeighbors(Set<NodeInfoI> neighbors) {
    	this.neighbours = neighbors;
    }
    
    
    // ==== Components methods ====
    public QueryResultI treatRequest(RequestI request) throws Exception{
    	if (request instanceof RequestContinuation)
    		return treatRequest((RequestContinuationI) request);
    	else if (request instanceof Request)
    		return treatRequest((Request) request);
    	else throw new Exception ("Request is not of type Request or RequestContinuation");
    }
    
    /**
     * Méthode SYNCHRONE: renvoie le résultat de la requête **sans continuation** après l'avoir traité
     * @param la requête RequestI
     * @return le résultat de la requête QueryResultI
     */
    public QueryResultI treatRequest(Request request) throws Exception{
    	if (request.isAsynchronous()) {  //on traite seulement synchrone pour l'instant
    		throw new Exception("Asynchronous request is being treated in the synchronous method");
    	}
    	
		randomOccurence();// simulation des changements de valeur des sensors avant traitement
		
    	this.logMessage("Reception d'une requete ");
    	
		Object res = null;
		Query query = (Query) request.getQueryCode(); //on récupere la query pour l'exécuter
		ProcessingNodeI pn = new ProcessingNode(this);
		
		if (query instanceof BQuery) {
			if (exec == null) exec = new ExecutionState(this,pn, true);
				
			res = interpreter.visit((BQuery) query, exec);
		} else if (query instanceof GQuery) {
			if (exec == null) exec = new ExecutionState(this,pn, false);
			
			res = interpreter.visit((GQuery) query, exec);
		} else throw new Exception("La requête n'est pas une query reconnue");

		return (QueryResultI) res;
		
    }
    
    /**
     * Méthode SYNCHRONE: renvoie le résultat de la requête **avec continuation** après l'avoir traité,
     * la requête est également envoyée aux voisins selon la direction de la requête
     * @param la requête RequestContinuationI
     * @return le résultat de la requête QueryResultI
     */
    public QueryResultI treatRequest(RequestContinuationI request) throws Exception{
		randomOccurence();// simulation des changements de valeur des sensors avant traitement
		
		this.logMessage("Reception d'une requete continuation");
    	if (request == null) throw new Exception("Request is null");
    	if (request.isAsynchronous()){ throw new Exception("Asynchronous request is being treated in the synchronous method"); }
		
    	ExecutionStateI exec = request.getExecutionState();
		ProcessingNodeI pn = new ProcessingNode(this);
		
		// TRAITEMENT LOCAL DE LA REQUETE
		
		//System.out.println("-----------EXECUTION SUR LE NODE: ------------\n processingNode " + pn.getNodeIdentifier()
		// + " exec state: " + exec);
    	Object res = null;
		Query query = (Query) request.getQueryCode();
		if (exec != null) {
			ExecutionState e = (ExecutionState) exec;
			//exécution sur un noeud déjà visité on stop le traitement
			if (e.getVisitedNodes().contains(pn.getNodeIdentifier())) {
				System.out.println("===========node deja visité==============");
				return null;
			}
			
			exec.updateProcessingNode(pn);
		}
		
		if (query instanceof BQuery) {
			if (exec == null) exec = new ExecutionState(this,pn, true);
				
			res = interpreter.visit((BQuery) query, exec);
			if (res != null)
				this.logMessage("Resulat du traitement local: "+ res.toString());
		} else if (query instanceof GQuery) {
			if (exec == null) exec = new ExecutionState(this,pn, false);
			
			res = interpreter.visit((GQuery) query, exec);
			if (res != null)
				this.logMessage("Resulat du traitement local: "+ res.toString());
		} else throw new Exception("La requête n'est pas une query reconnue");
		
		ExecutionState ex = (ExecutionState) exec;
		ex.addVisitedNode(pn.getNodeIdentifier());
		
        QueryResultI result = (QueryResultI) res;
        
        // TRAITEMENT DE LA CONTINUATION 
        
       	if (!exec.noMoreHops() && exec.withinMaximalDistance(pn.getPosition())) {
            exec.incrementHops(); /* cela ne devrait pas poser de probleme même si la requête est en FCont */
        	
        	RequestContinuationI reqCont = new RequestContinuation(
        			request.isAsynchronous(), 
        			request.requestURI(), 
        			request.getQueryCode(),
        			request.clientConnectionInfo(),
        			exec);
        			
        	//on determine les voisins sur lesquels on envoie la continuation
        	Set<Direction> dirs = exec.getDirections();
        	
        	for (Direction dir: dirs){
				
				for (NodeInfoI voisin : neighbours) {
					if (voisin == null) continue;
					if (voisin.nodePosition().directionFrom(this.nodeInfo.nodePosition())==dir && !ex.getVisitedNodes().contains(voisin.nodeIdentifier())){
						
						// Propagation de la requête
						
						QueryResultI tmp = null;
						OutboundPortProvider port = portsMap.get(dir);
						
						if (port.connected()) { 
						    this.logMessage("Propagation de la continuation vers le "+ dir + " avec port " + port.getPortURI());
						    tmp = port.execute(reqCont);
						} else {
						    throw new Exception("Port pas connecté, la continuation se termine");
						}

						if (tmp != null) {
							//System.out.println("QueryResult donné:\n" + result);
							if (result != null) {
								// On ajoute le resultat de la continuation au resultat local
		    					result.gatheredSensorsValues().addAll(tmp.gatheredSensorsValues());
								result.positiveSensorNodes().addAll(tmp.positiveSensorNodes());
							}
						} else {
							throw new Exception("execute error from ports");
						}
					 }
				}
				
			}
        }
        return result; // fin de la continuation
    }
    
    
    /*
     * Asynchronous
     */
    public void treatRequestAsynchronous(RequestI request) throws Exception{
    	if (request instanceof RequestContinuation)
    		treatRequestAsynchronous((RequestContinuationI) request);
    	else if (request instanceof Request)
    		treatRequestAsynchronous((Request) request);
    	else throw new Exception ("Request is not of type Request or RequestContinuation");
    }
    
    /**
     * Méthode ASYNCHRONE: envoie le résultat local de la requête **sans continuation** au client
     * @param request la requête asynchrone
     * @throws Exception
     */
    public void treatRequestAsynchronous(Request request) throws Exception {
    	if (!request.isAsynchronous()) { throw new Exception("Synchronous request is being treated in the asynchronous method"); }
    	this.logMessage("Reception d'une requête asynchrone *sans continuation*, connexion au port entrant du client");
    	// Connexion au port entrant pour envoyer le résultat
		ConnectionInfoI co = request.clientConnectionInfo();
    	this.doPortConnection(
    			outp.getPortURI(),
    			((EndPointDescriptor) co.endPointInfo()).getInboundPortURI(),
    			RequestResultConnector.class.getCanonicalName());
		
		Object res = null;
		Query query = (Query) request.getQueryCode(); //on récupere la query pour l'exécuter
		ProcessingNodeI pn = new ProcessingNode(this);
		
		if (query instanceof BQuery) {
			if (exec == null) exec = new ExecutionState(this,pn, true);
				
			res = interpreter.visit((BQuery) query, exec);
		} else if (query instanceof GQuery) {
			if (exec == null) exec = new ExecutionState(this,pn, false);
			
			res = interpreter.visit((GQuery) query, exec);
		} else throw new Exception("La requête n'est pas une query reconnue");
		
		// envoie au client le résultat
		this.outp.acceptRequestResult(request.requestURI(), (QueryResultI) res);

    }
    
    /**
     * Méthode ASYNCHRONE: envoie le résultat local de la requête **avec continuation** au client, 
     * la requête est également envoyée aux voisins selon la direction de la requête
     * @param requestContinuation la requête avec continuation
     * @throws Exception
     */
    public void treatRequestAsynchronous(RequestContinuationI request) throws Exception {
    
    	if (request == null) throw new Exception("Request is null in treatRequest");
    	if (!request.isAsynchronous()){ throw new Exception("Asynchronous request is being treated in the synchronous method"); }
    	this.logMessage("Reception d'une requête asynchrone *avec continuation*, connexion au port entrant du client\"");
    	
    	//Connexion au port entrant pour envoyer le résultat
    	this.doPortConnection(
    			outp.getPortURI(),
    			((BCM4JavaEndPointDescriptor) request.clientConnectionInfo().endPointInfo()).getInboundPortURI(),
    			RequestResultConnector.class.getCanonicalName());
    	
		ExecutionStateI exec = request.getExecutionState();
		ProcessingNodeI pn = new ProcessingNode(this);
		
		// TRAITEMENT LOCAL DE LA REQUETE
		
		//System.out.println("-----------EXECUTION SUR LE NODE: ------------\n processingNode " + pn.getNodeIdentifier()
		// + " exec state: " + exec);
    	Object res = null;
		Query query = (Query) request.getQueryCode();
		if (exec != null) {
			ExecutionState e = (ExecutionState) exec;
			//exécution sur un noeud déjà visité on stop le traitement
			if (e.getVisitedNodes().contains(pn.getNodeIdentifier())) {
				System.out.println("===========node deja visité==============");
				return;
			}
			
			exec.updateProcessingNode(pn);
		}
		
		if (query instanceof BQuery) {
			if (exec == null) exec = new ExecutionState(this,pn, true);
				
			res = interpreter.visit((BQuery) query, exec);
			if (res != null)
				this.logMessage("Resulat du traitement local: "+ res.toString());
		} else if (query instanceof GQuery) {
			if (exec == null) exec = new ExecutionState(this,pn, false);
			
			res = interpreter.visit((GQuery) query, exec);
			if (res != null)
				this.logMessage("Resulat du traitement local: "+ res.toString());
		} else throw new Exception("La requête n'est pas une query reconnue");
		
		ExecutionState ex = (ExecutionState) exec;
		ex.addVisitedNode(pn.getNodeIdentifier());
		
        QueryResultI result = (QueryResultI) res;
        
        // TRAITEMENT DE LA CONTINUATION 
        
       	if (!exec.noMoreHops() && exec.withinMaximalDistance(pn.getPosition())) {
            exec.incrementHops(); /* cela ne devrait pas poser de probleme même si la requête est en FCont */
        	
        	RequestContinuationI reqCont = new RequestContinuation(
        			request.isAsynchronous(), 
        			request.requestURI(), 
        			request.getQueryCode(),
        			request.clientConnectionInfo(),
        			exec);
        			
        	//on determine les voisins sur lesquels on envoie la continuation
        	Set<Direction> dirs = exec.getDirections();
        	
        	for (Direction dir: dirs){
				
				for (NodeInfoI voisin : neighbours) {
					if (voisin == null) continue;
					if (voisin.nodePosition().directionFrom(this.nodeInfo.nodePosition())==dir && !ex.getVisitedNodes().contains(voisin.nodeIdentifier())){
						
						// Propagation de la requête
						
						OutboundPortProvider port = portsMap.get(dir);
						
						if (port.connected()) { 
						    this.logMessage("Propagation de la continuation vers le "+ dir + " avec port " + port.getPortURI());
						    //envoie au voisin la requete
						    port.executeAsync(reqCont);
						} else {
						    throw new Exception("Port pas connecté, la continuation se termine");
						}
					 }
				}
				
			}
        }
       	
       	outp.acceptRequestResult(request.requestURI(), result);
    }
    
    @Override
    public void execute() throws Exception{
        super.execute();
        
        try {
            AcceleratedClock ac = clockOP.getClock(CVM.TEST_CLOCK_URI);
            this.doPortDisconnection(clockOP.getPortURI());
            clockOP.unpublishPort();
            clockOP.destroyPort();
            ac.waitUntilStart();

            char numero = this.nodeInfo.nodeIdentifier().charAt(this.nodeInfo.nodeIdentifier().length()-1);
            int i = Integer.valueOf(numero); 
            
          /* remplacer i par 0 si on veut exécution sans décalage par noeuds
           * le cas à 0 représente les noeuds qui s'installent entre d'autres noeuds
           */
            Instant instant = CVM.START_INSTANT.plusSeconds(i + 40);
            
            long d = ac.nanoDelayUntilInstant(instant);

            this.scheduleTask(
                    o -> { 					
                        this.logMessage("executing node component " + nodeInfo.nodeIdentifier()) ;
                        this.runTask(
                                new AbstractComponent.AbstractTask() {
                                    @Override
                                    public void run() {
                                        try {
                                        	((Node)this.getTaskOwner()).logMessage("Connecting to the register");
                                        	((Node)this.getTaskOwner()).doPortConnection(
                                        			outpr.getPortURI(),
                                        			CVM.uriInPortRegister,
                                        			RegisterConnector.class.getCanonicalName());
                                        		
                                                neighbours = outpr.register(nodeInfo); 
                                        	
                                            for (NodeInfoI voisin : neighbours) {
                                                ((Node)this.getTaskOwner()).ask4Connection(voisin);
                                                Direction direction = voisin.nodePosition().directionFrom(nodeInfo.nodePosition()); 
                                                OutboundPortProvider p = portsMap.get(direction);
                                                p.ask4Connection(nodeInfo);
                                            }

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
    public void finalise() {
    	this.logMessage("Finalising");
        try {
			super.finalise();
			
			Set<NodeInfoI> clonedNeighbours = new HashSet<>(neighbours);
			for (NodeInfoI voisin : clonedNeighbours) {
        		this.ask4Disconnection(voisin);
            }
            outpr.unregister(nodeInfo.nodeIdentifier());
            
			inp.unpublishPort();
			inpn.unpublishPort();
	        outpSE.unpublishPort();
	        outpSW.unpublishPort();
	        outpNE.unpublishPort();
	        outpNW.unpublishPort();
	        outpr.unpublishPort();
	        
	        //if (outp.connected()) outp.unpublishPort();
	        
		} catch (ComponentShutdownException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /*@Override
    public void shutdown() {
    	this.logMessage("Shutdown");
    	try {
			if (this.inp.connected()) {
				this.inp.destroyPort();
			}
			if (this.inpn.connected()) {
				this.inpn.destroyPort();
			}
			if (this.outpSE.connected()) {
				this.outpSE.destroyPort();
			}
			if (this.outpNE.connected()) {
				this.outpNE.destroyPort();
			}
			if (this.outpNW.connected()) {
				this.outpNW.destroyPort();
			}
			if (this.outpr.connected()) {
				this.outpr.destroyPort();
			}
			if (this.outp.connected()) {
				this.outp.unpublishPort();
				this.outp.destroyPort();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }*/
     
    
    /* 
     * Node-Node connection methods 
     */
    
    /**
     * Connect the initial node to the target neighbor node, if the target neighbor is already connected to
     * another node, the target neighbor will ask4Disconnection from it before connecting to the initial node
     * @param neighbor the node information of the target neighbor 
     */
    public void ask4Connection(NodeInfoI neighbor) throws Exception {
		this.logMessage("Ask4Connection - Connexion souhaité vers "+neighbor.nodeIdentifier());
    	System.out.println("traitement du noeud: " + nodeInfo.nodeIdentifier()+" connection souhaité vers "+neighbor.nodeIdentifier() );
    	Direction d = neighbor.nodePosition().directionFrom(nodeInfo.nodePosition());
	    OutboundPortProvider port = portsMap.get(d);
	    
	    // On cherche z le NodeInfo qui utilise deja le port dont on a besoin, pour pouvoir le deconnecter et le supprimer de nos voisins
	    if (port.connected()) {
		    NodeInfoI z = null;
		    for (NodeInfoI voisin : neighbours) {
		    	if (voisin == null) continue;
				Direction directionVoisin = voisin.nodePosition().directionFrom(nodeInfo.nodePosition());
				if (directionVoisin.equals(d)){
					System.out.println("direction égale " + directionVoisin+ "\nvoisin:" + voisin);
					z = voisin;
					break;
				}
				
			}
			
			if (z!=null && !z.nodeIdentifier().equals(neighbor.nodeIdentifier())) {
				 System.out.println("==========voisin z deconnecté pour faire de la place============");
				 System.out.println("Node: "+this.nodeInfo.nodeIdentifier()+" deconnecte "+z.nodeIdentifier()+" pour faire place à " + neighbor.nodeIdentifier());
				 System.out.println("================================================================");
				 this.ask4Disconnection(z);
				 neighbours.remove(z);
			}
	    }
	
	    if (port != null ) {
			this.logMessage("Ask4Connection - Connexion effectué depuis port =  "+port.getPortURI()+ " vers "+ neighbor.p2pEndPointInfo().toString());
			System.out.println("connexion effectué : " + port.getPortURI()+" --> "+neighbor.p2pEndPointInfo().toString());
	        this.doPortConnection(
	            port.getPortURI(),
	            neighbor.p2pEndPointInfo().toString(),
	            NeighborConnector.class.getCanonicalName());
	        
	        if (!neighbours.contains(neighbor)) neighbours.add(neighbor);
	        
	    } 
    }
    
    /**
     * Disconnect the initial node to the neighbour node and find a nearest new neighbour from the same direction
     * @param neighbour the target neighbour node
     * @throws Exception
     */
    public void ask4Disconnection(NodeInfoI neighbour) throws Exception {
    	if (neighbour == null) return;
        Direction directionFrom = neighbour.nodePosition().directionFrom(this.nodeInfo.nodePosition());
        if (portsMap.get(directionFrom).connected()) {
			this.logMessage("Ask4Disconnection - Deconnexion en "+directionFrom);
            this.doPortDisconnection(portsMap.get(directionFrom).getPortURI());
        }
        // On cherche un nouveau voisin pour remplacer celui qui s'est deconnecté
        NodeInfoI newVoisin = this.outpr.findNewNeighbour(this.nodeInfo, directionFrom);
        if (!neighbours.contains(newVoisin))neighbours.add(newVoisin);
        
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    /**
     * Methods to simulate random occurences to update the sensors value of the current node
     * @throws NumberFormatException
     */
	public void randomOccurence() throws NumberFormatException {
    	 Random random = new Random();
    	 
    	 for (int i = 0; i < sensors.size(); i++) {
    		 SensorData sensorData = (SensorData) sensors.get(i);
             Serializable value = sensorData.getValue();
             double probability = random.nextDouble();
             if (value instanceof Integer) {
            	 int v;
            	 /* Can modifiy the probability and the value changed */
            	 /* Currently increases the value of the sensor by 2 or 3 */
            	 /* Or decreases it by 1 or 2 */
            	 if (probability < 0.6) { 
	                 v =  (Integer) value + (int)random.nextDouble() * 2 + 2;
            	 }
            	 else {
            		 v = (Integer) value - (int)random.nextDouble() * 2 - 1;
            	 }
            	 sensorData.setData((Serializable) Integer.valueOf(v));
	                	 
             } else if (value instanceof Double) {
            	 double v;
            	 if (probability < 0.6) {
	                 v = (Double) value + (int) random.nextDouble() * 2 + 2;
            	 }
            	 else {
            		 v = (Double) value - (int) random.nextDouble() * 2 - 1;
            	 }
            	 sensorData.setData((Serializable) Double.valueOf(v));
             }
    	 }
    }
}
