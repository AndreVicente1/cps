package connexion;

import fr.sorbonne_u.cps.sensor_network.interfaces.EndPointDescriptorI;

/**
 * In BCM4, this class gives the inbound port uri of a component
 */
public class EndPointDescriptor extends BCM4JavaEndPointDescriptor implements EndPointDescriptorI{
	private static final long serialVersionUID = 1L;

	public EndPointDescriptor(String portURI) {
		super(portURI);
	}

}
