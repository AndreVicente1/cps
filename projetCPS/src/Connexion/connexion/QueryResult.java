package Connexion.connexion;
import java.util.ArrayList;

import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;

public class QueryResult implements QueryResultI {
	
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
		// TODO Auto-generated method stub
		return idList;
	}
	
	@Override
	public boolean isGatherRequest() {
		// TODO Auto-generated method stub
		return !b;
	}

	@Override
	public ArrayList<SensorDataI> gatheredSensorsValues() {
		// TODO Auto-generated method stub
		return dataList;
	}
	
	public void addData(SensorDataI s) {
		dataList.add(s);
	}
	
	public void addId(String s) {
		idList.add(s);
	}

}