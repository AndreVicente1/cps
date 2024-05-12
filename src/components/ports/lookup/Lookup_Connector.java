package components.ports.lookup;

import java.util.Set;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;

 
public class Lookup_Connector extends AbstractConnector implements LookupCI {
  
	 /**
     * @see fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI#findByIdentifier(String)
     */
	@Override
	public ConnectionInfoI findByIdentifier(String sensorNodeId) throws Exception {
		return ((LookupCI)this.offering).findByIdentifier(sensorNodeId);
	}
	
	 /**
     * @see fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI#findByZone(GeographicalZoneI)
     */
	@Override
	public Set<ConnectionInfoI> findByZone(GeographicalZoneI z) throws Exception {
		return ((LookupCI)this.offering).findByZone(z);
	}

}
