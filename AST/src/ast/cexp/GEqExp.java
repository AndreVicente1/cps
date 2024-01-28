package src.ast.cexp;

import src.ast.rand.Rand;
import src.ast.exception.EvaluationException;
import src.ast.interfaces.IVisitor;

public class GEqExp extends CExp{
	public GEqExp(Rand rand1, Rand rand2) {
        super(rand1, rand2);
    }

	@Override
	public <Result> Result eval(IVisitor<Result> visitor) throws EvaluationException {
		// TODO Auto-generated method stub
		return visitor.visit(this);
	}
	
	
}
