package components.cvm;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.helpers.CVMDebugModes;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.EndPointDescriptorI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestResultCI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI;
import fr.sorbonne_u.utils.aclocks.ClocksServer;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import ast.base.ABase;
import ast.bexp.AndBExp;
import ast.bexp.CExpBExp;
import ast.cexp.GEqExp;
import ast.cont.DCont;
import ast.cont.FCont;
import ast.cont.ICont;
import ast.dirs.FDirs;
import ast.dirs.RDirs;
import ast.gather.FGather;
import ast.gather.Gather;
import ast.position.Position;
import ast.query.BQuery;
import ast.query.GQuery;
import ast.rand.CRand;
import ast.rand.SRand;
import components.Client;
import connexion.ConnectionInfo;
import components.Node;
import components.Registration;
import connexion.EndPointDescriptor;
import connexion.SensorData;
import connexion.requests.RequestBuilder;
import connexion.requests.RequestContinuation;

/**
 * Class for the CVM, main execution class
 */
public class CVM_multirequests extends AbstractCVM {
	
	/* Global attributes */
	public static int nbNodes = 15; //à modifier selon nombre de noeuds voulu
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
	protected static final long START_DELAY = 3000L;
	public static final double ACCELERATION_FACTOR = 60.0;
	long unixEpochStartTimeInNanos = TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis() + START_DELAY);

	
    public static void main(String[] args) throws Exception{
        CVM_multirequests c = new CVM_multirequests();
        c.startStandardLifeCycle(100000L); // à augmenter si beaucoup de noeuds!
        Thread.sleep(100000L);
        System.exit(0);
    }

    public CVM_multirequests() throws Exception {
    	super();
    }
    
    /**
     * Crée des composants noeuds avec une valeur de temperature et de fumee des senseurs aléatoires spécifiques, et la position aléatoire mais doit être positionnée en diagonale des autres noeuds  
     * @param nb le nombre de noeuds à créer
     * @return la liste des uri des noeuds
     */
    private ArrayList<String> createRandomNodes(int nb, double range){
    	 ArrayList<String> nodes = new ArrayList<>();
         Random random = new Random();
         ArrayList<PositionI> usedPositions = new ArrayList<>();

         for (int i = 0; i < nb; i++) {
        	
        	 double temperatureValue = 20.0 + (40.0 - 20.0) * random.nextDouble(); // Température entre 20 et 40
        	 System.out.format("%.1f", temperatureValue);
        	 double smokeValue = 1.0 + (10.0 - 1.0) * random.nextDouble(); // Fumée entre 1 et 10
        	 
             SensorDataI sensorTemperature = new SensorData<Double>(temperatureValue, "URI_Node" + i, "temperature");
             SensorDataI sensorSmoke = new SensorData<Double>(smokeValue, "URI_Node" + i, "fumee");

             ArrayList<SensorDataI> sensors = new ArrayList<>();
             sensors.add(sensorTemperature);
             sensors.add(sensorSmoke);

             String uriOutPortNE = "URI_NodePortOutNE" + i;
             String uriOutPortNW = "URI_NodePortOutNW" + i;
             String uriOutPortSE = "URI_NodePortOutSE" + i;
             String uriOutPortSW = "URI_NodePortOutSW" + i;
             
             String nodeInURI = "URI_Node-ClientPortIn" + i;
             
             String nodeInURI4Node = "URI_Node-NodePortIn" + i;
             
             String uriOutPortNodeRegister = "URI_RegisterNode_RegisterPortOut" + i;
             String uriNode = "URI_Node" + i;
             
             String uriOutPortNodeClient = "URI_Node-ClientPortOut" + i;
             // position unique en diagonale
             PositionI pos;
             
             do {
                 int signX = random.nextBoolean() ? 1 : -1;
                 int signY = random.nextBoolean() ? 1 : -1;
                 pos = new Position(signX * i, signY * i);
             } while (usedPositions.contains(pos));
             usedPositions.add(pos);
             
             //double range = random.nextDouble() * (10.0 - 1.0) + 1; // Portée entre 1 et 10
             //double range = 10.0;
             
             //System.out.println("node URI: " + uriNode + " range = " + range + " sensor value: temp: " + temperatureValue + " fumee: " + smokeValue);
             String uri = null;
			 try {
				uri = AbstractComponent.createComponent(Node.class.getCanonicalName(), new Object[]{1, 1, uriNode, 
							nodeInURI, nodeInURI4Node,uriOutPortNodeClient, 
							uriOutPortNE, uriOutPortNW, uriOutPortSE, uriOutPortSW, 
							uriOutPortNodeRegister, 
							pos, range, sensors});
				nodes.add(uri);
			 } catch (Exception e) {
				e.printStackTrace();
			 }
             
             
         }
         
         return nodes;
     }
    
    private ArrayList<String> createFixedNodes(int nb, double range) {
        ArrayList<String> nodes = new ArrayList<>();
        ArrayList<PositionI> positions = createDiagonalPositions(nb);

        for (int i = 0; i < nb; i++) {
            double temperatureValue = 25.0; // Température fixe pour le test
            double smokeValue = 5.0; // Fumée fixe pour le test

            SensorDataI sensorTemperature = new SensorData<Double>(temperatureValue, "URI_Node" + i, "temperature");
            SensorDataI sensorSmoke = new SensorData<Double>(smokeValue, "URI_Node" + i, "fumee");

            ArrayList<SensorDataI> sensors = new ArrayList<>();
            sensors.add(sensorTemperature);
            sensors.add(sensorSmoke);

            String uriOutPortNE = "URI_NodePortOutNE" + i;
            String uriOutPortNW = "URI_NodePortOutNW" + i;
            String uriOutPortSE = "URI_NodePortOutSE" + i;
            String uriOutPortSW = "URI_NodePortOutSW" + i;
            
            String nodeInURI = "URI_Node-ClientPortIn" + i;
            String nodeInURI4Node = "URI_Node-NodePortIn" + i;
            String uriOutPortNodeRegister = "URI_RegisterNode_RegisterPortOut" + i;
            String uriNode = "URI_Node" + i;
            String uriOutPortNodeClient = "URI_Node-ClientPortOut" + i;

            String uri = null;
            try {
                uri = AbstractComponent.createComponent(Node.class.getCanonicalName(), new Object[]{
                    1, 1, uriNode, 
                    nodeInURI, nodeInURI4Node, uriOutPortNodeClient, 
                    uriOutPortNE, uriOutPortNW, uriOutPortSE, uriOutPortSW, 
                    uriOutPortNodeRegister, 
                    positions.get(i), range, sensors
                });
                nodes.add(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return nodes;
    }

    private ArrayList<PositionI> createDiagonalPositions(int nb) {
        ArrayList<PositionI> positions = new ArrayList<>();
        for (int i = 0; i < nb; i++) {
            int x = (i % 2) == 0 ? i : -i;
            int y = (i % 2) == 0 ? i : -i;
            positions.add(new Position(x, y));
        }
        return positions;
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

    	/* Gquery test */
        /*double maxDistance = 20.0;
        ICont fcont = new FCont(new ABase(new Position(3.0, 5.0)), maxDistance);
        Gather fgather = new FGather("fumee");
        QueryI gquery = new GQuery(fgather,fcont);
        
    	/* BQuery test 
		BQuery bquery = 
		new BQuery(
				new AndBExp(
					new CExpBExp(
						new GEqExp(
								new SRand("temperature"), //temperature >= 50.0?
								new CRand(10.0))),
					new CExpBExp(
						new GEqExp(
								new SRand("fumee"), //fumee >= 3.0
								new CRand(1.0)))),
				//new DCont(new RDirs(Direction.SE, new FDirs(Direction.NE)), 10)
				//new ECont()
				fcont
				);*/
		
    	// cont
		ABase base = new ABase(new Position(3.0, 5.0)); 
		double maxDistance = 20.0;
		RDirs dirs = new RDirs(Direction.SE, new FDirs(Direction.NE)); 
		int maxJumps = 10;
		
		// query
		boolean isAsync = true;
		String uri = "URI_requete";
		String queryType = "BQuery";
		String gatherType = "FGather";
		String contType = "FCont";
		String sensorId = "";
		String clientInboundPortURI = clientAsynchronousIn;
		String bexpType = "And";
		String cexpType = "GEq";
		SRand rand1 = new SRand("temperature");
		CRand rand2 = new CRand(10.0);
		SRand rand1Fumee = new SRand("fumee");
		CRand rand2Fumee = new CRand(1.0);
		
		/* Modifier le query en parametre de la requete selon le test */
		/*EndPointDescriptorI endpoint = new EndPointDescriptor(clientAsynchronousIn, RequestResultCI.class);
		ConnectionInfoI co = new ConnectionInfo(uriClient, endpoint);
		RequestContinuationI request = new RequestContinuation(true,"URI_requete", (QueryI) bquery, co, null);*/
		
		RequestContinuationI request = RequestBuilder.createRequestContinuation(
			    isAsync, 
			    uri, 
			    clientInboundPortURI,
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
			    maxJumps,
			    null // execState toujours à null
			);
		
        AbstractComponent.createComponent(
			ClocksServer.class.getCanonicalName(),
			new Object[]{
				TEST_CLOCK_URI, // URI attribuée à l’horloge
				unixEpochStartTimeInNanos, // moment du démarrage en temps réel Unix
				START_INSTANT, // instant de démarrage du scénario
				ACCELERATION_FACTOR}); // facteur d’acccélération

    	
        uriClient = AbstractComponent.createComponent(Client.class.getCanonicalName(), new Object[]{1,1, uriClient, clientOutURI, clientRegOutURI,clientAsynchronousIn, request, 2});
      
        //ArrayList<String> uris = createRandomNodes(nbNodes, 10.0);
        ArrayList<String> uris = createFixedNodes(nbNodes, 10.0);
        
        uriRegistration = AbstractComponent.createComponent(Registration.class.getCanonicalName(), new Object[]{1,1, uriRegistration,uriInPortRegister, registerClInURI});
        
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

