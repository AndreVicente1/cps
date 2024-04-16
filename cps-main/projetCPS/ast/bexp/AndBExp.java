package ast.bexp;

import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public class AndBExp extends BExp {
    private final BExp bexp1;
    private final BExp bexp2;

    public AndBExp(BExp b1, BExp b2) {
        this.bexp1 = b1;
        this.bexp2 = b2;
    }

	@Override
	public Object eval(IVisitor visitor, ExecutionStateI e) throws EvaluationException {
		
		return visitor.visit(this, e);
	}
	
	public BExp getBExpLeft() {
		return bexp1;
	}
	
	public BExp getBExpRight() {
		return bexp2;
	}

}
