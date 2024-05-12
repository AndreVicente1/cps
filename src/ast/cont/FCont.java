package ast.cont;

import ast.base.Base;
import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

/**
 * Represents a continuation for a flooding request
 */
public class FCont extends Cont{
	private static final long serialVersionUID = 1L;
	private final Base base;
    private final double maxDistance;

    /**
     * Constructs an FCont instance with the specified base and maximum distance
     * @param base The base of the continuation
     * @param maxDistance The maximum distance for the continuation
     */
    public FCont(Base base, double maxDistance) {
        this.base = base;
        this.maxDistance = maxDistance;
    }

    /**
     * @see Cont#eval(IVisitor, ExecutionStateI)
     */
	@Override
	public Object eval(IVisitor visitor, ExecutionStateI e) throws EvaluationException {
		
		return visitor.visit(this, e);
	}
	
	/**
	 * Returns the base of the continuation
	 * @return The base
	 */
	public Base getBase() {
		return base;
	}
	
	/**
	 * Returns the maximum distance for the continuation
	 * @return The maximum distance
	 */
	public double getMaxDistance() {
		return maxDistance;
	}

	@Override
	public String toString() {
		return "FCont:\n\t" + base + "\n\tmax distance: " + maxDistance;
	}
}
