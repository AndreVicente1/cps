package ast.rand;

import interfaces.IVisitor;

public class CRand extends Rand{
	private double dist;
	
	public CRand(double dist) {
		this.dist = dist;
	}
	
	public double getDistance() {
		return dist;
	}
	
	public <Result> Result eval(IVisitor<Result> visitor) {
		return visitor.visit(this);
	}
}
