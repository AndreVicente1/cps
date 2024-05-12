package ast.bexp;

import ast.cexp.CExp;
import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

/**
 * Represents a boolean expression based on a conditional expression
 */
public class CExpBExp extends BExp{
	private static final long serialVersionUID = 1L;
	private final CExp cexp;
	
	/**
	 * Constructs a CExpBExp instance with the specified conditional expression
	 * @param cexp The conditional expression
	 */
	public CExpBExp(CExp cexp) {
		this.cexp = cexp;
	}
	
	/**
	 * Returns the conditional expression
	 * @return The conditional expression
	 */
	public CExp getCexp() {
		return cexp;
	}

	/**
	 * @see BExp#eval(IVisitor, ExecutionStateI)
	 */
	@Override
	public Object eval(IVisitor visitor, ExecutionStateI e) throws EvaluationException {
		
		return visitor.visit(this, e);
	}
	
	@Override
	public String toString() {
		return "CExpBExp:\n\t" + cexp;
	}
}
