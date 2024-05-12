package ast.gather;

import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

/**
 * Represents a gather operation for a specific sensor ID and the end of the Gather recursion
 */
public class FGather extends Gather{
    private static final long serialVersionUID = 1L;

    /**
     * Constructs an FGather instance with the specified sensor ID
     * @param sensorId The ID of the sensor to evaluate
     */
	public FGather(String sensorId) {
        super(sensorId);
    }
    
	/**
	 * @see Gather#eval(IVisitor, ExecutionStateI)
	 */
	@Override
    public Object eval(IVisitor visitor, ExecutionStateI e) throws EvaluationException {
		return visitor.visit(this, e);
    }
    
    @Override
    public String toString() {
    	return "\tFGather: " + sensorId;
    }
}
