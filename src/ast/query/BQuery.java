package ast.query;

import ast.bexp.BExp;
import ast.cont.Cont;
import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

/**
 * Represents a query based on a boolean expression
 */
public class BQuery extends Query {
    private static final long serialVersionUID = 1L;
	private final BExp bexpr;

	/**
     * Constructs a query with the given boolean expression and continuation
     * @param bexpr The boolean expression
     * @param cont The continuation
     */
    public BQuery(BExp bexpr, Cont cont) {
    	super(cont);
        this.bexpr = bexpr;
    }

    /**
     * Returns the boolean expression of this query
     * @return The boolean expression
     */
    public BExp getExpression() {
        return bexpr;
    }

    /**
     * @see Query#eval(IVisitor, ExecutionStateI)
     */
	@Override
	public Object eval(IVisitor visitor, ExecutionStateI e) throws EvaluationException {
		
		return visitor.visit(this, e);
	}
	
	@Override
	public String toString() {
		return "BQuery:\n\t" + bexpr + "\n" + cont;
	}
}
