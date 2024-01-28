package src.ast.gather;

import src.ast.exception.EvaluationException;
import src.ast.interfaces.IVisitor;

public abstract class Gather {
	protected String sensorId;

    public Gather(String sensorId) {
        this.sensorId = sensorId;
    }

    public String getSensorId() {
        return sensorId;
    }
    
    public abstract <Result> Result eval(IVisitor<Result> visitor) throws EvaluationException;
}
