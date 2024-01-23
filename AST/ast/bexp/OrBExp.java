package ast.bexp;

import interfaces.IVisitor;

public class OrBExp extends BExp{
	private BExp bexp1;
    private BExp bexp2;
    
    public OrBExp(BExp b1, BExp b2) {
        this.bexp1 = b1;
        this.bexp2 = b2;
    }

	@Override
	public <Result> Result eval(IVisitor<Result> visitor) {
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
