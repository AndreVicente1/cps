package componentTest;

import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

public interface RequestCI extends RequiredCI {
    public QueryResultI request(RequestI request) throws Exception;
}
