package components.cvm;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;

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

/**
 * Class for the CVM, main execution class
 */
public class CVM_multirequests extends AbstractCVM {
	
	/* Global attributes */
	public static int nbNodes = 50; //à modifier selon nombre de noeuds voulu
	public static final String uriInPortRegister="URI_RegisterPortIn"; /* Register InboundPort URI for Node-Register connexion */
	public static final String registerClInURI = "URI_Register_ClientPortIn"; /* Register Ports for Client-Register connexion */
	
	/* Client and Node Components URI */
	protected static String uriClient = "URI_Client";
	protected static final String clientAsynchronousIn = "URI_Client_AsynchronousIn";
	
	/* Register Component URI */
	protected static String uriRegistration = "URI_Register";
	
    /* Client Outbound Port URI */
	protected static final String clientOutURI = "URI_ClientPortOut";
	
	/* Client Port for Client-Register connexion */
	protected static final String clientRegOutURI = "URI_Client_RegisterPortIn";
	
	
    public static final String TEST_CLOCK_URI = "test-clock";
	public static final Instant START_INSTANT = Instant.parse("2024-01-31T09:00:00.00Z");
	protected static final long REQUEST_RHYTHM = 10L;
	
	protected static final long START_DELAY = 3000L;
	public static final double ACCELERATION_FACTOR = 60.0;
	long unixEpochStartTimeInNanos = TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis() + START_DELAY);

	/** Number of threads for each pool */
	/* Node */
	private static final int NTHREADS_NEW_REQ_POOL = 4;
	private static final int NTHREADS_CONT_REQ_POOL = 4;
	private static final int NTHREADS_SYNC_REQ_POOL = 4;
	private static final int NTHREADS_CONNECTION_POOL = 4;
	/* Register */
	private static final int NTHREADS_REGISTER_POOL = 5;
	private static final int NTHREADS_LOOKUP_POOL = 5;
	
    public static void main(String[] args) throws Exception{
        CVM_multirequests c = new CVM_multirequests();
        c.startStandardLifeCycle(600000L); // à augmenter si beaucoup de noeuds!
        Thread.sleep(100000L);
        System.exit(0);
    }

    public CVM_multirequests() throws Exception {
    	super();
    }
    
    //a redefinir pour creer interconexion statique entre composant
    @Override
    public void deploy() throws Exception {
    	
    	assert    !this.deploymentDone() ;
        /*AbstractCVM.DEBUG_MODE.add(CVMDebugModes.LIFE_CYCLE);
        AbstractCVM.DEBUG_MODE.add(CVMDebugModes.INTERFACES);
        AbstractCVM.DEBUG_MODE.add(CVMDebugModes.PORTS);
        AbstractCVM.DEBUG_MODE.add(CVMDebugModes.CONNECTING);
        AbstractCVM.DEBUG_MODE.add(CVMDebugModes.CALLING);
        AbstractCVM.DEBUG_MODE.add(CVMDebugModes.EXECUTOR_SERVICES);*/
		
    	// cont
		ABase base = new ABase(new Position(3.0, 5.0)); 
		double maxDistance = 2000.0;
		RDirs dirs = new RDirs(Direction.SE, new FDirs(Direction.NE)); 
		int maxJumps = 10;
		
		// query
		boolean isAsync = true;
		String uri = "URI_requete";
		String queryType = "BQuery"; //BQuery
		String gatherType = "FGather";
		String contType = "FCont";
		String sensorId = "";
		String bexpType = "And";
		String cexpType = "GEq";
		SRand rand1 = new SRand("temperature");
		CRand rand2 = new CRand(10.0);
		SRand rand1Fumee = new SRand("fumee");
		CRand rand2Fumee = new CRand(1.0);
		
		// client parameters
        int nbRequests = 1;
		RequestI request = RequestBuilder.createRequest(
			    isAsync, 
			    uri, 
			    queryType,
			    gatherType,
			    contType,
			    sensorId,
			    null,
			    bexpType,
			    RequestBuilder.createBExp("CExp", null, null, RequestBuilder.createCExp(cexpType, rand1, rand2)),
			    RequestBuilder.createBExp("CExp", null, null, RequestBuilder.createCExp(cexpType, rand1Fumee, rand2Fumee)),
			    cexpType,
			    rand1,
			    rand2,
			    base,
			    maxDistance,
			    dirs,
			    maxJumps
			);
		
		// query 2
		boolean isAsync2 = true;
		String uri2 = "URI_requete2";
		String queryType2 = "GQuery"; //BQuery
		String gatherType2 = "FGather";
		String contType2 = "FCont";
		String sensorId2 = "humidite";
		String bexpType2 = "And";
		String cexpType2 = "GEq";
		SRand rand12 = new SRand("temperature");
		CRand rand22 = new CRand(10.0);
		SRand rand1humidite = new SRand("humidite");
		CRand rand2humidite = new CRand(20.0);
		
		RequestI request2 = RequestBuilder.createRequest(
			    isAsync2, 
			    uri2, 
			    queryType2,
			    gatherType2,
			    contType2,
			    sensorId2,
			    null, // next gather
			    bexpType2,
			    RequestBuilder.createBExp("CExp", null, null, RequestBuilder.createCExp(cexpType, rand12, rand22)),
			    RequestBuilder.createBExp("CExp", null, null, RequestBuilder.createCExp(cexpType, rand1humidite, rand2humidite)),
			    cexpType2,
			    rand12,
			    rand22,
			    base,
			    maxDistance,
			    dirs,
			    maxJumps
			);
		RequestI request3 = RequestBuilder.createRequest(
			    isAsync2, 
			    "URI_requete3", 
			    queryType2,
			    gatherType2,
			    contType2,
			    sensorId2,
			    null, // next gather
			    bexpType2,
			    RequestBuilder.createBExp("CExp", null, null, RequestBuilder.createCExp(cexpType, rand12, rand22)),
			    RequestBuilder.createBExp("CExp", null, null, RequestBuilder.createCExp(cexpType, rand1humidite, rand2humidite)),
			    cexpType2,
			    rand12,
			    rand22,
			    base,
			    maxDistance,
			    dirs,
			    maxJumps
			);
		RequestI request4 = RequestBuilder.createRequest(
			    isAsync2, 
			    "URI_requete4", 
			    queryType2,
			    gatherType2,
			    contType2,
			    sensorId2,
			    null, // next gather
			    bexpType2,
			    RequestBuilder.createBExp("CExp", null, null, RequestBuilder.createCExp(cexpType, rand12, rand22)),
			    RequestBuilder.createBExp("CExp", null, null, RequestBuilder.createCExp(cexpType, rand1humidite, rand2humidite)),
			    cexpType2,
			    rand12,
			    rand22,
			    base,
			    maxDistance,
			    dirs,
			    maxJumps
			);
		RequestI request5 = RequestBuilder.createRequest(
			    isAsync2, 
			    "URI_requete5", 
			    queryType2,
			    gatherType2,
			    contType2,
			    sensorId2,
			    null, // next gather
			    bexpType2,
			    RequestBuilder.createBExp("CExp", null, null, RequestBuilder.createCExp(cexpType, rand12, rand22)),
			    RequestBuilder.createBExp("CExp", null, null, RequestBuilder.createCExp(cexpType, rand1humidite, rand2humidite)),
			    cexpType2,
			    rand12,
			    rand22,
			    base,
			    maxDistance,
			    dirs,
			    maxJumps
			);
		
		List<RequestI> requests3 = new ArrayList<>();
		//requests3.add(request3);
		List<RequestI> requests4 = new ArrayList<>();
		//requests4.add(request4);
		List<RequestI> requests5 = new ArrayList<>();
		//requests5.add(request5);
		
		Map<String, List<RequestI>> allRequests = new HashMap<>();
		
		// Requêtes du client 1
		List<RequestI> requests1 = new ArrayList<>();
		//requests1.add(request);
		
		// Requêtes du client 2
		List<RequestI> requests2 = new ArrayList<>();
		//requests2.add(request2);
	
		allRequests.put("1", requests1);
		allRequests.put("2", requests2);
		allRequests.put("3",requests3);
		allRequests.put("4",requests4);
		allRequests.put("5",requests5);
		Map<String, String> toSend_id = new HashMap<>();
		String nodeId_toSend = "";
		toSend_id.put("1", nodeId_toSend);
		toSend_id.put("2", nodeId_toSend);
		toSend_id.put("3", nodeId_toSend);
		toSend_id.put("4", nodeId_toSend);
		toSend_id.put("5", nodeId_toSend);
		
		Map<String, GeographicalZoneI> zones = new HashMap<>();
		GeographicalZoneI zone = new GeographicalZone(-20,-1,1,20);
		GeographicalZoneI zone2 = new GeographicalZone(-10,0,0,10);
		zones.put("1", zone2);
		zones.put("2", zone2);
		zones.put("3", zone2);
		zones.put("4", zone2);
		zones.put("5", zone2);
		
		String plugin_client_uri = "plugin_client";
		
        AbstractComponent.createComponent(
			ClocksServer.class.getCanonicalName(),
			new Object[]{
				TEST_CLOCK_URI, // URI attribuée à l’horloge
				unixEpochStartTimeInNanos, // moment du démarrage en temps réel Unix
				START_INSTANT, // instant de démarrage du scénario
				ACCELERATION_FACTOR}); // facteur d’acccélération
    	
        ClientBuilder.build(5, 1, 1, uriClient, clientAsynchronousIn, allRequests, nbRequests, toSend_id, zones, plugin_client_uri,REQUEST_RHYTHM);
        
        double range = 10.0;
        Map<String, Double> datas = new HashMap<>();
        datas.put("temperature", 10.0);
        datas.put("fumee", 5.0);
        datas.put("humidite", 22.0);
        String uriNode = "URI_Node";
        NodeBuilder.createFixedNodes(nbNodes, range, NTHREADS_NEW_REQ_POOL, NTHREADS_CONT_REQ_POOL, NTHREADS_SYNC_REQ_POOL,NTHREADS_CONNECTION_POOL, datas, uriNode);
        
        uriRegistration = AbstractComponent.createComponent(Registration.class.getCanonicalName(), new Object[]{1,1, uriRegistration,uriInPortRegister, registerClInURI, NTHREADS_REGISTER_POOL, NTHREADS_LOOKUP_POOL});
        
        super.deploy();
    }

    @Override
    public void finalise() throws Exception {
        super.finalise();
    }
    
    @Override
    public void shutdown() throws Exception{
    	assert this.allFinalised();
    	super.shutdown();
    }

}

