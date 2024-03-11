package components.cvm;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.helpers.CVMDebugModes;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.utils.aclocks.ClocksServer;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import ast.position.Position;
import components.Client;
import components.Node;
import components.Registration;
import connexion.SensorData;

/**
 * Class for the CVM, main execution class
 */
public class CVM extends AbstractCVM {
	
	/* Global attributes */
	public static int nbNodes = 4; //à incrémenter selon nombre de noeuds
	public static final String uriInPortRegister="URI_RegisterPortIn"; /* Register InboundPort URI for Node-Register connexion */
	public static final String registerClInURI = "URI_Register_ClientPortIn"; /* Register Ports for Client-Register connexion */
	
	/* Client and Node Components URI */
	protected static String uriClient = "URI_Client";
	
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
        CVM c = new CVM();
        c.startStandardLifeCycle(10000L); // durée de 10 secondes
        Thread.sleep(10000L);
        System.exit(0);
    }

    public CVM() throws Exception {
    	super();
    }
    
    /**
     * Crée des composants noeuds avec une valeur de temperature et de fumee des senseurs aléatoires spécifiques, et la position aléatoire mais doit être positionnée en diagonale des autres noeuds  
     * @param nb le nombre de noeuds à créer
     * @return la liste des uri des noeuds
     */
    public ArrayList<String> createRandomNodes(int nb){
    	 ArrayList<String> nodes = new ArrayList<>();
         Random random = new Random();
         ArrayList<PositionI> usedPositions = new ArrayList<>();

         for (int i = 0; i < nb; i++) {
        	
        	 double temperatureValue = 20.0 + (40.0 - 20.0) * random.nextDouble(); // Température entre 20 et 40
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
             
             // Générer une position unique en diagonale
             PositionI pos;
             
             do {
                 // Assurez-vous que x et y peuvent avoir des signes indépendamment définis
                 int signX = random.nextBoolean() ? 1 : -1;
                 int signY = random.nextBoolean() ? 1 : -1;
                 pos = new Position(signX * i, signY * i);
             } while (usedPositions.contains(pos));
             usedPositions.add(pos);
           
             
             /*int xSign = (i % 2 == 0) ? 1 : -1; // Alterne entre positif et négatif pour x
             int ySign = (i % 4 < 2) ? 1 : -1; // Alterne indépendamment pour y
             PositionI pos = new Position(xSign * i, ySign * i); // Position en diagonale
             */
             
             //double range = random.nextDouble() * (10.0 - 1.0) + 1; // Portée entre 1 et 10
             double range = 10.0;
             
             //System.out.println("node URI: " + uriNode + " range = " + range + " sensor value: temp: " + temperatureValue + " fumee: " + smokeValue);
             String uri = null;
			 try {
				uri = AbstractComponent.createComponent(Node.class.getCanonicalName(), new Object[]{1, 1, uriNode, 
							nodeInURI, nodeInURI4Node, 
							uriOutPortNE, uriOutPortNW, uriOutPortSE, uriOutPortSW, 
							uriOutPortNodeRegister, 
							pos, range, sensors});
				nodes.add(uri);
			 } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			 }
             
             
         }
         
         return nodes;
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

        AbstractComponent.createComponent(
			ClocksServer.class.getCanonicalName(),
			new Object[]{
				TEST_CLOCK_URI, // URI attribuée à l’horloge
				unixEpochStartTimeInNanos, // moment du démarrage en temps réel Unix
				START_INSTANT, // instant de démarrage du scénario
				ACCELERATION_FACTOR}); // facteur d’acccélération

    	
        uriClient = AbstractComponent.createComponent(Client.class.getCanonicalName(), new Object[]{1,1, uriClient, clientOutURI, clientRegOutURI});

        ArrayList<String> uris = createRandomNodes(30);
        uriRegistration = AbstractComponent.createComponent(Registration.class.getCanonicalName(), new Object[]{1,1, uriRegistration,uriInPortRegister, registerClInURI});
        
        super.deploy();
    }

    /*@Override
    public void execute() throws Exception {
    	executeComponent(uriRegistration);
    	executeComponent(uriNode1);
        executeComponent(uriNode2);
    	executeComponent(uriClient);
        
        super.execute();
    }*/

    @Override
    public void finalise() throws Exception {
        //finilise des composants
        /*finaliseComponent(uriClient);
        finaliseComponent(uriNode1);
        finaliseComponent(uriNode2);
		finaliseComponent(uriRegistration);*/
        super.finalise();
    }
    
    @Override
    public void shutdown() throws Exception{
    	assert this.allFinalised();
    	super.shutdown();
    }

}

