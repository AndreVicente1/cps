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
import exception.EvaluationException;

public interface IVisitor<Result> {
	//TODO EvaluationException class
	Result visit(AndBExp andExp) throws EvaluationException;
	Result visit(CExpBExp cbExp) throws EvaluationException;
	Result visit(NotBExp notExp) throws EvaluationException;
	Result visit(OrBExp orExp) throws EvaluationException;
	Result visit(SBExp sExp) throws EvaluationException;
	
	Result visit(EqCExp eqExp) throws EvaluationException;
	Result visit(GEqExp geqExp) throws EvaluationException;
	Result visit(LCExp lcExp) throws EvaluationException;
	
	Result visit(DCont dCont) throws EvaluationException;
	Result visit(ECont eCont) throws EvaluationException;
	Result visit(FCont fCont) throws EvaluationException;
	
	Result visit(RGather rgather) throws EvaluationException;
	Result visit(FGather fgather) throws EvaluationException;
	
	Result visit(BQuery bquery) throws EvaluationException;
	Result visit(GQuery gquery) throws EvaluationException;
	
	Result visit(CRand crand) throws EvaluationException;
	Result visit(SRand srand) throws EvaluationException;
}
