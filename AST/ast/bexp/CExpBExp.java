package ast.bexp;

import ast.cexp.CExp;
import interfaces.IVisitor;

public class CExpBExp extends BExp{
	private CExp cexp;
	
	public CExpBExp(CExp cexp) {
		this.cexp = cexp;
	}
	
	public CExp getCexp() {
		return cexp;
	}

	@Override
	public <Result> Result eval(IVisitor<Result> visitor) {
		// TODO Auto-generated method stub
		return visitor.visit(this);
	}
	
	
}
