package src.Connexion.connexion;

import fr.sorbonne_u.cps.sensor_network.interfaces.*;

import java.util.Set;

public class Node implements NodeI {
    private String nodeId;
    private SensorDataI sensor;
    private PositionI position;
    private double range;

    //TODO
    private Set<NodeInfoI> neighbours;

    public Node(String nodeId, SensorDataI sensor, PositionI pos, double range){
        this.nodeId = nodeId;
        this.sensor = sensor;
        this.position = pos;
        this.range = range;
    }

    @Override
    public String getNodeIdentifier() {
        return nodeId;
    }

    @Override
    public PositionI getPosition() {
        return position;
    }

    @Override
    public Set<NodeInfoI> getNeighbours() {
        //TODO pas pour l'audit 1
        return null;
    }

    @Override
    public SensorDataI getSensorData(String sensorIdentifier) {
        return sensor;
    }

    @Override
    public String nodeIdentifier() {
        return nodeId;
    }

    @Override
    public EndPointDescriptorI endPointInfo() {
        //TODO
        return null;
    }

    @Override
    public PositionI nodePosition() {
        return position;
    }

    @Override
    public double nodeRange() {
        return range;
    }

    @Override
    public EndPointDescriptorI p2pEndPointInfo() {
        //TODO
        return null;
    }
}
