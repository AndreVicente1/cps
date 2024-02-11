package src.Connexion.connexion;

import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import result.Interpreter;
import src.ast.query.*;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;


public class Requesting implements RequestingCI{
	Interpreter interpreter;
	ProcessingNodeI currNode;
	public Requesting (Interpreter interpreter) {
		this.interpreter = interpreter;
	}
	@Override
	public QueryResultI execute(RequestI request) throws Exception {
		if (!request.isAsynchronous()) { //on traite seulement synchrone pour l'instant
			Object res = null;
			Query query = (Query) request.getQueryCode(); //on récupere la query pour l'exécuter

			if (query instanceof BQuery) {
				ExecutionStateI exec = new ExecutionState(currNode, true);
				res = interpreter.visit((BQuery) query, exec);
			} else if (query instanceof GQuery) {
				ExecutionStateI exec = new ExecutionState(currNode, false);
				res = interpreter.visit((GQuery) query, exec);
			} else throw new Exception("La requête n'est pas une query reconnue");

			return (QueryResultI) res;
		}
		else {
			//TODO partie 3 et 4
			return null;
		}
	}

	@Override
	public void executeAsync(RequestI request) throws Exception {
		// TODO on ne s'occupe pas de ça pour l'instant
		
	}

}
