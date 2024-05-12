package ast.cont;

import ast.dirs.Dirs;
import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

/**
 * Represents a continuation for a directional request
 */
public class DCont extends Cont{
	private static final long serialVersionUID = 1L;
	private final Dirs dirs;
	private final int maxJumps;
	
	/**
	 * Constructs a DCont instance with the specified direction and maximum number of jumps
	 * @param dir The direction of the continuation
	 * @param maxJumps The maximum number of jumps
	 */
	public DCont(Dirs dir, int maxJumps) {
        this.dirs = dir;
        this.maxJumps = maxJumps;
    }

	/**
	 * @see Cont#eval(IVisitor, ExecutionStateI)
	 */
	@Override
	public Object eval(IVisitor visitor, ExecutionStateI e) throws EvaluationException {
		
		return visitor.visit(this, e);
	}
	
	/**
	 * Returns the directions of the continuation
	 * @return the directions of the continuation
	 */
	public Dirs getDirs() {
		return dirs;
	}
	
	/**
	 * Returns the number of max jumps of the continuation
	 * @return the number of max jumps
	 */
	public int getMaxJumps() {
		return maxJumps;
	}
	
	@Override
	public String toString() {
		return "DCont:\n\t" + dirs + "\n" + "max jumps: " + maxJumps;
	}
}
