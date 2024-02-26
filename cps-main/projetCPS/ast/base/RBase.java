package ast.base;

import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public class RBase extends Base{
	
	public RBase(PositionI pos) {
		super(pos);
	}

	public PositionI getPos() {
		return position;
	}
	
    public <Result> Result eval(IVisitor<Result> visitor, ExecutionStateI e) throws EvaluationException {
        return visitor.visit(this, e);
    }
}
