package components.node_node;

import components.Node;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI;

public class InboundPortNodeNode extends AbstractInboundPort implements SensorNodeP2PCI{
	 
	private static final long serialVersionUID = 1L;
	
	protected final String connection_pool_uri;
	protected final String synchronous_pool_uri;
	protected final String asynchronous_pool_uri;

	public InboundPortNodeNode(ComponentI owner, String uri, String connection_pool_uri, String sync_pool_uri, String async_pool_uri) throws Exception{
        super(uri, SensorNodeP2PCI.class, owner);

        assert owner instanceof Node;
        assert owner.validExecutorServiceURI(connection_pool_uri);
        assert owner.validExecutorServiceURI(sync_pool_uri);
        assert owner.validExecutorServiceURI(async_pool_uri);
        
        this.connection_pool_uri = connection_pool_uri;
        this.synchronous_pool_uri = sync_pool_uri;
        this.asynchronous_pool_uri = async_pool_uri;
    }

	@Override
	public void ask4Disconnection(NodeInfoI neighbour) throws Exception {
		this.getOwner().handleRequest(
				connection_pool_uri,
                new AbstractComponent.AbstractService<Void>() {
                    @Override
                    public Void call() throws Exception{
                        ((Node)this.getServiceOwner()).ask4Disconnection(neighbour);
						return null;
                    }
                });
	}

	@Override
	public void ask4Connection(NodeInfoI newNeighbour) throws Exception {
		this.getOwner().handleRequest(
				connection_pool_uri,
                new AbstractComponent.AbstractService<Void>() {
                    @Override
                    public Void call() throws Exception{
                        ((Node)this.getServiceOwner()).ask4Connection(newNeighbour);
						return null;
                    }
                });
	}

	@Override
	public QueryResultI execute(RequestContinuationI request) throws Exception {
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
	public void executeAsync(RequestContinuationI requestContinuation) throws Exception {
		this.getOwner().handleRequest(
				asynchronous_pool_uri,
                new AbstractComponent.AbstractService<Void>() {
                    @Override
                    public Void call() throws Exception{
                        ((Node)this.getServiceOwner()).executeAsync(requestContinuation);
						return null;
                    }
                });
	}
}
