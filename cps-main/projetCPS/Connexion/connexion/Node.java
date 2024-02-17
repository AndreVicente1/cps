package Connexion.connexion;

import fr.sorbonne_u.cps.sensor_network.interfaces.*;

import java.util.ArrayList;
import java.util.Set;

public class Node implements NodeI {
    private String nodeId;
    private ArrayList<SensorDataI> sensors;
    private PositionI position;
    private double range;

    //TODO
    private Set<NodeInfoI> neighbours;

    public Node(String nodeId, ArrayList<SensorDataI> sensors, PositionI pos, double range){
        this.nodeId = nodeId;
        this.sensors = sensors;
        this.position = pos;
        this.range = range;
    }
    
    //méthode auxiliaire pour rechercher un senseur
    private SensorDataI searchSensor(String sensorIdentifier) {
    	for (SensorDataI s : sensors) {
    		if (s.getSensorIdentifier().equals(sensorIdentifier)) {
    			System.out.println("Trouvé et ajouté " + sensorIdentifier);
    			return s;
    		}
    	}
    	System.out.println("SENSOR NULL");
    	return null;
    }
    
    @Override
    public String getNodeIdentifier() {
        return nodeId;
    }

    @Override
    public PositionI getPosition() {
        return position;
    }

    @Override
    public Set<NodeInfoI> getNeighbours() {
        //TODO pas pour l'audit 1
        return null;
    }

    @Override
    public SensorDataI getSensorData(String sensorIdentifier) {
        return searchSensor(sensorIdentifier);
    }
    

    @Override
    public String nodeIdentifier() {
        return nodeId;
    }

    @Override
    public EndPointDescriptorI endPointInfo() {
        //TODO
        return null;
    }

    @Override
    public PositionI nodePosition() {
        return position;
    }

    @Override
    public double nodeRange() {
        return range;
    }

    @Override
    public EndPointDescriptorI p2pEndPointInfo() {
        //TODO
        return null;
    }
    
    @Override
    public ArrayList<SensorDataI> getAllSensors(){
    	return sensors;
    }
}
