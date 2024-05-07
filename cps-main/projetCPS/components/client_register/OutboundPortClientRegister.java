package components.client_register;

import java.util.Set;

import components.Client;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;

public class OutboundPortClientRegister extends AbstractOutboundPort implements LookupCI{
	
	public OutboundPortClientRegister(ComponentI owner, String uri) throws Exception {
        super(uri, LookupCI.class, owner);
        
        assert owner instanceof Client;
    }
	
	public OutboundPortClientRegister(ComponentI owner) throws Exception {
        super(LookupCI.class, owner);
        
        assert owner instanceof Client;
    }
	
	@Override
	public ConnectionInfoI findByIdentifier(String sensorNodeId) throws Exception {
		return ((LookupCI)this.getConnector()).findByIdentifier(sensorNodeId);
	}

	@Override
	public Set<ConnectionInfoI> findByZone(GeographicalZoneI z) throws Exception {
		return ((LookupCI)this.getConnector()).findByZone(z);
	}

}
