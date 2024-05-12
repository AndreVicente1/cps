package ast.bexp;

import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

/**
 * Represents a logical OR operation between two boolean expressions
 */
public class OrBExp extends BExp{
	private static final long serialVersionUID = 1L;
	private final BExp bexp1;
    private final BExp bexp2;
    
    /**
     * Constructs an OrBExp instance with the specified boolean expressions
     * @param b1 The left boolean expression
     * @param b2 The right boolean expression
     */
    public OrBExp(BExp b1, BExp b2) {
        this.bexp1 = b1;
        this.bexp2 = b2;
    }

    /**
     * @see BExp#eval(IVisitor, ExecutionStateI)
     */
	@Override
	public Object eval(IVisitor visitor, ExecutionStateI e) throws EvaluationException {
		
		return visitor.visit(this, e);
	}
	
	/**
     * Returns the left boolean expression
     * @return The left boolean expression
     */
    public BExp getBExpLeft() {
        return bexp1;
    }
    
    /**
     * Returns the right boolean expression
     * @return The right boolean expression
     */
    public BExp getBExpRight() {
        return bexp2;
    }
	
    @Override
	public String toString() {
		return "OrBExp:\n\t" + bexp1 + "\n\t" + bexp2;
	}
}
