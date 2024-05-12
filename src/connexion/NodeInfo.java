package connexion;

import fr.sorbonne_u.cps.sensor_network.interfaces.BCM4JavaEndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.EndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;

/**
 * This class provides information about a node in the sensor network.
 * Since components and such can not access nodes directly, 
 * they have access to their important information through this class.
 */
public class NodeInfo extends ConnectionInfo implements NodeInfoI{
	private static final long serialVersionUID = 1L;
	
	/** the position of the node */
	private PositionI pos;
	
	/** the range the node can connect to */
	private double range;
	
	/** in BCM4, this attribute represents the inbound port uri of the node where the node will receive queries from Clients */
	private BCM4JavaEndPointDescriptorI portEntrantRequesting;
	
	/**
	 * Constructs a NodeInfo instance
	 * @param ID The identifier of the node
	 * @param portEntrantP2P The inbound port URI for the connection between node
	 * @param portEntrantRequesting The inbound port URI of the node where it will receive queries from Clients
	 * @param p The position of the node
	 * @param range The range of the node
	 */
	public NodeInfo(String ID,BCM4JavaEndPointDescriptorI portEntrantP2P,BCM4JavaEndPointDescriptorI portEntrantRequesting,PositionI p,double range) {
		super(ID, portEntrantP2P);
		this.pos = p;
		this.range = range;
		this.portEntrantRequesting = portEntrantRequesting; 
	}

	/**
	 * @see fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI#nodePosition()
	 */
	@Override
	public PositionI nodePosition() {
		return pos;
	}

	/**
	 * @see fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI#nodeRange()
	 */
	@Override
	public double nodeRange() {
		return range;
	}

	/**
	 * @see fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI#p2pEndPointInfo()
	 */
	@Override
	public EndPointDescriptorI p2pEndPointInfo() {
		return portEntrant;
	}
	
	/**
	 * Returns the inbound port URI for the incoming requesting
	 * @return The descriptor for the incoming requesting end point
	 */
	public EndPointDescriptorI requestingEndPointInfo(){
		return portEntrantRequesting;
	}
	
	@Override
	public String toString() {
		return
				"Position: " + pos.toString() + "\n" +
				 "ID: " + nodeIdentifier() + "\n";
	}
}
