package componentTest;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import interpreter.Interpreter;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import connexion.BCM4JavaEndPointDescriptor;
import connexion.ExecutionState;
import connexion.ProcessingNode;
import connexion.Request;
import connexion.RequestContinuation;
import connexion.NodeInfo;
import ast.query.BQuery;
import ast.query.GQuery;
import ast.query.Query;
import componentNode_Node.InboundPortProviderNode;
import componentNode_Node.NeighborConnector;
import componentNode_Node.OutboundPortProvider;
import componentNode_Register.OutboundPortNodeRegister;
import componentNode_Register.RegisterConnector;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import fr.sorbonne_u.utils.aclocks.AcceleratedClock;
import fr.sorbonne_u.utils.aclocks.ClocksServer;
import fr.sorbonne_u.utils.aclocks.ClocksServerConnector;
import fr.sorbonne_u.utils.aclocks.ClocksServerOutboundPort;
import fr.sorbonne_u.cps.sensor_network.registry.interfaces.RegistrationCI;

@RequiredInterfaces(required= {SensorNodeP2PCI.class, RegistrationCI.class})
@OfferedInterfaces(offered={RequestingCI.class, SensorNodeP2PCI.class})

public class Node extends AbstractComponent  {
	// Node attributes
	private NodeInfoI nodeInfo;
    private ArrayList<SensorDataI> sensors;

    private Set<NodeInfoI> neighbours = new HashSet<NodeInfoI>();
    // Component attributes
    InboundPortProvider inp;
    
    Interpreter interpreter;
	ExecutionStateI exec;
	
	/* Inbound Port Node-Node */
	InboundPortProviderNode inpn;
	
	/* Outbound Ports Node-Node */
	HashMap<Direction, OutboundPortProvider> portsMap = new HashMap<>();
    OutboundPortProvider outpNE;
    OutboundPortProvider outpNW;
    OutboundPortProvider outpSE;
    OutboundPortProvider outpSW;
    
    /* Outbound Port Node-Register */
    OutboundPortNodeRegister outpr;

    protected ClocksServerOutboundPort clockOP;

    //rajouter nodeinfo, neighbors, senseurs
    public Node(int nbThreads, int nbSchedulableThreads,
                       String uri,
                       String uriInPort,
                       String uriInPortNode,
                       String uriOutPortNE, String uriOutPortNW, String uriOutPortSE, String uriOutPortSW,
                       String uriOutPortNodeRegister,
                       PositionI p,
                       double range,
                       ArrayList<SensorDataI> sensors) throws Exception {
        super(uri, nbThreads, nbSchedulableThreads);
        
        this.nodeInfo = new NodeInfo(uri,new BCM4JavaEndPointDescriptor(uriInPortNode,SensorNodeP2PCI.class),new BCM4JavaEndPointDescriptor(uriInPort,RequestingCI.class),p,range);
        this.sensors = sensors;
        
        this.inp = new InboundPortProvider(this, uriInPort);
        inp.publishPort();
        
        this.inpn = new InboundPortProviderNode(this, uriInPortNode);
        inpn.publishPort();
        
        this.outpNE = new OutboundPortProvider(this, uriOutPortNE);
        outpNE.publishPort();
        this.outpNW = new OutboundPortProvider(this, uriOutPortNW);
        outpNW.publishPort();
        this.outpSE = new OutboundPortProvider(this, uriOutPortSE);
        outpSE.publishPort();
        this.outpSW = new OutboundPortProvider(this, uriOutPortSW);
        outpSW.publishPort();
   
        portsMap.put(Direction.SE, outpSE);
        portsMap.put(Direction.SW, outpSW);
        portsMap.put(Direction.NE, outpNE);
        portsMap.put(Direction.NW, outpNW);
        
        this.outpr = new OutboundPortNodeRegister(this,uriOutPortNodeRegister);
        outpr.publishPort();
        
        interpreter = new Interpreter();
        
        
        this.addOfferedInterface(SensorNodeP2PCI.class);
        this.addOfferedInterface(RequestingCI.class);
        
        this.addRequiredInterface(SensorNodeP2PCI.class);
        this.addRequiredInterface(RegistrationCI.class);
        
        this.getTracer().setTitle("Node Component") ;
        this.getTracer().setRelativePosition(2, 1);
        this.toggleTracing();
    }
    
    @Override
    public void start() throws ComponentStartException {
        this.logMessage("starting node component " + nodeInfo.nodeIdentifier());

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

        super.start();
    }
    
  //méthode auxiliaire pour rechercher un senseur
    private SensorDataI searchSensor(String sensorIdentifier) {
    	for (SensorDataI s : sensors) {
    		if (s.getSensorIdentifier().equals(sensorIdentifier)) {
    			//System.out.println("Trouvé et ajouté " + sensorIdentifier);
    			return s;
    		}
    	}
    	System.out.println("SENSOR NULL");
    	return null;
    }
    
    /**
     * Retourne les informations du noeud NodeInfoI courrant
     * @return le NodeInfoI du noeud
     */
    public NodeInfoI getNodeInfo() {
        return nodeInfo;
    }

    /**
     * Renvoie l'ensemble des voisins du noeud, donné par le registre lorsqu'il se connecte
     * @return l'ensemble des voisins du noeud
     */
    public Set<NodeInfoI> getNeighbours() {
        return neighbours;
    }

    /**
     * Renvoie la valeur du senseur par son identifiant en paramètre
     * @param l'identifiant du noeud dont on cherche la valeur
     * @return la valeur du senseur
     */
    public SensorDataI getSensorData(String sensorIdentifier) {
        return searchSensor(sensorIdentifier);
    }
    
    /**
     * Cette méthode a été rajouté pour récupérer tous les noeuds
     * @return la liste de touts les senseurs du noeud courrant
     */
    public ArrayList<SensorDataI> getAllSensors(){
    	return sensors;
    }
    
    /**
     * Method used by the register to set the node's neighbors
     * @param the neighbors set to be given to the node
     */
    public void setNeighbors(Set<NodeInfoI> neighbors) {
    	this.neighbours = neighbors;
    }
    
    
    // ==== Components methods ====
    public QueryResultI treatRequest(RequestI request) throws Exception{
    	if (request instanceof RequestContinuation)
    		return treatRequest((RequestContinuationI) request);
    	else if (request instanceof Request)
    		return treatRequest((Request) request);
    	else throw new Exception ("Request is not of type Request or RequestContinuation");
    }
    
    /**
     * Renvoie le résultat de la requête **sans continuation** après l'avoir traité
     * @param la requête RequestI
     * @return le résultat de la requête QueryResultI
     */
    public QueryResultI treatRequest(Request request) throws Exception{
    	this.logMessage("Reception d'une requete ");
		if (request == null) throw new Exception("Request is null");
    	
		if (!request.isAsynchronous()) { //on traite seulement synchrone pour l'instant
			Object res = null;
			Query query = (Query) request.getQueryCode(); //on récupere la query pour l'exécuter
			ProcessingNodeI pn = new ProcessingNode(this);
			
			if (query instanceof BQuery) {
				if (exec == null) exec = new ExecutionState(this,pn, true);
					
				res = interpreter.visit((BQuery) query, exec);
			} else if (query instanceof GQuery) {
				if (exec == null) exec = new ExecutionState(this,pn, false);
				
				res = interpreter.visit((GQuery) query, exec);
			} else throw new Exception("La requête n'est pas une query reconnue");

			return (QueryResultI) res;
		}
		else {
			//TODO partie 3 et 4
			return null;
		}
    }
    
    /**
     * Renvoie le résultat de la requête **avec continuation** après l'avoir traité
     * @param la requête RequestContinuationI
     * @return le résultat de la requête QueryResultI
     */
    public QueryResultI treatRequest(RequestContinuationI request) throws Exception{
		this.logMessage("Reception d'une requete continuation");
    	if (request == null) throw new Exception("Request is null in treatRequest");
    	if (!request.isAsynchronous()){
    		ExecutionStateI exec = request.getExecutionState();
    		ProcessingNodeI pn = new ProcessingNode(this);
    		
    		//traite localement la requete
    		//System.out.println("-----------EXECUTION SUR LE NODE: ------------\n processingNode " + pn.getNodeIdentifier()
    		// + " exec state: " + exec);
	    	Object res = null;
			Query query = (Query) request.getQueryCode();
			if (exec != null) {
				ExecutionState e = (ExecutionState) exec;
				System.out.println("===========visited nodes ==============");
				e.printVisited();
				System.out.println("=========================");
				//exécution sur un noeud déjà visité
				if (e.getVisitedNodes().contains(pn.getNodeIdentifier())) {
					System.out.println("===========node deja visité==============");
					return null;
				}
				
				exec.updateProcessingNode(pn);
			}
			if (query instanceof BQuery) {
				if (exec == null) exec = new ExecutionState(this,pn, true);
					
				res = interpreter.visit((BQuery) query, exec);
				if (res != null)
					this.logMessage("Resulat du traitement local: "+ res.toString());
			} else if (query instanceof GQuery) {
				if (exec == null) exec = new ExecutionState(this,pn, false);
				
				res = interpreter.visit((GQuery) query, exec);
				if (res != null)
					this.logMessage("Resulat du traitement local: "+ res.toString());
			} else throw new Exception("La requête n'est pas une query reconnue");
			
			ExecutionState ex = (ExecutionState) exec;
			ex.addVisitedNode(pn.getNodeIdentifier());    
			//System.out.println("!!!!!!!!UPDATED PN: " + exec.get)	
	        QueryResultI result = (QueryResultI) res;
	       	//System.out.println("result local!!! = =======" + result);
	        
	       	if (!exec.noMoreHops() || exec.withinMaximalDistance(pn.getPosition())) {
	            exec.incrementHops(); /* cela ne devrait pas poser de probleme même si la requête est en FCont */
	        	//poursuite du calcul
	        	RequestContinuationI reqCont = new RequestContinuation(
	        			request.isAsynchronous(), 
	        			request.requestURI(), 
	        			request.getQueryCode(),
	        			exec);
	        			
	        	//on determine les voisins sur lesquels on envoie la continuation
	        	Set<Direction> dirs = exec.getDirections();
	        	
	        	for (Direction dir: dirs){
					
					for (NodeInfoI voisin : neighbours) {
						
						if (voisin.nodePosition().directionFrom(this.nodeInfo.nodePosition())==dir && !ex.getVisitedNodes().contains(voisin.nodeIdentifier())){
							System.out.println("=================VOISIN==================");
							System.out.println(voisin);
							System.out.println("===================================");
							//FIXME: probleme sur la connexion du port, l'exécution du noeud est bon mais la connexion ne marche pas
							//System.out.println("voisin trouvé "+dir);
							//System.out.println("execution sur le noeud: " + nodeInfo);
							//propagation de la requête
							QueryResultI tmp = null;
							OutboundPortProvider port = portsMap.get(dir);
							
							if (port.connected()) { 
							    System.out.println("!!!!!!!!!!!!!!!!!!execution suivant à " + dir + " avec port " + port.getPortURI());
							    this.logMessage("Propagation de la continuation vers le "+ dir + " avec port " + port.getPortURI());
							    tmp = port.execute(reqCont);
							} else {
							    throw new Exception("Port pas connecté, la continuation se termine");
							}

							if (tmp != null) {
								//System.out.println("QueryResult donné:\n" + result);
								if (result != null) {
			    					result.gatheredSensorsValues().addAll(tmp.gatheredSensorsValues());
									result.positiveSensorNodes().addAll(tmp.positiveSensorNodes());
								}
							} else {
								throw new Exception("execute error from ports");
							}
						 }
					}
					
				}
	        }
	        return result; // fin de la continuation
	    } else {
	    	//TODO partie 3 et 4
			return null;
	    }
    }

    
    @Override
    public void execute() throws Exception{
        super.execute();

        this.doPortConnection(
			outpr.getPortURI(),
			CVM.uriInPortRegister,
			RegisterConnector.class.getCanonicalName());
		
        neighbours = this.outpr.register(this.nodeInfo); 
        
        try {
            AcceleratedClock ac = clockOP.getClock(CVM.TEST_CLOCK_URI);
            this.doPortDisconnection(clockOP.getPortURI());
            clockOP.unpublishPort();
            clockOP.destroyPort();
            
            ac.waitUntilStart();

            char numero = this.nodeInfo.nodeIdentifier().charAt(this.nodeInfo.nodeIdentifier().length()-1);
            int i = Integer.valueOf(numero); 
            Instant instant = CVM.START_INSTANT.plusSeconds(i + 40);
            long d = ac.nanoDelayUntilInstant(instant);

            this.scheduleTask(
                    o -> { 					
                        this.logMessage("executing node component " + nodeInfo.nodeIdentifier()) ;
                        this.runTask(
                                new AbstractComponent.AbstractTask() {
                                    @Override
                                    public void run() {
                                        try {
                                            for (NodeInfoI voisin : neighbours) {
                                                ((Node)this.getTaskOwner()).ask4Connection(voisin);
                                                Direction direction = voisin.nodePosition().directionFrom(nodeInfo.nodePosition()); 
                                                OutboundPortProvider p = portsMap.get(direction);
                                                p.ask4Connection(nodeInfo);
                                            }

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
    public void shutdown() {
    	this.logMessage("Shutting down");
        try {
			super.shutdown();
		} catch (ComponentShutdownException e) {
			e.printStackTrace();
		}
        try {
			inp.unpublishPort();
			inpn.unpublishPort();
	        outpSE.unpublishPort();
	        outpSW.unpublishPort();
	        outpNE.unpublishPort();
	        outpNW.unpublishPort();
	        outpr.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
       
    }
    
    /* 
     * Node-Node connection methods 
     */
    
    /**
     * Connect the initial node to the target neighbor node, if the target neighbor is already connected to
     * another node, the target neighbor will disconnect from it before connecting to the initial node
     * @param neighbor the node information of the target neighbor 
     */
    public void ask4Connection(NodeInfoI neighbor) throws Exception {
		this.logMessage("Ask4Connection - Connexion souhaité vers "+neighbor.nodeIdentifier());
    	System.out.println("traitement du noeud: " + nodeInfo.nodeIdentifier()+" connection souhaité vers "+neighbor.nodeIdentifier() );
    	Direction d = neighbor.nodePosition().directionFrom(nodeInfo.nodePosition());
	    OutboundPortProvider port = portsMap.get(d);
     
	    NodeInfoI z = null;
	    for (NodeInfoI voisin : neighbours) {
			Direction directionVoisin = voisin.nodePosition().directionFrom(nodeInfo.nodePosition()); //ptete
			if (directionVoisin.equals(d)){
				System.out.println("direction égale " + directionVoisin+ "\nvoisin:" + voisin);
				z = voisin;
				break;
			}
			
		}
		
		if (z!=null && !z.nodeIdentifier().equals(neighbor.nodeIdentifier())) {
			 System.out.println("==========voisin z deconnecté pour faire de la place============");
			 System.out.println("Node: "+this.nodeInfo.nodeIdentifier()+" deconnecte "+z.nodeIdentifier()+" pour faire place à " + neighbor.nodeIdentifier());
			 System.out.println("================================================================");
			 this.disconnect(z);
			 neighbours.remove(z);
		}
	 	 
	
	    if (port != null ) {
			this.logMessage("Ask4Connection - Connexion effectué depuis port =  "+port.getPortURI()+ " vers "+ neighbor.p2pEndPointInfo().toString());
			System.out.println("connexion effectué : " + port.getPortURI()+" --> "+neighbor.p2pEndPointInfo().toString());
	        this.doPortConnection(
	            port.getPortURI(),
	            neighbor.p2pEndPointInfo().toString(),
	            NeighborConnector.class.getCanonicalName());
	        
	        if (!neighbours.contains(neighbor)) neighbours.add(neighbor);
	        
	    } 
    }
    
    /**
     * Disconnect the initial node to the neighbour node
     * @param neighbour the target neighbour node
     * @throws Exception
     */
    public void disconnect(NodeInfoI neighbour) throws Exception {

        Direction directionFrom = neighbour.nodePosition().directionFrom(this.nodeInfo.nodePosition());
        if (portsMap.get(directionFrom).connected()) {
            this.doPortDisconnection(portsMap.get(directionFrom).getPortURI());
        }
    }
    
}
