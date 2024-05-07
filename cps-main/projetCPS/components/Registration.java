package components;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import components.client_register.InboundPortRegisterClient;
import components.cvm.CVM;
import components.node_register.InboundPortRegister;

import java.time.Instant;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;

@OfferedInterfaces(offered = {RegistrationCI.class, LookupCI.class})
/**
 * Class for the Register Component
 */
public class Registration extends AbstractComponent{
	protected ClocksServerOutboundPort clockOP;
	
	private ConcurrentHashMap<String, NodeInfoI> registre = new ConcurrentHashMap<>();
	private InboundPortRegister inpr;
	private InboundPortRegisterClient inprc;
	
	/* Pool of threads handling */
	
	/** URI of the pool of threads used to register */
	protected static final String	REGISTER_POOL_URI = "register pool" ;
	/** URI of the pool of threads used to look up nodes in the Registry component */
	protected static final String 	LOOKUP_POOL_URI = "look up pool";
	/** the number of threads to be used in the pool of threads are directly given in the constructor for each pool */
	
	private Registration(int nbThreads, int nbSchedulableThreads,
	        String uriRegister,
	        String uriInPortRegister,
	        String uriInPortRegisterClient,
	        int nbThreadsRegisterPool,
	        int nbThreadsLookupPool) throws Exception{
	
		super(uriRegister, nbThreads, nbSchedulableThreads);
		
		/* creating pool of threads, one for registring nodes, one for looking up nodes registered */
        this.createNewExecutorService(REGISTER_POOL_URI, nbThreadsRegisterPool, false);
        this.createNewExecutorService(LOOKUP_POOL_URI, nbThreadsLookupPool, false);
        
		inpr = new InboundPortRegister(this,uriInPortRegister, REGISTER_POOL_URI);
		inpr.publishPort();
		
		inprc = new InboundPortRegisterClient(this, uriInPortRegisterClient, LOOKUP_POOL_URI);
		inprc.publishPort();
		
		this.addOfferedInterface(RegistrationCI.class);
        this.addOfferedInterface(LookupCI.class);
        
        this.getTracer().setTitle("Register Component") ;
        this.getTracer().setRelativePosition(1,2);
        this.toggleTracing();
	}
	
    @Override
    public void start() throws ComponentStartException {
        this.logMessage("starting registration component.") ;
        super.start() ;
    }
	   
	/**
	 * Return true if the node corresponding to nodeIdentifier is registered in the register
	 * @param the node identifier nodeIdentifier
	 * @return true if the node is in the register
	 * @throws Exception 
	 */
	public boolean registered(String nodeIdentifier) throws Exception {
		return registre.containsKey(nodeIdentifier);
	}

	/**
	 * Register the new node and return a set of nodes information to which this new node can connect
     * given its range and the range of the returned nodes. This method blocks until the operation is complete.
	 * @param the information of the node nodeInfo
	 * @return a set of nodes information the node can connect to
	 */
	public Set<NodeInfoI> register(NodeInfoI nodeInfo) throws Exception {
		try {
            if (registre.putIfAbsent(nodeInfo.nodeIdentifier(), nodeInfo) == null) {
                this.logMessage("Added node " + nodeInfo.nodeIdentifier() + " to Register");
                return findClosestNeighbors(nodeInfo);
            } else {
                throw new Exception("Node déjà enregistré.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new Exception("Thread was interrupted", e);
        } catch (ExecutionException e) {
            throw new Exception("Error during registration", e.getCause());
        }
	}
	
	private Set<NodeInfoI> findClosestNeighbors(NodeInfoI nodeInfo) {
        Map<Direction, NodeInfoI> closestNeighborsByDirection = new EnumMap<>(Direction.class);

        registre.values().forEach(existingNode -> {
            if (!existingNode.nodeIdentifier().equals(nodeInfo.nodeIdentifier())) {
                Direction dir = nodeInfo.nodePosition().directionFrom(existingNode.nodePosition());
                if (dir != null && isInRange(nodeInfo, existingNode)) {
                    NodeInfoI currentClosest = closestNeighborsByDirection.get(dir);
                    if (currentClosest == null || 
                        nodeInfo.nodePosition().distance(existingNode.nodePosition()) <
                        nodeInfo.nodePosition().distance(currentClosest.nodePosition())) {
                        closestNeighborsByDirection.put(dir, existingNode);
                    }
                }
            }
        });

        return new HashSet<>(closestNeighborsByDirection.values());
    }


	
	/**
	 * Auxiliary method to identify whether two nodes are in range of each other
	 * @param two NodeInfo to compare
	 * @return true if the two nodes are in range of each other
	 */
	private boolean isInRange(NodeInfoI node1, NodeInfoI node2) {
    	return node1.nodePosition().distance(node2.nodePosition()) < node1.nodeRange() &&  node1.nodePosition().distance(node2.nodePosition()) < node2.nodeRange();
   
	}

	/**
	 * Find a new neighbour for the given node in the given direction and return its connection info, or null if none exists.
	 * @param the NodeInfoI of the wanted neighbors of the node, the direction wanted
	 * @return the closest neighbor's information to the node
	 */
	public NodeInfoI findNewNeighbour(NodeInfoI nodeInfo, Direction d) throws Exception {
	    this.logMessage("Finding new neighbour for " + nodeInfo.nodeIdentifier() + " in direction " + d);
	    
	    NodeInfoI closestNeighbor = null;
	    double closestDistance = Double.MAX_VALUE;
	
	    for (NodeInfoI potentialNeighbor : registre.values()) {
	        if (!potentialNeighbor.nodeIdentifier().equals(nodeInfo.nodeIdentifier())) {
	            Direction dir = nodeInfo.nodePosition().directionFrom(potentialNeighbor.nodePosition());
	            double distance = nodeInfo.nodePosition().distance(potentialNeighbor.nodePosition());
	            if (dir.equals(d) && isInRange(nodeInfo, potentialNeighbor) && distance < closestDistance) {
	                closestDistance = distance;
	                closestNeighbor = potentialNeighbor;
	            }
	        }
	    }
	
	    if (closestNeighbor == null) this.logMessage("Could not find a neighbor");
	    
	    if (closestNeighbor == null) {
	    	this.logMessage("Could not find a neighbor");
	    }
	    
	    return closestNeighbor;
	}


	/**
	 * Unregister the node corresponding to the nodeIdentifier from the register
	 * @param the node identifier
	 */
	public void unregister(String nodeIdentifier) throws Exception {
	    this.logMessage("Unregistering node " + nodeIdentifier);
	    NodeInfoI removedNode = registre.remove(nodeIdentifier);
        if (removedNode != null) {
            this.logMessage("Node " + nodeIdentifier + " has been unregistered.");
        } else {
            this.logMessage("No node found with identifier " + nodeIdentifier + " to unregister.");
        }
	}

	
	/**
	 * Find the node information from the node identifier
	 * @param the node identifier of the sensor
	 * @return the connection information of the node
	 */
	public ConnectionInfoI findByIdentifier(String sensorNodeId) throws Exception {
	    this.logMessage("Finding a node by identifier " + sensorNodeId);
	    NodeInfoI nodeInfo = registre.get(sensorNodeId);
	    if (nodeInfo != null) {
	        this.logMessage("Found");
	        return nodeInfo;
	    }
	    this.logMessage("/!\\ Identifier is not in Register");
	    return null;
	}

	/**
	 * Find all of the node information from the geographical zone
	 * @param the geographical zone
	 * @return a set of the nodes connection information
	 */
	public Set<ConnectionInfoI> findByZone(GeographicalZoneI z) throws Exception {
	    this.logMessage("Finding nodes by geographical zone " + z);
	    Set<ConnectionInfoI> nodesInZone = registre.values().stream()
	                                                .filter(nodeInfo -> z.in(nodeInfo.nodePosition()))
	                                                .collect(Collectors.toSet());
	
	    return nodesInZone;
	}
	
	@Override
	public void shutdown() {
		this.logMessage("Shutting down");
		try {
			inpr.unpublishPort();
			inprc.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
