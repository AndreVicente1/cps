package src.ast.bexp;

import src.ast.exception.EvaluationException;
import src.ast.interfaces.IVisitor;

public class OrBExp extends BExp{
	private final BExp bexp1;
    private final BExp bexp2;
    
    public OrBExp(BExp b1, BExp b2) {
        this.bexp1 = b1;
        this.bexp2 = b2;
    }

	@Override
	public <Result> Result eval(IVisitor<Result> visitor) throws EvaluationException {
		// TODO Auto-generated method stub
		return visitor.visit(this);
	}
	
	public BExp getBExpLeft() {
		return bexp1;
	}
	
	public BExp getBExpRight() {
		return bexp2;
	}
}
