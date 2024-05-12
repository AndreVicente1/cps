package connexion.ast_test;

import fr.sorbonne_u.cps.sensor_network.interfaces.*;

import java.util.ArrayList;
import java.util.Set;

public class NodeTest {
    private NodeInfoI nodeInfo;
    private ArrayList<SensorDataI> sensors;

    private Set<NodeInfoI> neighbours;

    public NodeTest(NodeInfoI nodeInfo, ArrayList<SensorDataI> sensors, Set<NodeInfoI> neighbours){
        this.nodeInfo = nodeInfo;
        this.sensors = sensors;
        
        this.neighbours = neighbours;
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
    
    public NodeInfoI getNodeInfo() {
        return nodeInfo;
    }

    /*
     * Renvoie l'ensemble des voisins du noeud, donné par le registre lorsqu'il se connecte
     * @return l'ensemble des voisins du noeud
     */
    public Set<NodeInfoI> getNeighbours() {
        return neighbours;
    }

    public SensorDataI getSensorData(String sensorIdentifier) {
        return searchSensor(sensorIdentifier);
    }
    
    /*
     * Cette méthode a été rajouté pour récupérer tous les noeuds
     */
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
