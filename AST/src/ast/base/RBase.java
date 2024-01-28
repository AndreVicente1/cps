package src.ast.base;

import src.ast.exception.EvaluationException;
import src.ast.interfaces.IVisitor;

public class RBase extends Base{
    public <Result> Result eval(IVisitor<Result> visitor) throws EvaluationException {
        return visitor.visit(this);
    }
}
