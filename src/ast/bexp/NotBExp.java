package ast.bexp;

import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;


/**
 * Represents a logical NOT operation on a boolean expression
 */
public class NotBExp extends BExp{
	private static final long serialVersionUID = 1L;
	private final BExp bexp;
	
	/**
	 * Constructs a NotBExp instance with the specified boolean expression
	 * @param b The boolean expression to negate
	 */
	public NotBExp(BExp b) {
		bexp = b;
	}

	/**
	 * @see BExp#eval(IVisitor, ExecutionStateI)
	 */
	@Override
	public Object eval(IVisitor visitor, ExecutionStateI e) throws EvaluationException {
		
		return visitor.visit(this, e);
	}
	
	/**
	 * Returns the boolean expression to negate
	 * @return The boolean expression to negate
	 */
	public BExp getBexp() {
		return bexp;
	}
	
	@Override
	public String toString() {
		return "NotBExp:\n\t" + bexp;
	}
}
