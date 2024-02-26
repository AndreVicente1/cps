package connexion;

import fr.sorbonne_u.cps.sensor_network.interfaces.*;

import java.util.ArrayList;
import java.util.Set;

public class Node implements NodeI {
    private String nodeId;
    private ArrayList<SensorDataI> sensors;
    private PositionI position;
    private double range;

    private Set<NodeInfoI> neighbours;
    private EndPointDescriptorI uriInPort;

    public Node(String nodeId, ArrayList<SensorDataI> sensors, PositionI pos, double range, Set<NodeInfoI> neighbours,  EndPointDescriptorI uriInPort){
        this.nodeId = nodeId;
        this.sensors = sensors;
        this.position = pos;
        this.range = range;
        
        this.neighbours = neighbours;
        this.uriInPort = uriInPort;
    }
    
    //méthode auxiliaire pour rechercher un senseur
    private SensorDataI searchSensor(String sensorIdentifier) {
    	for (SensorDataI s : sensors) {
    		if (s.getSensorIdentifier().equals(sensorIdentifier)) {
    			//System.out.println("Trouvé et ajouté " + sensorIdentifier);
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
        return neighbours;
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
        return uriInPort;
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
        return uriInPort;
    }
    
    /*
     * Cette méthode a été rajouté pour récupérer tous les noeuds
     */
    @Override
    public ArrayList<SensorDataI> getAllSensors(){
    	return sensors;
    }
    
    /*
     * Method for testing Continuation
     */
    public void setNeighbors(Set<NodeInfoI> neighbors) {
    	this.neighbours = neighbors;
    }

}
