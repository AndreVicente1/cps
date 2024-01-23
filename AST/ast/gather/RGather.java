package ast.gather;

import interfaces.IVisitor;

public class RGather extends Gather{
	private Gather next;

    public RGather(String sensorId, Gather next) {
        super(sensorId);
        this.next = next;
    }

	@Override
	public <Result> Result eval(IVisitor<Result> visitor) {
		// TODO Auto-generated method stub
		return visitor.visit(this);
	}
}
