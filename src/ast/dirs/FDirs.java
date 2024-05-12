package ast.dirs;

import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

/**
 * Represents a singular direction and the end of the Dirs recursion
 */
public class FDirs extends Dirs {
	private static final long serialVersionUID = 1L;
	private final Direction dir;
	
	/**
	 * Constructs an FDirs instance with the specified direction
	 * @param dir The fixed direction
	 */
	public FDirs(Direction dir) {
		this.dir = dir;
	}

	/**
	 * Returns the direction of the class
	 * @returns the direction
	 */
	@Override
	public Direction getDir() {
		return dir;
	}

	/**
	 * @see Dirs#eval(IVisitor, ExecutionStateI)
	 */
	@Override
	public Object eval(IVisitor visitor, ExecutionStateI e) throws EvaluationException {
		return visitor.visit(this, e);
	}
	
	@Override
	public String toString() {
		return "\tFDirs: " + dir;
	}
}
