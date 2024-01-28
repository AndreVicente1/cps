package ast.bexp;

import exception.EvaluationException;
import interfaces.IVisitor;

public class SBExp extends BExp{
	private final int sensorId;
	
	public SBExp(int sensorId) {
		this.sensorId = sensorId;
	}
	
	public int getSensorId() {
		return sensorId;
	}

	@Override
	public <Result> Result eval(IVisitor<Result> visitor) throws EvaluationException {
		// TODO Auto-generated method stub
		return visitor.visit(this);
	}
}
