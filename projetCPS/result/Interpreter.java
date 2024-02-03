package result;

import java.util.Collections;
import java.util.Queue;
import java.util.LinkedList;
import src.ast.base.ABase;
import src.ast.base.RBase;
import src.ast.bexp.*;
import src.ast.cexp.EqCExp;
import src.ast.cexp.GEqExp;
import src.ast.cexp.LCExp;
import src.ast.cont.DCont;
import src.ast.cont.ECont;
import src.ast.cont.FCont;
import src.ast.dirs.Dirs;
import src.ast.dirs.FDirs;
import src.ast.dirs.RDirs;
import src.ast.gather.FGather;
import src.ast.gather.Gather;
import src.ast.gather.RGather;
import src.ast.query.BQuery;
import src.ast.query.GQuery;
import src.ast.rand.CRand;
import src.ast.rand.SRand;
import src.ast.interfaces.IVisitor;
import src.ast.exception.EvaluationException;
import src.fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import src.fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import src.fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import src.ast.cont.ICont;
import src.fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import src.fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import src.fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Interpreter implements IVisitor<Object>{

	@Override
	public Object visit(AndBExp andExp, ExecutionStateI e) throws EvaluationException{
		boolean left, right;
		Object eval;
		if ((eval = andExp.getBExpLeft().eval(this, e)) instanceof Boolean){
			left = (boolean) eval;
		} else throw new EvaluationException("AndExpression Left expression is not a boolean");
		if ((eval = andExp.getBExpRight().eval(this, e)) instanceof Boolean){
			right = (boolean) eval;
		} else throw new EvaluationException("AndExpression Right expression is not a boolean");
		return left && right;
	}

	@Override
	public Object visit(CExpBExp cbExp, ExecutionStateI e) throws EvaluationException {
		return cbExp.getCexp().eval(this, e);
	}

	@Override
	public Object visit(NotBExp notExp, ExecutionStateI e) throws EvaluationException {
		boolean res;
		Object eval;
		if ((eval = notExp.getBexp().eval(this, e)) instanceof Boolean){
			res = (boolean) eval;
		} else throw new EvaluationException("Not Expression is not a boolean");
		return !res;
	}

	@Override
	public Object visit(OrBExp orExp, ExecutionStateI e) throws EvaluationException{
		boolean left, right;
		Object eval;
		if ((eval = orExp.getBExpLeft().eval(this, e)) instanceof Boolean){
			left = (boolean) eval;
		} else throw new EvaluationException("OrExpression Left expression is not a boolean");
		if ((eval = orExp.getBExpRight().eval(this, e)) instanceof Boolean){
			right = (boolean) eval;
		} else throw new EvaluationException("OrExpression Right expression is not a boolean");
		return left || right;
	}

	@Override
	public Object visit(SBExp sExp, ExecutionStateI e) {
		String sensorId = sExp.getSensorId();
		SensorDataI sd = e.getProcessingNode().getSensorData(sensorId);
		return sd.getValue();
	}

	@Override
	public Object visit(EqCExp eqExp, ExecutionStateI e) throws EvaluationException {
		Object left,right;
		left = eqExp.getRand1().eval(this, e);
		right = eqExp.getRand2().eval(this, e);
		if (!(left instanceof Comparable && right instanceof Comparable)) {
			throw new EvaluationException("Operands are not comparable");
		}
		return left.equals(right);
	}

	@Override
	public Object visit(GEqExp geqExp, ExecutionStateI e) throws EvaluationException {
		Comparable left = (Comparable) geqExp.getRand1().eval(this, e);
		Comparable right = (Comparable) geqExp.getRand2().eval(this, e);
		return left.compareTo(right) >= 0;
	}

	@Override
	public Object visit(LCExp lcExp, ExecutionStateI e) throws EvaluationException {
		Comparable left = (Comparable) lcExp.getRand1().eval(this, e);
		Comparable right = (Comparable) lcExp.getRand2().eval(this, e);
		return left.compareTo(right) < 0;
	}

	@Override
	public Object visit(DCont dCont, ExecutionStateI e) {
		Dirs dirs = dCont.getDirs();
		int maxJumps = dCont.getMaxJumps();
		// on recupere tout les voisins du noeuds actuel
		Set<NodeInfoI> neighbors = e.getProcessingNode().getNeighbours();
		// on recupere la direction de propagation
		Direction d = dirs.getDir();

		if (d.equals(Direction.NE)) {

		}
		else if  (d.equals(Direction.NW)){

		}
		else if (d.equals(Direction.SE)){

		}
		else if (d.equals(Direction.SW)){

		}

		//e.getCurrentResult();   peut etre utliser ca?
		//e.addToCurrentResult(); peut etre utliser ca?
		// TODO
		return null;
	}

	// methode helper pour trouver les voisins recursivement :
	private Set<String> traverse(ProcessingNodeI node, int jumps, Set<String> visited, ExecutionStateI e) {
		if (jumps == 0) {
			return visited;
		}

		Set<String> newVisited = new HashSet<>(visited); // Copie pour éviter les modifications concurrentes
		for (NodeInfoI neighbor : node.getNeighbours()) {
			if (!visited.contains(neighbor.nodeIdentifier())) {
				// Ajouter le voisin à la liste des visités pour cette itération
				newVisited.add(neighbor.nodeIdentifier());
				// Appel récursif pour chaque voisin non visité, avec un saut de moins


				newVisited.addAll(traverse(neighbor.nodeIdentifier(), jumps - 1, newVisited, e));
			}
		}

		return newVisited;
	}

	@Override
	public Object visit(ECont eCont, ExecutionStateI e) {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}


	private void recursiveVisit(ProcessingNodeI currentNode, PositionI basePosition, Set<String> visitedNodeIds, ExecutionStateI e) throws EvaluationException {
		// Ajouter l'identifiant du nœud actuel à l'ensemble des visités
		visitedNodeIds.add(currentNode.getNodeIdentifier());

		// Traiter tous les voisins du nœud actuel
		for (NodeInfoI neighborInfo : currentNode.getNeighbours()) {
			ProcessingNodeI neighborNode = e.updateProcessingNode(neighborInfo);
			// Vérifier si le voisin est dans la distance maximale et n'a pas été visité
			if (!visitedNodeIds.contains(neighborNode.getNodeIdentifier()) && e.withinMaximalDistance(neighborNode.getPosition())) {
				// Continuer récursivement avec le voisin
				recursiveVisit(neighborNode, basePosition, visitedNodeIds, e);
			}
		}
	}

	@Override
	public Object visit(FCont fCont, ExecutionStateI e) throws EvaluationException {
		// Évaluer la base pour obtenir une position (p)
		PositionI basePosition = (PositionI) fCont.getBase().eval(this, e);
		// Ensemble pour stocker les identifiants des nœuds visités
		Set<String> visitedNodeIds = new HashSet<>();
		// Commencer la visite récursive à partir du nœud de traitement actuel
		recursiveVisit(e.getProcessingNode(), basePosition, visitedNodeIds, e);

		// Retourner la liste des identifiants des nœuds visités
		return new ArrayList<>(visitedNodeIds);
	}

	/*
	@Override
	public Object visit(FCont fCont, ExecutionStateI e) throws EvaluationException {
		PositionI basePosition = (PositionI) fCont.getBase().eval(this, e);
		double maxDistance = fCont.getMaxDistance();

		//parcours en largeur
		Queue<ProcessingNodeI> queue = new LinkedList<>();
		Set<String> visitedNodeIds = new HashSet<>();
		ProcessingNodeI node = e.getProcessingNode();
		queue.add(node);
		visitedNodeIds.add(node.getNodeIdentifier());

		while (!queue.isEmpty()) {
			ProcessingNodeI currentNode = queue.poll();
			double currentDistance = basePosition.distance(currentNode.getPosition());

			if (currentDistance > maxDistance) {
				continue;
			}

			// on traite les voisins des voisins jusqu'à ce que la distance soit atteinte
			for (NodeInfoI neighbor : currentNode.getNeighbours()) {
				if (!visitedNodeIds.contains(neighbor.nodeIdentifier())) {
					queue.add(neighbor);
					visitedNodeIds.add(neighbor.nodeIdentifier()); // marqué comme visité
				}
			}
		}

		// Retourner la liste des identifiants des nœuds visités
		return new ArrayList<>(visitedNodeIds);
	}


	@Override
	public Object visit(FCont fCont, ExecutionStateI e) throws EvaluationException {
		PositionI  p = (PositionI) fCont.getBase().eval(this,e);
		double r = fCont.getMaxDistance();
		Set<NodeInfoI> neighbors = e.getProcessingNode().getNeighbours();
		Set<NodeInfoI> res = new HashSet<>();
		double max = 0;
		for (NodeInfoI n : neighbors){
			max += p.distance(n.nodePosition());
			//CONTINUER EXEC AVEC CONT ICI
			res.add(n);

			if (max > r) break;
		}

		//RENVOYER LISTE NOEUDS OU SENSOR OU SENSORID
		return res;
	}*/

	@Override
	public Object visit(RGather rgather, ExecutionStateI e) throws EvaluationException {
		String sensorId = rgather.getSensorId();

		List<String> sensorIds = new ArrayList<>(); //Liste d'identifiants des senseurs dont on cherche les données
		sensorIds.add(rgather.getSensorId());

		Gather next = rgather.getNext();
		if (next != null) {
			List<String> nextSensorIds;
			if (next instanceof RGather){
				nextSensorIds = (List<String>) visit((RGather) next, e);
			}
			else if (next instanceof FGather){
				nextSensorIds = (List<String>) visit((FGather) next, e);
			}
			else throw new EvaluationException("Not a Gather");
			sensorIds.addAll(nextSensorIds);
		}

		return sensorIds;
	}

	@Override
	public Object visit(FGather fgather, ExecutionStateI e) {
		String sensorId = fgather.getSensorId();
		List<String> res = new ArrayList<>();
		res.add(sensorId);
		return res;
	}

	@Override
	public Object visit(BQuery bquery, ExecutionStateI e) {
		BExp bexp = bquery.getExpression();
		ICont cont = bquery.getCont();

		//TODO
		//CONT???

		return null;
	}

	@Override
	public Object visit(GQuery gquery, ExecutionStateI e) throws EvaluationException {
		List<Object> data = new ArrayList<>(); // collecte des données
		Gather gather = gquery.getGather();
		ICont cont = gquery.getCont();

		List<String> sensorIds;
		if (gather instanceof RGather)
			sensorIds = (List<String>) this.visit((RGather) gather, e);
		else if (gather instanceof FGather)
			sensorIds = (List<String>) this.visit((RGather) gather, e);
		else throw new EvaluationException("Not a Gather");

		for (String id : sensorIds){
			data.add(e.getProcessingNode().getSensorData(id).getValue());
		}
		//TODO
		// CONT???

		return data;
	}

	@Override
	public Object visit(CRand crand, ExecutionStateI e) {
		return crand.getConstante();
	}

	@Override
	public Object visit(SRand srand, ExecutionStateI e) throws EvaluationException {
		String sensorId = srand.getSensorId();
		SensorDataI sensor = e.getProcessingNode().getSensorData(sensorId);
		if (!(sensor.getValue() instanceof Double)){
			throw new EvaluationException("In Srand evaluation, sensor is not of type Double");
		}

		return sensorId;
	}

	public Object visit(ABase aBase, ExecutionStateI e) {

		return aBase.getPos();
	}

	@Override
	public Object visit(RBase rbase, ExecutionStateI e) throws EvaluationException {
		return e.getProcessingNode().getPosition();
	}

	@Override
	public Object visit(FDirs fdirs, ExecutionStateI e) throws EvaluationException {
		return fdirs.getDir();
	}

	@Override
	public Object visit(RDirs rdirs, ExecutionStateI e) throws EvaluationException {
		List<Object> evalDirs = new ArrayList<>();
		evalDirs.add(0,rdirs.getDirs().eval(this, e));
		return evalDirs;
	}


}
