package src.Connexion.connexion;

import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;

import java.io.Serializable;
import java.time.Instant;

public class SensorData implements SensorDataI {
    //private int entier;
    private boolean feu;
    private String nodeId;
    private String sensorId;

    //valeur des senseurs
    private double vitesseVent;
    private Direction directionVent;

    public SensorData(boolean feu, String nodeId, String sensorId, double vitesse, Direction direction){
        //this.entier = entier;
        this.feu = feu;
        this.nodeId = nodeId;
        this.sensorId = sensorId;

        this.vitesseVent = vitesse;
        this.directionVent = direction;

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
        return this;
    }

    @Override
    public Instant getTimestamp() {
        //TODO
        return null;
    }

    public void setDataWindSpeed(double speed){
        vitesseVent = speed;
    }

    public void setDataWindDirection(Direction dir){
        directionVent = dir;
    }
}
