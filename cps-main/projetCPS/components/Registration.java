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
	/** number of threads to be used in the pool of threads.				*/
	protected static final int		NTHREADS = 5 ;
	
	private Registration(int nbThreads, int nbSchedulableThreads,
	        String uriRegister,
	        String uriInPortRegister,
	        String uriInPortRegisterClient) throws Exception{
	
		super(uriRegister, nbThreads, nbSchedulableThreads);
		inpr = new InboundPortRegister(this,uriInPortRegister, REGISTER_POOL_URI);
		inpr.publishPort();
		
		inprc = new InboundPortRegisterClient(this, uriInPortRegisterClient, LOOKUP_POOL_URI);
		inprc.publishPort();
		
		this.addOfferedInterface(RegistrationCI.class);
        this.addOfferedInterface(LookupCI.class);
        
        /* creating pool of threads, one for registring nodes, one for looking up nodes registered */
        this.createNewExecutorService(REGISTER_POOL_URI, NTHREADS, false);
        this.createNewExecutorService(LOOKUP_POOL_URI, NTHREADS, false);
        
        this.getTracer().setTitle("Register Component") ;
        this.getTracer().setRelativePosition(1,2);
        this.toggleTracing();
	}
	
    @Override
    public void start() throws ComponentStartException {
        this.logMessage("starting registration component.") ;

        // Horloge accélérée
        /*try {
            clockOP = new ClocksServerOutboundPort(this);
            clockOP.publishPort();
            this.doPortConnection(
                    clockOP.getPortURI(),
                    ClocksServer.STANDARD_INBOUNDPORT_URI,
                    ClocksServerConnector.class.getCanonicalName());
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        super.start() ;
    }
	   
	/**
	 * Return true if the node corresponding to nodeIdentifier is registered in the register
	 * @param the node identifier nodeIdentifier
	 * @return true if the node is in the register
	 * @throws Exception 
	 */
	public boolean registered(String nodeIdentifier) throws Exception {
    	try {
			return this.getExecutorService(REGISTER_POOL_URI).submit(() -> {
				return registre.containsKey(nodeIdentifier);
			}).get(); // TODO: a voir si bloquante ou pas
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
            throw new Exception("Thread was interrupted", e);
		} catch (ExecutionException e) {
			throw new Exception("Error during registered", e.getCause());
		}
	}

	/**
	 * Register the new node and return a set of nodes information to which this new node can connect
     * given its range and the range of the returned nodes. This method blocks until the operation is complete.
	 * @param the information of the node nodeInfo
	 * @return a set of nodes information the node can connect to
	 */
	public Set<NodeInfoI> register(NodeInfoI nodeInfo) throws Exception {
		try {
            // submit pour exécuter la tâche dans le pool de threads
            return this.getExecutorService(REGISTER_POOL_URI).submit(() -> {
                if (registre.putIfAbsent(nodeInfo.nodeIdentifier(), nodeInfo) == null) {
                    this.logMessage("Added node " + nodeInfo.nodeIdentifier() + " to Register");
                    return findClosestNeighbors(nodeInfo);
                } else {
                    throw new Exception("Node déjà enregistré.");
                }
            }).get();  // Attente bloquante pour obtenir le résultat
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
	    
	    NodeInfoI res = this.getExecutorService(REGISTER_POOL_URI).submit(() -> {
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
		    return closestNeighbor;
	    }).get();
	    
	    if (res == null) {
	    	this.logMessage("Could not find a neighbor");
	    }
	    
	    return res;
	}


	/**
	 * Unregister the node corresponding to the nodeIdentifier from the register
	 * @param the node identifier
	 */
	public void unregister(String nodeIdentifier) throws Exception {
	    this.logMessage("Unregistering node " + nodeIdentifier);
	    this.getExecutorService(REGISTER_POOL_URI).submit(() -> {
		    NodeInfoI removedNode = registre.remove(nodeIdentifier);
	        if (removedNode != null) {
	            this.logMessage("Node " + nodeIdentifier + " has been unregistered.");
	        } else {
	            this.logMessage("No node found with identifier " + nodeIdentifier + " to unregister.");
	        }
	    }).get(); // TODO: voir si on doit laisser bloquer ici, je pense que oui comme la suppression est importante
	}

	
	/**
	 * Find the node information from the node identifier
	 * @param the node identifier of the sensor
	 * @return the connection information of the node
	 */
	public ConnectionInfoI findByIdentifier(String sensorNodeId) throws Exception {
		// surement pas besoin de pool de threads ici, on utilise déjà une concurrentHashMap?
	    this.logMessage("Finding a node by identifier " + sensorNodeId);
	    
	    return this.getExecutorService(LOOKUP_POOL_URI).submit(() -> {
		    NodeInfoI nodeInfo = registre.get(sensorNodeId);
		    if (nodeInfo != null) {
		        this.logMessage("Found");
		        return nodeInfo;
		    }
		    this.logMessage("/!\\ Identifier is not in Register");
		    return null;
	    }).get();
	}

	/**
	 * Find all of the node information from the geographical zone
	 * @param the geographical zone
	 * @return a set of the nodes connection information
	 */
	public Set<ConnectionInfoI> findByZone(GeographicalZoneI z) throws Exception {
	    this.logMessage("Finding nodes by geographical zone " + z);
	    return this.getExecutorService(LOOKUP_POOL_URI).submit(() -> {
		    Set<ConnectionInfoI> nodesInZone = registre.values().stream()
		                                                .filter(nodeInfo -> z.in(nodeInfo.nodePosition()))
		                                                .collect(Collectors.toSet());
		
		    return nodesInZone;
	    }).get();
	}

	
	/*@Override
	public void finalise() throws Exception {
    	this.logMessage("Finalising");
        super.finalise();
    }*/
	
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
