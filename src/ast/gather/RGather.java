package ast.gather;

import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

/**
 * Represents a recursive gather operation in the AST
 */
public class RGather extends Gather{
	private static final long serialVersionUID = 1L;
	private final Gather next;

	/**
     * Constructs an RGather instance with the specified sensor ID and the next gather operation
     * @param sensorId The ID of the sensor to gather data from
     * @param next The next gather operation
     */
    public RGather(String sensorId, Gather next) {
        super(sensorId);
        this.next = next;
    }

    /**
     * @see Gather#eval(IVisitor, ExecutionStateI)
     */
	@Override
	public Object eval(IVisitor visitor, ExecutionStateI e) throws EvaluationException {
	
		return visitor.visit(this, e);
	}
	
	/**
	 * Returns the next gather operation
	 * @return the next gather operation
	 */
	public Gather getNext(){
		return next;
	}
	
	@Override
	public String toString() {
		return "RGather: " + sensorId + "\n\t" + next;
	}
}
