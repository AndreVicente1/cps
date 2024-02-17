package ast.gather;

import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public class FGather extends Gather{

    public FGather(String sensorId) {
        super(sensorId);
    }
    
    public <Result> Result eval(IVisitor<Result> visitor, ExecutionStateI e) throws EvaluationException {
		return visitor.visit(this, e);
    }
}
