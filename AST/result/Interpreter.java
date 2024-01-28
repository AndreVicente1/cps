package result;

import src.DataSensors;
import src.Sensor;
import src.ast.base.ABase;
import src.ast.base.RBase;
import src.ast.bexp.AndBExp;
import src.ast.bexp.CExpBExp;
import src.ast.bexp.NotBExp;
import src.ast.bexp.OrBExp;
import src.ast.bexp.SBExp;
import src.ast.cexp.EqCExp;
import src.ast.cexp.GEqExp;
import src.ast.cexp.LCExp;
import src.ast.cont.DCont;
import src.ast.cont.ECont;
import src.ast.cont.FCont;
import src.ast.gather.FGather;
import src.ast.gather.RGather;
import src.ast.query.BQuery;
import src.ast.query.GQuery;
import src.ast.rand.CRand;
import src.ast.rand.SRand;
import src.ast.interfaces.IVisitor;
import src.ast.exception.EvaluationException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Interpreter implements IVisitor<Object>{

	@Override
	public Object visit(AndBExp andExp) throws EvaluationException{
		boolean left, right;
		Object eval;
		if ((eval = andExp.getBExpLeft().eval(this)) instanceof Boolean){
			left = (boolean) eval;
		} else throw new EvaluationException("AndExpression Left expression is not a boolean");
		if ((eval = andExp.getBExpRight().eval(this)) instanceof Boolean){
			right = (boolean) eval;
		} else throw new EvaluationException("AndExpression Right expression is not a boolean");
		return left && right;
	}

	@Override
	public Object visit(CExpBExp cbExp) throws EvaluationException {
		return cbExp.getCexp().eval(this);
	}

	@Override
	public Object visit(NotBExp notExp) throws EvaluationException {
		boolean res;
		Object eval;
		if ((eval = notExp.getBexp().eval(this)) instanceof Boolean){
			res = (boolean) eval;
		} else throw new EvaluationException("Not Expression is not a boolean");
		return !res;
	}

	@Override
	public Object visit(OrBExp orExp) throws EvaluationException{
		boolean left, right;
		Object eval;
		if ((eval = orExp.getBExpLeft().eval(this)) instanceof Boolean){
			left = (boolean) eval;
		} else throw new EvaluationException("OrExpression Left expression is not a boolean");
		if ((eval = orExp.getBExpRight().eval(this)) instanceof Boolean){
			right = (boolean) eval;
		} else throw new EvaluationException("OrExpression Right expression is not a boolean");
		return left || right;
	}

	@Override
	public Object visit(SBExp sExp) { // /!\ A MODIFIER PEUT ETRE
		int sensorId = sExp.getSensorId();
		Sensor sensor = DataSensors.getSensorById(sensorId);
		return sensor.getValue();
	}

	@Override
	public Object visit(EqCExp eqExp) throws EvaluationException {
		Object left,right;
		left = eqExp.getRand1().eval(this);
		right = eqExp.getRand2().eval(this);
		if (!(left instanceof Comparable && right instanceof Comparable)) {
			throw new EvaluationException("Operands are not comparable");
		}
		return left.equals(right);
	}

	@Override
	public Object visit(GEqExp geqExp) throws EvaluationException {
		Comparable left = (Comparable) geqExp.getRand1().eval(this);
		Comparable right = (Comparable) geqExp.getRand2().eval(this);
		return left.compareTo(right) >= 0;
	}

	@Override
	public Object visit(LCExp lcExp) throws EvaluationException {
		Comparable left = (Comparable) lcExp.getRand1().eval(this);
		Comparable right = (Comparable) lcExp.getRand2().eval(this);
		return left.compareTo(right) < 0;
	}

	@Override
	public Object visit(DCont dCont) {
		// TODO Auto-generated method stub
		Directions directions = dCont.getDirections();
		int maxJumps = dCont.getMaxJumps();


	}

	@Override
	public Object visit(ECont eCont) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(FCont fCont) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(RGather rgather) {
		int sensorId = rgather.getSensorId();

		List<Integer> sensorIds = new ArrayList<>(); //Liste d'identifiants des senseurs dont on cherche les données
		sensorIds.add(rgather.getSensorId());

		RGather next = rgather.getNextGather();
		if (next != null) {
			List<Integer> nextSensorIds = (List<Integer>) visit(next);
			sensorIds.addAll(nextSensorIds);
		}

		return sensorIds;
	}

	@Override
	public Object visit(FGather fgather) {
		int sensorId = fgather.getSensorId();
		List<Integer> res = new ArrayList<>();
		res.add(sensorId);
		return res;
	}

	@Override
	public Object visit(BQuery bquery) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(GQuery gquery) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(CRand crand) {
		return crand.getConstante();
	}

	@Override
	public Object visit(SRand srand) throws EvaluationException {
		int sensorId = srand.getSensorId();
		Sensor sensor = DataSensors.getSensorById(sensorId);
		if (!(sensor.getValue() instanceof Double)){
			throw new EvaluationException("In Srand evaluation, sensor is not of type Double");
		}

		return sensorId;
	}

	public Object visit(ABase aBase) {
		int sensorId = aBase.getSensorId();
		return DataSensors.getSensorById(baseSensorId);
	}

	@Override
	public Object visit(RBase rbase) throws EvaluationException {
		return null;
	}


}
