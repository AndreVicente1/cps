package src.ast.base;

import src.ast.exception.EvaluationException;
import src.ast.interfaces.IVisitor;

public abstract class Base {

    public abstract <Result> Result eval(IVisitor<Result> visitor) throws EvaluationException;
}
