package Connexion.connexion;

import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;

import java.io.Serializable;
import java.time.Instant;

public class SensorData<T extends Serializable> implements SensorDataI {
    private T data;
    private String nodeId;
    private String sensorId;


    public SensorData(T data, String nodeId, String sensorId){
        this.data = data;
        this.nodeId = nodeId;
        this.sensorId = sensorId;

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
        //TODO
        return null;
    }

    @Override
    public Serializable getValue() {
    	System.out.println("VALUE = ");
        return data;
    }

    @Override
    public Instant getTimestamp() {
        //TODO
        return null;
    }

    public void setData(T data){
        this.data = data;
    }
}
