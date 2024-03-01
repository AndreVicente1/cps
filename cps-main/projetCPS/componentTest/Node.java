package componentTest;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import interpreter.Interpreter;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI;

import java.util.ArrayList;
import java.util.Set;

import connexion.BCM4JavaEndPointDescriptor;
import connexion.ExecutionState;
import connexion.ProcessingNode;
import connexion.QueryResult;
import connexion.RequestContinuation;
import connexion.Requesting;
import connexion.SensorData;
import connexion.NodeInfo;
import ast.exception.EvaluationException;
import ast.position.Position;
import ast.query.BQuery;
import ast.query.GQuery;
import ast.query.Query;
import componentNode_Node.InboundPortProviderNode;
import componentNode_Node.NeighborConnector;
import componentNode_Node.OutboundPortProvider;
import componentNode_Register.OutboundPortProviderRegister;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;

@RequiredInterfaces(required= {SensorNodeP2PCI.class})
@OfferedInterfaces(offered={RequestingCI.class, SensorNodeP2PCI.class})

public class Node extends AbstractComponent  {
	// Node attributes
	private NodeInfoI nodeInfo;
    private ArrayList<SensorDataI> sensors;

    private Set<NodeInfoI> neighbours;
	
    // Component attributes
    InboundPortProvider inp;
    
    Interpreter interpreter;
	ExecutionStateI exec;
	
	/* Inbound Port Node-Node */
	InboundPortProviderNode inpn;
	
	/* Outbound Ports Node-Node */
    OutboundPortProvider outpNE;
    OutboundPortProvider outpNW;
    OutboundPortProvider outpSE;
    OutboundPortProvider outpSW;
    
    /* Outbound Port Node-Register */
    OutboundPortProviderRegister outpr;

    //rajouter nodeinfo, neighbors, senseurs
    public Node(int nbThreads, int nbSchedulableThreads,
                       String uri,
                       String uriInPort,
                       String uriInPortNode,
                       String uriOutPortNE, String uriOutPortNW, String uriOutPortSE, String uriOutPortSW,
                       String uriOutPortRegister,
                       PositionI p,
                       double range,
                       ArrayList<SensorDataI> sensors) throws Exception {
        super(uri, nbThreads, nbSchedulableThreads);
        // TODO : fix l'instanciation de Nodeinfo, passer le port entrant à BCM4JavaEndPointDescriptorI
        this.nodeInfo = new NodeInfo(uri,new BCM4JavaEndPointDescriptor(uriInPortNode),p,range);
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
        
        this.outpr = new OutboundPortProviderRegister(this,uriOutPortRegister);
        outpr.publishPort();
        //données arbitraires, statique
        /*SensorDataI sensordata = new SensorData<Double>(30.0,"nodetest","temperature");
        ArrayList<SensorDataI> sensors = new ArrayList<>();
        sensors.add(sensordata);
        PositionI position = new Position(2.5, 3.0);
        currNode = new Node("nodetest", sensors,position,0.0, null);
        */
        
        //TODO: instancier le noeud et l'exec, et les enregistrer
        //dans le registre
        
        Interpreter interpreter = new Interpreter();
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
    
    /*
     * Retourne les informations du noeud NodeInfoI courrant
     * @return le NodeInfoI du noeud
     */
    public NodeInfoI getNodeInfo() {
        return nodeInfo;
    }

    /*
     * Renvoie l'ensemble des voisins du noeud, donné par le registre lorsqu'il se connecte
     * @return l'ensemble des voisins du noeud
     */
    public Set<NodeInfoI> getNeighbours() {
        return neighbours;
    }

    /*
     * Renvoie la valeur du senseur par son identifiant en paramètre
     * @param l'identifiant du noeud dont on cherche la valeur
     * @return la valeur du senseur
     */
    public SensorDataI getSensorData(String sensorIdentifier) {
        return searchSensor(sensorIdentifier);
    }
    
    /*
     * Cette méthode a été rajouté pour récupérer tous les noeuds
     */
    public ArrayList<SensorDataI> getAllSensors(){
    	return sensors;
    }
    
    /*
     * Method used by the register to set the node's neighbors
     * @param the neighbors set to be given to the node
     */
    public void setNeighbors(Set<NodeInfoI> neighbors) {
    	this.neighbours = neighbors;
    }
    
    
    // ==== Components methods ====
    
    /*
     * Renvoie le résultat de la requête **sans continuation** après l'avoir traité
     * @param la requête RequestI
     * @return le résultat de la requête QueryResultI
     */
    public QueryResultI treatRequest(RequestI request) throws Exception{
		if (request == null) throw new Exception("Request is null");
    	
		if (!request.isAsynchronous()) { //on traite seulement synchrone pour l'instant
			Object res = null;
			Query query = (Query) request.getQueryCode(); //on récupere la query pour l'exécuter
			ProcessingNodeI pn = new ProcessingNode(this);
			
			if (query instanceof BQuery) {
				if (exec != null) exec = new ExecutionState(this,pn, true);
					
				res = interpreter.visit((BQuery) query, exec);
			} else if (query instanceof GQuery) {
				if (exec != null) exec = new ExecutionState(this,pn, false);
				
				res = interpreter.visit((GQuery) query, exec);
			} else throw new Exception("La requête n'est pas une query reconnue");

			return (QueryResultI) res;
		}
		else {
			//TODO partie 3 et 4
			return null;
		}
    }
    
    /*
     * Renvoie le résultat de la requête **avec continuation** après l'avoir traité
     * @param la requête RequestContinuationI
     * @return le résultat de la requête QueryResultI
     */
    public QueryResultI treatRequest(RequestContinuationI request) throws Exception{
    	if (request == null) throw new Exception("Request is null in treatRequest");
    	if (!request.isAsynchronous()){
	    	ExecutionStateI exec = request.getExecutionState();
	        ProcessingNodeI pn = new ProcessingNode(this);
	        
	        exec.updateProcessingNode(pn);
	        //traite localement la requete
	        QueryResultI result = treatRequest(request);
	       	
	        if (!exec.noMoreHops() || exec.withinMaximalDistance(pn.getPosition())) {
	            exec.incrementHops(); /* cela ne devrait pas poser de probleme même si la requête est en FCont */
	        	//poursuite du calcul
	        	RequestContinuationI reqCont = new RequestContinuation(
	        			request.isAsynchronous(), 
	        			request.requestURI(), 
	        			request.getQueryCode(),
	        			exec);
	        			
	        	//on determine les voisins sur lesquels on envoie la continuation
				for (Direction dir: this.exec.getDirections()){
					for (NodeInfoI voisin : neighbours) {
						if (this.getNodeInfo().nodePosition().directionFrom(voisin.nodePosition())==dir){
							//connexion au voisin
							this.connect(voisin);
							//propagation de la requête
							QueryResultI tmp = null;
							switch(dir) {
	    						case SE:
	    							tmp = outpSE.execute(reqCont);
	    							break;
	    						case SW:
	    							tmp = outpSW.execute(reqCont);
	    							break;
	    						case NE:
	    							tmp = outpNE.execute(reqCont);
	    							break;
	    						case NW:
	    							tmp = outpNW.execute(reqCont);
	    							break;
	    						default:
	    							throw new Exception("direction not valid, la continuation se termine");
	    					}
							
							if (tmp != null) {
		    					result.gatheredSensorsValues().addAll(tmp.gatheredSensorsValues());
								result.positiveSensorNodes().addAll(tmp.positiveSensorNodes());
							} else
								throw new Exception("execute error from ports");
							this.disconnect(voisin);
				        	
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

        //TODO : étape 1: Se connecter au registre, appeler la méthode register
        this.outpr.register(this.nodeInfo);
        //étape2: Attribution des voisins
       	neighbours.add(this.outpr.findNewNeighbour(this.nodeInfo,Direction.SW));
       	neighbours.add(this.outpr.findNewNeighbour(this.nodeInfo,Direction.SE));
       	neighbours.add(this.outpr.findNewNeighbour(this.nodeInfo,Direction.NW));
       	neighbours.add(this.outpr.findNewNeighbour(this.nodeInfo,Direction.NE));
        //TODO : étape fin: disconnect du registre
    }

    @Override
    public void shutdown() throws Exception {
        super.shutdown();
        inp.unpublishPort();
        inpn.unpublishPort();
        outpSE.unpublishPort();
        outpSW.unpublishPort();
        outpNE.unpublishPort();
        outpNW.unpublishPort();
        outpr.unpublishPort();
    }
    
    /* 
     * Node-Node connection methods 
     */
    public void connect(NodeInfoI neighbor) throws Exception {
    	Direction d = nodeInfo.nodePosition().directionFrom(neighbor.nodePosition());
    	
    	switch(d) {
	    	case SE:
		    	this.doPortConnection(
		    			outpSE.getPortURI(),
		                neighbor.p2pEndPointInfo().toString(),
		                NeighborConnector.class.getCanonicalName());
	    	case SW:
	    		this.doPortConnection(
		    			outpSW.getPortURI(),
		                neighbor.p2pEndPointInfo().toString(),
		                NeighborConnector.class.getCanonicalName());
	    	
	    	case NE:
	    		this.doPortConnection(
		    			outpNE.getPortURI(),
		                neighbor.p2pEndPointInfo().toString(),
		                NeighborConnector.class.getCanonicalName());
	    		
	    	case NW:
	    		this.doPortConnection(
		    			outpNW.getPortURI(),
		                neighbor.p2pEndPointInfo().toString(),
		                NeighborConnector.class.getCanonicalName());
    	}
    }
    
    public void disconnect(NodeInfoI neighbour) throws Exception {
    	this.doPortDisconnection(
    			neighbour.p2pEndPointInfo().toString()
    			);
    }
    
}
