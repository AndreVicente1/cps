package ast.cexp;

import ast.rand.Rand;
import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

/**
 * Represents a conditional expression that checks if the first operand is greater than or equal to the second operand
 */
public class GEqExp extends CExp{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a GEqExp instance with the specified operands
	 * @param rand1 The first operand
	 * @param rand2 The second operand
	 */
	public GEqExp(Rand rand1, Rand rand2) {
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
		return "GEqCExp:\n\t" + super.rand1 + "\n\t" + super.rand2;
	}
}
