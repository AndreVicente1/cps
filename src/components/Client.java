package components;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;
import components.plugins.Plugin_Client;
import components.plugins.Plugin_Client_test;
import connexion.ConnectionInfo;
import connexion.EndPointDescriptor;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import fr.sorbonne_u.utils.aclocks.ClocksServerCI;

@RequiredInterfaces(required = {ClocksServerCI.class})
/**
 * Class for the Client Component.
 * The Client is meant to send request to a node through its ports
 */
public class Client extends AbstractComponent {
	/** The outbound port to connect to the clock */
    protected ClocksServerOutboundPort clockOP;
    /** The clock */
    protected AcceleratedClock ac;
    
    /** Plugin */
	protected Plugin_Client plugin;
	//protected Plugin_Client_test plugin;
	protected String plugin_uri = "plugin_client";
    
	/**
	 * Constructs a Client component that sends requests to nodes through designated ports
	 * It initializes necessary plugins to manage client-specific behaviors and setups connection details
	 *
	 * @param nbThreads The number of threads available for the client's general processing
	 * @param nbSchedulableThreads The number of threads that can be scheduled for specific client tasks
	 * @param uriClient The URI identifier for this client
	 * @param inPort The inbound port of the client, used to complete de connection information of the client
	 * @param requests The list of requests that this client will send
	 * @param nbRequests The number of requests to manage
	 * @param nodeId The identifier of the node to which requests are sent
	 * @param geo The geographical zone in which the client is looking at
	 * @param plugin_uri The URI for the plugin managing client behaviors
	 * @throws Exception 
	 */
    protected Client(int nbThreads, int nbSchedulableThreads,
                     String uriClient,
                     String inPort,
                     List<RequestI> requests,
                     int nbRequests,
                     String nodeId,
                     GeographicalZoneI geo,
                     String plugin_uri
                     ) throws Exception{

        super(uriClient, nbThreads, nbSchedulableThreads);
        
        ConnectionInfoI co = new ConnectionInfo(uriClient, new EndPointDescriptor(inPort));
        
        plugin = new Plugin_Client(requests,
					            nbRequests,
					            nodeId,
					            inPort,
				                geo,
				                plugin_uri,
				                co);
        
        /* Performance tests
         * plugin = new Plugin_Client_test(requests,
	            nbRequests,
	            nodeId,
	            inPort,
                geo,
                plugin_uri,
                co);*/
        
        this.plugin.setPluginURI(plugin_uri);
        
        this.getTracer().setTitle("Client Component") ;
        this.getTracer().setRelativePosition(2,2);
        this.toggleTracing();
        
    }

    /**
     * @see fr.sorbonne_u.components.AbstractComponent#start()
     */
    @Override
    public void start() throws ComponentStartException {
        this.logMessage("starting client component.") ;

        // Horloge accélérée
        try {
            clockOP = new ClocksServerOutboundPort(this);
            clockOP.publishPort();
            this.doPortConnection(
                    clockOP.getPortURI(),
                    ClocksServer.STANDARD_INBOUNDPORT_URI,
                    ClocksServerConnector.class.getCanonicalName());
            this.ac = clockOP.getClock(Config.TEST_CLOCK_URI);
            this.doPortDisconnection(clockOP.getPortURI());
            clockOP.unpublishPort();
            clockOP.destroyPort();
            
            this.installPlugin(plugin);
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.start() ;
    }
	
    /**
     * @see fr.sorbonne_u.components.AbstractComponent#execute()
     */
    @Override
    public void execute() throws Exception {
        super.execute();
        this.logMessage("Client executing");
        try {
            ac.waitUntilStart();
            
            Instant instant = ac.getStartInstant().plusSeconds(Config.timeC);
            long d = ac.nanoDelayUntilInstant(instant);

            this.scheduleTask(
                    o -> { 
                        this.logMessage("executing client component ") ;
                        this.runTask(
                                new AbstractComponent.AbstractTask() {
                                    @Override
                                    public void run() {
                                        try {
                                        	this.getTaskOwner().logMessage("Plugin working");
                                        	plugin.setClock(ac);
                                        	plugin.sendRequest();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }) ;
                        

                    }, d, TimeUnit.NANOSECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @see fr.sorbonne_u.components.AbstractComponent#finalise()
     */
    @Override
    public void finalise() throws Exception {
    	this.logMessage("Finalising");
        plugin.finalise();
    }
    
    /**
     * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
     */
    @Override
    public void shutdown() throws ComponentShutdownException {
		this.logMessage("Shutting down");
		plugin.uninstall();
    }
    
    /**
     * @see components.plugin.Plugin_Client#acceptRequestResult
     */
    public void acceptRequestResult(String Uri, QueryResultI qr) {
    	plugin.acceptRequestResult(Uri, qr);
    }
   
}
