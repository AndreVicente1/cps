package ast.bexp;

import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public class NotBExp extends BExp{
	private final BExp bexp;
	
	public NotBExp(BExp b) {
		bexp = b;
	}

	@Override
	public Object eval(IVisitor visitor, ExecutionStateI e) throws EvaluationException {
		
		return visitor.visit(this, e);
	}
	
	public BExp getBexp() {
		return bexp;
	}
}
