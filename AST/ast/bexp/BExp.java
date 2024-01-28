package ast.bexp;

import exception.EvaluationException;
import interfaces.IVisitor;

public abstract class BExp {
	public abstract <Result> Result eval(IVisitor<Result> visitor) throws EvaluationException;

}
