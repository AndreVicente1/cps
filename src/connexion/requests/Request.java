package connexion.requests;

import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI;


public class Request implements RequestI{
	
	private static final long serialVersionUID = 1L;
	private ConnectionInfoI co;
	public String URI;
	private QueryI q;
	private boolean isAsynchronous;
	
	 /**
     * Constructs a new Request with specified parameters.
     *
     * @param isAsynchronous specifies if the request should be processed asynchronously
     * @param URI the unique identifier of the request
     * @param query the query associated with this request, implementing {@code QueryI}
     * @param c the connection information for the client making the request
     */
	public Request(boolean isAsynchronous, String URI, QueryI query, ConnectionInfoI c) {
		this.isAsynchronous = isAsynchronous;
		this.URI = URI;
		this.q = query;
		this.co = c;
	}
	/**
     * @see fr.sorbonne_u.cps.sensor_network.interfaces.RequestI#requestURI()
     */
	@Override
	public String requestURI() {
		return URI;
	}
	/**
     * @see fr.sorbonne_u.cps.sensor_network.interfaces.RequestI#getQueryCode()
     */
	@Override
	public QueryI getQueryCode() { //to execute the request
		return q;
	}
	/**
     * @see fr.sorbonne_u.cps.sensor_network.interfaces.RequestI#isAsynchronous()
     */
	@Override
	public boolean isAsynchronous() {
		return isAsynchronous;
	}
	/**
     * @see fr.sorbonne_u.cps.sensor_network.interfaces.RequestI#clientConnectionInfo()
     */
	@Override
	public ConnectionInfoI clientConnectionInfo() {
		
		return co;
	}

	public void setConnectionInfo(ConnectionInfoI co) {
		this.co = co;
	}
}
