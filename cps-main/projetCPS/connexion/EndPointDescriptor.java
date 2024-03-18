package connexion;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.cps.sensor_network.interfaces.EndPointDescriptorI;

public class EndPointDescriptor extends BCM4JavaEndPointDescriptor implements EndPointDescriptorI{

	public EndPointDescriptor(String portURI, Class<? extends OfferedCI> offeredInterface) {
		super(portURI, offeredInterface);
	}

}
