package interfaces;

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

public interface IVisitor<Result> {
	
	Result visit(AndBExp andExp);
	Result visit(CExpBExp cbExp);
	Result visit(NotBExp notExp);
	Result visit(OrBExp orExp);
	Result visit(SBExp sExp);
	
	Result visit(EqCExp eqExp);
	Result visit(GEqExp geqExp);
	Result visit(LCExp lcExp);
	
	Result visit(DCont dCont);
	Result visit(ECont eCont);
	Result visit(FCont fCont);
	
	Result visit(RGather rgather);
	Result visit(FGather fgather);
	
	Result visit(BQuery bquery);
	Result visit(GQuery gquery);
	
	Result visit(CRand crand);
	Result visit(SRand srand);
}
