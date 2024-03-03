package componentTest;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import connexion.Request;
import connexion.RequestContinuation;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import ast.base.ABase;
import ast.cont.ECont;
import ast.cont.FCont;
import ast.cont.ICont;
import ast.gather.FGather;
import ast.gather.Gather;
import ast.position.Position;
import ast.query.GQuery;
import componentClient_Register.LookupConnector;
import componentClient_Register.OutboundPortClientRegister;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;

@RequiredInterfaces(required={RequestingCI.class, LookupCI.class})
@OfferedInterfaces(offered= {RequestingCI.class})

public class Client extends AbstractComponent {
    OutboundPortClient outc; //le port sortant agit comme un RequestingCI
    OutboundPortClientRegister outcr; //le port pour le registre
    private String uriInPortRegister;
    
    QueryResultI result;

    protected ClocksServerOutboundPort clockOP;
    
    protected Client(int nbThreads, int nbSchedulableThreads,
                     String uriClient,
                     String uriOutPort,
                     String uriOutPortClientRegister,
                     String uriInPortRegister) throws Exception{

        super(uriClient, nbThreads, nbSchedulableThreads);
        this.outc = new OutboundPortClient( this, uriOutPort);
        outc.publishPort();
        
        this.outcr = new OutboundPortClientRegister(this, uriOutPortClientRegister);
        outcr.publishPort();
        
        this.uriInPortRegister = uriInPortRegister;
        
        this.addOfferedInterface(RequestingCI.class);
        this.addRequiredInterface(RequestingCI.class);
        this.addRequiredInterface(LookupCI.class);

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

        /* Connexion au registre */
    	this.doPortConnection(
    			outcr.getPortURI(),
    			uriInPortRegister, //peut être mettre en global pour que tous puisse le récupérer au lieu attribut?
    			LookupConnector.class.getCanonicalName());

        // Horloge accélérée
        try {

            AcceleratedClock ac = clockOP.getClock(CVM.TEST_CLOCK_URI);
            this.doPortDisconnection(clockOP.getPortURI());
            clockOP.unpublishPort();
            clockOP.destroyPort();
            // toujours faire waitUntilStart avant d’utiliser l’horloge pour
            // calculer des moments et instants
            ac.waitUntilStart();

            Instant instant = CVM.START_INSTANT.plusSeconds(100);
            long d = ac.nanoDelayUntilInstant(instant); // délai en nanosecondes

            this.scheduleTask(
                    o -> { 

                        this.logMessage("executing client component.") ;
                        this.runTask(
                                new AbstractComponent.AbstractTask() {
                                    @Override
                                    public void run() {
                                        try {
                                        	ConnectionInfoI uriNode = outcr.findByIdentifier("nodetest1");
                                        	
                                        	((Client)this.getTaskOwner()).doPortConnection( //requête
                                                    outc.getPortURI(),
                                                    uriNode.endPointInfo().toString(),
                                                    RequestConnector.class.getCanonicalName());
                                        	
                                            double maxDistance = 20.0;
                                            ICont fcont = new FCont(new ABase(new Position(3.0, 5.0)), maxDistance);
                                            Gather fgather = new FGather("temperature");
                                            QueryI gquery = new GQuery(fgather,fcont);

                                            RequestContinuation request = new RequestContinuation(false,"URI_requete", (QueryI) gquery, null);
                                            result = outc.execute(request);
                                            
                                            printResult();

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }) ;

                    }, d, TimeUnit.NANOSECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        /*double maxDistance = 20.0;
        ICont fcont = new FCont(new ABase(new Position(3.0, 5.0)), maxDistance);
        Gather fgather = new FGather("temperature");
        QueryI gquery = new GQuery(fgather,fcont);

        RequestContinuation request = new RequestContinuation(false,"URI_requete", (QueryI) gquery, null);
        result = this.outc.execute(request);
        
        printResult();*/
    }

    @Override
    public void shutdown() {
        try {
			super.shutdown();
		} catch (ComponentShutdownException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        try {
			outc.unpublishPort();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //outc.destroyPort();
    }
    
    public void printResult() {
    	System.out.println(result);
    }
}
