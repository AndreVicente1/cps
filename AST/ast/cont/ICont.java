package ast.cont;

import exception.EvaluationException;
import interfaces.IVisitor;

public interface ICont {
	<Result> Result eval(IVisitor<Result> visitor) throws EvaluationException;
}
