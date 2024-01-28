package src;

import java.util.List;

public class Sensor {
    private static int sensorId = 0;
    private Object value; // bool ou double
    private final Sensor[] neighbors; // voisins du senseur
    private final String position; // position du senseur


    public Sensor(Object val, Sensor[] neighbors, String position) {
        sensorId++;
        this.value = val;
        this.neighbors = neighbors;
        this.position = position;
    }

    public int getSensorId() {
        return sensorId;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getPosition(){
        return position;
    }

    public Sensor[] getNeighbors(){
        return neighbors;
    }
}
