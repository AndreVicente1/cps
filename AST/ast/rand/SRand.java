package ast.rand;

import interfaces.IVisitor;

public class SRand extends Rand{

	private int sensorId;
	
	public SRand(int sensorId) {
		this.sensorId = sensorId;
	}
	
	@Override
	public <Result> Result eval(IVisitor<Result> visitor) {
		// TODO Auto-generated method stub
		return visitor.visit(this);
	}

	public int getSensorId() {
		return sensorId;
	}
}
