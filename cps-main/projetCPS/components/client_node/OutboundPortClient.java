package components.client_node;

import components.Client;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;

public class OutboundPortClient extends AbstractOutboundPort implements RequestingCI  {
    public OutboundPortClient(ComponentI owner, String uri) throws Exception {
        super(uri, RequestingCI.class, owner);

        assert owner instanceof Client;
    }

	@Override
	public QueryResultI execute(RequestI request) throws Exception {
		return ((RequestingCI)this.getConnector()).execute(request);
	}

	@Override
	public void executeAsync(RequestI request) throws Exception {
		// TODO Auto-generated method stub
	
	}

}
