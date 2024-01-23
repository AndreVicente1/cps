package ast.rand;

import interfaces.IVisitor;

public abstract class Rand {
	public abstract <Result> Result eval(IVisitor<Result> visitor);
}
