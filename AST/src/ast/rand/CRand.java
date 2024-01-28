package src.ast.rand;

import src.ast.exception.EvaluationException;
import src.ast.interfaces.IVisitor;

public class CRand extends Rand{
	private final double cst;
	
	public CRand(double cst) {
		this.cst = cst;
	}
	
	public double getConstante() {
		return cst;
	}
	
	public <Result> Result eval(IVisitor<Result> visitor) throws EvaluationException {
		return visitor.visit(this);
	}
}
