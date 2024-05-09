package components.ports.p2p;

import components.Node;
import components.plugins.Plugin_Node;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI;

public class P2P_InboundPort extends AbstractInboundPort implements SensorNodeP2PCI{
	 
	private static final long serialVersionUID = 1L;
	
	protected final String connection_pool_uri;
	protected final String synchronous_pool_uri;
	protected final String async_cont_pool_uri;
	protected final String plugin_uri;

	public P2P_InboundPort(ComponentI owner, String uri, String connection_pool_uri, String sync_pool_uri, String async_pool_uri, String plugin_uri) throws Exception{
        super(uri, SensorNodeP2PCI.class, owner);

        assert owner instanceof Node;
        assert owner.validExecutorServiceURI(connection_pool_uri);
        assert owner.validExecutorServiceURI(sync_pool_uri);
        assert owner.validExecutorServiceURI(async_pool_uri);
        assert plugin_uri != null;
        
        this.connection_pool_uri = connection_pool_uri;
        this.synchronous_pool_uri = sync_pool_uri;
        this.async_cont_pool_uri = async_pool_uri;
        this.plugin_uri = plugin_uri;
	}
	
	public P2P_InboundPort(ComponentI owner, String connection_pool_uri, String sync_pool_uri, String async_pool_uri, String plugin_uri) throws Exception{
        super(SensorNodeP2PCI.class, owner);

        assert owner instanceof Node;
        assert owner.validExecutorServiceURI(connection_pool_uri);
        assert owner.validExecutorServiceURI(sync_pool_uri);
        assert owner.validExecutorServiceURI(async_pool_uri);
        assert plugin_uri != null;
        
        this.connection_pool_uri = connection_pool_uri;
        this.synchronous_pool_uri = sync_pool_uri;
        this.async_cont_pool_uri = async_pool_uri;
        this.plugin_uri = plugin_uri;
	}

	@Override
	public void ask4Disconnection(NodeInfoI neighbour) throws Exception {
		this.getOwner().runTask(
				connection_pool_uri,
                new AbstractComponent.AbstractTask(plugin_uri) {
                    @Override
                    public void run(){
                        try {
							((Plugin_Node)this.getTaskProviderReference()).ask4Disconnection(neighbour);
						} catch (Exception e) {
							e.printStackTrace();
						}
                    }
                });
	}

	@Override
	public void ask4Connection(NodeInfoI newNeighbour) throws Exception {
		this.getOwner().runTask(
				connection_pool_uri,
                new AbstractComponent.AbstractTask(plugin_uri) {
                    @Override
                    public void run(){
                        try {
							((Plugin_Node) this.getTaskProviderReference()).ask4Connection(newNeighbour);
						} catch (Exception e) {
							e.printStackTrace();
						}
                    }
                });
	}

	@Override
	public QueryResultI execute(RequestContinuationI request) throws Exception {
		return this.getOwner().handleRequest(
				synchronous_pool_uri,
                new AbstractComponent.AbstractService<QueryResultI>(plugin_uri) {
                    @Override
                    public QueryResultI call() throws Exception{
                        return (QueryResultI) ((Plugin_Node)this.getServiceProviderReference()).execute(request);
                    }
                });
	}

	@Override
	public void executeAsync(RequestContinuationI requestContinuation) throws Exception {
		this.getOwner().runTask(
				async_cont_pool_uri,
				new AbstractComponent.AbstractTask(plugin_uri) {
                    @Override
                    public void run() {
                        try {
                        	((Plugin_Node)this.getTaskProviderReference()).executeAsync(requestContinuation);
						} catch (Exception e) {
							e.printStackTrace();
						}
                    }
                });
	}
}
