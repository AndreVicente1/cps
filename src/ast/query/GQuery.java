package ast.query;

import ast.gather.Gather;
import ast.cont.Cont;
import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

/**
 * Represents a gather query
 */
public class GQuery extends Query{
	private static final long serialVersionUID = 1L;
	private final Gather gather;
	
	/**
     * Constructs a gather query with the given gather operation and continuation
     * @param gather The gather operation
     * @param cont The continuation
     */
	public GQuery(Gather gather, Cont cont) {
		super(cont);
		this.gather = gather;
	}
	
	/**
     * Returns the gather operation of this query
     * @return The gather operation
     */
	public Gather getGather(){
		return gather;
	}

	/**
	 * @see Query#eval(IVisitor, ExecutionStateI)
	 */
	@Override
	public Object eval(IVisitor visitor, ExecutionStateI e) throws EvaluationException {
		
		return visitor.visit(this, e);
	}
	
	@Override
	public String toString() {
		return "GQuery:\n\t" + gather + "\n\t" + cont;
	}
}
