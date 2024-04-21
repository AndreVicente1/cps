package connexion;

import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;

import java.io.Serializable;
import java.time.Instant;

public class SensorData<T extends Serializable> implements SensorDataI {
    private static final long serialVersionUID = 1L;
	private T data;
    private String nodeId;
    private String sensorId;
    private Instant timestamp;

    public SensorData(T data, String nodeId, String sensorId){
        this.data = data;
        this.nodeId = nodeId;
        this.sensorId = sensorId;
        this.timestamp = Instant.now();
    }

    public String		getNodeIdentifier(){
        return nodeId;
    }

    @Override
    public String getSensorIdentifier() {
        return sensorId;
    }

    @Override
    public Class<? extends Serializable> getType() {
        return data.getClass();
    }

    @Override
    public Serializable getValue() {
        return data;
    }

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
