package componentTest;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;

public class OutboundPortClient extends AbstractOutboundPort implements RequestCI  {
    public OutboundPortClient(ComponentI owner, String uri) throws Exception {
        super(uri, RequestCI.class, owner);

        assert owner instanceof Client;
    }

    @Override
    public QueryResultI request(RequestI request) throws Exception {
        return ((RequestCI)this.getConnector()).request(request);
    }

}
