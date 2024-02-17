package ast.rand;

import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public class CRand extends Rand{
	private final double cst;
	
	public CRand(double cst) {
		this.cst = cst;
	}
	
	public double getConstante() {
		return cst;
	}
	
	public <Result> Result eval(IVisitor<Result> visitor, ExecutionStateI e) throws EvaluationException {
		return visitor.visit(this, e);
	}
}
