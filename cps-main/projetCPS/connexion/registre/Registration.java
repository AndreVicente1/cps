package connexion.registre;

import java.util.Set;

import componentClient_Register.InboundPortRegisterClient;
import componentNode_Register.InboundPortRegister;

import java.util.ArrayList;
import java.util.EnumMap;
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
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;

@OfferedInterfaces(offered = {RegistrationCI.class, LookupCI.class})
public class Registration extends AbstractComponent{
	protected ClocksServerOutboundPort clockOP;
	
	private ArrayList<NodeInfoI> registre = new  ArrayList<NodeInfoI>();
	private InboundPortRegister inpr;
	private InboundPortRegisterClient inprc;
	
	private Registration(int nbThreads, int nbSchedulableThreads,
	        String uriRegister,
	        String uriInPortRegister,
	        String uriInPortRegisterClient) throws Exception{
	
		super(uriRegister, nbThreads, nbSchedulableThreads);
		inpr = new InboundPortRegister(this,uriInPortRegister);
		inpr.publishPort();
		
		inprc = new InboundPortRegisterClient(this, uriInPortRegisterClient);
		inprc.publishPort();
		
		this.addOfferedInterface(RegistrationCI.class);
        this.addOfferedInterface(LookupCI.class);
        
        this.getTracer().setTitle("Register Component") ;
        this.getTracer().setRelativePosition(1,2);
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
	   
	/**
	 * Return true if the node corresponding to nodeIdentifier is registered in the register
	 * @param the node identifier nodeIdentifier
	 * @return true if the node is in the register
	 */
	public boolean registered(String nodeIdentifier) throws Exception {
		for (NodeInfoI nodeInfo : registre) {
			if (nodeInfo.nodeIdentifier().equals(nodeIdentifier)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Register the new node and return a set of nodes information to which this new node can connect given its range and the range of the returned nodes.
	 * @param the information of the node nodeInfo
	 * @return a set of nodes information the node can connect to
	 */
	public Set<NodeInfoI> register(NodeInfoI nodeInfo) throws Exception {
	    if (!registered(nodeInfo.nodeIdentifier())){
	    	this.logMessage("Added node " + nodeInfo.nodeIdentifier() + " to Register" );
	        registre.add(nodeInfo);
	
	        Map<Direction, NodeInfoI> voisinsParDirection = new EnumMap<>(Direction.class);
	        for (NodeInfoI existingNode : registre) {
	            if (!existingNode.nodeIdentifier().equals(nodeInfo.nodeIdentifier())) {
	                Direction dir = nodeInfo.nodePosition().directionFrom(existingNode.nodePosition());
	                if (dir != null && isInRange(nodeInfo, existingNode)) {
	                    NodeInfoI currentClosest = voisinsParDirection.get(dir);
	                    if (currentClosest == null || nodeInfo.nodePosition().distance(existingNode.nodePosition()) < nodeInfo.nodePosition().distance(currentClosest.nodePosition())) {
	                        voisinsParDirection.put(dir, existingNode);
	                    }
	                }
	            }
	        }
	
	        return new HashSet<>(voisinsParDirection.values());
	    } else {
	        throw new Exception("Node déjà enregistré.");
	    }
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
		NodeInfoI closestVois = null;
	    double closest = Double.MAX_VALUE;
	    for (NodeInfoI potential : registre) {
	        if (!potential.nodeIdentifier().equals(nodeInfo.nodeIdentifier())) {
	            Direction dir = nodeInfo.nodePosition().directionFrom(potential.nodePosition());
	            double dist = nodeInfo.nodePosition().distance(potential.nodePosition());
	            // le noeud le plus proche et dans la range
	            if (dir.equals(d) && isInRange(nodeInfo, potential)) {
					
	                if (dist < closest) {
	                    closest = dist;
	                    closestVois = potential;
	     
	                }
	            }
	        }
	    }
	  
	 	//System.out.println("closest = "+closestVois.toString());
	    return closestVois;
	}

	/**
	 * Unregister the node corresponding to the nodeIdentifier from the register
	 * @param the node identifier
	 */
	public void unregister(String nodeIdentifier) throws Exception {
		this.logMessage("Unregistering node " + nodeIdentifier);
		registre.removeIf(nodeInfo -> nodeInfo.nodeIdentifier().equals(nodeIdentifier));
	}

	
	/**
	 * Find the node information from the node identifier
	 * @param the node identifier of the sensor
	 * @return the connection information of the node
	 */
	public ConnectionInfoI findByIdentifier(String sensorNodeId) throws Exception {
		this.logMessage("Finding a node by identifier " + sensorNodeId);
		for (NodeInfoI nodeInfo : registre) {
			if (nodeInfo.nodeIdentifier().equals(sensorNodeId)) {
				this.logMessage("Found");
				return nodeInfo;
			}
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
		this.logMessage("Finding a node by geographical zone " + z);
		Set<ConnectionInfoI> nodeInfoSet = new HashSet<>();
		for (NodeInfoI nodeInfo : registre) {
			if (z.in(nodeInfo.nodePosition())){
				nodeInfoSet.add(nodeInfo);
			}
		}
		return nodeInfoSet;
	}
	
	@Override
	public void shutdown() {
		this.logMessage("Shutting down");
        try {
			super.shutdown();
		} catch (ComponentShutdownException e) {
			e.printStackTrace();
		}
        
        try {
			inpr.unpublishPort();
			inprc.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
