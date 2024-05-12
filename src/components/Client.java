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
import components.cvm.DistributedCVM;
import components.plugins.Plugin_Client;
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
 * The Client is meant to send request to a node through its ports,
 */
public class Client extends AbstractComponent {
	private ConnectionInfoI co;
	protected final String requestResultPort;
	
    protected ClocksServerOutboundPort clockOP;
    protected AcceleratedClock ac;
    
    /** Plugin*/
	protected Plugin_Client plugin;
	protected String plugin_uri = "plugin_client";
    
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
        
        this.co = new ConnectionInfo(uriClient, new EndPointDescriptor(inPort));
        this.requestResultPort = inPort;
        
        plugin = new Plugin_Client(requests,
					            nbRequests,
					            nodeId,
					            inPort,
				                geo,
				                plugin_uri,
				                co);
        
        this.plugin.setPluginURI(plugin_uri);
        this.installPlugin(plugin);
        
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

        // Horloge accélérée
        try {

            this.ac = clockOP.getClock(DistributedCVM.TEST_CLOCK_URI);
            this.doPortDisconnection(clockOP.getPortURI());
            clockOP.unpublishPort();
            clockOP.destroyPort();
            // toujours faire waitUntilStart avant d’utiliser l’horloge pour
            // calculer des moments et instants
           
            ac.waitUntilStart();

            Instant instant = DistributedCVM.START_INSTANT.plusSeconds(DistributedCVM.nbNodes * 60);
            long d = ac.nanoDelayUntilInstant(instant); // délai en nanosecondes

            this.scheduleTask(
                    o -> { 
                        this.logMessage("executing client component ") ;
                        this.runTask(
                                new AbstractComponent.AbstractTask() {
                                    @Override
                                    public void run() {
                                        try {
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
        super.finalise();
    }
    
    /**
     * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
     */
    @Override
    public void shutdown() throws ComponentShutdownException {
		this.logMessage("Shutting down");
		super.shutdown();
    }
    
    /**
     * @see components.plugin.Plugin_Client#acceptRequestResult
     */
    public void acceptRequestResult(String Uri, QueryResultI qr) {
    	plugin.acceptRequestResult(Uri, qr);
    }
   
}
