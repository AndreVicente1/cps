package components.client_node;

import components.Node;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;

public class InboundPortProvider extends AbstractInboundPort implements RequestingCI {

    private static final long serialVersionUID = 1L;
    
    protected final String synchronous_pool_uri;
	protected final String asynchronous_pool_uri;

	public InboundPortProvider(ComponentI owner, String uri, String sync_pool_uri, String async_pool_uri) throws Exception{
        super(uri, RequestingCI.class, owner);

        assert owner instanceof Node;
        assert owner.validExecutorServiceURI(sync_pool_uri);
        assert owner.validExecutorServiceURI(async_pool_uri);
        
        this.synchronous_pool_uri = sync_pool_uri;
        this.asynchronous_pool_uri = async_pool_uri;

    }

	@Override
	public QueryResultI execute(RequestI request) throws Exception {
		return this.getOwner().handleRequest(
				synchronous_pool_uri,
                new AbstractComponent.AbstractService<QueryResultI>() {
                    @Override
                    public QueryResultI call() throws Exception{
                        return (QueryResultI) ((Node)this.getServiceOwner()).execute(request);
                    }
                });
	}

	@Override
	public void executeAsync(RequestI request) throws Exception {
		this.getOwner().handleRequest(
				asynchronous_pool_uri,
                new AbstractComponent.AbstractService<Void>() {
                    @Override
                    public Void call() throws Exception{
                        ((Node)this.getServiceOwner()).executeAsync(request);
						return null;
                    }
                });
	}
}
