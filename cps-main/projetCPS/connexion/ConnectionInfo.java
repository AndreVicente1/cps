package connexion;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.EndPointDescriptorI;

/*
 * Cette classe donne les informations pour un client pour se connecter à un noeud du réseau
 */
public class ConnectionInfo implements ConnectionInfoI {
	private static final long serialVersionUID = 1L;
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
	
	@Override
	public String toString() {
		return "Connection Info: " + ID + " port: " + portEntrant;
	}
}
