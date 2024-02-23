package connexion.registre;

import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;

import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;
import connexion.NodeI;

public class Registration implements RegistrationCI, LookupCI{
	private ArrayList<NodeInfoI> registre = new  ArrayList<NodeInfoI>();
	

	public Registration() {}
	
	/*
	 * RegistrationCI methods
	 */
	@Override
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
	@Override
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
	
	private boolean isInRange(NodeInfoI node1, NodeInfoI node2) {
    	return node1.nodePosition().distance(node2.nodePosition()) < node1.nodeRange() ||  node1.nodePosition().distance(node2.nodePosition()) < node2.nodeRange();
   
	}

	/*
	 * Find a new neighbour for the given node in the given direction and return its connection info, or null if none exists.
	 */
	@Override
	public NodeInfoI findNewNeighbour(NodeInfoI nodeInfo, Direction d) throws Exception {
		NodeI node = (NodeI) nodeInfo;
		Set<NodeInfoI> neighbours = node.getNeighbours();
		NodeInfoI closestVois = null;
		
	    double closest = Double.MAX_VALUE;
		
	    for (NodeInfoI potential : registre) {
	        if (!potential.nodeIdentifier().equals(nodeInfo.nodeIdentifier()) && !neighbours.contains(potential)) {
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

	@Override
	public void unregister(String nodeIdentifier) throws Exception {
		registre.removeIf(nodeInfo -> nodeInfo.nodeIdentifier().equals(nodeIdentifier));
	}

	
	/*
	 * Lookup methods
	 */
	@Override
	public ConnectionInfoI findByIdentifier(String sensorNodeId) throws Exception {
		for (NodeInfoI nodeInfo : registre) {
			NodeI node = (NodeI) nodeInfo ;
			if (node.getSensorData(sensorNodeId)!= null) {
				return node;
			}
		}
		return null;
	}

	@Override
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
