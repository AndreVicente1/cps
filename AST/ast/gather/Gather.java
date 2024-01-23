package ast.gather;

import interfaces.IVisitor;

public abstract class Gather {
	protected String sensorId;

    public Gather(String sensorId) {
        this.sensorId = sensorId;
    }
    
    public abstract <Result> Result eval(IVisitor<Result> visitor);
}
