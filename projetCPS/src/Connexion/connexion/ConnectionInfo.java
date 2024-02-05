package connexion;
import fr.sorbonne_u.cps.sensor_network.interfaces.BCM4JavaEndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.EndPointDescriptorI;

public class ConnectionInfo implements ConnectionInfoI {
	private final String ID;
	protected final EndPointDescriptorI portEntrant;
	public ConnectionInfo(String id, EndPointDescriptorI portEntrant) {
		this.ID = id;
		this.portEntrant = portEntrant;
	}
	
	@Override
	public String nodeIdentifier() {
		return ID;
	}

	@Override
	public EndPointDescriptorI endPointInfo() {
		return portEntrant;
	}
	
}
