package components;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PImplI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingImplI;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import components.plugins.Plugin_Node;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;

@RequiredInterfaces(required = {ClocksServerCI.class})
/**
 * Class for the Node Component.
 * The Node is meant to receive requests either directly from a Client, or from another Node
 * The Node will then proccess the request and send the query result to the client through its specific port
 */
public class Node extends AbstractComponent implements SensorNodeP2PImplI, RequestingImplI {
	/** The outbound port to connect to the clock */
    protected ClocksServerOutboundPort clockOP;
    /** The clock */
    protected AcceleratedClock ac;

    /** Plugin*/
	protected Plugin_Node plugin;
	protected String plugin_uri = "plugin_node";

	/**
	 * Constructs a Node component responsible for processing requests from clients or other nodes
	 * It initializes necessary ports, an accelerated clock for timing, and plugins to manage specific node behaviors
	 *
	 * @param nbThreads The number of threads available for general processing
	 * @param nbSchedulableThreads The number of threads that can be scheduled for specific tasks
	 * @param uri The URI identifier for this node
	 * @param uriInPort The URI for the inbound port for receiving requests directly from clients
	 * @param uriInPortNode The URI for the inbound port for receiving requests from other nodes
	 * @param p The physical position of the node
	 * @param range The operational range of the node for communication or sensing
	 * @param sensors The list of sensors attached to the node, encapsulating sensor data
	 * @param nbThreadsNewReqPool The number of threads in the pool for handling new requests
	 * @param nbThreadsContReqPool The number of threads in the pool for continuing processing of asynchronous requests
	 * @param nbThreadsConnectionPool The number of threads in the connection pool for managing nodes connections
	 * @param nbThreadsSyncPool The number of threads in the synchronization pool for synchronous requests
	 * @param plugin_uri The URI for the plugin managing additional node behaviors
	 * @throws Exception
	 */
    protected Node(int nbThreads, int nbSchedulableThreads,
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
                       String plugin_uri
                       ) throws Exception {
        super(uri, nbThreads, nbSchedulableThreads);

        plugin = new Plugin_Node(	 
                        uri,
                        uriInPort,
                        uriInPortNode,
                        p,
                        range,
                        sensors,
                        nbThreadsNewReqPool,
                        nbThreadsContReqPool,
                        nbThreadsConnectionPool,
                        nbThreadsSyncPool,
                        plugin_uri);
        
        plugin.setPluginURI(plugin_uri);

        this.getTracer().setTitle("Node Component") ;
        this.getTracer().setRelativePosition(2, 1);
        //this.toggleTracing();
    }
    
    /**
     * @see fr.sorbonne_u.components.AbstractComponent#start()
     */
    @Override
    public void start() throws ComponentStartException {
    	
    	try {
        	clockOP = new ClocksServerOutboundPort(this);
            clockOP.publishPort();
            this.doPortConnection(
                    clockOP.getPortURI(),
                    ClocksServer.STANDARD_INBOUNDPORT_URI,
                    ClocksServerConnector.class.getCanonicalName());
            
            this.ac = clockOP.getClock(Config.TEST_CLOCK_URI);
            this.doPortDisconnection(clockOP.getPortURI());
            clockOP.unpublishPort();
            clockOP.destroyPort();
            
            this.installPlugin(plugin);
            
    	} catch (Exception e) {
            e.printStackTrace();
        }
    	
    	
        super.start();
    }
    
    
    // ==== Components methods ====
    
    /**
     * @see fr.sorbonne_u.components.AbstractComponent#execute()
     */
    @Override
    public void execute() throws Exception{
        super.execute();
        this.logMessage("Node executing");
        try {
            ac.waitUntilStart();
            
            Instant instant = ac.getStartInstant().plusSeconds(Config.timeN);
            
            long d = ac.nanoDelayUntilInstant(instant);

            this.scheduleTask(
                    o -> { 					
                        this.logMessage("executing node component " + plugin.getNodeInfo().nodeIdentifier()) ;
                        this.runTask(
                                new AbstractComponent.AbstractTask() {
                                    @Override
                                    public void run() {
                                    	this.getTaskOwner().logMessage("Plugin working");
                                        plugin.execute();
                                    }
                                }) ;

                    }, d, TimeUnit.NANOSECONDS);
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @see fr.sorbonne_u.components.AbstractComponent#finalise()
     */
    @Override
    public void finalise() throws Exception {
        plugin.finalise();
    }
    
    /**
     * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
     */
    @Override
    public void shutdown() throws ComponentShutdownException {
        try {
			plugin.uninstall();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /*************************************************************
     * Component life-cycle methods
     *************************************************************/
    
    /* 
     * Node-Node connection methods 
     */
    
    /**
     * @see fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PImplI#ask4Connection(NodeInfoI)
     * Connect the initial node to the target neighbor node, if the target neighbor is already connected to
     * another node, the target neighbor will ask4Disconnection from it before connecting to the initial node
     * @param neighbor the node information of the target neighbor 
     */
    public void ask4Connection(NodeInfoI neighbor) throws Exception {
        plugin.ask4Connection(neighbor);
    }
    
    /**
     * @see fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PImplI#ask4Disconnection(NodeInfoI)
     * Disconnect the initial node to the neighbour node and find a nearest new neighbour from the same direction
     * @param neighbour the target neighbour node
     * @throws Exception
     */
    public void ask4Disconnection(NodeInfoI neighbour) throws Exception {
        plugin.ask4Disconnection(neighbour);
    }
    
    /*************************************************************************
    * Request Handling Methods
    /*************************************************************************
    
    /**
     * @see fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingImplI#execute(RequestI)
     * Méthode SYNCHRONE: renvoie le résultat de la requête après l'avoir traité
     * @param la requête RequestI
     * @return le résultat de la requête QueryResultI
     */
    public QueryResultI execute(RequestI request) throws Exception {
        return plugin.execute(request);
    }

    /**
     * @see fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PImplI#execute(RequestContinuationI)
     * Méthode SYNCHRONE: renvoie le résultat de la requête **avec continuation**, cette méthode est utilisée par les noeuds
     * @param la requête RequestContinuationI
     * @return le résultat de la requête QueryResultI
     */
	@Override
	public QueryResultI execute(RequestContinuationI request) throws Exception {
        return plugin.execute(request);
	}
	
	/**
	 * @see fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingImplI#executeAsync(RequestI)
	 * Méthode ASYNCHRONE: traite et renvoie le résultat de la requête au client après l'avoir traité, cette méthode est appelée par le client via les ports
	 * Le noeud envoie le résultat local directement au client
	 * @param request la requête à traiter
	 * @throws Exception
	 */
	public void executeAsync(RequestI request) throws Exception {
        plugin.executeAsync(request);
    }

	/**
     * @see fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PImplI#executeAsync(RequestContinuationI)
     * Méthode ASYNCHRONE: traite et envoie le résultat local de la requête **avec continuation** au client
     * la requête est également envoyée aux voisins selon la direction de la requête
     * @param requestContinuation la requête avec continuation
     * @throws Exception
     */
	@Override
	public void executeAsync(RequestContinuationI request) throws Exception {
        plugin.executeAsync(request);
	}
	
	
	
	

	/**
     * Retourne les informations du noeud NodeInfoI courrant
     * @return le NodeInfoI du noeud
     */
    public NodeInfoI getNodeInfo() {
        return plugin.getNodeInfo();
    }

    /**
     * Renvoie l'ensemble des voisins du noeud, donné par le registre lorsqu'il se connecte
     * @return l'ensemble des voisins du noeud
     */
    public Set<NodeInfoI> getNeighbours() {
        return plugin.getNeighbours();
    }

    /**
     * Renvoie la valeur du senseur par son identifiant en paramètre
     * @param l'identifiant du noeud dont on cherche la valeur
     * @return la valeur du senseur
     */
    public SensorDataI getSensorData(String sensorIdentifier) {
        return plugin.getSensorData(sensorIdentifier);
    }
    
    /**
     * Cette méthode a été rajouté pour récupérer tous les noeuds
     * @return la liste de touts les senseurs du noeconnectionud courrant
     */
    public ArrayList<SensorDataI> getAllSensors(){
    	return plugin.getAllSensors();
    }
    
    /**
     * Method used by the register to set the node's neighbors
     * @param the neighbors set to be given to the node
     */
    public void setNeighbors(Set<NodeInfoI> neighbors) {
    	plugin.setNeighbors(neighbors);
    }
    
    /**
     * Getter for the set of treated requests
     * @return the set of treated requests
     */
    public Set<String> getTreatedRequests() {
    	return plugin.getTreatedRequests();
    }
}
