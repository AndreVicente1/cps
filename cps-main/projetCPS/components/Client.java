package components;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestResultCI;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import components.cvm.CVM;
import components.plugins.Plugin_Client;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;

@RequiredInterfaces(required={RequestingCI.class, LookupCI.class})
@OfferedInterfaces(offered= {RequestingCI.class, RequestResultCI.class})

/**
 * Class for the Client Component
 */
public class Client extends AbstractComponent {
    protected ClocksServerOutboundPort clockOP;
    protected AcceleratedClock ac;
    
    /** Plugin*/
	protected Plugin_Client plugin;
	protected String plugin_uri = "plugin_client";
    
    protected Client(int nbThreads, int nbSchedulableThreads,
                     String uriClient,
                     String uriOutPort,
                     RequestI request,
                     int nbRequests,
                     String nodeId,
                     GeographicalZoneI geo,
                     String plugin_uri
                     ) throws Exception{

        super(uriClient, nbThreads, nbSchedulableThreads);
        
        plugin = new Plugin_Client(uriClient,
					            uriOutPort,
					            request,
					            nbRequests,
					            nodeId,
				                geo,
				                plugin_uri,
				                ac);
        
        this.plugin.setPluginURI(plugin_uri);
        this.installPlugin(plugin);
        
        this.getTracer().setTitle("Client Component") ;
        this.getTracer().setRelativePosition(2,2);
        this.toggleTracing();
        
    }

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
	
    @Override
    public void execute() throws Exception {
        super.execute();

        // Horloge accélérée
        try {

            this.ac = clockOP.getClock(CVM.TEST_CLOCK_URI);
            this.doPortDisconnection(clockOP.getPortURI());
            clockOP.unpublishPort();
            clockOP.destroyPort();
            // toujours faire waitUntilStart avant d’utiliser l’horloge pour
            // calculer des moments et instants
            plugin.setClock(ac);
            ac.waitUntilStart();

            Instant instant = CVM.START_INSTANT.plusSeconds(CVM.nbNodes * 60);
            long d = ac.nanoDelayUntilInstant(instant); // délai en nanosecondes

            this.scheduleTask(
                    o -> { 
                        this.logMessage("executing client component.") ;
                        this.runTask(
                                new AbstractComponent.AbstractTask() {
                                    @Override
                                    public void run() {
                                        try {
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

    @Override
    public void finalise() throws Exception {
    	this.logMessage("Finalising");
        super.finalise();
    }
    
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
