package ast.bexp;

import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public class SBExp extends BExp{
	private final String sensorId;
	
	public SBExp(String sensorId) {
		this.sensorId = sensorId;
	}
	
	public String getSensorId() {
		return sensorId;
	}

	@Override
	public <Result> Result eval(IVisitor<Result> visitor, ExecutionStateI e) throws EvaluationException {
		
		return visitor.visit(this, e);
	}
}
