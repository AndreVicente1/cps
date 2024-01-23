package ast.cexp;

import ast.rand.Rand;
import interfaces.IVisitor;

public class EqCExp extends CExp{
	 public EqCExp(Rand rand1, Rand rand2) {
	        super(rand1, rand2);
	 }

	@Override
	public <Result> Result eval(IVisitor<Result> visitor) {
		// TODO Auto-generated method stub
		return visitor.visit(this);
	}
	 
}
