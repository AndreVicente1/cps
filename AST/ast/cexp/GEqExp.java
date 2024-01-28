package ast.cexp;

import ast.rand.Rand;
import exception.EvaluationException;
import interfaces.IVisitor;

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
