package ast.bexp;

import interfaces.IVisitor;

public class NotBExp extends BExp{
	private BExp bexp;
	
	public NotBExp(BExp b) {
		bexp = b;
	}

	@Override
	public <Result> Result eval(IVisitor<Result> visitor) {
		// TODO Auto-generated method stub
		return visitor.visit(this);
	}
	
	public BExp getBexp() {
		return bexp;
	}
}
