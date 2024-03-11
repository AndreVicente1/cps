package ast.cexp;

import ast.rand.Rand;
import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public class LCExp extends CExp{
	 public LCExp(Rand rand1, Rand rand2) {
	     super(rand1, rand2);
	 }

	@Override
	public <Result> Result eval(IVisitor<Result> visitor, ExecutionStateI e) throws EvaluationException {
		
		return visitor.visit(this, e);
	}
}
