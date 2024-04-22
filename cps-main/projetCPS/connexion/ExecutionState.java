package connexion;

import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import components.Node;

public class ExecutionState implements ExecutionStateI {
    private static final long serialVersionUID = 1L;
	Node initialNode;
    ProcessingNodeI currNode;
    private QueryResultI currResult;
    
    private boolean isContinuation = false;
    private boolean isDirectional = false;
    private Set<Direction> directions = new HashSet<>();
    // on incrémente les hops, donc l'attribut doit être protégé
    private int hops = 0;
    private int maxHops = Integer.MAX_VALUE; //Lorsque FCont, maxHops ne compte pas
    private boolean isFlooding = false;
    private double maxDistance = Double.MAX_VALUE; //Lorsque DCont, maxDistance ne compte pas
    
    private Set<String> visited = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>()); // Empecher à ce que la requête se fasse sur un noeud déjà visité

    public ExecutionState(Node initialNode ,ProcessingNodeI pn, boolean boolRequest){
        this.initialNode = initialNode;
    	currNode = pn;
        currResult = new QueryResult(boolRequest);
        
    }
    
    public boolean wasVisited(String node) {
    	return visited.contains(node);
    }
    
    public void addVisitedNode(String p){
        visited.add(p);
        System.out.println("Node " + p + " marked as visited.");
    }
    
    public Set<String> getVisitedNodes(){
        return visited;
    }
    
    public void printVisited() {
    	System.out.println("visited nodes: ");
    	for (String s : visited) {
    		System.out.println(s);
    	}
    }
    
    @Override
    public ProcessingNodeI getProcessingNode() {
        return currNode;
    }
    

    @Override
    public void updateProcessingNode(ProcessingNodeI pn) {
    	//System.out.println("------------------- " + currNode + " update proc node to " + pn);
        currNode = pn;
    }

    // ---------- ASYNCHRONE
    @Override
    public QueryResultI getCurrentResult() {
        return currResult;
    }

    @Override
    public void addToCurrentResult(QueryResultI result) {
		if (!wasVisited(currNode.getNodeIdentifier())) {
	        visited.add(currNode.getNodeIdentifier());
	        currResult.gatheredSensorsValues().addAll(result.gatheredSensorsValues());
	        currResult.positiveSensorNodes().addAll(result.positiveSensorNodes());
	    }
    }

    // ASYNCHRONE -----------------
    
    
    
    @Override
    public boolean isContinuationSet() {
        return isContinuation;
    }

    
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
    	hops++;
    }

    @Override
    public boolean isFlooding() {
        return isFlooding;
    }

    @Override
    public boolean withinMaximalDistance(PositionI p) {
        return initialNode.getNodeInfo().nodePosition().distance(p)<maxDistance;
    }
    
    /*
     * Setters pour DCont
     */ 
    public void setIsContinuation(boolean cont) {
    	isContinuation = cont;
    }
    
    public void setIsDirectional(boolean directional) {
    	isDirectional = directional;
    }
    
    public void setMaxJumps(int max) {
    	maxHops = max;
    }
    
    /*
     * Setters pour FCont
     */
    
    public void setIsFlooding(boolean flooding) {
    	isFlooding = flooding;
    }
    
    public void setMaxDist(double dist) {
    	maxDistance = dist;
    }
    
    /*
     * Debug
     */
    @Override
    public String toString() {
    	return 
    			"ProcessingNode: " + getProcessingNode().getNodeIdentifier() + "\n"
    			+ "isContinuation: " + isContinuation + "\n"
    			+ "isDirectional: " + isDirectional + "\n"
    			+ "hops = " + hops + "\n"
    			+ "maxHops = " + maxHops + "\n"
    			+ "isFlooding: " + isFlooding + "\n"
    			+ "maxDistance = " + maxDistance + "\n"; 
    }
}
