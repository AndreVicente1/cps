package connexion;
import java.util.ArrayList;

import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;

public class QueryResult implements QueryResultI {
	
	private static final long serialVersionUID = 1L;
	private ArrayList<String> idList;
	private ArrayList<SensorDataI> dataList;
	private boolean b;
	
	public QueryResult(boolean isBooleanRequest) {
		idList = new ArrayList<String>();
		dataList = new ArrayList<SensorDataI>();
		b = isBooleanRequest;
	}
	
	@Override
	public boolean isBooleanRequest() {
		return b;
	}

	@Override
	public ArrayList<String> positiveSensorNodes() {
		return idList;
	}
	
	@Override
	public boolean isGatherRequest() {
		return !b;
	}

	@Override
	public ArrayList<SensorDataI> gatheredSensorsValues() {
		return dataList;
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		//s.append("Query is a boolean: " + b + "\n");
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