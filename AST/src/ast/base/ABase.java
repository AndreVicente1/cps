package src.ast.base;

import src.ast.exception.EvaluationException;
import src.ast.interfaces.IVisitor;

public class ABase extends Base {
	private final String position;
	
	public ABase(String pos) {
		position = pos;
	}
	
	public String getPos() {
		return position;
	}

	public <Result> Result eval(IVisitor<Result> visitor) throws EvaluationException{
		return visitor.visit(this);
	}
}
