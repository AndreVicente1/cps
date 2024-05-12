package ast.cexp;

import ast.rand.Rand;

import java.io.Serializable;

import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

/**
 * Represents a conditional expression in the AST
 */
public abstract class CExp implements Serializable{
	private static final long serialVersionUID = 1L;
	protected Rand rand1;
    protected Rand rand2;

    /**
     * Constructs a CExp instance with the specified random operands
     * @param rand1 The first operand
     * @param rand2 The second operand
     */
    public CExp(Rand rand1, Rand rand2) {
        this.rand1 = rand1;
        this.rand2 = rand2;
    }

    
    /**
     * Returns the first operand associated with this conditional expression
     * @return The first operand
     */
    public Rand getRand1() {
        return rand1;
    }

    /**
     * Returns the second operand associated with this conditional expression
     * @return The second operand
     */
    public Rand getRand2() {
        return rand2;
    }

    /**
	 * Evaluates the conditional expression using the provided visitor and execution state
	 * @param visitor The visitor to apply, here we use the design pattern visitor to evaluate this AST
	 * @param e The execution state of the query
	 * @return The result of the evaluation
	 * @throws
	 */
    public abstract Object eval(IVisitor visitor, ExecutionStateI e) throws EvaluationException;
}
