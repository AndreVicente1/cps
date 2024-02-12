package ast.base;

import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

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
