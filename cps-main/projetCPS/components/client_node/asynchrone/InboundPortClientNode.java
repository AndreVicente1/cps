package components.client_node.asynchrone;

import components.Client;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestResultCI;

public class InboundPortClientNode extends AbstractInboundPort implements RequestResultCI {

    public InboundPortClientNode(ComponentI owner, String uri) throws Exception{
        super(uri, RequestResultCI.class, owner);

        assert owner instanceof Client;

    }

	@Override
	public void acceptRequestResult(String requestURI, QueryResultI result) throws Exception {
		 this.getOwner().runTask(
	                new AbstractComponent.AbstractTask() {
	                    @Override
	                    public void run() {
	                        try {
	                            ((Client)this.getTaskOwner()).
	                                acceptRequestResult(
	                                    requestURI,
	                                    result) ;
	                        } catch (Exception e) {
	                            e.printStackTrace() ;
	                        }
	                    }
	                }) ;
	}
    
}
