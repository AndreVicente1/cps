package ast.gather;

import exception.EvaluationException;
import interfaces.IVisitor;

public class RGather extends Gather{
	private final Gather next;

    public RGather(String sensorId, Gather next) {
        super(sensorId);
        this.next = next;
    }

	@Override
	public <Result> Result eval(IVisitor<Result> visitor) throws EvaluationException {
		// TODO Auto-generated method stub
		return visitor.visit(this);
	}
	public Gather getNext(){
		return next;
	}
}
