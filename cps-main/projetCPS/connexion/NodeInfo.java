package connexion;

import fr.sorbonne_u.cps.sensor_network.interfaces.BCM4JavaEndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.EndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;

/*
 * Cette classe donne les informations d'un noeud du réseau de capteurs
 */
public class NodeInfo extends ConnectionInfo implements NodeInfoI{
	private PositionI pos;
	private double range;
	private BCM4JavaEndPointDescriptorI portEntrantRequesting;
	public NodeInfo(String ID,BCM4JavaEndPointDescriptorI portEntrantP2P,BCM4JavaEndPointDescriptorI portEntrantRequesting,PositionI p,double range) {
		super(ID, portEntrantP2P);
		this.pos = p;
		this.range = range;
		this.portEntrantRequesting = portEntrantRequesting; 
	}

	@Override
	public PositionI nodePosition() {
		return pos;
	}

	@Override
	public double nodeRange() {
		return range;
	}

	@Override
	public EndPointDescriptorI p2pEndPointInfo() {
		return portEntrant;
	}
	public EndPointDescriptorI requestingEndPointInfo(){
		return portEntrantRequesting;
	}
	
	@Override
	public String toString() {
		return
				"Position: " + pos.toString() + "\n"
				+ /*"Range: " + range + "\n"*/
				 "ID: " + nodeIdentifier() + "\n";
	}
}
