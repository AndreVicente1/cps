package ast.bexp;

import exception.EvaluationException;
import interfaces.IVisitor;

public class AndBExp extends BExp {
    private final BExp bexp1;
    private final BExp bexp2;

    public AndBExp(BExp b1, BExp b2) {
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
