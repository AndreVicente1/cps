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
	
	public Request(boolean isAsynchronous, String URI, QueryI query, ConnectionInfoI c) {
		this.isAsynchronous = isAsynchronous;
		this.URI = URI;
		this.q = query;
		this.co = c;
	}
	
	@Override
	public String requestURI() {
		return URI;
	}

	@Override
	public QueryI getQueryCode() { //to execute the request
		return q;
	}

	@Override
	public boolean isAsynchronous() {
		return isAsynchronous;
	}

	@Override
	public ConnectionInfoI clientConnectionInfo() {
		
		return co;
	}

}
