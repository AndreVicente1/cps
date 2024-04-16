package ast.cont;

import ast.base.Base;
import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public class FCont implements ICont{
	private final Base base;
    private final double maxDistance;

    public FCont(Base base, double maxDistance) {
        this.base = base;
        this.maxDistance = maxDistance;
    }

	@Override
	public Object eval(IVisitor visitor, ExecutionStateI e) throws EvaluationException {
		
		return visitor.visit(this, e);
	}
	
	public Base getBase() {
		return base;
	}
	
	public double getMaxDistance() {
		return maxDistance;
	}

}
