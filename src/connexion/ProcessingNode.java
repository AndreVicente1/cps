package connexion;

import java.util.Set;

import components.Node;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;

/**
 * Represents the processing node during a request handling. 
 * This class represents the node for an execution state.
 */
public class ProcessingNode implements ProcessingNodeI{
	/** The node the ProcessingNode class represents */
	private Node node;
	
	/**
	 * Constructs a ProcessingNode instance
	 * @param node The node associated with this processing node
	 */
	public ProcessingNode(Node node){
		this.node = node;
	}
	
	/**
	 * @see fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI#getNodeIdentifier()
	 */
	@Override
	public String getNodeIdentifier() {
		return node.getNodeInfo().nodeIdentifier();
	}
	
	/**
	 * @see fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI#getPosition()
	 */
	@Override
	public PositionI getPosition() {
		return node.getNodeInfo().nodePosition();
	}

	/**
	 * @see fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI#getNeighbours()
	 */
	@Override
	public Set<NodeInfoI> getNeighbours() {
		return node.getNeighbours();
	}

	/**
	 * @see fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI#getSensorData(String)
	 */
	@Override
	public SensorDataI getSensorData(String sensorIdentifier) {
		return node.getSensorData(sensorIdentifier);
	}
	
	/**
	 * Obtain the node the processing node represents
	 * @return
	 */
	public Node getNode() { return node; }
	
	@Override
	public String toString() {
		return node.getNodeInfo().toString();
	}
	
}
