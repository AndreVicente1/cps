package ast.cont;

import interfaces.IVisitor;

public interface ICont {
	<Result> Result eval(IVisitor<Result> visitor);
}
