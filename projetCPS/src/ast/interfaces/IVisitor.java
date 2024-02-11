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
import src.ast.dirs.RDirs;
import src.ast.gather.FGather;
import src.ast.gather.RGather;
import src.ast.query.BQuery;
import src.ast.query.GQuery;
import src.ast.rand.CRand;
import src.ast.rand.SRand;
import src.ast.exception.EvaluationException;
import src.ast.dirs.FDirs;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public interface IVisitor<Result> {
	//TODO EvaluationException class
	Result visit(AndBExp andExp, ExecutionStateI e) throws EvaluationException;
	Result visit(CExpBExp cbExp, ExecutionStateI e) throws EvaluationException;
	Result visit(NotBExp notExp, ExecutionStateI e) throws EvaluationException;
	Result visit(OrBExp orExp, ExecutionStateI e) throws EvaluationException;
	Result visit(SBExp sExp, ExecutionStateI e) throws EvaluationException;
	
	Result visit(EqCExp eqExp, ExecutionStateI e) throws EvaluationException;
	Result visit(GEqExp geqExp, ExecutionStateI e) throws EvaluationException;
	Result visit(LCExp lcExp, ExecutionStateI e) throws EvaluationException;
	
	Result visit(DCont dCont, ExecutionStateI e) throws EvaluationException;
	Result visit(ECont eCont, ExecutionStateI e) throws EvaluationException;
	Result visit(FCont fCont, ExecutionStateI e) throws EvaluationException;
	
	Result visit(RGather rgather, ExecutionStateI e) throws EvaluationException;
	Result visit(FGather fgather, ExecutionStateI e) throws EvaluationException;
	
	Result visit(BQuery bquery, ExecutionStateI e) throws EvaluationException;
	Result visit(GQuery gquery, ExecutionStateI e) throws EvaluationException;
	
	Result visit(CRand crand, ExecutionStateI e) throws EvaluationException;
	Result visit(SRand srand, ExecutionStateI e) throws EvaluationException;

	Result visit(ABase abase, ExecutionStateI e) throws EvaluationException;
	Result visit (RBase rbase, ExecutionStateI e) throws EvaluationException;

	Result visit(FDirs fdirs, ExecutionStateI e) throws EvaluationException;
	Result visit(RDirs rdirs, ExecutionStateI e) throws EvaluationException;
}
