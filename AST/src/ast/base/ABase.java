package src.ast.base;

import src.ast.exception.EvaluationException;
import src.ast.interfaces.IVisitor;

public class ABase extends Base {
	private final int[] position; // position[0] = x, position[1] = y
	
	public ABase(int[] pos) {
		position = pos;
	}
	
	public int[] getPos() {
		return position;
	}

	public <Result> Result eval(IVisitor<Result> visitor) throws EvaluationException{
		return visitor.visit(this);
	}
}
