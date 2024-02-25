package connexion;

import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import interpreter.Interpreter;
import ast.query.*;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;


public class Requesting implements RequestingCI{
	Interpreter interpreter;
	NodeI initialNode;
	NodeI currNode;
	ExecutionStateI exec;
	
	public Requesting (Interpreter interpreter, NodeI node) {
		this.interpreter = interpreter;
		currNode = node;
		initialNode = node;
	
	}
	
	@Override
	public QueryResultI execute(RequestI request) throws Exception {
		//si la requete est une continuation
		if (request instanceof RequestContinuationI) {
			RequestContinuationI requestcont = (RequestContinuationI) request;
			exec = requestcont.getExecutionState();
			exec.updateProcessingNode(currNode);
		}
		
		if (!request.isAsynchronous()) { //on traite seulement synchrone pour l'instant
			Object res = null;
			Query query = (Query) request.getQueryCode(); //on récupere la query pour l'exécuter
			
			if (query instanceof BQuery) {
				if (exec != null) exec = new ExecutionState(initialNode,currNode, true);
					
				res = interpreter.visit((BQuery) query, exec);
			} else if (query instanceof GQuery) {
				if (exec != null) exec = new ExecutionState(initialNode,currNode, false);
				
				res = interpreter.visit((GQuery) query, exec);
			} else throw new Exception("La requête n'est pas une query reconnue");

			return (QueryResultI) res;
		}
		else {
			//TODO partie 3 et 4
			return null;
		}
	}
	
	public ExecutionStateI getExecutionState() {return exec;}
	
	@Override
	public void executeAsync(RequestI request) throws Exception {
		// TODO on ne s'occupe pas de ça pour l'instant
		
	}

}
