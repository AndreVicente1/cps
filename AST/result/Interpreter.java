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

public class Interpreter implements IVisitor<Object>{

	@Override
	public Object visit(AndBExp andExp) {
		// TODO Auto-generated method stub
		boolean left = visit(andExp.getBExpLeft());
		boolean right = visit(andExp.getBExpRight());
		return left && right;
	}

	@Override
	public Object visit(CExpBExp cbExp) {
		// TODO Auto-generated method stub
		return visit(cbExp.getCexp());
	}

	@Override
	public Object visit(NotBExp notExp) {
		// TODO Auto-generated method stub
		boolean res = (boolean) notExp.getBexp().eval(this);
		return !res;
	}

	@Override
	public Object visit(OrBExp orExp) {
		// TODO Auto-generated method stub
		boolean left = orExp.getBExpLeft().eval(this);
		boolean right = orExp.getBExpRight().eval(this);
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
