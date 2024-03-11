package ast.interfaces;

import ast.base.ABase;
import ast.base.RBase;
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
import ast.dirs.RDirs;
import ast.gather.FGather;
import ast.gather.RGather;
import ast.query.BQuery;
import ast.query.GQuery;
import ast.rand.CRand;
import ast.rand.SRand;
import ast.exception.EvaluationException;
import ast.dirs.FDirs;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public interface IVisitor<Result> {
	
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
