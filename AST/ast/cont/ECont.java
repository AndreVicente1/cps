package ast.cont;

import exception.EvaluationException;
import interfaces.IVisitor;

public class ECont implements ICont{

	@Override
	public <Result> Result eval(IVisitor<Result> visitor) throws EvaluationException {
		// TODO Auto-generated method stub
		return visitor.visit(this);
	}
	
}
