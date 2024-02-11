package src.ast.base;

import src.ast.exception.EvaluationException;
import src.ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public class RBase extends Base{
    public <Result> Result eval(IVisitor<Result> visitor, ExecutionStateI e) throws EvaluationException {
        return visitor.visit(this, e);
    }
}
