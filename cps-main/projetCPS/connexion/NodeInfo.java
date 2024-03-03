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
	public NodeInfo(String ID,BCM4JavaEndPointDescriptorI portEntrant,PositionI p,double range) {
		super(ID, portEntrant);
		this.pos = p;
		this.range = range;
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
	
	@Override
	public String toString() {
		return
				"Position: " + pos.toString() + "\n"
				+ /*"Range: " + range + "\n"*/
				 "ID: " + nodeIdentifier() + "\n";
	}
}
