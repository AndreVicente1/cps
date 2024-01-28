package ast.rand;

import exception.EvaluationException;
import interfaces.IVisitor;

public class CRand extends Rand{
	private final double dist;
	
	public CRand(double dist) {
		this.dist = dist;
	}
	
	public double getDistance() {
		return dist;
	}
	
	public <Result> Result eval(IVisitor<Result> visitor) throws EvaluationException {
		return visitor.visit(this);
	}
}
