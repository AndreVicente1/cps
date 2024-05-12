package ast.base;

import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

/**
 * Represents the base position
 */
public class RBase extends Base{
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a RBase instance with the specified position
	 * @param pos The position of the base
	 */
	public RBase(PositionI pos) {
		super(pos);
	}

	/**
	 * Obtains the position of the RBase
	 * @return The position of the RBase
	 */
	public PositionI getPos() {
		return position;
	}
	
	/**
	 * @see Base#eval(IVisitor, ExecutionStateI)
	 */
	@Override
    public Object eval(IVisitor visitor, ExecutionStateI e) throws EvaluationException {
        return visitor.visit(this, e);
    }
    
    @Override
    public String toString() {
    	return "RBase:\n\t" + super.position;
    }
}
