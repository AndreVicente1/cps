package connexion.ast_test;

import java.util.Set;

import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;

public class ProcessingNodeTest implements ProcessingNodeI{
	private NodeTest node;
	public ProcessingNodeTest(NodeTest node){
		this.node = node;
	}
	@Override
	public String getNodeIdentifier() {
		return node.getNodeInfo().nodeIdentifier();
	}
	@Override
	public PositionI getPosition() {
		return node.getNodeInfo().nodePosition();
	}

	@Override
	public Set<NodeInfoI> getNeighbours() {
		return node.getNeighbours();
	}

	@Override
	public SensorDataI getSensorData(String sensorIdentifier) {
		return node.getSensorData(sensorIdentifier);
	}
}
