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
import components.Config;
import components.Registration;
import components.builders.ClientBuilder;
import components.builders.NodeBuilder;
import connexion.geographical.GeographicalZone;
import connexion.requests.RequestBuilder;

/**
 * Class for the CVM with multiple requests sent
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
	
	Instant START_INSTANT = Instant.now();
	public static final long START_DELAY = 1000L;
	public final long unixEpochStartTimeInNanos = TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis() + START_DELAY);
	
    public static void main(String[] args) throws Exception{
        CVM_multirequests c = new CVM_multirequests();
        c.startStandardLifeCycle(60000L); // à augmenter si beaucoup de noeuds!
        Thread.sleep(10000L);
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
		
		/*RequestI request2 = RequestBuilder.createRequest(
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
			);*/
		
		// RANDOM REQUEST
		RequestI request2 = RequestBuilder.createRandomRequest(isAsync2, uri2);
		
		Map<String, List<RequestI>> allRequests = new HashMap<>();
		
		// Requêtes du client 1
		List<RequestI> requests1 = new ArrayList<>();
		requests1.add(request);
		
		// Requêtes du client 2
		List<RequestI> requests2 = new ArrayList<>();
		requests2.add(request2);
		
		allRequests.put("1", requests1);
		allRequests.put("2", requests2);
		
		Map<String, String> toSend_id = new HashMap<>();
		String nodeId_toSend = "";
		toSend_id.put("1", nodeId_toSend);
		toSend_id.put("2", nodeId_toSend);
		
		Map<String, GeographicalZoneI> zones = new HashMap<>();
		GeographicalZoneI zone = new GeographicalZone(-20,-1,1,20);
		GeographicalZoneI zone2 = new GeographicalZone(-10,0,0,10);
		zones.put("1", zone);
		zones.put("2", zone2);
		
		String plugin_client_uri = "plugin_client";
		
		long unixEpochStartTimeInNanos = TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis() + START_DELAY);
		
        AbstractComponent.createComponent(
			ClocksServer.class.getCanonicalName(),
			new Object[]{
				Config.TEST_CLOCK_URI, // URI attribuée à l’horloge
				unixEpochStartTimeInNanos, // moment du démarrage en temps réel Unix
				START_INSTANT, // instant de démarrage du scénario
				Config.ACCELERATION_FACTOR}); // facteur d’acccélération
        
        double range = 10.0;
        Map<String, Double> datas = new HashMap<>();
        datas.put("temperature", 10.0);
        datas.put("fumee", 5.0);
        datas.put("humidite", 22.0);
        String uriNode = "URI_Node";
        
        uriRegistration = AbstractComponent.createComponent(Registration.class.getCanonicalName(), new Object[]{1,1, uriRegistration,uriInPortRegister, registerClInURI, Config.NTHREADS_REGISTER_POOL, Config.NTHREADS_LOOKUP_POOL});
        
        NodeBuilder.createFixedNodes(nbNodes, range, Config.NTHREADS_NEW_REQ_POOL, Config.NTHREADS_CONT_REQ_POOL, Config.NTHREADS_SYNC_REQ_POOL,Config.NTHREADS_CONNECTION_POOL, datas, uriNode, 0);
        
        ClientBuilder.build(2, 1, 1, uriClient, clientAsynchronousIn, allRequests, nbRequests, toSend_id, zones, plugin_client_uri, 0);
        
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

