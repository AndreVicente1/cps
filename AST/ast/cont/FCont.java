package ast.cont;

import ast.base.Base;
import exception.EvaluationException;
import interfaces.IVisitor;

public class FCont implements ICont{
	private final Base base;
    private final double maxDistance;

    public FCont(Base base, double maxDistance) {
        this.base = base;
        this.maxDistance = maxDistance;
    }

	@Override
	public <Result> Result eval(IVisitor<Result> visitor) throws EvaluationException {
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
