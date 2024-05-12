package components.ports.registration;

import java.util.Set;

import components.Node;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;

public class Registration_OutboundPort extends AbstractOutboundPort implements RegistrationCI  {
	private static final long serialVersionUID = 1L;
	
	public Registration_OutboundPort(ComponentI owner, String uri) throws Exception {
        super(uri, RegistrationCI.class, owner);
        
        assert owner instanceof Node;
    }
	
	public Registration_OutboundPort(ComponentI owner) throws Exception {
        super(RegistrationCI.class, owner);
        
        assert owner instanceof Node;
    }
	/**
     * @see fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI#registered(String)
     */
	@Override
	public boolean registered(String nodeIdentifier) throws Exception {
		return ((RegistrationCI)this.getConnector()).registered(nodeIdentifier);
	}
	/**
     * @see fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI#register(NodeInfoI)
     */
	@Override
	public Set<NodeInfoI> register(NodeInfoI nodeInfo) throws Exception {
		return ((RegistrationCI)this.getConnector()).register(nodeInfo);
	}
	/**
     * @see fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI#findNewNeighbour(NodeInfoI,Direction)
     */
	@Override
	public NodeInfoI findNewNeighbour(NodeInfoI nodeInfo, Direction d) throws Exception {
		return ((RegistrationCI)this.getConnector()).findNewNeighbour(nodeInfo, d);
	}
	/**
     * @see fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI#unregister(String)
     */
	@Override
	public void unregister(String nodeIdentifier) throws Exception {
		((RegistrationCI)this.getConnector()).unregister(nodeIdentifier);
	}
	


}