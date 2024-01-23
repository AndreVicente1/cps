package ast.cont;

import ast.base.Base;
import interfaces.IVisitor;

public class FCont implements ICont{
	private Base base;
    private double maxDistance;

    public FCont(Base base, double maxDistance) {
        this.base = base;
        this.maxDistance = maxDistance;
    }

	@Override
	public <Result> Result eval(IVisitor<Result> visitor) {
		// TODO Auto-generated method stub
		return visitor.visit(this);
	}
	
	public Base getBase() {
		return base;
	}
	
	public double getMaxDistance() {
		return maxDistance;
	}

}
