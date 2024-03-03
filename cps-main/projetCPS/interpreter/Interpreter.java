package interpreter;

import java.util.Collections;
import java.util.Queue;
import java.util.LinkedList;

import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import connexion.ExecutionState;
import connexion.QueryResult;
import ast.base.ABase;
import ast.base.Base;
import ast.base.RBase;
import ast.bexp.*;
import ast.cexp.EqCExp;
import ast.cexp.GEqExp;
import ast.cexp.LCExp;
import ast.cont.DCont;
import ast.cont.ECont;
import ast.cont.FCont;
import ast.dirs.Dirs;
import ast.dirs.FDirs;
import ast.dirs.RDirs;
import ast.gather.FGather;
import ast.gather.Gather;
import ast.gather.RGather;
import ast.query.BQuery;
import ast.query.GQuery;
import ast.rand.CRand;
import ast.rand.SRand;
import ast.interfaces.IVisitor;
import ast.exception.EvaluationException;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import ast.cont.ICont;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Interpreter implements IVisitor<Object>{

	
	// ================================== Bexp ============================================
	
	@Override
	public Object visit(AndBExp andExp, ExecutionStateI e) throws EvaluationException{
		EvaluationResult left = (EvaluationResult) andExp.getBExpLeft().eval(this, e);
	    EvaluationResult right = (EvaluationResult) andExp.getBExpRight().eval(this, e);
		if (left.getResult() && right.getResult()) {
			return new EvaluationResult(true, left.fusionSensorIds(right));
		}
		else return new EvaluationResult(false);
	}

	@Override
	public Object visit(CExpBExp cbExp, ExecutionStateI e) throws EvaluationException {
		return cbExp.getCexp().eval(this, e);
	}

	@Override
	public Object visit(NotBExp notExp, ExecutionStateI e) throws EvaluationException {
		 EvaluationResult result = (EvaluationResult) notExp.getBexp().eval(this, e);
	    return new EvaluationResult(!result.getResult(), result.getSensorIds());
	}

	@Override
	public Object visit(OrBExp orExp, ExecutionStateI e) throws EvaluationException{
		 EvaluationResult left = (EvaluationResult) orExp.getBExpLeft().eval(this, e);
	    EvaluationResult right = (EvaluationResult) orExp.getBExpRight().eval(this, e);

	    if (left.getResult() || right.getResult()) {
	        return new EvaluationResult(true, left.fusionSensorIds(right));
	    }
	    return new EvaluationResult(false);
	}

	@Override
	public Object visit(SBExp sExp, ExecutionStateI e) {
		String sensorId = sExp.getSensorId();
		SensorDataI sd = (SensorDataI) e.getProcessingNode().getSensorData(sensorId);
		return sd.getValue();
	}
	
	
	
	// ================================== Cexp ============================================

	@Override
	public Object visit(EqCExp eqExp, ExecutionStateI e) throws EvaluationException {
	    Object left = eqExp.getRand1().eval(this, e);
	    Object right = eqExp.getRand2().eval(this, e);
	    ArrayList<String> sensorIds = new ArrayList<>();

	    // Identifiez si l'un des opérandes est un SRand pour sauvegarder son l'id du capteur
	    if (eqExp.getRand1() instanceof SRand) {
	        sensorIds.add(((SRand) eqExp.getRand1()).getSensorId());
	    } else if (eqExp.getRand2() instanceof SRand) {
	        sensorIds.add(((SRand) eqExp.getRand2()).getSensorId());
	    }

	    if (left instanceof Comparable && right instanceof Comparable) {
	        boolean result = left.equals(right);
	        return new EvaluationResult(result, sensorIds);
	    } else {
	        throw new EvaluationException("Operands are not comparable");
	    }
	}
	

	@Override
	public Object visit(GEqExp geqExp, ExecutionStateI e) throws EvaluationException {
	    Object leftObj = geqExp.getRand1().eval(this, e);
	    Object rightObj = geqExp.getRand2().eval(this, e);
	    ArrayList<String> sensorIds = new ArrayList<>();

	    // Identifiez si l'un des opérandes est un SRand pour sauvegarder son l'id du capteur
	    if (geqExp.getRand1() instanceof SRand) {
	        sensorIds.add(((SRand) geqExp.getRand1()).getSensorId());
	    } else if (geqExp.getRand2() instanceof SRand) {
	        sensorIds.add(((SRand) geqExp.getRand2()).getSensorId());
	    }
	    if (leftObj instanceof Comparable && rightObj instanceof Comparable) {
	        Comparable leftComp = (Comparable) leftObj;
	        Comparable rightComp = (Comparable) rightObj;

	        if (leftObj.getClass().equals(rightObj.getClass())) {
	            return new EvaluationResult(leftComp.compareTo(rightComp) >= 0, sensorIds);
	        } else {
	            throw new EvaluationException("Incompatible types for comparison");
	        }
	    } else {
	        throw new EvaluationException("Non-comparable types");
	    }
	}

	
	@Override
	public Object visit(LCExp lcExp, ExecutionStateI e) throws EvaluationException {
	    Object leftObj = lcExp.getRand1().eval(this, e);
	    Object rightObj = lcExp.getRand2().eval(this, e);
	    ArrayList<String> sensorIds = new ArrayList<>();

	    // Identifiez si l'un des opérandes est un SRand pour sauvegarder son l'id du capteur
	    if (lcExp.getRand1() instanceof SRand) {
	    	sensorIds.add(((SRand) lcExp.getRand1()).getSensorId());
	    } else if (lcExp.getRand2() instanceof SRand) {
	    	sensorIds.add(((SRand) lcExp.getRand2()).getSensorId());
	    }

	    if (leftObj instanceof Comparable && rightObj instanceof Comparable) {
	        Comparable leftComp = (Comparable) leftObj;
	        Comparable rightComp = (Comparable) rightObj;

	        if (leftObj.getClass().equals(rightObj.getClass())) {
	            return new EvaluationResult(leftComp.compareTo(rightComp) < 0, sensorIds);
	        } else {
	            throw new EvaluationException("Incompatible types for comparison");
	        }
	    } else {
	        throw new EvaluationException("Non-comparable types");
	    }
	}
	
	
	
	
	// ================================== Cont ============================================

	@Override
	public Object visit(DCont dCont, ExecutionStateI e) throws EvaluationException {
		Dirs d = dCont.getDirs();
        int j = dCont.getMaxJumps();

        ExecutionState exec = (ExecutionState) e;
        
        exec.setIsContinuation(true);
        exec.setIsDirectional(true);
        
        if (d instanceof FDirs)
        	exec.getDirections().add((Direction) d.eval(this, e));
        else if (d instanceof RDirs)
        	exec.getDirections().addAll((Collection<? extends Direction>) d.eval(this, exec));
        else 
        	throw new EvaluationException("Continuation is not of type Cont");
        
        exec.setMaxJumps(j);

        return null;
    }


	@Override
	public Object visit(ECont eCont, ExecutionStateI e) {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}

	@Override
	public Object visit(FCont fCont, ExecutionStateI e) {
		Base base = fCont.getBase();
		ExecutionState exec = (ExecutionState) e;
		
		exec.setIsContinuation(true);
		exec.setIsFlooding(true);
        exec.setMaxDist(fCont.getMaxDistance());
        
        //propagation sur toutes les directions
        exec.getDirections().add(Direction.NE);
        exec.getDirections().add(Direction.SW);
        exec.getDirections().add(Direction.NW);
        exec.getDirections().add(Direction.SE);
        
        System.out.println("nouvel execution state après visit fcont");
        System.out.println(e.toString());
        return null;
	}
	
	
	// ================================== Gather ============================================

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
	
	
	// ================================== Query ============================================

	@Override
	public Object visit(BQuery bquery, ExecutionStateI e) throws EvaluationException {
		BExp bexp = bquery.getExpression();

		QueryResultI qr = new QueryResult( true);
		
		//ArrayList<String> idSensors = new ArrayList<>();
		//ArrayList<SensorDataI> dataSensors = new ArrayList<>();
        try {
        	EvaluationResult res = (EvaluationResult) bexp.eval(this, e);
            if (res.getResult()){
				String id = e.getProcessingNode().getNodeIdentifier();
				((QueryResult)qr).addId(id);
				//A VERIFIER
				for (String sensorId : res.getSensorIds()) {
	                ((QueryResult)qr).addData(e.getProcessingNode().getSensorData(sensorId));
	            }
				
			}
        } catch (EvaluationException ex) {
            throw new RuntimeException(ex);
        }

		//TODO
		//CONT???
        ICont cont = bquery.getCont();
        
		cont.eval(this, e); //modifier l'executionState
		

		return qr;
	}

	@Override
	public Object visit(GQuery gquery, ExecutionStateI e) throws EvaluationException {
		//List<Object> data = new ArrayList<>(); // collecte des données
		if (e == null) System.out.println("E IS NULL");
		else System.out.println(e.toString());
		Gather gather = gquery.getGather();
		
		QueryResultI qr = new QueryResult(false);

		List<String> sensorIds = new ArrayList<String>();
		if (gather instanceof RGather)
			sensorIds = (List<String>) this.visit((RGather) gather, e);
		else if (gather instanceof FGather)
			sensorIds = (List<String>) this.visit((FGather) gather, e);
		else throw new EvaluationException("Not a Gather");
		
		/*for (String s : sensorIds) {
			System.out.println(s);
		}*/
		
		if (!sensorIds.isEmpty()) {
			qr.positiveSensorNodes().add(e.getProcessingNode().getNodeIdentifier());
		}

		for (String id : sensorIds){
			qr.gatheredSensorsValues().add(e.getProcessingNode().getSensorData(id));
			System.out.println("senseur id = " + id + " data = " + e.getProcessingNode().getSensorData(id).getValue());
		}
		
		
		//TODO
		// CONT???
		ICont cont = gquery.getCont();
		
		cont.eval(this, e); //modifier l'executionState

		return qr;
	}
	
	
	
	// ================================== Rand ============================================

	@Override
	public Object visit(CRand crand, ExecutionStateI e) {
		return crand.getConstante();
	}

	@Override
	public Object visit(SRand srand, ExecutionStateI e) throws EvaluationException {
		String sensorId = srand.getSensorId();
		SensorDataI sensor = (SensorDataI) e.getProcessingNode().getSensorData(sensorId);
		if (!(sensor.getValue() instanceof Double)){
			throw new EvaluationException("In SRand evaluation, sensor is not of type Double");
		}

		return sensor.getValue();
	}
	
	
	
	// ================================== Base ============================================

	public Object visit(ABase aBase, ExecutionStateI e) {

		return aBase.getPos();
	}

	@Override
	public Object visit(RBase rbase, ExecutionStateI e) throws EvaluationException {
		return e.getProcessingNode().getPosition();
	}
	
	
	
	// ================================== Direction ============================================

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
