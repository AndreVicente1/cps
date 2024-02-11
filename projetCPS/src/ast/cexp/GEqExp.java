package src.ast.cexp;

import src.ast.rand.Rand;
import src.ast.exception.EvaluationException;
import src.ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public class GEqExp extends CExp{
	public GEqExp(Rand rand1, Rand rand2) {
        super(rand1, rand2);
    }

	@Override
	public <Result> Result eval(IVisitor<Result> visitor, ExecutionStateI e) throws EvaluationException {
		// TODO Auto-generated method stub
		return visitor.visit(this, e);
	}
	
	
}
