package components.cvm;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import ast.base.ABase;
import ast.dirs.FDirs;
import ast.dirs.RDirs;
import ast.position.Position;
import ast.rand.CRand;
import ast.rand.SRand;
import components.Registration;
import components.builders.ClientBuilder;
import components.builders.NodeBuilder;
import connexion.geographical.GeographicalZone;
import connexion.requests.RequestBuilder;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.exceptions.VerboseException;
import fr.sorbonne_u.utils.aclocks.ClocksServer;

public class DistributedCVM extends AbstractDistributedCVM {
	
	// JVM URIs
	protected static final String JVM_URI_1 = "JVM_URI_1";
	protected static final String JVM_URI_2 = "JVM_URI_2";
	protected static final String JVM_URI_3 = "JVM_URI_3";
	protected static final String JVM_URI_4 = "JVM_URI_4";
	protected static final String JVM_URI_5 = "JVM_URI_5";
	
	// Register
	public static final String uriInPortRegister = CVM.uriInPortRegister;
	public static final String registerClInURI = CVM.registerClInURI;
	protected static String uriRegistration = "URI_Register";
	
	// Clock
	public static final String TEST_CLOCK_URI = "test-clock";
	public static final Instant START_INSTANT = Instant.now();
	protected static final long START_DELAY = 3000L;
	public static final double ACCELERATION_FACTOR = 60.0;
	public static final long unixEpochStartTimeInNanos = TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis() + START_DELAY);
	
	/** Number of threads for each pool */
	/* Node */
	private static final int NTHREADS_NEW_REQ_POOL = 7;
	private static final int NTHREADS_CONT_REQ_POOL = 7;
	private static final int NTHREADS_SYNC_REQ_POOL = 7;
	private static final int NTHREADS_CONNECTION_POOL = 7;
	/* Register */
	private static final int NTHREADS_REGISTER_POOL = 5;
	private static final int NTHREADS_LOOKUP_POOL = 5;
	
	// Nodes parameters
	String uriNode = "URI_Node";
	public static int nbNodes = 50;
	double range = 10.0;
    Map<String, Double> datas = new HashMap<>();
    double tempValue = 10.0;
    double fumeeValue = 5.0;
    double humiditeValue = 22.0;
    
    // Client parameters
    int nbClient = 1;
    protected static String uriClient = "URI_Client";
	protected static String clientAsynchronousIn = "URI_Client_AsynchronousIn";
	protected static String clientOutURI = "URI_ClientPortOut";
	protected static String clientRegOutURI = "URI_Client_RegisterPortIn";
	
	// Each list of request, node ids to send and geographical zone for each client
	Map<String, List<RequestI>> allRequests = new HashMap<>();
	
	Map<String, String> toSend_id = new HashMap<>();
	String nodeId_toSend = "";
	
	Map<String, GeographicalZoneI> zones = new HashMap<>();
	GeographicalZoneI zone = new GeographicalZone(-1,-1,1,1);
	
	String plugin_client_uri = "plugin_client";
	
    /* Request */
	int nbRequest = 1; // Nombre de même requête à envoyer
    // query
	boolean isAsync = true;
	String uri = "URI_requete";
	String queryType = "GQuery";
	String gatherType = "FGather";
	String contType = "FCont";
	String sensorId = "";
	String bexpType = "And";
	String cexpType = "GEq";
	SRand rand1 = new SRand("temperature");
	CRand rand1Temp = new CRand(10.0);
	SRand rand2 = new SRand("fumee");
	CRand rand2Fumee = new CRand(1.0);
	SRand rand3 = new SRand("humidite");
	CRand rand3Humidite = new CRand(22.0);
	
	// continuation
	ABase base = new ABase(new Position(3.0, 5.0)); 
	double maxDistance = 2000.0;
	RDirs dirs = new RDirs(Direction.SE, new FDirs(Direction.NE)); 
	int maxJumps = 10;
			
	RequestI request = RequestBuilder.createRequest(
		    isAsync, 
		    uri, 
		    queryType,
		    gatherType,
		    contType,
		    sensorId,
		    null,
		    bexpType,
		    RequestBuilder.createBExp("CExp", null, null, RequestBuilder.createCExp(cexpType, rand1, rand1Temp)),
		    RequestBuilder.createBExp("CExp", null, null, RequestBuilder.createCExp(cexpType, rand3, rand3Humidite)),
		    cexpType,
		    rand1,
		    rand2,
		    base,
		    maxDistance,
		    dirs,
		    maxJumps
		);
	
	public DistributedCVM(String[] args) throws Exception {
		super(args);
		datas.put("temperature", tempValue);
	    datas.put("fumee", fumeeValue);
	    datas.put("humidite", humiditeValue);
	    
	    // Requêtes du client 1
		List<RequestI> requests1 = new ArrayList<>();
		requests1.add(request);
		
		allRequests.put("1", requests1);
		
		toSend_id.put("1", nodeId_toSend);
		//toSend_id.put("2", nodeId_toSend);
		
		zones.put("1", zone);
		
		datas.put("temperature", tempValue);
		datas.put("fumee", fumeeValue);
		datas.put("humidite", humiditeValue);
	}

	public static void main(String[] args) {
		try {
			VerboseException.VERBOSE = true;
			DistributedCVM dcvm = new DistributedCVM(args);
			dcvm.startStandardLifeCycle(30000L);
			Thread.sleep(10000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void instantiateAndPublish() throws Exception {
		String jvmURI = AbstractCVM.getThisJVMURI();
		if (jvmURI.equals(JVM_URI_1)) { // Compo 1
			AbstractComponent.createComponent(
					ClocksServer.class.getCanonicalName(),
					new Object[]{
						TEST_CLOCK_URI, // URI attribuée à l’horloge
						unixEpochStartTimeInNanos, // moment du démarrage en temps réel Unix
						START_INSTANT, // instant de démarrage du scénario
						ACCELERATION_FACTOR}); // facteur d’acccélération
			
			AbstractComponent.createComponent(Registration.class.getCanonicalName(), 
						new Object[]{1,1, uriRegistration,uriInPortRegister, registerClInURI, NTHREADS_REGISTER_POOL, NTHREADS_LOOKUP_POOL});
		
			uriNode = "JVM" + 1 + "-" + uriNode;
	        uriClient = "JVM" + 1 + "-" + uriClient;
	        clientAsynchronousIn = "JVM" + 1 + "-" + clientAsynchronousIn;
	    	clientOutURI = "JVM" + 1 + "-" +clientOutURI;
	    	clientRegOutURI = "JVM" + 1 + "-" +clientRegOutURI;
	        
	        NodeBuilder.createFixedNodes(nbNodes, range, NTHREADS_NEW_REQ_POOL, NTHREADS_CONT_REQ_POOL, NTHREADS_SYNC_REQ_POOL,NTHREADS_CONNECTION_POOL, datas, uriNode,1);
			
			ClientBuilder.build(nbClient, 1, 1, uriClient, clientAsynchronousIn, allRequests, nbRequest, toSend_id, zones, plugin_client_uri,1);
		
		} else if (jvmURI.equals(JVM_URI_2)) { // Compo 2
			uriNode = "JVM" + 2 + "-" + uriNode;
	        uriClient = "JVM" + 2 + "-" + uriClient;
	        clientAsynchronousIn = "JVM" + 2 + "-" + clientAsynchronousIn;
	    	clientOutURI = "JVM" + 2 + "-" +clientOutURI;
	    	clientRegOutURI = "JVM" + 2 + "-" +clientRegOutURI;
			NodeBuilder.createFixedNodes(nbNodes, range, NTHREADS_NEW_REQ_POOL, NTHREADS_CONT_REQ_POOL, NTHREADS_SYNC_REQ_POOL,NTHREADS_CONNECTION_POOL, datas, uriNode,2);
			
			ClientBuilder.build(nbClient, 1, 1, uriClient, clientAsynchronousIn, allRequests, nbRequest, toSend_id, zones, plugin_client_uri,2);
		
		} else if (jvmURI.equals(JVM_URI_3)) {
			uriNode = "JVM" + 3 + "-" + uriNode;
	        uriClient = "JVM" + 3 + "-" + uriClient;
	        clientAsynchronousIn = "JVM" + 3 + "-" + clientAsynchronousIn;
	    	clientOutURI = "JVM" + 3 + "-" +clientOutURI;
	    	clientRegOutURI = "JVM" + 3 + "-" +clientRegOutURI;
			NodeBuilder.createFixedNodes(nbNodes, range, NTHREADS_NEW_REQ_POOL, NTHREADS_CONT_REQ_POOL, NTHREADS_SYNC_REQ_POOL,NTHREADS_CONNECTION_POOL, datas, uriNode,3);
			
			ClientBuilder.build(nbClient, 1, 1, uriClient, clientAsynchronousIn, allRequests, nbRequest, toSend_id, zones, plugin_client_uri,3);
		
		} else if (jvmURI.equals(JVM_URI_4)) {
			uriNode = "JVM" + 4 + "-" + uriNode;
	        uriClient = "JVM" + 4 + "-" + uriClient;
	        clientAsynchronousIn = "JVM" + 4 + "-" + clientAsynchronousIn;
	    	clientOutURI = "JVM" + 4 + "-" +clientOutURI;
	    	clientRegOutURI = "JVM" + 4 + "-" +clientRegOutURI;
			NodeBuilder.createFixedNodes(nbNodes, range, NTHREADS_NEW_REQ_POOL, NTHREADS_CONT_REQ_POOL, NTHREADS_SYNC_REQ_POOL,NTHREADS_CONNECTION_POOL, datas, uriNode,4);
			
			ClientBuilder.build(nbClient, 1, 1, uriClient, clientAsynchronousIn, allRequests, nbRequest, toSend_id, zones, plugin_client_uri,4);
		
		} else if (jvmURI.equals(JVM_URI_5)) {
			uriNode = "JVM" + 5 + "-" + uriNode;
	        uriClient = "JVM" + 5 + "-" + uriClient;
	        clientAsynchronousIn = "JVM" + 5 + "-" + clientAsynchronousIn;
	    	clientOutURI = "JVM" + 5 + "-" +clientOutURI;
	    	clientRegOutURI = "JVM" + 5 + "-" +clientRegOutURI;
			NodeBuilder.createFixedNodes(nbNodes, range, NTHREADS_NEW_REQ_POOL, NTHREADS_CONT_REQ_POOL, NTHREADS_SYNC_REQ_POOL,NTHREADS_CONNECTION_POOL, datas, uriNode,5);
			
			ClientBuilder.build(nbClient, 1, 1, uriClient, clientAsynchronousIn, allRequests, nbRequest, toSend_id, zones, plugin_client_uri,5);
			
		} else {
			System.out.println("Unknown JVM URI: " + jvmURI);
		}
		super.instantiateAndPublish();
	}


}
