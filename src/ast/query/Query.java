package ast.query;

import ast.cont.Cont;
import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

/**
 * Represents a generic query with its continuation
 */
public abstract class Query implements fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI {
	
	private static final long serialVersionUID = 1L;
	protected Cont cont;
	
	/**
     * Constructs a query with the given continuation
     * @param cont The continuation of the query
     */

    public Query(Cont cont) {
        this.cont = cont;
    }

    /**
   	 * Evaluates the query using the provided visitor and execution state
   	 * @param visitor The visitor to apply, here we use the design pattern visitor to evaluate this AST
   	 * @param e The execution state of the query
   	 * @return The result of the evaluation
   	 * @throws
   	 */
    public abstract Object eval(IVisitor visitor, ExecutionStateI e) throws EvaluationException;

    /**
     * Returns the continuation of the query
     * @return the continuation of the query
     */
    public Cont getCont() {
        return cont;
    }

    /**
     * Sets the continuation of the query with the one in the parameter
     * @param the new continuation of the query
     */
    public void setCont(Cont cont) {
        this.cont = cont;
    }
}

