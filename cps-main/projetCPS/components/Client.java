package components;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import fr.sorbonne_u.cps.sensor_network.interfaces.ConnectionInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import connexion.NodeInfo;
import connexion.Request;
import connexion.RequestContinuation;
import connexion.registre.GeographicalZone;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;

import java.time.Instant;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import ast.base.ABase;
import ast.bexp.AndBExp;
import ast.bexp.CExpBExp;
import ast.cexp.GEqExp;
import ast.cont.DCont;
import ast.cont.ECont;
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
import components.client_node.*;
import components.client_register.LookupConnector;
import components.client_register.OutboundPortClientRegister;
import components.cvm.CVM;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.LookupCI;

@RequiredInterfaces(required={RequestingCI.class, LookupCI.class})
@OfferedInterfaces(offered= {RequestingCI.class})

/**
 * Class for the Client Component
 */
public class Client extends AbstractComponent {
    OutboundPortClient outc; //le port sortant agit comme un RequestingCI
    OutboundPortClientRegister outcreg; //le port pour le registre
    
    QueryResultI result;

    protected ClocksServerOutboundPort clockOP;
    
    protected Client(int nbThreads, int nbSchedulableThreads,
                     String uriClient,
                     String uriOutPort,
                     String uriOutPortClientRegister) throws Exception{

        super(uriClient, nbThreads, nbSchedulableThreads);
        this.outc = new OutboundPortClient( this, uriOutPort);
        outc.publishPort();
        
        this.outcreg = new OutboundPortClientRegister(this, uriOutPortClientRegister);
        outcreg.publishPort();
       
        
        this.addOfferedInterface(RequestingCI.class);
        this.addRequiredInterface(RequestingCI.class);
        this.addRequiredInterface(LookupCI.class);

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

        /* Connexion au registre */
    	this.doPortConnection(
    			outcreg.getPortURI(),
    			CVM.registerClInURI, //peut être mettre en global pour que tous puisse le récupérer au lieu attribut?
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
                                        	((Client)this.getTaskOwner()).logMessage("Looking for a node");
	                                        //NodeInfo uriNode =(NodeInfo) outcreg.findByIdentifier("URI_Node1");
                                        	//GeographicalZoneI geo = new  GeographicalZone(-0.1, -0.1, 0.1, 0.1); //node 1 
                                        	GeographicalZoneI zone = new GeographicalZone(-1, 1, -1, 1); // node 3
	                                        Set<ConnectionInfoI> nodesInZone = outcreg.findByZone(zone);
	                                        NodeInfoI firstNode = null;
	                                        
	                                        //On prend le premier noeud
	                                        if (!nodesInZone.isEmpty()) {
									        	Iterator<ConnectionInfoI> iterator = nodesInZone.iterator();
									            firstNode = (NodeInfoI) iterator.next(); 
									        } else {
									        	throw new Exception("Le HashSet est vide.");
									        }
									     
	                                        NodeInfo uriNode = (NodeInfo) firstNode;
	                                        ((Client)this.getTaskOwner()).logMessage("Node trouvé dans la zone geographique : "+uriNode.nodeIdentifier());
	                                        
	                                        
	                                        ((Client)this.getTaskOwner()).logMessage("Connecting to its port");
											((Client)this.getTaskOwner()).doPortConnection( //requête
                                                    outc.getPortURI(),
                                                    uriNode.requestingEndPointInfo().toString(), 
                                                    RequestConnector.class.getCanonicalName());
											
											/* Gquery test */
                                            double maxDistance = 20.0;
                                            ICont fcont = new FCont(new ABase(new Position(3.0, 5.0)), maxDistance);
                                            Gather fgather = new FGather("fumee");
                                            QueryI gquery = new GQuery(fgather,fcont);
                                            
                                            
                                            /* BQuery test */
                                    		BQuery bquery = 
                                    		new BQuery(
                                    				new AndBExp(
                                    					new CExpBExp(
                                    						new GEqExp(
                                    								new SRand("temperature"), //temperature >= 50.0?
                                    								new CRand(30.0))),
                                    					new CExpBExp(
                                    						new GEqExp(
                                    								new SRand("fumee"), //fumee >= 3.0
                                    								new CRand(2.0)))),
                                    				new DCont(new RDirs(Direction.SE, new FDirs(Direction.NE)), 1)
                                    				//new ECont()
                                    				//fcont
                                    				);
                                    		
                                    		/* Modifier le query en parametre de la requete selon le test */
                                    		RequestContinuation request = new RequestContinuation(false,"URI_requete", (QueryI) bquery, null);
                                            
                                    		((Client)this.getTaskOwner()).logMessage("Sending request");
                                            result = outc.execute(request);
                                            
                                            System.out.println("======================result====================");
                                            printResult();
                                            ((Client)this.getTaskOwner()).logMessage("Result received:\n"+result.toString());
                                            System.out.println("===================================================");

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
    public void finalise() {
       
    	this.logMessage("Finalising");
        try {
			super.finalise();
			
			outc.unpublishPort();
			outcreg.unpublishPort();
			
		} catch (ComponentShutdownException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    
    public void printResult() {
    	System.out.println(result);
    }
}
