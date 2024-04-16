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
	
	public Object eval(IVisitor visitor, ExecutionStateI e) throws EvaluationException {
		return visitor.visit(this, e);
	}
}
