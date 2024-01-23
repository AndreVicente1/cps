package result;

import ast.bexp.AndBExp;
import ast.bexp.CExpBExp;
import ast.bexp.NotBExp;
import ast.bexp.OrBExp;
import ast.bexp.SBExp;
import ast.cexp.EqCExp;
import ast.cexp.GEqExp;
import ast.cexp.LCExp;
import ast.cont.DCont;
import ast.cont.ECont;
import ast.cont.FCont;
import ast.gather.FGather;
import ast.gather.RGather;
import ast.query.BQuery;
import ast.query.GQuery;
import ast.rand.CRand;
import ast.rand.SRand;
import interfaces.IVisitor;
import exception.EvaluationException;

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
		boolean res = true;
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
	public Object visit(EqCExp eqExp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(GEqExp geqExp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(LCExp lcExp) {
		// TODO Auto-generated method stub
		return null;
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
		return null;
	}

	@Override
	public Object visit(SRand srand) {
		// TODO Auto-generated method stub
		return null;
	}

	

	
}
