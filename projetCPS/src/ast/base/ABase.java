package src.ast.base;

import src.ast.exception.EvaluationException;
import src.ast.interfaces.IVisitor;
import src.fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import src.fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public class ABase extends Base {
	private final PositionI position;
	
	public ABase(PositionI pos) {
		position = pos;
	}
	
	public PositionI getPos() {
		return position;
	}

	public <Result> Result eval(IVisitor<Result> visitor, ExecutionStateI e) throws EvaluationException{
		return visitor.visit(this, e);
	}
}
