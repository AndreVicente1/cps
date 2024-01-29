package src.ast.bexp;

import src.ast.exception.EvaluationException;
import src.ast.interfaces.IVisitor;
import src.fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public class NotBExp extends BExp{
	private final BExp bexp;
	
	public NotBExp(BExp b) {
		bexp = b;
	}

	@Override
	public <Result> Result eval(IVisitor<Result> visitor, ExecutionStateI e) throws EvaluationException {
		// TODO Auto-generated method stub
		return visitor.visit(this, e);
	}
	
	public BExp getBexp() {
		return bexp;
	}
}
