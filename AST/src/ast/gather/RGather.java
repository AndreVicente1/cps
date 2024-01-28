package src.ast.gather;

import src.ast.exception.EvaluationException;
import src.ast.interfaces.IVisitor;

public class RGather extends Gather{
	private final Gather next;

    public RGather(int sensorId, Gather next) {
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
