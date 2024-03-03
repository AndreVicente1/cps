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
	protected static String uriNode1 = "URI_Node1";
	protected static String uriNode2 = "URI_Node2";
	
	protected static String uriNode3 = "URI_Node3";
	protected static String uriNode4 = "URI_Node4";
	
	/* Register Component URI */
	protected static String uriRegistration = "URI_Register";
    /* Client Outbound Port URI */
	protected static final String clientOutURI = "URI_ClientPortOut";
	
	/* Client Port for Client-Register connexion */
	protected static final String clientRegOutURI = "URI_Client_RegisterPortIn";
	
	/* Node Inbound Port URI for Client-Node connexion */
	protected static final String nodeInURI1 = "URI_Node-ClientPortIn1";
	protected static final String nodeInURI2 = "URI_Node-ClientPortIn2";
	protected static final String nodeInURI3 = "URI_Node-ClientPortIn3";
	protected static final String nodeInURI4 = "URI_Node-ClientPortIn4";
	
	/* Node Inbound Port URI for Node-Node connexion */
	protected static final String nodeInURI4Node1 = "URI_Node-NodePortIn1";
	protected static final String nodeInURI4Node2 = "URI_Node-NodePortIn2";
	protected static final String nodeInURI4Node3 = "URI_Node-NodePortIn3";
	protected static final String nodeInURI4Node4 = "URI_Node-NodePortIn4";
	
	/* Node Outbound Port URI for all directions */
	protected static final String uriOutPortNE1 = "URI_NodePortOutNE1";
	protected static final String uriOutPortNW1 = "URI_NodePortOutNW1";
	protected static final String uriOutPortSE1 = "URI_NodePortOutSE1";
	protected static final String uriOutPortSW1 = "URI_NodePortOutSW1";
	
	protected static final String uriOutPortNE2 = "URI_NodePortOutNE2";
	protected static final String uriOutPortNW2 = "URI_NodePortOutNW2";
	protected static final String uriOutPortSE2 = "URI_NodePortOutSE2";
	protected static final String uriOutPortSW2 = "URI_NodePortOutSW2";
	
	protected static final String uriOutPortNE3 = "URI_NodePortOutNE3";
	protected static final String uriOutPortNW3 = "URI_NodePortOutNW3";
	protected static final String uriOutPortSE3 = "URI_NodePortOutSE3";
	protected static final String uriOutPortSW3 = "URI_NodePortOutSW3";
	
	protected static final String uriOutPortNE4 = "URI_NodePortOutNE4";
	protected static final String uriOutPortNW4 = "URI_NodePortOutNW4";
	protected static final String uriOutPortSE4 = "URI_NodePortOutSE4";
	protected static final String uriOutPortSW4 = "URI_NodePortOutSW4";
	
	/* Node OutboundPort URI for Node-Register connexion */
	protected static final String uriOutPortNodeRegister1 = "URI_RegisterNode_RegisterPortOut1";
	protected static final String uriOutPortNodeRegister2 = "URI_RegisterNode_RegisterPortOut2";
	protected static final String uriOutPortNodeRegister3 = "URI_RegisterNode_RegisterPortOut3";
	protected static final String uriOutPortNodeRegister4 = "URI_RegisterNode_RegisterPortOut4";

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

        PositionI positionNode1 = new Position(0.,0.);
        PositionI positionNode2 = new Position(1.,1.);
        PositionI positionNode3 = new Position(-1,1.);
        /* Isolated node if range too low, change if needed */
        PositionI positionNode4 = new Position(4.,4.);
        
        SensorDataI sensorNode1 = new SensorData<Double>(30.0, uriNode1, "temperature");
        SensorDataI sensorNode2 = new SensorData<Double>(40.0, uriNode2, "temperature");
        SensorDataI sensorNode3 = new SensorData<Double>(40.0, uriNode3, "temperature");
        SensorDataI sensorNode4 = new SensorData<Double>(40.0, uriNode4, "temperature");
        
        SensorDataI sensorNode1f = new SensorData<Double>(2.0, uriNode1, "fumee");
        SensorDataI sensorNode2f = new SensorData<Double>(4.0, uriNode2, "fumee");
        SensorDataI sensorNode3f = new SensorData<Double>(6.0, uriNode3, "fumee");
        SensorDataI sensorNode4f = new SensorData<Double>(8.0, uriNode4, "fumee");
        
        ArrayList<SensorDataI> sensorNode1List = new ArrayList<SensorDataI>();
        ArrayList<SensorDataI> sensorNode2List = new ArrayList<SensorDataI>();
        ArrayList<SensorDataI> sensorNode3List = new ArrayList<SensorDataI>();
        ArrayList<SensorDataI> sensorNode4List = new ArrayList<SensorDataI>();
        
        sensorNode1List.add(sensorNode1); sensorNode1List.add(sensorNode1f);
        sensorNode2List.add(sensorNode2); sensorNode2List.add(sensorNode2f);
        sensorNode3List.add(sensorNode3); sensorNode3List.add(sensorNode3f);
        sensorNode4List.add(sensorNode4); sensorNode4List.add(sensorNode4f);
    	
        uriClient = AbstractComponent.createComponent(Client.class.getCanonicalName(), new Object[]{1,1, uriClient, clientOutURI, clientRegOutURI});
        uriNode1 = AbstractComponent.createComponent(Node.class.getCanonicalName(), new Object[]{1,1, uriNode1, nodeInURI1,nodeInURI4Node1,uriOutPortNE1,uriOutPortNW1,uriOutPortSE1,uriOutPortSW1,uriOutPortNodeRegister1,positionNode1,2.,sensorNode1List});
        uriNode2 = AbstractComponent.createComponent(Node.class.getCanonicalName(), new Object[]{1,1, uriNode2, nodeInURI2,nodeInURI4Node2,uriOutPortNE2,uriOutPortNW2,uriOutPortSE2,uriOutPortSW2,uriOutPortNodeRegister2,positionNode2,1.5,sensorNode2List});
        
        uriNode3 = AbstractComponent.createComponent(Node.class.getCanonicalName(), new Object[]{1,1, uriNode3, nodeInURI3,nodeInURI4Node3,uriOutPortNE3,uriOutPortNW3,uriOutPortSE3,uriOutPortSW3,uriOutPortNodeRegister3,positionNode3,1.5,sensorNode3List});
        uriNode4 = AbstractComponent.createComponent(Node.class.getCanonicalName(), new Object[]{1,1, uriNode4, nodeInURI4,nodeInURI4Node4,uriOutPortNE4,uriOutPortNW4,uriOutPortSE4,uriOutPortSW4,uriOutPortNodeRegister4,positionNode4,10.,sensorNode4List});
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

