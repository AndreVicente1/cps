package componentTest;

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
import connexion.SensorData;
import connexion.registre.Registration;

public class CVM extends AbstractCVM {
	
	/* Client and Node Components URI */
	protected static  String uriClient = "URI_Client";
	protected static  String uriNode1 = "URI_Node1";
	protected static  String uriNode2 = "URI_Node2";
	
	/* Register Component URI */
	protected static  String uriRegistration = "URI_Register";
    /* Client Outbound Port URI */
	protected static final String clientOutURI = "URI_ClientPortTestOut";
	
	/* Client and Register Ports for Client-Register connexion */
	protected static final String registerClInURI = "URI_Register_ClientPortIn";
	protected static final String clientRegOutURI = "URI_Client_RegisterPortIn";
	
	/* Node Inbound Port URI for Client-Node connexion */
	protected static final String nodeInURI1 = "URI_NodePortIn1";
	protected static final String nodeInURI2 = "URI_NodePortIn2";
	
	/* Node Inbound Port URI for Node-Node connexion */
	protected static final String nodeInURI4Node1 = "URI_Node-NodePortIn1";
	protected static final String nodeInURI4Node2 = "URI_Node-NodePortIn2";
	
	/* Node Outbound Port URI for all directions */
	protected static final String uriOutPortNE1 = "URI_NodePortOutNE1";
	protected static final String uriOutPortNW1 = "URI_NodePortOutNW1";
	protected static final String uriOutPortSE1 = "URI_NodePortOutSE1";
	protected static final String uriOutPortSW1 = "URI_NodePortOutSW1";
	
	protected static final String uriOutPortNE2 = "URI_NodePortOutNE2";
	protected static final String uriOutPortNW2 = "URI_NodePortOutNW2";
	protected static final String uriOutPortSE2 = "URI_NodePortOutSE2";
	protected static final String uriOutPortSW2 = "URI_NodePortOutSW2";
	
	/* Register INboundPort URI for Node-Register connexion */
	protected static final String uriInPortRegister="URI_RegisterPortIn";
	
	/* Node OutboundPort URI for Node-Register connexion */
	protected static final String uriOutPortNodeRegister1 = "URI_RegisterNode_RegisterPortOut1";
	protected static final String uriOutPortNodeRegister2 = "URI_RegisterNode_RegisterPortOut2";

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
        SensorDataI sensorNode1 = new SensorData<Double>(30.0, uriNode1, "temperature");
        SensorDataI sensorNode2 = new SensorData<Double>(40.0, uriNode2, "temperature");
        ArrayList<SensorDataI> sensorNode1List = new ArrayList<SensorDataI>();
        ArrayList<SensorDataI> sensorNode2List = new ArrayList<SensorDataI>();
        sensorNode1List.add(sensorNode1);
        sensorNode2List.add(sensorNode2);
    	uriClient = AbstractComponent.createComponent(Client.class.getCanonicalName(), new Object[]{1,1, uriClient, clientOutURI, clientRegOutURI, uriInPortRegister});
        uriNode1 = AbstractComponent.createComponent(Node.class.getCanonicalName(), new Object[]{1,1, uriNode1, nodeInURI1,nodeInURI4Node1,uriOutPortNE1,uriOutPortNW1,uriOutPortSE1,uriOutPortSW1,uriOutPortNodeRegister1,uriInPortRegister,positionNode1,99999999.0,sensorNode1List});
        uriNode2 = AbstractComponent.createComponent(Node.class.getCanonicalName(), new Object[]{1,1, uriNode2, nodeInURI2,nodeInURI4Node2,uriOutPortNE2,uriOutPortNW2,uriOutPortSE2,uriOutPortSW2,uriOutPortNodeRegister2,uriInPortRegister,positionNode2,99999999.0,sensorNode2List});
        uriRegistration = AbstractComponent.createComponent(Registration.class.getCanonicalName(), new Object[]{1,1, uriRegistration,uriInPortRegister, registerClInURI});
        
         
         
        // On peut ensuite les interconnecter statiquement par la méthode doPortConnection
        /*this.doPortConnection( //requête
                uriClient,
                clientOutURI,
                nodeInURI1,
                RequestConnector.class.getCanonicalName());
         */
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
        finaliseComponent(uriClient);
        finaliseComponent(uriNode1);
        finaliseComponent(uriNode2);
		finaliseComponent(uriRegistration);
        super.finalise();
    }

    /*@Override
    public void shutdown() throws Exception {
        //depublier les ports sortant
        localUnpublishPort(providerOut);
        localUnpublishPort(clientOut);
        localUnpublishPort(clientIn);
        localUnpublishPort(providerIn);
        super.shutdown();
    }*/
}

