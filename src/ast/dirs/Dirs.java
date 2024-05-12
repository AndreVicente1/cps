package ast.dirs;

import java.io.Serializable;

import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

/**
 * Represents the direction(s) in the AST
 */
public abstract class Dirs implements Serializable{
    private static final long serialVersionUID = 1L;
    
    /**
	 * Evaluates the directions using the provided visitor and execution state
	 * @param visitor The visitor to apply, here we use the design pattern visitor to evaluate this AST
	 * @param e The execution state of the query
	 * @return The result of the evaluation
	 * @throws
	 */
	public abstract Object eval(IVisitor visitor, ExecutionStateI e) throws EvaluationException;
    
	 /**
     * Returns the direction 
     * @return The direction
     */
	public Direction getDir() {
        return null;
    }
}
