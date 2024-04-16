package ast.bexp;

import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public abstract class BExp {
	public abstract Object eval(IVisitor visitor, ExecutionStateI e) throws EvaluationException;

}
