package ast.bexp;

import interfaces.IVisitor;

public class SBExp extends BExp{
	private int sensorId;
	
	public SBExp(int sensorId) {
		this.sensorId = sensorId;
	}
	
	public int getSensorId() {
		return sensorId;
	}

	@Override
	public <Result> Result eval(IVisitor<Result> visitor) {
		// TODO Auto-generated method stub
		return visitor.visit(this);
	}
}
