package ast.rand;

import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

/**
 * Represents a constant value
 */
public class CRand extends Rand{
	private static final long serialVersionUID = 1L;
	private final double cst;
	
	/**
     * Constructs a constant random value with the given constant
     * @param cst The constant value
     */
	public CRand(double cst) {
		this.cst = cst;
	}
	
	/**
     * Gets the constant value of this constant value
     * @return The constant value
     */
	public double getConstante() {
		return cst;
	}
	
	/**
	 * @see Rand#eval(IVisitor, ExecutionStateI)
	 */
	public Object eval(IVisitor visitor, ExecutionStateI e) throws EvaluationException {
		return visitor.visit(this, e);
	}
	
	@Override
	public String toString() {
		return "--- CRand: " + Double.toString(cst);
	}
}
