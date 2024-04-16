package ast.gather;

import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public abstract class Gather{
	protected final String sensorId;

    public Gather(String sensorId) {
        this.sensorId = sensorId;
    }

    public String getSensorId() {
        return sensorId;
    }
    
    public abstract Object eval(IVisitor visitor, ExecutionStateI e) throws EvaluationException;
}
