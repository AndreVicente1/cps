package ast.query;

import ast.bexp.BExp;
import ast.cont.ICont;
import exception.EvaluationException;
import interfaces.IVisitor;

public class BQuery extends Query {
    private final BExp bexpr;
    private ICont cont;

    public BQuery(BExp bexpr, ICont cont) {
    	super(cont);
        this.bexpr = bexpr;
    }


    public BExp getExpression() {
        return bexpr;
    }



	@Override
	public <Result> Result eval(IVisitor<Result> visitor) throws EvaluationException {
		// TODO Auto-generated method stub
		return visitor.visit(this);
	}
	
}
