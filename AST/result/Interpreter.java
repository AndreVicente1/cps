package result;

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

public class Interpreter implements IVisitor<Object>{

	@Override
	public Object visit(AndBExp andExp) throws EvaluationException{
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return cbExp.getCexp().eval(this);
	}

	@Override
	public Object visit(NotBExp notExp) throws EvaluationException {
		// TODO Auto-generated method stub
		boolean res;
		Object eval;
		if ((eval = notExp.getBexp().eval(this)) instanceof Boolean){
			res = (boolean) eval;
		} else throw new EvaluationException("Not Expression is not a boolean");
		return !res;
	}

	@Override
	public Object visit(OrBExp orExp) throws EvaluationException{
		// TODO Auto-generated method stub
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
	public Object visit(SBExp sExp) {
		// TODO Auto-generated method stub
		return null;
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
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(FGather fgather) {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return crand.getConstante();
	}

	@Override
	public Object visit(SRand srand) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(ABase abase) throws EvaluationException {
		return null;
	}

	@Override
	public Object visit(RBase rbase) throws EvaluationException {
		return null;
	}


}
