package connexion.registre;

import java.util.Set;

import componentTest.OutboundPortClient;

import java.util.ArrayList;
import java.util.HashSet;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;

@OfferedInterfaces(offered = {RegistrationCI.class, LookupCI.class})
/*
 * Cette classe est unique, on peut en faire un singleton
 */
public class Registration extends AbstractComponent{
	private static Registration instance = null;
	
	private ArrayList<NodeInfoI> registre = new  ArrayList<NodeInfoI>();
	
	/*
	 * Design Pattern Singleton
	 */
	private Registration(int nbThreads, int nbSchedulableThreads,
	        String uriRegister) throws Exception{
	
		super(uriRegister, nbThreads, nbSchedulableThreads);
	}
	
	public static Registration getInstance(int nbThreads, int nbSchedulableThreads, String uriRegister) throws Exception {
        if (instance == null) {
            instance = new Registration(nbThreads, nbSchedulableThreads, uriRegister);
        }
        return instance;
    }
	
	/*
	 * RegistrationCI methods
	 */
	public boolean registered(String nodeIdentifier) throws Exception {
		for (NodeInfoI nodeInfo : registre) {
			if (nodeInfo.nodeIdentifier().equals(nodeIdentifier)) {
				return true;
			}
		}
		return false;
	}

	/*
	 * Register the new node and return a set of nodes information to which this new node can connect given its range and the range of the returned nodes.
	 */
	public Set<NodeInfoI> register(NodeInfoI nodeInfo) throws Exception {
		
		if (!registered(nodeInfo.nodeIdentifier())){
			registre.add(nodeInfo);
			
			Set<NodeInfoI> voisinsPotentiels = new HashSet<>();
			for (NodeInfoI existingNode : registre) {
				if (!existingNode.nodeIdentifier().equals(nodeInfo.nodeIdentifier())) {
					if (isInRange(nodeInfo, existingNode)) {
                    	voisinsPotentiels.add(existingNode);
					}
				}
			}
			return voisinsPotentiels;
		} else {
			throw new Exception("Node deja enregistré.");
    	}
	}
	
	/*
	 * Auxiliary method to identify whether two nodes are in range of each other
	 */
	private boolean isInRange(NodeInfoI node1, NodeInfoI node2) {
    	return node1.nodePosition().distance(node2.nodePosition()) < node1.nodeRange() &&  node1.nodePosition().distance(node2.nodePosition()) < node2.nodeRange();
   
	}

	/*
	 * Find a new neighbour for the given node in the given direction and return its connection info, or null if none exists.
	 * @param the NodeInfoI of the wanted neighbors of the node, the direction wanted
	 * @return the closest neighbor's information to the node
	 */
	public NodeInfoI findNewNeighbour(NodeInfoI nodeInfo, Direction d) throws Exception {
		NodeInfoI closestVois = null;
	    double closest = Double.MAX_VALUE;
	    for (NodeInfoI potential : registre) {
	        if (!potential.nodeIdentifier().equals(nodeInfo.nodeIdentifier())) {
	            Direction dir = nodeInfo.nodePosition().directionFrom(potential.nodePosition());
	            double dist = nodeInfo.nodePosition().distance(potential.nodePosition());

	            // le noeud le plus proche et dans la range
	            if (dir == d && isInRange(nodeInfo, potential)) {
	                if (dist < closest) {
	                    closest = dist;
	                    closestVois = potential;
	                }
	            }
	        }
	    }
	    return closestVois;
	}

	public void unregister(String nodeIdentifier) throws Exception {
		registre.removeIf(nodeInfo -> nodeInfo.nodeIdentifier().equals(nodeIdentifier));
	}

	
	/*
	 * Lookup methods
	 */
	public ConnectionInfoI findByIdentifier(String sensorNodeId) throws Exception {
		for (NodeInfoI nodeInfo : registre) {
			if (nodeInfo.nodeIdentifier().equals(sensorNodeId)) {
				return nodeInfo;
			}
		}
		return null;
	}

	public Set<ConnectionInfoI> findByZone(GeographicalZoneI z) throws Exception {
		Set<ConnectionInfoI> nodeInfoSet = new HashSet<>();
		for (NodeInfoI nodeInfo : registre) {
			if (z.in(nodeInfo.nodePosition())){
				nodeInfoSet.add(nodeInfo);
			}
		}
		return nodeInfoSet;
	}
	
}
