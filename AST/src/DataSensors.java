package src;

import java.util.List;

public final class DataSensors {
    private static int size = 0;
    private static Sensor[] sensors = new Sensor[size];
    DataSensors(Sensor[] sensors, int size){
        DataSensors.sensors = sensors;
        DataSensors.size = size;
    }

    public static Sensor getSensorById(int sensorId){
        return sensors[sensorId];
    }
}
