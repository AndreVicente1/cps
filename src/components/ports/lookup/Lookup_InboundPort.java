package components.ports.lookup;

import java.util.Set;

import components.Registration;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;

public class Lookup_InboundPort extends AbstractInboundPort implements LookupCI{
	
	private static final long serialVersionUID = 1L;
	
	protected final String lookup_pool_uri;

	public Lookup_InboundPort(ComponentI owner, String uri, String lookup_pool_uri) throws Exception{
        super(uri, LookupCI.class, owner);

        assert owner instanceof Registration;
        assert owner.validExecutorServiceURI(lookup_pool_uri);
        
        this.lookup_pool_uri = lookup_pool_uri;
    }
	
	/**
     * @see fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI#findByIdentifier(String)
     */
	@Override
	public ConnectionInfoI findByIdentifier(String sensorNodeId) throws Exception {
		return this.getOwner().handleRequest(
				lookup_pool_uri,
                new AbstractComponent.AbstractService<ConnectionInfoI>() {
                    @Override
                    public ConnectionInfoI call() throws Exception{
                        return ((Registration)this.getServiceOwner()).findByIdentifier(sensorNodeId);
						
                    }
                });
	}
	/**
     * @see fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI#findByZone(GeographicalZoneI)
     */
	@Override
	public Set<ConnectionInfoI> findByZone(GeographicalZoneI z) throws Exception {
		return this.getOwner().handleRequest(
				lookup_pool_uri,
                new AbstractComponent.AbstractService<Set<ConnectionInfoI>>() {
                    @Override
                    public Set<ConnectionInfoI> call() throws Exception{
                    	return ((Registration)this.getServiceOwner()).findByZone(z);
                    }
                });
	}

}
