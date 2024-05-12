package connexion.requests;

import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI;

/**
 * This class represents a request in the process of being treated.
 * The request continuation is sent by a node to another node to propagate the original request.
 * It contains a new attribute execution state to follow the current state of the request
 */
public class RequestContinuation extends Request implements RequestContinuationI{
	private static final long serialVersionUID = 1L;
	/** The execution state of the request */
	ExecutionStateI eState;

	/**
	 * Creates a new instance of a continuous request
	 * @param isAsynchronous specifies if the request should be processed asynchronously
     * @param URI the unique identifier of the request
     * @param query the query associated with this request, implementing QueryI
     * @param c the connection information for the client making the request
	 * @param eState the execution state of the request being processed
	 */
	public RequestContinuation(boolean isAsynchronous, String URI, QueryI query, ConnectionInfoI co, ExecutionStateI eState) {
		super(isAsynchronous, URI, query, co);
		this.eState = eState;
	}

	/**
	 * @see fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI#getExecutionState()
	 */
	@Override
	public ExecutionStateI getExecutionState() {
		return eState;
	}

}
