package ast.rand;

import exception.EvaluationException;
import interfaces.IVisitor;

public abstract class Rand {
	public abstract <Result> Result eval(IVisitor<Result> visitor) throws EvaluationException;
}
