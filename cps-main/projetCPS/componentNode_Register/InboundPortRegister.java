package componentNode_Register;

import java.util.Set;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;
import connexion.registre.Registration;

public class InboundPortRegister extends AbstractInboundPort implements RegistrationCI{
	 
	public InboundPortRegister(ComponentI owner, String uri) throws Exception{
        super(uri, RegistrationCI.class, owner);

        assert owner instanceof Registration;
    }

	@Override
	public boolean registered(String nodeIdentifier) throws Exception {
		return this.getOwner().handleRequest(
                new AbstractComponent.AbstractService<Boolean>() {
                    @Override
                    public Boolean call() throws Exception{
                        return ((Registration)this.getServiceOwner()).registered(nodeIdentifier);
                    }
                });
	}

	@Override
	public Set<NodeInfoI> register(NodeInfoI nodeInfo) throws Exception {
		return this.getOwner().handleRequest(
                new AbstractComponent.AbstractService<Set<NodeInfoI>>() {
                    @Override
                    public Set<NodeInfoI> call() throws Exception{
                        return ((Registration)this.getServiceOwner()).register(nodeInfo);
                    }
                });
	}

	@Override
	public NodeInfoI findNewNeighbour(NodeInfoI nodeInfo, Direction d) throws Exception {
		return this.getOwner().handleRequest(
                new AbstractComponent.AbstractService<NodeInfoI>() {
                    @Override
                    public NodeInfoI call() throws Exception{
                        return ((Registration)this.getServiceOwner()).findNewNeighbour(nodeInfo, d);
                    }
                });
	}

	@Override
	public void unregister(String nodeIdentifier) throws Exception {
		this.getOwner().handleRequest(
                new AbstractComponent.AbstractService<Void>() {
                    @Override
                    public Void call() throws Exception{
                        ((Registration)this.getServiceOwner()).unregister(nodeIdentifier);
                        return null;
                    }
                });
	}
}
