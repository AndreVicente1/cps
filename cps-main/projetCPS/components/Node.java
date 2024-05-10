package components;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PImplI;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import components.cvm.CVM;
import components.plugins.Plugin_Node;
import components.ports.p2p.P2P_InboundPort;
import components.ports.p2p.P2P_OutboundPort;
import components.ports.registration.Registration_OutboundPort;
import components.ports.requestResult.RequestResult_OutboundPort;
import components.ports.requesting.Requesting_InboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
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

    /** Plugin*/
	protected Plugin_Node plugin;
	protected String plugin_uri = "plugin_node";

    //rajouter nodeinfo, neighbors, senseurs
    public Node(int nbThreads, int nbSchedulableThreads,
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
        this.installPlugin(plugin);

        this.getTracer().setTitle("Node Component") ;
        this.getTracer().setRelativePosition(2, 1);
        //this.toggleTracing(); // supprimer si beaucoup de noeuds!! sinon ca bug
    }
    
    @Override
    public void start() throws ComponentStartException {
        super.start();
    }
    
    
    // ==== Components methods ====
    
    @Override
    public void execute() throws Exception{
        super.execute();
        
        try {
        	clockOP = new ClocksServerOutboundPort(this);
            clockOP.publishPort();
            this.doPortConnection(
                    clockOP.getPortURI(),
                    ClocksServer.STANDARD_INBOUNDPORT_URI,
                    ClocksServerConnector.class.getCanonicalName());
            AcceleratedClock ac = clockOP.getClock(CVM.TEST_CLOCK_URI);
            this.doPortDisconnection(clockOP.getPortURI());
            clockOP.unpublishPort();
            clockOP.destroyPort();
            ac.waitUntilStart();

            char numero = this.plugin.getNodeInfo().nodeIdentifier().charAt(this.plugin.getNodeInfo().nodeIdentifier().length()-1);
            int i = Integer.valueOf(numero); 
            
            Instant instant = CVM.START_INSTANT.plusSeconds(i + 20);
            
            long d = ac.nanoDelayUntilInstant(instant);

            this.scheduleTask(
                    o -> { 					
                        this.logMessage("executing node component " + plugin.getNodeInfo().nodeIdentifier()) ;
                        this.runTask(
                                new AbstractComponent.AbstractTask() {
                                    @Override
                                    public void run() {
                                        plugin.execute();
                                    }
                                }) ;

                    }, d, TimeUnit.NANOSECONDS);
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void finalise() throws Exception {
        plugin.finalise();
        super.finalise();
    }
    
    @Override
    public void shutdown() throws ComponentShutdownException {
        try {
			plugin.uninstall();
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
        plugin.ask4Connection(neighbor);
    }
    
    /**
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
     * Méthode SYNCHRONE: renvoie le résultat de la requête après l'avoir traité
     * @param la requête RequestI
     * @return le résultat de la requête QueryResultI
     */
    public QueryResultI execute(RequestI request) throws Exception {
        return plugin.execute(request);
    }

    /**
     * Méthode SYNCHRONE: renvoie le résultat de la requête **avec continuation**, cette méthode est utilisée par les noeuds
     * @param la requête RequestContinuationI
     * @return le résultat de la requête QueryResultI
     */
	@Override
	public QueryResultI execute(RequestContinuationI request) throws Exception {
        return plugin.execute(request);
	}
	
	/**
	 * Méthode ASYNCHRONE: traite et renvoie le résultat de la requête au client après l'avoir traité, cette méthode est appelée par le client via les ports
	 * Le noeud envoie le résultat local directement au client
	 * @param request la requête à traiter
	 * @throws Exception
	 */
	public void executeAsync(RequestI request) throws Exception {
        plugin.executeAsync(request);
    }

	/**
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
