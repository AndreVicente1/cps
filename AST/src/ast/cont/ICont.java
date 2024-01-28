package src.ast.cont;

import src.ast.exception.EvaluationException;
import src.ast.interfaces.IVisitor;

public interface ICont {
	<Result> Result eval(IVisitor<Result> visitor) throws EvaluationException;
}
