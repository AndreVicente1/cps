package connexion;

import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;

import java.io.Serializable;
import java.time.Instant;

/**
 * Represents sensor data obtained from a sensor node in a sensor network
 * @param <T> The type of data being sensed
 */
public class SensorData<T extends Serializable> implements SensorDataI {
    private static final long serialVersionUID = 1L;
    
    /** The sensed data */
	private T data;

    /** The identifier of the node where the sensor is located */
    private String nodeId;

    /** The identifier of the sensor */
    private String sensorId;

    /** The timestamp indicating when the data was collected */
    private Instant timestamp;

    /**
     * Constructs a sensor data with the specified data, node identifier, and sensor identifier
     * @param data The sensed data
     * @param nodeId The identifier of the node where the sensor is located
     * @param sensorId The identifier of the sensor
     */
    public SensorData(T data, String nodeId, String sensorId){
        this.data = data;
        this.nodeId = nodeId;
        this.sensorId = sensorId;
        this.timestamp = Instant.now();
    }

    /**
     * Obtains the identifier of the node where the sensor data was collected.
     * @return The node identifier.
     */
    public String		getNodeIdentifier(){
        return nodeId;
    }

    /**
     * @see fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI#getSensorIdentifier()
     */
    @Override
    public String getSensorIdentifier() {
        return sensorId;
    }

    /**
     * @see fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI#getType()
     */
    @Override
    public Class<? extends Serializable> getType() {
        return data.getClass();
    }

    /**
     * @see fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI#getValue()
     */
    @Override
    public Serializable getValue() {
        return data;
    }

    /**
     * @see fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI#getTimestamp()
     */
    @Override
    public Instant getTimestamp() {
       return timestamp;
    }

    /**
     * Sets the data for this sensor data instance.
     * @param data The new data to set.
     */
    public void setData(T data){
        this.data = data;
    }
}
