package ast.base;

import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

/**
 * Represents the base position of a node
 */
public class ABase extends Base {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an ABase instance with the specified node position
	 * @param pos The position of the base
	 */
	public ABase(PositionI pos) {
		super(pos);
	}
	
	/**
	 * Obtains the position of the ABase
	 * @return The position of the ABase
	 */
	public PositionI getPos() {
		return position;
	}

	/**
	 * @see Base#eval(IVisitor, ExecutionStateI)
	 */
	@Override
	public Object eval(IVisitor visitor, ExecutionStateI e) throws EvaluationException{
		return visitor.visit(this, e);
	}
	
	@Override
	public String toString() {
		return "ABase:\n\t" + super.position;
	}
}
