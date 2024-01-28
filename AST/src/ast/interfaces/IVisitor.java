package src.ast.interfaces;

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
import src.ast.exception.EvaluationException;

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

	Result visit(ABase abase) throws EvaluationException;
	Result visit (RBase rbase) throws EvaluationException;
}
