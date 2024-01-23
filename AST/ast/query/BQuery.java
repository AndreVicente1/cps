package ast.query;

import ast.bexp.BExp;
import ast.cont.ICont;
import interfaces.IVisitor;

public class BQuery extends Query {
    private BExp bexpr;
    private ICont cont;

    public BQuery(BExp bexpr, ICont cont) {
    	super(cont);
        this.bexpr = bexpr;
    }


    public BExp getExpression() {
        return bexpr;
    }

    public void setExpression(BExp expr) {
        this.bexpr = expr;
    }


	@Override
	public <Result> Result eval(IVisitor<Result> visitor) {
		// TODO Auto-generated method stub
		return visitor.visit(this);
	}
	
}
