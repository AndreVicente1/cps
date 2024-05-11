package components.builders;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import ast.position.Position;
import components.Node;
import connexion.SensorData;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;

public class NodeBuilder {

    /**
     * Crée des composants noeuds avec une valeur de temperature et de fumee des senseurs aléatoires spécifiques, et la position aléatoire mais doit être positionnée en diagonale des autres noeuds  
     * @param nb le nombre de noeuds à créer
     * @return la liste des uri des noeuds
     */
	public static ArrayList<String> createRandomNodes(int nb, double range, 
    												int NTHREADS_NEW_REQ_POOL,
    												int NTHREADS_CONT_REQ_POOL,
    												int NTHREADS_SYNC_REQ_POOL,
    												int NTHREADS_CONNECTION_POOL
    												){
    	 ArrayList<String> nodes = new ArrayList<>();
         Random random = new Random();
         ArrayList<PositionI> usedPositions = new ArrayList<>();

         for (int i = 0; i < nb; i++) {
        	
        	 double temperatureValue = 20.0 + (40.0 - 20.0) * random.nextDouble(); // Température entre 20 et 40
        	 System.out.format("%.1f", temperatureValue);
        	 double smokeValue = 1.0 + (10.0 - 1.0) * random.nextDouble(); // Fumée entre 1 et 10
        	 
        	 String uriNode = "URI_Node" + i;
             SensorDataI sensorTemperature = new SensorData<Double>(temperatureValue, uriNode + i, "temperature");
             SensorDataI sensorSmoke = new SensorData<Double>(smokeValue, uriNode + i, "fumee");

             ArrayList<SensorDataI> sensors = new ArrayList<>();
             sensors.add(sensorTemperature);
             sensors.add(sensorSmoke);
             
             String nodeInURI = "URI_Node-ClientPortIn" + i;
             
             String nodeInURI4Node = "URI_Node-NodePortIn" + i;
             
             
             // position unique en diagonale
             PositionI pos;
             
             do {
                 int signX = random.nextBoolean() ? 1 : -1;
                 int signY = random.nextBoolean() ? 1 : -1;
                 pos = new Position(signX * i, signY * i);
             } while (usedPositions.contains(pos));
             usedPositions.add(pos);
             
             //System.out.println("node URI: " + uriNode + " range = " + range + " sensor value: temp: " + temperatureValue + " fumee: " + smokeValue);
             String uri = null;
			 try {
				uri = AbstractComponent.createComponent(Node.class.getCanonicalName(), new Object[]{1, 1, uriNode, 
							nodeInURI, nodeInURI4Node,
							pos, range, sensors,
							NTHREADS_NEW_REQ_POOL,
		                    NTHREADS_CONT_REQ_POOL,
		                    NTHREADS_CONNECTION_POOL,
		                    NTHREADS_SYNC_REQ_POOL});
				nodes.add(uri);
			 } catch (Exception e) {
				e.printStackTrace();
			 }
             
             
         }
         
         return nodes;
     }
    
	/**
	 * Crée une liste de noeuds avec des données de capteurs spécifiées dans une map. Chaque noeud est créé avec des données sensorielles dynamiques en paramètre
	 * Cette méthode utilise une position diagonale pour chaque nœud pour simplifier le positionnement.
	 * @see components.builders.NodeBuilder#createDiagonalPositions(int)
	 * 
	 * @param nb Le nombre total de noeuds à créer
	 * @param range La portée de chaque noeud
	 * @param NTHREADS_NEW_REQ_POOL Le nombre de threads dédiés aux nouvelles requêtes dans le pool de threads de chaque nœud
	 * @param NTHREADS_CONT_REQ_POOL Le nombre de threads pour les requêtes continues dans le pool de threads de chaque nœud
	 * @param NTHREADS_SYNC_REQ_POOL Le nombre de threads pour les requêtes synchronisées dans le pool de threads de chaque nœud
	 * @param NTHREADS_CONNECTION_POOL Le nombre de threads pour la gestion des connexions dans le pool de threads de chaque nœud
	 * @param sensorDatas Une map contenant les types de données des capteurs et leurs valeurs. Chaque clé représente un type de capteur, avec sa valeur
	 * @param uriNode L'URI de la première node, les autres seront complétés automatiquement
	 * @return Une liste de chaînes contenant les URIs des nœuds créés, utilisables pour des références ultérieures ou des connexions
	 * 
	 * @throws IllegalArgumentException
	 */
	public static <T extends Serializable> ArrayList<String> createFixedNodes(int nb, double range,
										    		int NTHREADS_NEW_REQ_POOL,
													int NTHREADS_CONT_REQ_POOL,
													int NTHREADS_SYNC_REQ_POOL,
													int NTHREADS_CONNECTION_POOL,
													Map<String, T> sensorDatas,
													String uriNode) {
        ArrayList<String> nodes = new ArrayList<>();
        ArrayList<PositionI> positions = createDiagonalPositions(nb);

        for (int i = 0; i < nb; i++) {
        	ArrayList<SensorDataI> sensors = new ArrayList<>();
        	String node_uri = uriNode + i;
			if (i==49) {
			        sensors.add(new SensorData<Double>(9999999.,node_uri,"temperature"));
			        sensors.add(new SensorData<Double>(9999999.,node_uri,"fumee"));
			        sensors.add(new SensorData<Double>(9999999.,node_uri,"humidite"));
            }else {
            	for (Map.Entry<String, T> entry : sensorDatas.entrySet()) {
                    String sensorType = entry.getKey();
                    T value = entry.getValue();
                    sensors.add(new SensorData<T>(value, node_uri, sensorType));
                }
            }
        	
            
            String nodeInURI = "URI_Node-ClientPortIn" + i;
            String nodeInURI4Node = "URI_Node-NodePortIn" + i;
            String plugin_node = "plugin_node" + i;

            String uri = null;
            try {
                uri = AbstractComponent.createComponent(Node.class.getCanonicalName(), new Object[]{
                    1, 1, node_uri, 
                    nodeInURI, nodeInURI4Node,
                    positions.get(i), range, sensors,
                    NTHREADS_NEW_REQ_POOL,
                    NTHREADS_CONT_REQ_POOL,
                    NTHREADS_CONNECTION_POOL,
                    NTHREADS_SYNC_REQ_POOL,
                    plugin_node
                });
                nodes.add(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return nodes;
    }

	
	/**
	 * Crée une liste de positions disposées de manière diagonale pour un nombre spécifié de noeuds
	 * Cette méthode alterne les positions diagonales entre les quadrants positifs et négatifs pour chaque noeud
	 * Chaque indice pair (0, 2, 4, ...) reçoit une position (i, i) située dans le quadrant positif (haut droit) du plan,
	 * tandis que chaque indice impair (1, 3, 5, ...) reçoit une position (-i, -i) dans le quadrant négatif (bas gauche)
	 * 
	 * @param nb Le nombre total de positions à générer. Cela correspond généralement au nombre de nœuds dans une simulation ou une configuration réseau.
	 * @return ArrayList<PositionI> Une liste contenant les objets de position. Chaque position est représentée par un point (x, y).
	 */
	private static ArrayList<PositionI> createDiagonalPositions(int nb) {
	    ArrayList<PositionI> positions = new ArrayList<>();
	    for (int i = 0; i < nb; i++) {
	        int x = i;  // Coordonnées x croissantes
	        int y = i;  // Coordonnées y correspondantes, maintenant toujours positives
	        positions.add(new Position(x, y));
	    }
	    return positions;
	}

}
