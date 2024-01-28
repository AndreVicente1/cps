package src.ast.gather;

import src.ast.exception.EvaluationException;
import src.ast.interfaces.IVisitor;

public class FGather extends Gather{

    public FGather(int sensorId) {
        super(sensorId);
    }
    
    public <Result> Result eval(IVisitor<Result> visitor) throws EvaluationException {
		return visitor.visit(this);
    }
}
