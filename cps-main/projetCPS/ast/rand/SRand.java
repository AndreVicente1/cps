package ast.rand;

import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public class SRand extends Rand{

	private final String sensorId;
	
	public SRand(String sensorId) {
		this.sensorId = sensorId;
	}
	
	@Override
	public <Result> Result eval(IVisitor<Result> visitor, ExecutionStateI e) throws EvaluationException {
		
		return visitor.visit(this, e);
	}

	public String getSensorId() {
		return sensorId;
	}
}
