package ast.cexp;

import ast.rand.Rand;
import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

/**
 * Represents a conditional expression that checks for equality between two operands
 */
public class EqCExp extends CExp{
	 private static final long serialVersionUID = 1L;

	 /**
	 * Constructs an EqCExp instance with the specified random variables
	 * @param rand1 The first random operand
	 * @param rand2 The second random operand
	 */ 
	public EqCExp(Rand rand1, Rand rand2) {
	        super(rand1, rand2);
	 }

	/**
	 * @see CExp#eval(IVisitor, ExecutionStateI)
	 */
	@Override
	public Object eval(IVisitor visitor, ExecutionStateI e) throws EvaluationException {
		
		return visitor.visit(this, e);
	}
	
	@Override
	public String toString() {
		return "EqCExp:\n\t" + super.rand1 + "\n\t" + super.rand2;
	}
}
