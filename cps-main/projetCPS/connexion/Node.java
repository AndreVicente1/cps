package connexion;

import fr.sorbonne_u.cps.sensor_network.interfaces.*;

import java.util.ArrayList;
import java.util.Set;

public class Node implements NodeI {
    private String nodeId;
    private ArrayList<SensorDataI> sensors;
    private PositionI position;
    private double range;

    //TODO: avec registre
    private Set<NodeInfoI> neighbours;

    public Node(String nodeId, ArrayList<SensorDataI> sensors, PositionI pos, double range, Set<NodeInfoI> neighbours){
        this.nodeId = nodeId;
        this.sensors = sensors;
        this.position = pos;
        this.range = range;
        
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
    
    /*
     * Cette méthode a été rajouté pour récupérer tous les noeuds
     */
    @Override
    public ArrayList<SensorDataI> getAllSensors(){
    	return sensors;
    }

    
    
    /*
     * Connection between nodes
     */
	@Override
	public void ask4Connection(NodeInfoI newNeighbour) throws Exception {
		 if (neighbours.contains(newNeighbour) ) {
	        System.out.println("Le nouveau voisin est déjà connecté.");
	        return;
	    }

	    double distance = this.position.distance(newNeighbour.nodePosition());

	    if (distance <= this.range) {
	        neighbours.add(newNeighbour);
	        // ajouter le noeud courrant chez les voisins du voisin
	        NodeI voisin = (NodeI) newNeighbour;
	        voisin.ask4Connection(this);
	        
	        System.out.println("Connexion établie avec le nouveau voisin");
	    } else {
	        System.out.println("Le nouveau voisin est hors de portée");
	    }
	}

	@Override
	public QueryResultI execute(RequestContinuationI request) throws Exception {
		// LOCAL
		QueryResultI res = new QueryResult();
		return null;
	}

	@Override
	public void executeAsync(RequestContinuationI requestContinuation) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ask4Disconnection(NodeInfoI neighbour) throws Exception {
		boolean isRemoved = neighbours.remove(neighbour);
	    if (isRemoved) {
            NodeI voisin = (NodeI) neighbour;
			voisin.ask4Disconnection(this);
	        System.out.println("Déconnexion réussie du voisin.");
	    } else {
	        System.out.println("Le voisin spécifié n'est pas trouvé.");
	    }
	}
}
