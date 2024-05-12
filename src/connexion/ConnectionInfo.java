package connexion;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.EndPointDescriptorI;

/*
 * This class provides the necessary information for a client to connect to a node in the network
 */
public class ConnectionInfo implements ConnectionInfoI {
	private static final long serialVersionUID = 1L;
	
	/** the node identifier */
	private final String ID;
	/** the inbound port uri of the node */
	protected final EndPointDescriptorI portEntrant;
	
	/**
	 * Constructs a ConnectionInfo instance
	 * @param id The identifier of the node
	 * @param portEntrant The inbound port uri of the node
	 */
	public ConnectionInfo(String id, EndPointDescriptorI portEntrant) {
		this.ID = id;
		this.portEntrant = portEntrant;
	}
	
	/**
	 * @see fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI#nodeIdentifier()
	 */
	@Override
	public String nodeIdentifier() {
		return ID;
	}

	/**
	 * @see fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI#endPointInfo()
	 */
	@Override
	public EndPointDescriptorI endPointInfo() {
		return portEntrant;
	}
	
	@Override
	public String toString() {
		return "Connection Info: " + ID + " port: " + portEntrant;
	}
}
