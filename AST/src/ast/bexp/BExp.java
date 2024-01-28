package src.ast.bexp;

import src.ast.exception.EvaluationException;
import src.ast.interfaces.IVisitor;

public abstract class BExp {
	public abstract <Result> Result eval(IVisitor<Result> visitor) throws EvaluationException;

}
