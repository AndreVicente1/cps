package ast.base;

import java.io.Serializable;

import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

/**
 * Represents a base in the sensor network
 */
public abstract class Base implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/** the position of the base */
	protected PositionI position;
	
	/**
	 * Constructs a Base instance with the specified position
	 * @param pos The position of the base
	 */
	public Base(PositionI pos){
		position = pos;
	}

	/**
	 * Evaluates the base using the provided visitor and execution state
	 * @param visitor The visitor to apply, here we use the design pattern visitor to evaluate this AST
	 * @param e The execution state of the query
	 * @return The result of the evaluation
	 * @throws
	 */
    public abstract Object eval(IVisitor visitor, ExecutionStateI e) throws EvaluationException;
}
