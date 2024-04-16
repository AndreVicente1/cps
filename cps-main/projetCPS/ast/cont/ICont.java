package ast.cont;

import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public interface ICont {
	Object eval(IVisitor visitor, ExecutionStateI e) throws EvaluationException;
}
