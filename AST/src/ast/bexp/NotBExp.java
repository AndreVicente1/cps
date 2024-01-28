package src.ast.bexp;

import src.ast.exception.EvaluationException;
import src.ast.interfaces.IVisitor;

public class NotBExp extends BExp{
	private final BExp bexp;
	
	public NotBExp(BExp b) {
		bexp = b;
	}

	@Override
	public <Result> Result eval(IVisitor<Result> visitor) throws EvaluationException {
		// TODO Auto-generated method stub
		return visitor.visit(this);
	}
	
	public BExp getBexp() {
		return bexp;
	}
}
