package components.builders;

import java.util.List;
import java.util.Map;

import components.Client;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;

/**
 * This class follows the Design Pattern Builder,
 * its purpose is to create a multiple Clients specified by the nbClients parameter in an easier way.
 * The main method build() will create unique Client components with a unique URI, their requests and their target
 */
public class ClientBuilder {
	
	 /**
     * Construit une série de clients avec des configurations spécifiques pour les requêtes
     * La structure de la Map de requêtes doit être comme suit:
     * {"1", (req1, req2, ...)} pour le client 1
     * {"2", (req1, req2, ...)} pour le client 2 etc
     *
     * @param nbClients Nombre total de clients à construire
     * @param nbThreads Nombre de threads pour client
     * @param nbSchedulableThreads Nombre de threads schedulables pour client
     * @param uriClient URI de base pour les clients
     * @param inPort URI du port d'entrée pour les clients
     * @param requestsMap HashMap associant un identifiant de client à sa liste de requêtes
     * @param nbRequests Nombre d'envoi d'une même requête sur le même port (par défaut 1)
     * @param nodeId Map associant un identifiant de client à son identifiant de noeud cible, même structure que la map des requêtes
     * @param geo Map associant un identifiant de client à sa zone géographique cible, même structure que la map des requêtes
     * @param plugin_uri URI de base pour les plugins spécifiques à chaque client
     */
    public static void build(int nbClients,
                            int nbThreads, int nbSchedulableThreads,
                            String uriClient,
                            String inPort,
                            Map<String, List<RequestI>> requests,
                            int nbRequests,
                            Map<String, String> nodeId,
                            Map<String, GeographicalZoneI> geo,
                            String plugin_uri,
                            int jvm) {

    	try {
    		for (int i = 0; i < nbClients; i++) {
    			
    			String client = String.valueOf(i + 1);
                List<RequestI> rqs = requests.get(client);
                String clientNodeId = nodeId.get(client);
                GeographicalZoneI clientGeo = geo.get(client);
                
                String uri_client = uriClient + "-JVM-" + jvm + "-" +i;
                String uri_inport = inPort + "-JVM-" + jvm + "-" +i;
                
				AbstractComponent.createComponent(
						Client.class.getCanonicalName(), 
						new Object[] {nbThreads, nbSchedulableThreads,
									uri_client, uri_inport,
									rqs, 
									nbRequests, 
									clientNodeId, 
									clientGeo, 
									plugin_uri+i});
    		}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}