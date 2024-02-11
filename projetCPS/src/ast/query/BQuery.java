package src.ast.query;

import src.ast.bexp.BExp;
import src.ast.cont.ICont;
import src.ast.exception.EvaluationException;
import src.ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

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
	public <Result> Result eval(IVisitor<Result> visitor, ExecutionStateI e) throws EvaluationException {
		// TODO Auto-generated method stub
		return visitor.visit(this, e);
	}
	
}
