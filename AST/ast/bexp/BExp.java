package ast.bexp;

import interfaces.IVisitor;

public abstract class BExp {
	public abstract <Result> Result eval(IVisitor<Result> visitor);

}
