package connexion.requests;

import java.time.Instant;

import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI;

/**
 * This class represents a request made by a client and is supposed to be sent to a Node.
 * If the query of this request is continuous, another instance of RequestI will be created,
 * and it will be a RequestContinuation
 */
public class Request implements RequestI{
	
	private static final long serialVersionUID = 1L;
	/** The connection information of the original sender, which is the Client */
	private ConnectionInfoI co;
	/** The URI of the request */
	public String URI;
	/** The query of the request */
	private QueryI q;
	/** A flag that specifies if the request should be processed asynchronously */
	private boolean isAsynchronous;
	
	private Instant tempsEnvoie;
	
	 /**
     * Constructs a new Request with specified parameters
     *
     * @param isAsynchronous specifies if the request should be processed asynchronously
     * @param URI the unique identifier of the request
     * @param query the query associated with this request, implementing QueryI
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
	
	public void setTempsEnvoie(Instant t) {
		this.tempsEnvoie=t;
	}
	public Instant getTempsEnvoie() {return tempsEnvoie;}
}
