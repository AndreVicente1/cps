package components.ports.requestResult;

import components.Node;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestResultCI;

public class RequestResult_OutboundPort extends AbstractOutboundPort implements RequestResultCI  {
    private static final long serialVersionUID = 1L;


	public RequestResult_OutboundPort(ComponentI owner, String uri) throws Exception {
        super(uri, RequestResultCI.class, owner);

        assert owner instanceof Node;
    }
    
    public RequestResult_OutboundPort(ComponentI owner) throws Exception {
        super(RequestResultCI.class, owner);

        assert owner instanceof Node;
    }


	@Override
	public void acceptRequestResult(String requestURI, QueryResultI result) throws Exception {
		((RequestResultCI)this.getConnector()).acceptRequestResult(requestURI, result);
	}


}
