package ast.gather;

import exception.EvaluationException;
import interfaces.IVisitor;

public class FGather extends Gather{

    public FGather(String sensorId) {
        super(sensorId);
    }
    
    public <Result> Result eval(IVisitor<Result> visitor) throws EvaluationException {
		return visitor.visit(this);
    }
}
