package src.ast.gather;

import src.ast.exception.EvaluationException;
import src.ast.interfaces.IVisitor;
import src.fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public class RGather extends Gather{
	private final Gather next;

    public RGather(String sensorId, Gather next) {
        super(sensorId);
        this.next = next;
    }

	@Override
	public <Result> Result eval(IVisitor<Result> visitor, ExecutionStateI e) throws EvaluationException {
		// TODO Auto-generated method stub
		return visitor.visit(this, e);
	}
	public Gather getNext(){
		return next;
	}
}
