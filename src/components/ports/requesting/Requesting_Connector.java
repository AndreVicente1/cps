package components.ports.requesting;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;


public class Requesting_Connector extends AbstractConnector implements RequestingCI{
  	
  	/**
     * @see fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI#execute(RequestI)
     */
	@Override
	public QueryResultI execute(RequestI request) throws Exception {
		return ((RequestingCI)this.offering).execute(request);
	}
	/**
     * @see fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI#executeAsync(RequestI)
     */
	@Override
	public void executeAsync(RequestI request) throws Exception {
		((RequestingCI)this.offering).executeAsync(request);
		
	}
	
	@Override
	public String toString() {
		return "Connector Client-Node";
	}
}
