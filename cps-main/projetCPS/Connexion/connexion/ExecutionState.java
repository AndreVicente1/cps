package Connexion.connexion;

import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;

import java.util.ArrayList;
import java.util.Set;

public class ExecutionState implements ExecutionStateI {
    ProcessingNodeI currNode;
    private QueryResultI currResult;
    private boolean isDirectional;
    private Set<Direction> directions;
    private int hops;
    private int maxHops;
    private boolean isFlooding;
    private double maxDistance;

    //TODO completer, ajouter tt arguments dans le contructeur
    public ExecutionState(ProcessingNodeI pn, boolean boolRequest){
        currNode = pn;
        currResult = new QueryResult(boolRequest);
        //TODO completer
    }

    @Override
    public ProcessingNodeI getProcessingNode() {
        return currNode;
    }

    @Override
    public void updateProcessingNode(ProcessingNodeI pn) {
        currNode = pn;
    }

    @Override
    public QueryResultI getCurrentResult() {
        return currResult;
    }

    @Override
    public void addToCurrentResult(QueryResultI result) {

        currResult.gatheredSensorsValues().addAll(result.gatheredSensorsValues());
        currResult.positiveSensorNodes().addAll(result.positiveSensorNodes());
    }

    @Override
    public boolean isContinuationSet() {
        //TODO
        return false;
    }

    //A REVERIFIER on ne s'occupe pas de ça pour l'audit 1
    @Override
    public boolean isDirectional() {
        return isDirectional;
    }

    @Override
    public Set<Direction> getDirections() {
        return directions;
    }

    @Override
    public boolean noMoreHops() {
        return this.maxHops == this.hops;
    }

    @Override
    public void incrementHops() {
        this.hops = this.hops + 1;
    }

    @Override
    public boolean isFlooding() {
        return isFlooding;
    }

    @Override
    public boolean withinMaximalDistance(PositionI p) {
        //il manque une variable pr la node qui a recu initialement la requete??
        return false;
    }
}
