package src;

import src.fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;

import java.io.Serializable;
import java.time.Instant;

public class Sensor implements SensorDataI {
    private static int cptId = 0;
    private int sensorId;
    private Object value; // bool ou double
    private final Sensor[] neighbors; // voisins du senseur
    private final int positionX;
    private final int positionY;


    public Sensor(Object val, Sensor[] neighbors, int positionX, int positionY) {
        cptId = sensorId++;
        this.value = val;
        this.neighbors = neighbors;
        this.positionX = positionX;
        this.positionY = positionY;
    }

    @Override
    public String getNodeIdentifier() {
        return null;
    }

    @Override
    public String getSensorIdentifier() {
        return Integer.toString(sensorId);
    }

    @Override
    public Class<? extends Serializable> getType() {
        return null;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public Instant getTimestamp() {
        return null;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public int[] getPosition(){
        int[] position = new int[2];
        position[0] = positionX;
        position[1] = positionY;
        return position;
    }

    public Sensor[] getNeighbors(){
        return neighbors;
    }
}
