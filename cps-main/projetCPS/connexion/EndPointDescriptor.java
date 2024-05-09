package connexion;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.cps.sensor_network.interfaces.EndPointDescriptorI;

public class EndPointDescriptor extends BCM4JavaEndPointDescriptor implements EndPointDescriptorI{

	private static final long serialVersionUID = 1L;

	public EndPointDescriptor(String portURI) {
		super(portURI);
	}

}
