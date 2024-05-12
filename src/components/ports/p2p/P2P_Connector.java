package components.ports.p2p;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI;

public class P2P_Connector extends AbstractConnector implements SensorNodeP2PCI{

		/**
	     * @see fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI#ask4Disconnection(NodeInfoI)
	     */
		@Override
		public void ask4Disconnection(NodeInfoI neighbour) throws Exception {
			((SensorNodeP2PCI)this.offering).ask4Disconnection(neighbour);
		}
		
		/**
	     * @see fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI#ask4Connection(NodeInfoI)
	     */
		@Override
		public void ask4Connection(NodeInfoI newNeighbour) throws Exception {
			((SensorNodeP2PCI)this.offering).ask4Connection(newNeighbour);
		}
		
		/**
	     * @see fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI#execute(RequestContinuationI)
	     */
		@Override
		public QueryResultI execute(RequestContinuationI request) throws Exception {
			return ((SensorNodeP2PCI)this.offering).execute(request);
		}
		
		/**
	     * @see fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI#executeAsync(RequestContinuationI)
	     */
		@Override
		public void executeAsync(RequestContinuationI requestContinuation) throws Exception {
			((SensorNodeP2PCI)this.offering).executeAsync(requestContinuation);
		}
		
		@Override
		public String toString() {
			return "Connector Node-Node ";
		}
	}
