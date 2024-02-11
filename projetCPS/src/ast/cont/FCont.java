package src.ast.cont;

import src.ast.base.Base;
import src.ast.exception.EvaluationException;
import src.ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public class FCont implements ICont{
	private final Base base;
    private final double maxDistance;

    public FCont(Base base, double maxDistance) {
        this.base = base;
        this.maxDistance = maxDistance;
    }

	@Override
	public <Result> Result eval(IVisitor<Result> visitor, ExecutionStateI e) throws EvaluationException {
		// TODO Auto-generated method stub
		return visitor.visit(this, e);
	}
	
	public Base getBase() {
		return base;
	}
	
	public double getMaxDistance() {
		return maxDistance;
	}

}
