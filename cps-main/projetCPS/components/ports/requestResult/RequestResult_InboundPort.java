package components.ports.requestResult;

import components.Client;
import components.plugins.Plugin_Client;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestResultCI;

public class RequestResult_InboundPort extends AbstractInboundPort implements RequestResultCI {

    private static final long serialVersionUID = 1L;
    private final String plugin_uri;

	public RequestResult_InboundPort(ComponentI owner, String uri, String plugin_uri) throws Exception{
        super(uri, RequestResultCI.class, owner);

        assert owner instanceof Client;
        assert plugin_uri != null;

        this.plugin_uri = plugin_uri;
    }

    public RequestResult_InboundPort(ComponentI owner, String plugin_uri) throws Exception{
        super(RequestResultCI.class, owner);

        assert owner instanceof Client;
        assert plugin_uri != null;
        
        this.plugin_uri = plugin_uri;
    }
    
	@Override
	public void acceptRequestResult(String requestURI, QueryResultI result) throws Exception {
		 this.getOwner().runTask(
	                new AbstractComponent.AbstractTask(plugin_uri) {
	                    @Override
	                    public void run() {
	                        try {
	                            ((Plugin_Client)this.getTaskProviderReference()).acceptRequestResult(
							                                    requestURI,
							                                    result);
	                        } catch (Exception e) {
	                            e.printStackTrace();
	                        }
	                    }
	                });
	}
    
}
