package components.client_node.asynchrone;

import components.Node;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestResultCI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;

public class OutboundPortNodeClient extends AbstractOutboundPort implements RequestResultCI  {
    public OutboundPortNodeClient(ComponentI owner, String uri) throws Exception {
        super(uri, RequestResultCI.class, owner);

        assert owner instanceof Node;
    }

	@Override
	public void acceptRequestResult(String requestURI, QueryResultI result) throws Exception {
		// TODO Auto-generated method stub
		
	}


}
