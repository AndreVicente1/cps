package ast.cexp;

import ast.rand.Rand;
import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public class EqCExp extends CExp{
	 public EqCExp(Rand rand1, Rand rand2) {
	        super(rand1, rand2);
	 }

	@Override
	public Object eval(IVisitor visitor, ExecutionStateI e) throws EvaluationException {
		
		return visitor.visit(this, e);
	}
	 
}
