package src.ast.rand;

import src.ast.exception.EvaluationException;
import src.ast.interfaces.IVisitor;

public class SRand extends Rand{

	private final int sensorId;
	
	public SRand(int sensorId) {
		this.sensorId = sensorId;
	}
	
	@Override
	public <Result> Result eval(IVisitor<Result> visitor) throws EvaluationException {
		// TODO Auto-generated method stub
		return visitor.visit(this);
	}

	public int getSensorId() {
		return sensorId;
	}
}
