package componentNode_Node;

import componentTest.Provider;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI;

public class InboundPortProviderNode extends AbstractInboundPort implements SensorNodeP2PCI{
	 
	public InboundPortProviderNode(ComponentI owner, String uri) throws Exception{
        super(uri, SensorNodeP2PCI.class, owner);

        assert owner instanceof Provider;
    }

	@Override
	public void ask4Disconnection(NodeInfoI neighbour) throws Exception {
		this.getOwner().handleRequest(
                new AbstractComponent.AbstractService<Void>() {
                    @Override
                    public Void call() throws Exception{
                        ((Provider)this.getServiceOwner()).disconnect(neighbour);
						return null;
                    }
                });
	}

	@Override
	public void ask4Connection(NodeInfoI newNeighbour) throws Exception {
		this.getOwner().handleRequest(
                new AbstractComponent.AbstractService<Void>() {
                    @Override
                    public Void call() throws Exception{
                        ((Provider)this.getServiceOwner()).connect(newNeighbour);
						return null;
                    }
                });
	}

	@Override
	public QueryResultI execute(RequestContinuationI request) throws Exception {
		return this.getOwner().handleRequest(
                new AbstractComponent.AbstractService<QueryResultI>() {
                    @Override
                    public QueryResultI call() throws Exception{
                        return (QueryResultI) ((Provider)this.getServiceOwner()).treatRequest(request);
                    }
                });
	}

	@Override
	public void executeAsync(RequestContinuationI requestContinuation) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
