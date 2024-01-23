package ast.gather;

import interfaces.IVisitor;

public class FGather extends Gather{

    public FGather(String sensorId) {
        super(sensorId);
    }
    
    public <Result> Result eval(IVisitor<Result> visitor) {
		return visitor.visit(this);
    }
}
