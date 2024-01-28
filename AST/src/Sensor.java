package src;

public class Sensor {
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

    public int getSensorId() {
        return sensorId;
    }

    public Object getValue() {
        return value;
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
