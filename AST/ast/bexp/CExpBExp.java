package ast.bexp;

import ast.cexp.CExp;
import exception.EvaluationException;
import interfaces.IVisitor;

public class CExpBExp extends BExp{
	private final CExp cexp;
	
	public CExpBExp(CExp cexp) {
		this.cexp = cexp;
	}
	
	public CExp getCexp() {
		return cexp;
	}

	@Override
	public <Result> Result eval(IVisitor<Result> visitor) throws EvaluationException {
		// TODO Auto-generated method stub
		return visitor.visit(this);
	}
	
	
}
