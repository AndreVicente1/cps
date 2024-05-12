package components.ports.registration;

import java.util.Set;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;

public class Registration_Connector extends AbstractConnector implements RegistrationCI{
	
	/**
     * @see fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI#registered(String)
     */
	@Override
	public boolean registered(String nodeIdentifier) throws Exception {
		return ((RegistrationCI)this.offering).registered(nodeIdentifier);
	}
	/**
     * @see fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI#register(NodeInfoI)
     */
	@Override
	public Set<NodeInfoI> register(NodeInfoI nodeInfo) throws Exception {
		return ((RegistrationCI)this.offering).register(nodeInfo);
	}
	/**
     * @see fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI#findNewNeighbour(NodeInfoI,Direction)
     */
	@Override
	public NodeInfoI findNewNeighbour(NodeInfoI nodeInfo, Direction d) throws Exception {
		return ((RegistrationCI)this.offering).findNewNeighbour(nodeInfo, d);
	}
	/**
     * @see fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI#unregister(String)
     */
	@Override
	public void unregister(String nodeIdentifier) throws Exception {
		((RegistrationCI)this.offering).unregister(nodeIdentifier);
	}
	
	@Override
	public String toString() {
		return "Connector Node-Register";
	}
}
