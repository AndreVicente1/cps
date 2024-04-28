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
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PImplI;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import connexion.BCM4JavaEndPointDescriptor;
import connexion.EndPointDescriptor;
import connexion.ExecutionState;
import connexion.ProcessingNode;
import connexion.SensorData;
import connexion.requests.Request;
import connexion.requests.RequestContinuation;
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
public class Node extends AbstractComponent implements SensorNodeP2PImplI {
	// Node attributes
	private NodeInfoI nodeInfo;
    private ArrayList<SensorDataI> sensors;
    private Interpreter interpreter;
	private volatile ExecutionStateI exec;

	// Concurrent HashMap en Set pour gérer la concurrence
    private Set<NodeInfoI> neighbours = new HashSet<>();
    private ConcurrentHashMap<String, Boolean> treatedRequests = new ConcurrentHashMap<>();

    // Component attributes
    /* Inbound Port Client-Node */
    InboundPortProvider inp;
    
    /* Outbound Port Asynchronous Node-Client */
    OutboundPortNodeClient outp;
	
	/* Inbound Port Node-Node */
	InboundPortNodeNode inpn;
	
	/* Outbound Ports Node-Node */
	Map<Direction, OutboundPortProvider> portsMap = new ConcurrentHashMap<>();
    OutboundPortProvider outpNE;
    OutboundPortProvider outpNW;
    OutboundPortProvider outpSE;
    OutboundPortProvider outpSW;
    
    /* Outbound Port Node-Register */
    OutboundPortNodeRegister outpr;

    protected ClocksServerOutboundPort clockOP;
    
    /**
     * Concurrency handling
     */
    
    /** URI of the pool of threads used to handle new asynchronous requests received from Clients */
	protected static final String	ASYNC_NEW_REQUEST_POOL_URI = "asynchronous new request pool" ;
	/** URI of the pool of threads used to handle propagated asynchronous requests received from Nodes */
	protected static final String	ASYNC_CONT_REQUEST_POOL_URI = "asynchronous continuation request pool" ;
	/** URI of the pool of threads used to handle synchronous requests */
	protected static final String	SYNCHRONOUS_POOL_URI = "synchronous pool" ;
	/** URI of the pool of threads used to handle connection/disconnection with other Nodes */
	protected static final String	CONNECTION_POOL_URI = "connection pool" ;
	/** number of threads to be used in the pool of threads are directly given in the constructor for each pool */
	
	private final Lock lock = new ReentrantLock();

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
                       ArrayList<SensorDataI> sensors,
                       int nbThreadsNewReqPool,
                       int nbThreadsContReqPool,
                       int nbThreadsConnectionPool,
                       int nbThreadsSyncPool
                       ) throws Exception {
    	
        super(uri, nbThreads, nbSchedulableThreads);
        
        this.nodeInfo = new NodeInfo(uri,new BCM4JavaEndPointDescriptor(uriInPortNode,SensorNodeP2PCI.class),new BCM4JavaEndPointDescriptor(uriInPort,RequestingCI.class),p,range);
        this.sensors = sensors;
        
        this.inp = new InboundPortProvider(this, uriInPort, SYNCHRONOUS_POOL_URI, ASYNC_NEW_REQUEST_POOL_URI);
        inp.publishPort();
        
        this.inpn = new InboundPortNodeNode(this, uriInPortNode, CONNECTION_POOL_URI, SYNCHRONOUS_POOL_URI, ASYNC_CONT_REQUEST_POOL_URI);
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
        
        /* creating pool of threads, one to handle requests sent from client, 
         * one to handle requests propagated from other nodes */
        // modifier si schedulable
        this.createNewExecutorService(CONNECTION_POOL_URI, nbThreadsConnectionPool, false);
        this.createNewExecutorService(ASYNC_NEW_REQUEST_POOL_URI, nbThreadsNewReqPool, false);
        this.createNewExecutorService(ASYNC_CONT_REQUEST_POOL_URI, nbThreadsContReqPool, false);
        this.createNewExecutorService(SYNCHRONOUS_POOL_URI, nbThreadsSyncPool, false);
        
        this.getTracer().setTitle("Node Component") ;
        this.getTracer().setRelativePosition(2, 1);
        //this.toggleTracing(); // supprimer si beaucoup de noeuds!! sinon ca bug
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
        for (SensorDataI s : sensors) {
    		if (s.getSensorIdentifier().equals(sensorIdentifier)) {
    			return s;
    		}
    	}
    	System.out.println("SENSOR NULL");
    	return null;
    }
    
    /**
     * Cette méthode a été rajouté pour récupérer tous les noeuds
     * @return la liste de touts les senseurs du noeconnectionud courrant
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
            Instant instant = CVM.START_INSTANT.plusSeconds(i + 10);
            
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
    public void finalise() throws Exception {
    	this.logMessage("Finalising");
        try {
			
			Set<NodeInfoI> clonedNeighbours = new HashSet<>(neighbours);
			for (NodeInfoI voisin : clonedNeighbours) {
        		this.ask4Disconnection(voisin);
            }
            outpr.unregister(nodeInfo.nodeIdentifier());
            
            //déconnexion des ports sortans
            //this.doPortDisconnection(inp.getPortURI());
            //this.doPortDisconnection(inpn.getPortURI());
            
            if (outpSE.connected())
            	this.doPortDisconnection(outpSE.getPortURI());
            if (outpSW.connected())
            	this.doPortDisconnection(outpSW.getPortURI());
            if (outpNE.connected())
            	this.doPortDisconnection(outpNE.getPortURI());
            if (outpNW.connected())
            	this.doPortDisconnection(outpNW.getPortURI());
            if (outpr.connected())
            	this.doPortDisconnection(outpr.getPortURI());
            if (outp.connected())
            	this.doPortDisconnection(outp.getPortURI());
	        
            
		} catch (ComponentShutdownException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        super.finalise();
    }
    
    @Override
    public void shutdown() throws ComponentShutdownException {
    	this.logMessage("Shutdown");
    	try {
    		inp.unpublishPort();
			inpn.unpublishPort();
	        outpSE.unpublishPort();
	        outpSW.unpublishPort();
	        outpNE.unpublishPort();
	        outpNW.unpublishPort();
	        outpr.unpublishPort();
			outp.unpublishPort();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    	super.shutdown();
    }
    
    /*************************************************************
     * Component life-cycle methods
     *************************************************************/
    
    /* 
     * Node-Node connection methods 
     */
    
    /**
     * Connect the initial node to the target neighbor node, if the target neighbor is already connected to
     * another node, the target neighbor will ask4Disconnection from it before connecting to the initial node
     * @param neighbor the node information of the target neighbor 
     */
    public void ask4Connection(NodeInfoI neighbor) throws Exception {
        this.logMessage("Ask4Connection - Connexion souhaitée vers " + neighbor.nodeIdentifier());
        System.out.println("traitement du noeud: " + nodeInfo.nodeIdentifier() + " connexion souhaitée vers " + neighbor.nodeIdentifier());

        this.getExecutorService(CONNECTION_POOL_URI).submit(() -> {
            try {
                Direction d = neighbor.nodePosition().directionFrom(nodeInfo.nodePosition());
                OutboundPortProvider port = portsMap.get(d);
                if (port != null) {
                    this.logMessage("Ask4Connection - Connexion établie depuis port =  " + port.getPortURI() + " vers " + neighbor.p2pEndPointInfo());
                    // Connect directly to the neighbor
                    this.doPortConnection(
                        port.getPortURI(),
                        neighbor.p2pEndPointInfo().toString(),
                        NeighborConnector.class.getCanonicalName());
                    
                    if (!neighbours.contains(neighbor)) {
                        neighbours.add(neighbor);
                        port.ask4Connection(this.nodeInfo);
                    }
                    
                    
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).get();
    }
    
    /**
     * Disconnect the initial node to the neighbour node and find a nearest new neighbour from the same direction
     * @param neighbour the target neighbour node
     * @throws Exception
     */
    public void ask4Disconnection(NodeInfoI neighbour) throws Exception {
    	this.getExecutorService(CONNECTION_POOL_URI).submit(() -> {
	    	if (neighbour == null) return;
	        Direction directionFrom = neighbour.nodePosition().directionFrom(this.nodeInfo.nodePosition());
	        try {
				if (portsMap.get(directionFrom).connected()) {
					this.logMessage("Ask4Disconnection - Deconnexion en "+directionFrom);
				    this.doPortDisconnection(portsMap.get(directionFrom).getPortURI());
				}
			
	        // On cherche un nouveau voisin pour remplacer celui qui s'est deconnecté
	        NodeInfoI newVoisin = this.outpr.findNewNeighbour(this.nodeInfo, directionFrom);
	        if (!neighbours.contains(newVoisin))neighbours.add(newVoisin);
	        } catch (Exception e) {
				e.printStackTrace();
			}
    	}).get();
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
    
    /*************************************************************************
    * Request Handling Methods
    /*************************************************************************
    
    /**
     * Méthode SYNCHRONE: renvoie le résultat de la requête après l'avoir traité
     * @param la requête RequestI
     * @return le résultat de la requête QueryResultI
     */
    public QueryResultI execute(RequestI request) throws Exception {
        //randomOccurence();
        this.logMessage("Réception d'une requête");
        
        if (request.isAsynchronous()) {
            throw new Exception("Asynchronous request is being treated in the synchronous method");
        }
        
        Boolean treated = treatedRequests.putIfAbsent(request.requestURI(), Boolean.TRUE);
	    if (treated != null) {
	        this.logMessage("Requête déjà traitée : " + request.requestURI());
	        return null; 
	    }
        
        return this.getExecutorService(SYNCHRONOUS_POOL_URI).submit(() -> {
	        
	        ProcessingNodeI pn = new ProcessingNode(this);
	        Query query = (Query) request.getQueryCode();
	
	        if (exec == null) {
	            exec = new ExecutionState(this, pn, query instanceof BQuery);
	        }
	
	        Object res = executeQuery(query, exec);
	        QueryResultI result = (QueryResultI) res;
	        if (res != null) {
	            this.logMessage("Résultat du traitement local: " + res.toString());
	        }
	
	        // continuation
	        if (((Query)request.getQueryCode()).getCont() != null)  {
	            handleContinuation((RequestContinuationI) request, exec, result);
	        }
	
	        return result;
       }).get();
    }

    /**
     * Méthode SYNCHRONE: renvoie le résultat de la requête **avec continuation**, cette méthode est utilisée par les noeuds
     * @param la requête RequestContinuationI
     * @return le résultat de la requête QueryResultI
     */
	@Override
	public QueryResultI execute(RequestContinuationI request) throws Exception {
		//randomOccurence();// simulation des changements de valeur des sensors avant traitement
		this.logMessage("Réception d'une requête de continuation");
		
		if (request == null) throw new Exception("Request is null");
	    if (request.isAsynchronous()) {
	        throw new Exception("Asynchronous request is being treated in the synchronous method");
	    }
	    
	    Boolean treated = treatedRequests.putIfAbsent(request.requestURI(), Boolean.TRUE);
	    if (treated != null) {
	        this.logMessage("Requête déjà traitée : " + request.requestURI());
	        return null; 
	    }
		
		return this.getExecutorService(SYNCHRONOUS_POOL_URI).submit(() -> {
	
		    ProcessingNodeI pn = new ProcessingNode(this);
		    exec = request.getExecutionState();
		    if (exec == null) {
		        exec = new ExecutionState(this, pn, request.getQueryCode() instanceof BQuery);
		    } else {
		        exec.updateProcessingNode(pn);
		    }
	
		    Object res = executeQuery((Query) request.getQueryCode(), exec);
		    QueryResultI result = (QueryResultI) res;
		    if (res != null) {
		        this.logMessage("Résultat du traitement local: " + res.toString());
		    }
	
		    handleContinuation((RequestI)request, exec, result);
	
		    return result;
		}).get();
	}
	
	/**
	 * Méthode ASYNCHRONE: traite et renvoie le résultat de la requête au client après l'avoir traité, cette méthode est appelée par le client via les ports
	 * Le noeud envoie le résultat local directement au client
	 * @param request la requête à traiter
	 * @throws Exception
	 */
	public void executeAsync(RequestI request) throws Exception {
		System.out.println("requete " + this.nodeInfo.nodeIdentifier() + " traite " + request.requestURI());
		if (!request.isAsynchronous()) {
            throw new Exception("Asynchronous request is being treated in the synchronous method");
        }
        this.logMessage("Réception d'une requête asynchrone, connexion au port entrant du client");
        
        Boolean treated = treatedRequests.putIfAbsent(request.requestURI(), Boolean.TRUE);
	    if (treated != null) {
	        this.logMessage("Requête déjà traitée : " + request.requestURI());
	        return; 
	    }
        
        // pas de get pour maximiser l'asynchrone et le parallélisme
		this.getExecutorService(ASYNC_NEW_REQUEST_POOL_URI).submit(() -> { 
	        try {
	        	System.out.println("executor node by client");
				this.doPortConnection(
				        outp.getPortURI(),
				        ((BCM4JavaEndPointDescriptor) request.clientConnectionInfo().endPointInfo()).getInboundPortURI(),
				        RequestResultConnector.class.getCanonicalName());
	
		        ProcessingNodeI pn = new ProcessingNode(this);
		        exec = exec != null ?
		                        ((RequestContinuationI) request).getExecutionState() :
		                        new ExecutionState(this, pn, request.getQueryCode() instanceof BQuery);
		        
		        if (exec != null) {
		            exec.updateProcessingNode(pn);
		        }
	        	
		        
		        // Exécution locale de la requête
		        QueryResultI result = (QueryResultI) executeQuery((Query) request.getQueryCode(), exec);
		        
		        if (((Query)request.getQueryCode()).getCont() != null)  {
		            handleContinuation((RequestContinuationI) request, exec, result);
		        }
	        	
		        this.logMessage("request uri: " + request.requestURI() + "\n" + result.toString());
		        // Envoi du résultat au client
		        outp.acceptRequestResult(request.requestURI(), result);
		        
	        } catch (Exception e) {
				e.printStackTrace();
			}
		});
   }

	/**
     * Méthode ASYNCHRONE: traite et envoie le résultat local de la requête **avec continuation** au client
     * la requête est également envoyée aux voisins selon la direction de la requête
     * @param requestContinuation la requête avec continuation
     * @throws Exception
     */
	@Override
	public void executeAsync(RequestContinuationI request) throws Exception {
		if (request == null) throw new Exception("Request is null in executeAsync");
		
		this.logMessage("Réception d'une requête asynchrone, Continuation depuis le noeud " + request.getExecutionState().getProcessingNode());
		//System.out.println("requete " + this.nodeInfo.nodeIdentifier() + " traite " + request.requestURI());
	    if (!request.isAsynchronous()) {
	        throw new Exception("Synchronous request is being treated in the asynchronous method");
	    }
	    
	    Boolean treated = treatedRequests.putIfAbsent(request.requestURI(), Boolean.TRUE);
	    if (treated != null) {
	        this.logMessage("Requête déjà traitée : " + request.requestURI());
	        return; 
	    }
	    
	    this.getExecutorService(ASYNC_CONT_REQUEST_POOL_URI).submit(() -> {
	
		    // Connexion au port entrant pour envoyer le résultat
		    try {
				this.doPortConnection(
				    outp.getPortURI(),
				    ((BCM4JavaEndPointDescriptor) request.clientConnectionInfo().endPointInfo()).getInboundPortURI(),
				    RequestResultConnector.class.getCanonicalName());

			    ProcessingNodeI pn = new ProcessingNode(this);
			    exec = request.getExecutionState();
			    if (exec == null) {
			        throw new Exception("Execution State is null during continuous request treating, it should not be the case");
			    } else {
			    	if (((ExecutionState)exec).wasVisited(this.nodeInfo.nodeIdentifier())) return;
			        exec.updateProcessingNode(pn);
			    }
				
			    QueryResultI result = (QueryResultI) executeQuery((Query) request.getQueryCode(), exec);
			    
			    handleContinuation(request, exec, result);
				
			    this.logMessage("request uri: " + request.requestURI() + "\n" + result.toString());
			    // Envoi du résultat au client
			    outp.acceptRequestResult(request.requestURI(),result);
				
			    //System.out.println("node " + nodeInfo.nodeIdentifier() + " done");
		    } catch (Exception e) {
				e.printStackTrace();
			}
	    });
	}
	
	/**
	 * Lance le traitement d'exécution de la requête via l'interpreteur
	 * @param query la requête à traiter par l'interpreteur
	 * @param exec l'état d'exécution en cours
	 * @return le résultat de la requête
	 * @throws Exception
	 */
    private Object executeQuery(Query query, ExecutionStateI exec) throws Exception {
	        if (query instanceof BQuery) {
	            return interpreter.visit((BQuery) query, exec);
	        } else if (query instanceof GQuery) {
	            return interpreter.visit((GQuery) query, exec);
	        } else {
	            throw new Exception("La requête n'est pas une query reconnue");
	        }
    }

    /**
     * Gère la continuation pour les requêtes **synchrone** et **asynchrone**
     * @param request la requête à traiter
     * @param exec l'état d'exécution en cours
     * @param result le résultat qu'on modifie directement car la fonction ne retourne rien
     * @throws Exception
     */
    private void handleContinuation(RequestI request, ExecutionStateI exec, QueryResultI result) throws Exception {
    	//System.out.println("requete " + request);
        ExecutionState ex = (ExecutionState) exec;
        ProcessingNodeI pn = ex.getProcessingNode();
        //ex.addVisitedNode(pn.getNodeIdentifier()); -> on le fait maintenant dans exec state, add to current result
        //exec.addToCurrentResult(result); -> soit on envoie le resultat local, soit le dernier noeud renvoie le resultat (dans l'exec state)

        // empty continuation??
        if (!exec.noMoreHops() && exec.withinMaximalDistance(pn.getPosition())) {
            exec.incrementHops();

            RequestContinuationI reqCont = new RequestContinuation(
                request.isAsynchronous(),
                request.requestURI(),
                request.getQueryCode(),
                request.clientConnectionInfo(),
                exec
            );
            
            Set<Direction> dirs = exec.getDirections();
            
            for (Direction dir : dirs) {
                for (NodeInfoI voisin : neighbours) {
                	//pas de voisins ou le noeud a detecté que le voisin a déjà traité la requete
                    if (voisin == null || ex.wasVisited(voisin.nodeIdentifier())) {
                    	continue;
                    }
                    if (voisin.nodePosition().directionFrom(this.nodeInfo.nodePosition()) == dir) {
                        OutboundPortProvider port = portsMap.get(dir);
                        if (port.connected()) {
                            this.logMessage("Propagation de la continuation vers le " + dir + " avec port " + port.getPortURI());
                            if (request.isAsynchronous()) {
                                port.executeAsync(reqCont); // Asynchrone : pas de retour attendu immédiatement
                            } else {
                                QueryResultI tmp = port.execute(reqCont); // Synchrone : traitement et collecte des résultats
                                if (tmp != null) {
	                                    result.gatheredSensorsValues().addAll(tmp.gatheredSensorsValues());
	                                    result.positiveSensorNodes().addAll(tmp.positiveSensorNodes());
                                } else {
                                    throw new Exception("Erreur lors de l'exécution dans les ports");
                                }
                                
                            }
                        } else {
                            throw new Exception("Port pas connecté, la continuation se termine");
                        }
                    }
                }
            }
        }
    }
}
