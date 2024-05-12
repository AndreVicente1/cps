package ast.dirs;

import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;


/**
 * Represents a recursive direction 
 */
public class RDirs extends Dirs{
	private static final long serialVersionUID = 1L;
	private final Direction dir; //NE || NW || SE || SW
	private final Dirs dirs;
	
	/**
	 * Constructs an RDirs instance with the specified direction and sub-directions
	 * @param dir The first direction
	 * @param dirs The sub-directions
	 */
	public RDirs(Direction dir, Dirs dirs) {
		this.dir = dir;
		this.dirs = dirs;
	}
	
	/**
	 * @see Dirs#getDir()
	 */
	@Override
	public Direction getDir() {
		return dir;
	}
	
	/**
	 * Returns the sub-directions
	 * @return the sub-directions
	 */
	public Dirs getDirs() {
		return dirs;
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
		return "\tRDirs: " + dir + "\n\t" + dirs;
	}
}
