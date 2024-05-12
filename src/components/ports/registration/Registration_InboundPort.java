package components.ports.registration;

import java.util.Set;

import components.Registration;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;

public class Registration_InboundPort extends AbstractInboundPort implements RegistrationCI{
	 
	private static final long serialVersionUID = 1L;
	protected final String register_pool_uri;

	public Registration_InboundPort(ComponentI owner, String uri, String register_pool_uri) throws Exception{
        super(uri, RegistrationCI.class, owner);

        assert owner instanceof Registration;
        assert owner.validExecutorServiceURI(register_pool_uri);
        
        this.register_pool_uri = register_pool_uri;
        
    }
	/**
     * @see fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI#registered(String)
     */
	@Override
	public boolean registered(String nodeIdentifier) throws Exception {
		return this.getOwner().handleRequest(
				register_pool_uri,
                new AbstractComponent.AbstractService<Boolean>() {
                    @Override
                    public Boolean call() throws Exception{
                        return ((Registration)this.getServiceOwner()).registered(nodeIdentifier);
                    }
                });
	}
	/**
     * @see fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI#register(NodeInfoI)
     */
	@Override
	public Set<NodeInfoI> register(NodeInfoI nodeInfo) throws Exception {
		return this.getOwner().handleRequest(
				register_pool_uri,
                new AbstractComponent.AbstractService<Set<NodeInfoI>>() {
                    @Override
                    public Set<NodeInfoI> call() throws Exception{
                        return ((Registration)this.getServiceOwner()).register(nodeInfo);
                    }
                });
	}
	/**
     * @see fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI#findNewNeighbour(NodeInfoI,Direction)
     */
	@Override
	public NodeInfoI findNewNeighbour(NodeInfoI nodeInfo, Direction d) throws Exception {
		return this.getOwner().handleRequest(
				register_pool_uri,
                new AbstractComponent.AbstractService<NodeInfoI>() {
                    @Override
                    public NodeInfoI call() throws Exception{
                        return ((Registration)this.getServiceOwner()).findNewNeighbour(nodeInfo, d);
                    }
                });
	}
	/**
     * @see fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI#unregister(String)
     */
	@Override
	public void unregister(String nodeIdentifier) throws Exception {
		this.getOwner().handleRequest(
				register_pool_uri,
                new AbstractComponent.AbstractService<Void>() {
                    @Override
                    public Void call() throws Exception{
                        ((Registration)this.getServiceOwner()).unregister(nodeIdentifier);
                        return null;
                    }
                });
	}
}
