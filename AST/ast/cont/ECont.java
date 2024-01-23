package ast.cont;

import interfaces.IVisitor;

public class ECont implements ICont{

	@Override
	public <Result> Result eval(IVisitor<Result> visitor) {
		// TODO Auto-generated method stub
		return visitor.visit(this);
	}
	
}
