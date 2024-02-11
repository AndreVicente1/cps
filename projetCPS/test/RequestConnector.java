package test;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;


public class RequestConnector extends AbstractConnector implements RequestCI{
    @Override
    public QueryResultI request(RequestI request) throws Exception {
        return ((ProviderCI)this.offering).treatRequest(request);
    }
}
