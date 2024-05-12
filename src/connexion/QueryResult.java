package connexion;
import java.util.ArrayList;

import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;

/**
 * Represents the result of a query, this class is created by a node and sent to the client
 * or directly received by the Client is the request was synchronous
 */
public class QueryResult implements QueryResultI {
	private static final long serialVersionUID = 1L;
	
	/** List of identifiers of query positive sensor nodes */
	private ArrayList<String> idList;

	/** List of sensor data obtained from the node's sensors */
	private ArrayList<SensorDataI> dataList;

	/** Indicates whether the query is a boolean request */
	private boolean b;

	/**
	 * Constructs a QueryResult instance, to be sent to the Client
	 * @param isBooleanRequest Indicates whether the query is a boolean request
	 */
	public QueryResult(boolean isBooleanRequest) {
		idList = new ArrayList<String>();
		dataList = new ArrayList<SensorDataI>();
		b = isBooleanRequest;
	}
	
	/**
	 * @see fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI#isBooleanRequest()
	 */
	@Override
	public boolean isBooleanRequest() {
		return b;
	}

	/**
	 * @see fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI#positiveSensorNodes()
	 */
	@Override
	public ArrayList<String> positiveSensorNodes() {
		return idList;
	}
	
	/**
	 * @see fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI#isGatherRequest()
	 */
	@Override
	public boolean isGatherRequest() {
		return !b;
	}

	/**
	 * @see fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI#gatheredSensorsValues()
	 */
	@Override
	public ArrayList<SensorDataI> gatheredSensorsValues() {
		return dataList;
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("List of positive sensor nodes: ");
		for (String nodeid : idList)
			s.append(nodeid + "  ");
		s.append("\nList of sensors (id + value):\n");
		for (SensorDataI sensor : dataList)
			if (sensor != null)
				s.append("NodeID: " + sensor.getNodeIdentifier() + ", senseurId: " + sensor.getSensorIdentifier() + ", value: " + sensor.getValue() + "\n");
		return s.toString();
	}

}