package components.ports.requestResult;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestResultCI;

public class RequestResult_Connector extends AbstractConnector implements RequestResultCI{
	
	/**
     * @see fr.sorbonne_u.cps.sensor_network.interfaces.RequestResultCI#acceptRequestResult(String,QueryResultI)
     */
	@Override
	public void acceptRequestResult(String requestURI, QueryResultI result) throws Exception {
		((RequestResultCI)this.offering).acceptRequestResult(requestURI, result);
	}
	
}
