package connexion.requests;

import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI;

public class RequestContinuation extends Request implements RequestContinuationI{
	private static final long serialVersionUID = 1L;
	ExecutionStateI eState;

	public RequestContinuation(boolean isAsynchronous, String URI, QueryI query, ConnectionInfoI co, ExecutionStateI eState) {
		super(isAsynchronous, URI, query, co);
		this.eState = eState;
	}

	@Override
	public ExecutionStateI getExecutionState() {
		return eState;
	}

}
