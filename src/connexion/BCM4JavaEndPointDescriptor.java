package connexion;

import java.util.HashSet;
import java.util.Set;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.cps.sensor_network.interfaces.BCM4JavaEndPointDescriptorI;

/**
 * In BCM4, this class gives the inbound port uri of a component
 */
public class BCM4JavaEndPointDescriptor implements BCM4JavaEndPointDescriptorI{
	private static final long serialVersionUID = 1L;
	
	/** the inbound port uri */
	private String portURI;
	/** the offered interfaces */
	private Set<Class<? extends OfferedCI>> offeredInterfaces = new HashSet<>();
	
	public BCM4JavaEndPointDescriptor(String portURI){
		this.portURI = portURI;
	}
	
	/**
	 * @see fr.sorbonne_u.cps.sensor_network.interfaces.BCM4JavaEndPointDescriptorI#getInboundPortURI()
	 */
	@Override
	public String getInboundPortURI() {
		return portURI;
	}

	/**
	 * @see fr.sorbonne_u.cps.sensor_network.interfaces.BCM4JavaEndPointDescriptorI#isOfferedInterface(Class)
	 */
	@Override
	public boolean isOfferedInterface(Class<? extends OfferedCI> inter) {
		assert inter != null : "L'interface est null";

        return offeredInterfaces.contains(inter);
  
	}
	
	@Override
	public String toString() {
		return portURI;
	}

}
