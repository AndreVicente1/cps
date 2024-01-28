package src.ast.rand;

import src.ast.exception.EvaluationException;
import src.ast.interfaces.IVisitor;

public abstract class Rand {
	public abstract <Result> Result eval(IVisitor<Result> visitor) throws EvaluationException;
}
