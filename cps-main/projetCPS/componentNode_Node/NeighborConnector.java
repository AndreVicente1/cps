package componentNode_Node;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI;

public class NeighborConnector extends AbstractConnector implements SensorNodeP2PCI{

		@Override
		public void ask4Disconnection(NodeInfoI neighbour) throws Exception {
			// TODO Auto-generated method stub
			((SensorNodeP2PCI)this.offering).ask4Disconnection(neighbour);
		}

		@Override
		public void ask4Connection(NodeInfoI newNeighbour) throws Exception {
			// TODO Auto-generated method stub
			((SensorNodeP2PCI)this.offering).ask4Connection(newNeighbour);
		}

		@Override
		public QueryResultI execute(RequestContinuationI request) throws Exception {
			return ((SensorNodeP2PCI)this.offering).execute(request);
		}

		@Override
		public void executeAsync(RequestContinuationI requestContinuation) throws Exception {
			// TODO Auto-generated method stub
			
		}
	}
