package src.ast.cont;

import src.ast.exception.EvaluationException;
import src.ast.interfaces.IVisitor;
import src.fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public interface ICont {
	<Result> Result eval(IVisitor<Result> visitor, ExecutionStateI e) throws EvaluationException;
}
