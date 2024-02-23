package connexion;

import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI;

public class RequestContinuation extends Request implements RequestContinuationI{
	ExecutionStateI eState;

	public RequestContinuation(boolean isAsynchronous, String URI, QueryI query, ExecutionStateI eState) {
		super(isAsynchronous, URI, query);
		this.eState = eState;
	}

	@Override
	public ExecutionStateI getExecutionState() {
		return eState;
	}

}
