package test;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;

public interface ProviderCI extends OfferedCI {
    public QueryResultI treatRequest(RequestI request) throws Exception;
    //TODO autres méthodes pour traiter d'autres requêtes? pas pour l'instant
}
