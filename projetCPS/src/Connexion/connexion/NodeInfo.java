package Connexion.connexion;

import fr.sorbonne_u.cps.sensor_network.interfaces.BCM4JavaEndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.EndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;

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
	
}
