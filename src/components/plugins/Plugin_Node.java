package components.plugins;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ast.query.BQuery;
import ast.query.GQuery;
import ast.query.Query;
import components.Config;
import components.Node;
import components.ports.p2p.P2P_Connector;
import components.ports.p2p.P2P_InboundPort;
import components.ports.p2p.P2P_OutboundPort;
import components.ports.registration.Registration_Connector;
import components.ports.registration.Registration_OutboundPort;
import components.ports.requestResult.RequestResult_Connector;
import components.ports.requestResult.RequestResult_OutboundPort;
import components.ports.requesting.Requesting_InboundPort;
import connexion.BCM4JavaEndPointDescriptor;
import connexion.EndPointDescriptor;
import connexion.ExecutionState;
import connexion.NodeInfo;
import connexion.ProcessingNode;
import connexion.requests.RequestContinuation;
import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestResultCI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PImplI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingImplI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import interpreter.Interpreter;


public class Plugin_Node extends AbstractPlugin implements RequestingImplI, SensorNodeP2PImplI {
	private static final long serialVersionUID = 1L;
	private NodeInfoI nodeInfo;
    private ArrayList<SensorDataI> sensors;
    private Interpreter interpreter;
	private ExecutionStateI exec;

	// Concurrent HashMap en Set pour gérer la concurrence
    private Set<NodeInfoI> neighbours = new HashSet<>();
    private Set<String> treatedRequests = new HashSet<>();

    // Component attributes
    /* Inbound Port Client-Node */
    Requesting_InboundPort inp;
    
    /* Outbound Port Asynchronous Node-Client */
    RequestResult_OutboundPort outp;
	
	/* Inbound Port Node-Node */
	P2P_InboundPort inpn;
	
	/* Outbound Ports Node-Node */
	Map<Direction, P2P_OutboundPort> neighboursPortsMap = new ConcurrentHashMap<>();

    /* Outbound Port Node-Register */
    Registration_OutboundPort outpr;
    
    private String uriInPort;
    private String uriInPortNode;
    
    /**
     * Concurrency handling
     */
    
    /** URI of the pool of threads used to handle new asynchronous requests received from Clients */
	protected static final String	ASYNC_NEW_REQUEST_POOL_URI = "asynchronous new request pool" ;
	protected final int nbThreadsNewReqPool;
	/** URI of the pool of threads used to handle propagated asynchronous requests received from Nodes */
	protected static final String	ASYNC_CONT_REQUEST_POOL_URI = "asynchronous continuation request pool" ;
	protected final int nbThreadsContReqPool;
	/** URI of the pool of threads used to handle synchronous requests */
	protected static final String	SYNCHRONOUS_POOL_URI = "synchronous pool" ;
	protected final int nbThreadsSyncPool;
	/** URI of the pool of threads used to handle connection/disconnection with other Nodes */
	protected static final String	CONNECTION_POOL_URI = "connection pool" ;
	protected final int nbThreadsConnectionPool;
	/** the number of threads to be used in the pool of threads are directly given in the constructor for each pool */
	
	/** lock for the requesting result connection */  
	private final Lock connexion_lock;
	
	/** Plugin URI */
	protected final String PLUGIN_URI;

	public Plugin_Node(
            String uri,
            String uriInPort,
            String uriInPortNode,
            PositionI p,
            double range,
            ArrayList<SensorDataI> sensors,
            int nbThreadsNewReqPool,
            int nbThreadsContReqPool,
            int nbThreadsConnectionPool,
            int nbThreadsSyncPool,
            String PLUGIN_URI) throws Exception  {
		
		super();
		
		this.nodeInfo = new NodeInfo(uri,new BCM4JavaEndPointDescriptor(uriInPortNode),new BCM4JavaEndPointDescriptor(uriInPort),p,range);
        this.sensors = sensors;

        interpreter = new Interpreter();
        
        connexion_lock = new ReentrantLock();
        
        this.uriInPort = uriInPort;
        this.uriInPortNode = uriInPortNode;
        
        this.PLUGIN_URI = PLUGIN_URI;
        this.nbThreadsConnectionPool = nbThreadsConnectionPool;
        this.nbThreadsContReqPool = nbThreadsContReqPool;
        this.nbThreadsNewReqPool = nbThreadsNewReqPool;
        this.nbThreadsSyncPool = nbThreadsSyncPool;
	}
	
	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#initialise()
	 */
	@Override
	public void initialise() throws Exception {
		/* creating pool of threads, one to handle requests sent from client, 
         * one to handle requests propagated from other nodes */
        this.createNewExecutorService(CONNECTION_POOL_URI, nbThreadsConnectionPool, false);
        this.createNewExecutorService(ASYNC_NEW_REQUEST_POOL_URI, nbThreadsNewReqPool, false);
        this.createNewExecutorService(ASYNC_CONT_REQUEST_POOL_URI, nbThreadsContReqPool, false);
        this.createNewExecutorService(SYNCHRONOUS_POOL_URI, nbThreadsSyncPool, false);
        
        this.inp = new Requesting_InboundPort(this.getOwner(), uriInPort, SYNCHRONOUS_POOL_URI, ASYNC_NEW_REQUEST_POOL_URI, PLUGIN_URI);
        inp.publishPort();
        
        this.inpn = new P2P_InboundPort(this.getOwner(), uriInPortNode, CONNECTION_POOL_URI, SYNCHRONOUS_POOL_URI, ASYNC_CONT_REQUEST_POOL_URI, PLUGIN_URI);
        inpn.publishPort();
        
        P2P_OutboundPort outpNE = new P2P_OutboundPort(this.getOwner());
        outpNE.publishPort();
        P2P_OutboundPort outpNW = new P2P_OutboundPort(this.getOwner());
        outpNW.publishPort();
        P2P_OutboundPort outpSE = new P2P_OutboundPort(this.getOwner());
        outpSE.publishPort();
        P2P_OutboundPort outpSW = new P2P_OutboundPort(this.getOwner());
        outpSW.publishPort();
   
        neighboursPortsMap.put(Direction.SE, outpSE);
        neighboursPortsMap.put(Direction.SW, outpSW);
        neighboursPortsMap.put(Direction.NE, outpNE);
        neighboursPortsMap.put(Direction.NW, outpNW);
        
        this.outpr = new Registration_OutboundPort(this.getOwner());
        outpr.publishPort();
        
        this.outp = new RequestResult_OutboundPort(this.getOwner());
        outp.publishPort();
        
        super.initialise();
	}

    /**
	 * @see fr.sorbonne_u.components.AbstractPlugin#installOn(fr.sorbonne_u.components.ComponentI)
	 */
	@Override
	public void	installOn(ComponentI owner) throws Exception {
		super.installOn(owner);	
		this.addRequiredInterface(SensorNodeP2PCI.class);
		this.addRequiredInterface(RegistrationCI.class);
		this.addRequiredInterface(RequestResultCI.class);
		this.addOfferedInterface(RequestingCI.class);
		this.addOfferedInterface(SensorNodeP2PCI.class);

	}

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#uninstall()
	 */
	@Override
	public void	uninstall() throws Exception {
		try {
			this.logMessage("shutting down");
			inp.unpublishPort();
			inpn.unpublishPort();
			
			for (P2P_OutboundPort port : neighboursPortsMap.values()) {
			    port.unpublishPort();
			}
			
	        outpr.unpublishPort();
			outp.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException();
		}		
		this.removeRequiredInterface(SensorNodeP2PCI.class);
		this.removeRequiredInterface(RegistrationCI.class);
		this.removeRequiredInterface(RequestResultCI.class);
		this.removeOfferedInterface(RequestingCI.class);
		this.removeOfferedInterface(SensorNodeP2PCI.class);
	}
	
	/**
	 * The first step of what the node will do after being created and initialised,
	 * the node will connect to the register component to register itself and connect to its neighbours
	 */
	public void execute() {
    	try {
        	this.getOwner().logMessage("Connecting to the register");
        	this.getOwner().doPortConnection(
        			outpr.getPortURI(),
        			Config.uriInPortRegister,
        			Registration_Connector.class.getCanonicalName());
        		
            Set<NodeInfoI> tmpNeighbours = outpr.register(this.nodeInfo); 
                
            for (NodeInfoI voisin : tmpNeighbours) {
            	neighbours.add(voisin);
                ((Node) this.getOwner()).ask4Connection(voisin);
                Direction direction = voisin.nodePosition().directionFrom(this.nodeInfo.nodePosition()); 
                P2P_OutboundPort p = neighboursPortsMap.get(direction);
                p.ask4Connection(this.nodeInfo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

	/**
	 * @see fr.sorbonne_u.components.AbstractPlugin#finalise()
	 */
    @Override
    public void finalise() throws Exception {
    	this.logMessage("Finalising");
        try {
			
			Set<NodeInfoI> clonedNeighbours = new HashSet<>(neighbours);
			for (NodeInfoI voisin : clonedNeighbours) {
        		this.ask4Disconnection(voisin);
            }
            outpr.unregister(nodeInfo.nodeIdentifier());
            
            for (P2P_OutboundPort port : neighboursPortsMap.values()) {
                if (port.connected()) {
                    this.getOwner().doPortDisconnection(port.getPortURI());
                }
            }
            if (outpr.connected())
            	this.getOwner().doPortDisconnection(outpr.getPortURI());
            if (outp.connected())
            	this.getOwner().doPortDisconnection(outp.getPortURI());
	        
            
		} catch (ComponentShutdownException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        super.finalise();
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
    
    /**
     * Getter for the set of treated requests
     * @return the set of treated requests
     */
    public Set<String> getTreatedRequests() {
    	return treatedRequests;
    }
    
	/*************************************************************
     * Component life-cycle methods
     *************************************************************/
    
    /**
     * Connect the initial node to the target neighbor node, if the target neighbor is already connected to
     * another node, the target neighbor will ask4Disconnection from it before connecting to the initial node
     * @param neighbor the node information of the target neighbor 
     */
    public void ask4Connection(NodeInfoI neighbor) throws Exception {
        this.logMessage("Ask4Connection - Desired connection to " + neighbor.nodeIdentifier());
        Direction d = neighbor.nodePosition().directionFrom(nodeInfo.nodePosition());
        P2P_OutboundPort port = neighboursPortsMap.get(d);
        
        if (port != null && port.connected()) {
            this.getOwner().doPortDisconnection(port.getPortURI());
            neighbours.removeIf(n -> d.equals(n.nodePosition().directionFrom(nodeInfo.nodePosition())));
        }

        if (port == null) {
            port = new P2P_OutboundPort(this.getOwner());
            port.publishPort();
            neighboursPortsMap.put(d, port);
        }

        this.getOwner().doPortConnection(
            port.getPortURI(),
            neighbor.p2pEndPointInfo().toString(),
            P2P_Connector.class.getCanonicalName()
        );

        this.logMessage("Ask4Connection - Connection established from port = " + port.getPortURI() + " to " + neighbor.p2pEndPointInfo());
        
        // Update neighbours set
        if (!neighbours.contains(neighbor)) {
            neighbours.add(neighbor);
        }
    }

    /**
     * Disconnect the initial node to the neighbour node and find a nearest new neighbour from the same direction
     * @param neighbour the target neighbour node
     * @throws Exception
     */
    public void ask4Disconnection(NodeInfoI neighbour) throws Exception {
        if (neighbour == null) return;
        Direction directionFrom = neighbour.nodePosition().directionFrom(nodeInfo.nodePosition());
        P2P_OutboundPort port = neighboursPortsMap.get(directionFrom);
        
        if (port != null && port.connected()) {
            this.getOwner().doPortDisconnection(port.getPortURI());
            this.logMessage("Ask4Disconnection - Disconnection in " + directionFrom);
        }
        neighbours.remove(neighbour);
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
        this.logMessage("Réception d'une requête");
        
        if (request.isAsynchronous()) {
            throw new Exception("Asynchronous request is being treated in the synchronous method");
        }
        
	    if(!treatedRequests.add(request.requestURI())) {
            return null;
        }
	    
        ProcessingNodeI pn = new ProcessingNode((Node) (this.getOwner()));
        Query query = (Query) request.getQueryCode();

        if (exec == null) {
            exec = new ExecutionState(this.nodeInfo.nodePosition(), pn, query instanceof BQuery);
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
    }

    /**
     * Méthode SYNCHRONE: renvoie le résultat de la requête **avec continuation**, cette méthode est utilisée par les noeuds
     * @param la requête RequestContinuationI
     * @return le résultat de la requête QueryResultI
     */
	@Override
	public QueryResultI execute(RequestContinuationI request) throws Exception {

		this.logMessage("Réception d'une requête de continuation");
		
		if (request == null) throw new Exception("Request is null");
	    if (request.isAsynchronous()) {
	        throw new Exception("Asynchronous request is being treated in the synchronous method");
	    }
	    
	    if(!treatedRequests.add(request.requestURI())) {
            return null;
        }	
	
	    ProcessingNodeI pn = new ProcessingNode((Node) (this.getOwner()));
	    exec = request.getExecutionState();
	    exec.updateProcessingNode(pn);

	    Object res = executeQuery((Query) request.getQueryCode(), exec);
	    QueryResultI result = (QueryResultI) res;
	    if (res != null) {
	        this.logMessage("Résultat du traitement local: " + res.toString());
	    }

	    handleContinuation((RequestI)request, exec, result);

	    return result;
	}
	
	/**
	 * Méthode ASYNCHRONE: traite et renvoie le résultat de la requête au client après l'avoir traité, cette méthode est appelée par le client via les ports
	 * Le noeud envoie le résultat local directement au client
	 * @param request la requête à traiter
	 * @throws Exception
	 */
	public void executeAsync(RequestI request) throws Exception {
		if (!request.isAsynchronous()) {
            throw new Exception("Asynchronous request is being treated in the synchronous method");
        }
        this.logMessage("Réception d'une requête asynchrone, connexion au port entrant du client");
        
	    if(!treatedRequests.add(request.requestURI())) {
            return;
        }        
       
        try {

	        ProcessingNodeI pn = new ProcessingNode((Node) (this.getOwner()));
	        exec = new ExecutionState(this.nodeInfo.nodePosition(), pn, request.getQueryCode() instanceof BQuery);
       
	        // Exécution locale de la requête
	        QueryResultI result = (QueryResultI) executeQuery((Query) request.getQueryCode(), exec);
	        
	        connexion_lock.lock();
	      
	        this.getOwner().doPortConnection(
			        outp.getPortURI(),
			        ((EndPointDescriptor) request.clientConnectionInfo().endPointInfo()).getInboundPortURI(),
			        RequestResult_Connector.class.getCanonicalName());
        	
	        this.logMessage("request uri: " + request.requestURI() + "\n" + result.toString());
	        // Envoi du résultat au client
	        outp.acceptRequestResult(request.requestURI(), result);
	        
	       
	    
	        if (outp.connected())
	        	this.getOwner().doPortDisconnection(outp.getPortURI());
	        connexion_lock.unlock();
	        
	        if (((Query) request.getQueryCode()).getCont() != null)  {
	            handleContinuation(request, exec, result);
	        }
	        
        } catch (Exception e) {
			e.printStackTrace();
		}
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
	    if (!request.isAsynchronous()) {
	        throw new Exception("Synchronous request is being treated in the asynchronous method");
	    }
	    
	    if(!treatedRequests.add(request.requestURI())) {
            return;
        }
	    // Connexion au port entrant pour envoyer le résultat
	    try {

		    ProcessingNodeI pn = new ProcessingNode((Node) (this.getOwner()));
		    exec = request.getExecutionState();
		    if (exec == null) {
		        throw new Exception("Execution State is null during continuous request treating, it should not be the case");
		    } else {
		        exec.updateProcessingNode(pn);
		    }
			
		    QueryResultI result = (QueryResultI) executeQuery((Query) request.getQueryCode(), exec);
		    
		    connexion_lock.lock();
		    this.getOwner().doPortConnection(
				    outp.getPortURI(),
				    ((EndPointDescriptor) request.clientConnectionInfo().endPointInfo()).getInboundPortURI(),
				    RequestResult_Connector.class.getCanonicalName());
			
		    this.logMessage("request uri: " + request.requestURI() + "\n" + result.toString());
		    // Envoi du résultat au client
		    outp.acceptRequestResult(request.requestURI(),result);
		    if (outp.connected())
		    	this.getOwner().doPortDisconnection(outp.getPortURI());
		    connexion_lock.unlock();
		    
		    handleContinuation(request, exec, result);
			
	    } catch (Exception e) {
			e.printStackTrace();
		}
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
     * Handles the continuation of a request based on whether it is directional or flooding
     * This method determines how the request should be propagated based on the execution state
     * 
     * @param request The incoming request to handle
     * @param exec The current state of execution that tracks the continuation logic and state
     * @param result The result object that is being built up as request is being processed
     * @throws Exception Throws an exception if there is an issue with request propagation
     */
    private void handleContinuation(RequestI request, ExecutionStateI exec, QueryResultI result) throws Exception {
    	if (exec.isContinuationSet()) {
    		RequestContinuationI reqCont = new RequestContinuation(
								                    request.isAsynchronous(),
								                    request.requestURI(),
								                    request.getQueryCode(),
								                    request.clientConnectionInfo(),
								                    exec
								                );
    		
    		if (exec.isDirectional() && !exec.noMoreHops()) {
    			exec.incrementHops();
    			this.treatDirectionalRequest(request, exec, result, reqCont);
    		} else if (exec.isFlooding() && exec.withinMaximalDistance(this.nodeInfo.nodePosition())) {
    			this.treatFloodingRequest(request, exec, result, reqCont);
    		}
    	}
    }
    
    /**
     * Handles the propagation of requests in specific directions based on the execution state
     * This method is used when the continuation strategy is directional
     *
     * @param request The request being processed
     * @param exec The execution state
     * @param result The current result
     * @param reqCont The continuation object containing details necessary for the request continuation
     * @throws Exception
     */
    private void treatDirectionalRequest(RequestI request, ExecutionStateI exec, QueryResultI result, RequestContinuationI reqCont) throws Exception {
        for (Direction d : exec.getDirections()) {
            for (NodeInfoI neighbour : neighbours) {
                Direction direction = this.nodeInfo.nodePosition().directionFrom(neighbour.nodePosition());
                if (direction != null && direction.equals(d)) {
                    sendRequestToNeighbors(request, direction, exec, result, reqCont);
                }
            }
        }
    }

    /**
     * Handles the flooding of requests to all neighbors within a certain range
     * This method is used when the continuation strategy is to flood the request to all accessible neighbour nodes
     *
     * @param request The request being processed
     * @param exec The execution state
     * @param result The current result
     * @param reqCont The continuation object containing details necessary for the request continuation
     * @throws Exception
     */
    private void treatFloodingRequest(RequestI request, ExecutionStateI exec, QueryResultI result, RequestContinuationI reqCont) throws Exception {
        PositionI currentPosition = this.nodeInfo.nodePosition();
        for (NodeInfoI neighbour : neighbours) {
            Direction direction = currentPosition.directionFrom(neighbour.nodePosition());
            sendRequestToNeighbors(request, direction, exec, result, reqCont);
        }
    }
    
    /**
     * Sends a request to a neighbor in a specified direction
     * This method handles both asynchronous and synchronous request forwarding, updating the result object if the request is synchronous
     *
     * @param request The request being propagated
     * @param dir The direction in which to send the request
     * @param exec The execution state for tracking the request propagation
     * @param result The current result
     * @param reqCont The request continuation details needed for sending
     * @throws Exception
     */
    private void sendRequestToNeighbors(RequestI request, Direction dir, ExecutionStateI exec, QueryResultI result, RequestContinuationI reqCont) {
    	P2P_OutboundPort port = neighboursPortsMap.get(dir);
    	try {
    		if (!port.connected()) {
                // Temporary fix - Attempt to manually reconnect
                for (NodeInfoI neighbour : neighbours) {
                    Direction neighbourDirection = nodeInfo.nodePosition().directionFrom(neighbour.nodePosition());
                    if (neighbourDirection == dir) {
                        this.getOwner().doPortConnection(
                            port.getPortURI(),
                            neighbour.p2pEndPointInfo().toString(),
                            P2P_Connector.class.getCanonicalName()
                        );
                        break;
                    }
                }
            }
            
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
	        } 
    	} catch (Exception e) {
    		new Exception("Port pas connecté, la continuation se termine");
    	}
    }
    
}