package ast.bexp;

import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

/**
 * Represents a boolean expression based on a specific sensor identifier
 */
public class SBExp extends BExp{
	private static final long serialVersionUID = 1L;
	private final String sensorId;
	
	/**
	 * Constructs an SBExp instance with the specified sensor identifier
	 * @param sensorId The sensor identifier
	 */
	public SBExp(String sensorId) {
		this.sensorId = sensorId;
	}
	
	/**
	 * Returns the sensor identifier associated with this boolean expression
	 * @return The sensor identifier
	 */
	public String getSensorId() {
		return sensorId;
	}

	/**
	 * @see BExp#eval(IVisitor, ExecutionStateI)
	 */
	@Override
	public Object eval(IVisitor visitor, ExecutionStateI e) throws EvaluationException {
		
		return visitor.visit(this, e);
	}
	
	@Override
	public String toString() {
		return "SBExp: " + sensorId;
	}
}
