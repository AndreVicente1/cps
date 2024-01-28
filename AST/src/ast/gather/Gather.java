package src.ast.gather;

import src.ast.exception.EvaluationException;
import src.ast.interfaces.IVisitor;

public abstract class Gather {
	protected final int sensorId;

    public Gather(int sensorId) {
        this.sensorId = sensorId;
    }

    public int getSensorId() {
        return sensorId;
    }
    
    public abstract <Result> Result eval(IVisitor<Result> visitor) throws EvaluationException;
}
