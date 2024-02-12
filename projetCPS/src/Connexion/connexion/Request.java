package Connexion.connexion;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI;


public class Request implements RequestI{
	//p11
	private ConnectionInfoI co;
	public static String URI;
	private QueryI q;
	private boolean isAsynchronous;
	
	public Request(boolean isAsynchronous, String URI, QueryI query) {
		this.isAsynchronous = isAsynchronous;
		this.URI = URI;
		this.q = query;
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
