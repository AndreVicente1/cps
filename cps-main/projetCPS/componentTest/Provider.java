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

import connexion.ExecutionState;
import connexion.Node;
import connexion.NodeI;
import connexion.RequestContinuation;
import connexion.Requesting;
import connexion.SensorData;
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

@RequiredInterfaces(required= {SensorNodeP2PCI.class})
@OfferedInterfaces(offered={RequestingCI.class, SensorNodeP2PCI.class})

public class Provider extends AbstractComponent  {
    InboundPortProvider inp;
    
    Interpreter interpreter;
    NodeI currNode;
	ExecutionStateI exec;
	
	/* Inbound Port Node-Node */
	InboundPortProviderNode inpn;
	/* Outbound Port Node-Node */
    OutboundPortProvider outp;
    /* Outbound Port Node-Register */
    OutboundPortProviderRegister outpr;

    public Provider(int nbThreads, int nbSchedulableThreads,
                       String uri,
                       String uriInPort,
                       String uriInPortNode,
                       String uriOutPort) throws Exception {
        super(uri, nbThreads, nbSchedulableThreads);
        this.inp = new InboundPortProvider(this, uriInPort);
        inp.publishPort();
        
        this.inpn = new InboundPortProviderNode(this, uriInPortNode);
        inpn.publishPort();
        
        this.outp = new OutboundPortProvider(this, uriOutPort);
        outp.publishPort();
        
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

    public QueryResultI treatRequest(RequestI request) throws Exception{
		
		if (!request.isAsynchronous()) { //on traite seulement synchrone pour l'instant
			Object res = null;
			Query query = (Query) request.getQueryCode(); //on récupere la query pour l'exécuter
			
			if (query instanceof BQuery) {
				if (exec != null) exec = new ExecutionState(currNode,currNode, true);
					
				res = interpreter.visit((BQuery) query, exec);
			} else if (query instanceof GQuery) {
				if (exec != null) exec = new ExecutionState(currNode,currNode, false);
				
				res = interpreter.visit((GQuery) query, exec);
			} else throw new Exception("La requête n'est pas une query reconnue");

			return (QueryResultI) res;
		}
		else {
			//TODO partie 3 et 4
			return null;
		}
    }
    
    public QueryResultI treatRequest(RequestContinuationI request) throws Exception{
	    //traiter localement?
    	if (!request.isAsynchronous()){
	    	RequestContinuationI requestcont = (RequestContinuationI) request;
	        requestcont.getExecutionState();
	        exec.updateProcessingNode(currNode);
	    	
	    	//envoie de la requete via port sortant
	       	ExecutionStateI exec = requestcont.getExecutionState();
	        if (!exec.noMoreHops() || exec.withinMaximalDistance(currNode.getPosition())) {
	            exec.incrementHops();
	        	//poursuite du calcul
	        	RequestContinuationI reqCont = new RequestContinuation(
	        			request.isAsynchronous(), 
	        			request.requestURI(), 
	        			request.getQueryCode(),
	        			exec);
	        	return (QueryResultI) outp.execute(reqCont);
	        }
	        else {
	        	
				return null;
			}
	    } else {
	    	//TODO partie 3 et 4
			return null;
	    }
    }

    @Override
    public void execute() throws Exception{
        super.execute();

        //étape 1: Se connecter au registre, appeler la méthode register
        //Au plus 4 voisins lui sera attribué
        
        //étape2: ???
        
        //étape fin: disconnect
    }

    @Override
    public void shutdown() throws Exception {
        super.shutdown();
        inp.unpublishPort();
        //inp.destroyPort();
    }
    
    /* 
     * Node-Node connection methods 
     */
    public void connect(NodeInfoI neighbor) throws Exception {
    	this.doPortConnection(
    			inp.getPortURI(),
                neighbor.p2pEndPointInfo().toString(),
                NeighborConnector.class.getCanonicalName());
    }
    
    public void disconnect(NodeInfoI neighbour) throws Exception {
    	this.doPortDisconnection(
    			neighbour.p2pEndPointInfo().toString()
    			);
    }
    
    /* 
     * Node-Register connection methods 
     */
    public boolean registered() throws Exception {
    	return outpr.registered(currNode.getNodeIdentifier());
    }
    
    public Set<NodeInfoI> register(NodeInfoI nodeInfo) throws Exception{
    	return outpr.register(nodeInfo);
    }
    
    public NodeInfoI findNewNeighbour(NodeInfoI nodeInfo, Direction d) throws Exception{
    	return outpr.findNewNeighbour(nodeInfo, d);
    }
    
    public void unregister(String nodeIdentifier) throws Exception {
    	outpr.unregister(nodeIdentifier);
    }
}
