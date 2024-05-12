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

/**
 * Interface for the Visitor design pattern used for evaluating the AST.
 */
public interface IVisitor {
	
	boolean visit(AndBExp andExp, ExecutionStateI e) throws EvaluationException;
	boolean visit(CExpBExp cbExp, ExecutionStateI e) throws EvaluationException;
	boolean visit(NotBExp notExp, ExecutionStateI e) throws EvaluationException;
	boolean visit(OrBExp orExp, ExecutionStateI e) throws EvaluationException;
	boolean visit(SBExp sExp, ExecutionStateI e) throws EvaluationException;
	
	boolean visit(EqCExp eqExp, ExecutionStateI e) throws EvaluationException;
	boolean visit(GEqExp geqExp, ExecutionStateI e) throws EvaluationException;
	boolean visit(LCExp lcExp, ExecutionStateI e) throws EvaluationException;
	
	Object visit(DCont dCont, ExecutionStateI e) throws EvaluationException;
	Object visit(ECont eCont, ExecutionStateI e) throws EvaluationException;
	Object visit(FCont fCont, ExecutionStateI e) throws EvaluationException;
	
	Object visit(RGather rgather, ExecutionStateI e) throws EvaluationException;
	Object visit(FGather fgather, ExecutionStateI e) throws EvaluationException;
	
	Object visit(BQuery bquery, ExecutionStateI e) throws EvaluationException;
	Object visit(GQuery gquery, ExecutionStateI e) throws EvaluationException;
	
	Object visit(CRand crand, ExecutionStateI e) throws EvaluationException;
	Object visit(SRand srand, ExecutionStateI e) throws EvaluationException;

	Object visit(ABase abase, ExecutionStateI e) throws EvaluationException;
	Object visit (RBase rbase, ExecutionStateI e) throws EvaluationException;

	Object visit(FDirs fdirs, ExecutionStateI e) throws EvaluationException;
	Object visit(RDirs rdirs, ExecutionStateI e) throws EvaluationException;
}
