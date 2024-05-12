package components.ports.p2p;

import components.Node;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI;

public class P2P_OutboundPort extends AbstractOutboundPort implements SensorNodeP2PCI  {
	private static final long serialVersionUID = 1L;
	
	public P2P_OutboundPort(ComponentI owner, String uri) throws Exception {
        super(uri, SensorNodeP2PCI.class, owner);
        
        assert owner instanceof Node;
    }
	
	public P2P_OutboundPort(ComponentI owner) throws Exception {
		 super(SensorNodeP2PCI.class, owner);
	        
	     assert owner instanceof Node;
	}
	/**
     * @see fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI#ask4Disconnection(NodeInfoI)
     */
	@Override
	public void ask4Disconnection(NodeInfoI neighbour) throws Exception {
		((SensorNodeP2PCI)this.getConnector()).ask4Disconnection(neighbour);
	}
	
	/**
     * @see fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI#ask4Connection(NodeInfoI)
     */
	@Override
	public void ask4Connection(NodeInfoI newNeighbour) throws Exception {
		((SensorNodeP2PCI)this.getConnector()).ask4Connection(newNeighbour);
	}
	
	/**
     * @see fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI#execute(RequestContinuationI)
     */
	@Override
	public QueryResultI execute(RequestContinuationI request) throws Exception {
		return ((SensorNodeP2PCI)this.getConnector()).execute(request);
	}
	/**
     * @see fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI#executeAsync(RequestContinuationI)
     */
	@Override
	public void executeAsync(RequestContinuationI requestContinuation) throws Exception {
		((SensorNodeP2PCI)this.getConnector()).executeAsync(requestContinuation);
	}


}