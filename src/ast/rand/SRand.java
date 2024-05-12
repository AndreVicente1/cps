package ast.rand;

import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

/**
 * Class representing a sensor ID
 */
public class SRand extends Rand{

	private static final long serialVersionUID = 1L;
	private final String sensorId;
	
	/**
     * Creates a new SRand instance associated with the specified sensor ID
     * @param sensorId The ID of the sensor
     */
	public SRand(String sensorId) {
		this.sensorId = sensorId;
	}
	
	/**
	 * @see Rand#eval(IVisitor, ExecutionStateI)
	 */
	@Override
	public Object eval(IVisitor visitor, ExecutionStateI e) throws EvaluationException {
		
		return visitor.visit(this, e);
	}

	/**
	 * Returns the sensor ID associated with the SRand
	 * @return the sensor ID
	 */
	public String getSensorId() {
		return sensorId;
	}
	
	@Override
	public String toString() {
		return "--- SRand: " + sensorId;
	}
}
