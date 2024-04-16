package interpreter;

import java.util.Collections;
import java.util.Queue;
import java.util.LinkedList;

import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import connexion.ExecutionState;
import connexion.ProcessingNode;
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
import components.Node;
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

/**
 * Interpreter pour nos AST qui utilise le patron de conception Visiteur
 */
public class Interpreter implements IVisitor{

	
	// ================================== Bexp ============================================
	
	@Override
	public boolean visit(AndBExp andExp, ExecutionStateI e) throws EvaluationException{
		boolean left = (boolean) andExp.getBExpLeft().eval(this, e);
	    boolean right = (boolean) andExp.getBExpRight().eval(this, e);
		if (left && right) {
			return true;
		}
		else return false;
	}

	@Override
	public boolean visit(CExpBExp cbExp, ExecutionStateI e) throws EvaluationException {
		return (boolean) cbExp.getCexp().eval(this, e);
	}

	@Override
	public boolean visit(NotBExp notExp, ExecutionStateI e) throws EvaluationException {
	    return !(boolean)notExp.getBexp().eval(this, e);
	}

	@Override
	public boolean visit(OrBExp orExp, ExecutionStateI e) throws EvaluationException{
		boolean left = (boolean) orExp.getBExpLeft().eval(this, e);
	    boolean right = (boolean) orExp.getBExpRight().eval(this, e);

	    if (left || right) {
	        return true;
	    }
	    return false;
	}

	@Override
	public boolean visit(SBExp sExp, ExecutionStateI e) throws EvaluationException {
		String sensorId = sExp.getSensorId();
		SensorDataI sd = (SensorDataI) e.getProcessingNode().getSensorData(sensorId);
		if (!(sd.getValue() instanceof Boolean)) throw new EvaluationException("Sensor " + sensorId + " value is not of type Boolean to call SBExp"); 
		return (boolean) sd.getValue();
	}
	
	
	
	// ================================== Cexp ============================================

	@Override
	public boolean visit(EqCExp eqExp, ExecutionStateI e) throws EvaluationException {
	    Object left = eqExp.getRand1().eval(this, e);
	    Object right = eqExp.getRand2().eval(this, e);
	    ArrayList<String> sensorIds = new ArrayList<>();

	    // Identifiez si l'un des opérandes est un SRand pour sauvegarder son l'id du capteur
	    if (eqExp.getRand1() instanceof SRand) {
	        sensorIds.add(((SRand) eqExp.getRand1()).getSensorId());
	    } else if (eqExp.getRand2() instanceof SRand) {
	        sensorIds.add(((SRand) eqExp.getRand2()).getSensorId());
	    }

        boolean result = left.equals(right);
        return result;
	 
	}
	

	@Override
	public boolean visit(GEqExp geqExp, ExecutionStateI e) throws EvaluationException {
	    Object leftObj = geqExp.getRand1().eval(this, e);
	    Object rightObj = geqExp.getRand2().eval(this, e);
	    ArrayList<String> sensorIds = new ArrayList<>();

	    if (geqExp.getRand1() instanceof SRand) {
	        sensorIds.add(((SRand) geqExp.getRand1()).getSensorId());
	    } else if (geqExp.getRand2() instanceof SRand) {
	        sensorIds.add(((SRand) geqExp.getRand2()).getSensorId());
	    }
	    
	    if (leftObj == null || rightObj == null) throw new EvaluationException("One operand is empty"); //une des opérandes est vide
	    
        Comparable leftComp = (Comparable) leftObj;
        Comparable rightComp = (Comparable) rightObj;

        if (leftObj.getClass().equals(rightObj.getClass())) {
            return leftComp.compareTo(rightComp) >= 0;
        } else {
            throw new EvaluationException("Incompatible types for comparison");
        }
	}

	
	@Override
	public boolean visit(LCExp lcExp, ExecutionStateI e) throws EvaluationException {
	    Object leftObj = lcExp.getRand1().eval(this, e);
	    Object rightObj = lcExp.getRand2().eval(this, e);
	    ArrayList<String> sensorIds = new ArrayList<>();

	    // Identifiez si l'un des opérandes est un SRand pour sauvegarder son l'id du capteur
	    if (lcExp.getRand1() instanceof SRand) {
	    	sensorIds.add(((SRand) lcExp.getRand1()).getSensorId());
	    } else if (lcExp.getRand2() instanceof SRand) {
	    	sensorIds.add(((SRand) lcExp.getRand2()).getSensorId());
	    }

        Comparable leftComp = (Comparable) leftObj;
        Comparable rightComp = (Comparable) rightObj;

        if (leftObj.getClass().equals(rightObj.getClass())) {
            return leftComp.compareTo(rightComp) < 0;
        } else {
            throw new EvaluationException("Incompatible types for comparison");
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
        else if (d instanceof RDirs) {
        	@SuppressWarnings("unchecked")
			ArrayList<Direction> set = (ArrayList<Direction>) d.eval(this, exec);
        	exec.getDirections().addAll(set);
        }
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
        for (Direction direction : Direction.values()) {
            exec.getDirections().add(direction);
        }
        
        return null;
	}
	
	
	// ================================== Gather ============================================

	@Override
	public Object visit(RGather rgather, ExecutionStateI e) throws EvaluationException {
		List<SensorDataI> sensorDataList = new ArrayList<>();
	    String sensorId = rgather.getSensorId();
	    SensorDataI sensorData = e.getProcessingNode().getSensorData(sensorId);
	    sensorDataList.add(sensorData);

	    Gather next = rgather.getNext();
	    if (next != null) {
	        List<SensorDataI> nextSensorData;
	        if (next instanceof RGather){
	            nextSensorData = (List<SensorDataI>) visit((RGather) next, e);
	        }
	        else if (next instanceof FGather){
	            nextSensorData = (List<SensorDataI>) visit((FGather) next, e);
	        }
	        else throw new EvaluationException("Not a Gather");
	        sensorDataList.addAll(nextSensorData);
	    }

	    return sensorDataList;
	}

	@Override
	public Object visit(FGather fgather, ExecutionStateI e) {
		List<SensorDataI> sensorDataList = new ArrayList<>();
	    String sensorId = fgather.getSensorId();
	    SensorDataI sensorData = e.getProcessingNode().getSensorData(sensorId);
	    sensorDataList.add(sensorData);
	    return sensorDataList;
	}
	
	
	// ================================== Query ============================================

	@Override
	public Object visit(BQuery bquery, ExecutionStateI e) throws EvaluationException {
		BExp bexp = bquery.getExpression();

		QueryResultI qr = new QueryResult( true);
		
		Node node = ((ProcessingNode) e.getProcessingNode()).getNode();
		try {
	        if ((boolean) bexp.eval(this, e)) {
	            String id = e.getProcessingNode().getNodeIdentifier();
	            ((QueryResult)qr).addId(id);

	            for (SensorDataI sensorData : node.getAllSensors()) {
	                ((QueryResult)qr).addData(sensorData);
	            }
	        }
	    } catch (EvaluationException ex) {
	        throw new RuntimeException(ex);
	    }
		// continuation
        ICont cont = bquery.getCont();
        
		cont.eval(this, e);
		
		return qr;
	}

	@Override
	public Object visit(GQuery gquery, ExecutionStateI e) throws EvaluationException {
		if (e == null) throw new EvaluationException("E IS NULL");
	    Gather gather = gquery.getGather();
	    
	    QueryResultI qr = new QueryResult(false);
	    List<SensorDataI> sensorDataList = new ArrayList<>();

	    if (gather instanceof RGather)
	        sensorDataList = (List<SensorDataI>) this.visit((RGather) gather, e);
	    else if (gather instanceof FGather)
	        sensorDataList = (List<SensorDataI>) this.visit((FGather) gather, e);
	    else throw new EvaluationException("Not a Gather");
	    
	    if (!sensorDataList.isEmpty()) {
	        qr.positiveSensorNodes().add(e.getProcessingNode().getNodeIdentifier());
	    }

	    for (SensorDataI sensorData : sensorDataList){
	        qr.gatheredSensorsValues().add(sensorData);
	    }
		
	    //continuation
		ICont cont = gquery.getCont();
		
		cont.eval(this, e);

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
		if (sensor == null) return null;
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
		evalDirs.add(rdirs.getDir());
		evalDirs.add(0,rdirs.getDirs().eval(this, e));
		return evalDirs;
	}


}
