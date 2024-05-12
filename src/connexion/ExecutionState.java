package connexion;

import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents the current state of query processing, evolving as it passes between nodes.
 * It maintains various status flags and parameters to control the processing flow across the sensor network
 */
public class ExecutionState implements ExecutionStateI {
    private static final long serialVersionUID = 1L;
    
    /** initial position, given when the executionState is created */
	PositionI initialPos;
	/** The current node processing the query */
    ProcessingNodeI currNode;
    /** The current result of the query being processed */
    private QueryResultI currResult;
    
    /** Flag to determine if the query should continue processing */
    private boolean isContinuation = false;
    /** Flag to determine if processing should consider specific directions */
    private boolean isDirectional = false;
    /** Directions to consider to send the request to */
    private Set<Direction> directions = new HashSet<>();
    /** The number of hops made so far, increased each time the query jumps from node to node */
    private int hops = 0;
    /** Max hops allowed */
    private int maxHops = Integer.MAX_VALUE;
    /** Flag to determine if the processing should flood the network */
    private boolean isFlooding = false;
    /** Max distance to consider, once reached, the query won't spread any further */
    private double maxDistance = Double.MAX_VALUE;

    /**
     * Constructs an ExecutionState with specified initial position, processing node, and query result initial state
     * 
     * @param initialPos The initial position of the query
     * @param pn The initial processing node, the execution state's processing node will be modified through the updateProcessingNode method
     * @param boolRequest The boolean that tells if the request is a boolean request
     */
    public ExecutionState(PositionI initialPos ,ProcessingNodeI pn, boolean boolRequest){
        this.initialPos = initialPos;
    	currNode = pn;
        currResult = new QueryResult(boolRequest);
        
    }
    
    /**
     * @see fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI#getProcessingNode()
     */
    @Override
    public ProcessingNodeI getProcessingNode() {
        return currNode;
    }
    
    /**
     * @see fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI#updateProcessingNode(ProcessingNodeI)
     */
    @Override
    public void updateProcessingNode(ProcessingNodeI pn) {
    	//System.out.println("------------------- " + currNode + " update proc node to " + pn);
        currNode = pn;
    }

    // ---------- ASYNCHRONE
    /**
     * @see fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI#getCurrentResult()
     */
    @Override
    public QueryResultI getCurrentResult() {
        return currResult;
    }

    /**
     * @see fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI#addToCurrentResult(QueryResultI)
     */
    @Override
    public void addToCurrentResult(QueryResultI result) {
        currResult.gatheredSensorsValues().addAll(result.gatheredSensorsValues());
        currResult.positiveSensorNodes().addAll(result.positiveSensorNodes());
    }

    // ASYNCHRONE -----------------
    
    
    /**
     * @see fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI#isContinuationSet()
     */
    @Override
    public boolean isContinuationSet() {
        return isContinuation;
    }

    /**
     * @see fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI#isDirectional()
     */
    @Override
    public boolean isDirectional() {
        return isDirectional;
    }

    /**
     * @see fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI#getDirections()
     */
    @Override
    public Set<Direction> getDirections() {
        return directions;
    }

    /**
     * @see fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI#noMoreHops()
     */
    @Override
    public boolean noMoreHops() {
        return this.maxHops == this.hops;
    }

    /**
     * @see fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI#incrementHops()
     */
    @Override
    public void incrementHops() {
    	hops++;
    }

    /**
     * @see fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI#isFlooding()
     */
    @Override
    public boolean isFlooding() {
        return isFlooding;
    }

    /**
     * @see fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI#withinMaximalDistance(PositionI)
     */
    @Override
    public boolean withinMaximalDistance(PositionI p) {
        return initialPos.distance(p)<maxDistance;
    }
    
    /*
     *Setters for DCont continuation
     */ 
    
    /**
     * Sets whether the processing should continue beyond the initial node
     * 
     * @param cont True if processing should continue; false otherwise
     */
    public void setIsContinuation(boolean cont) {
    	isContinuation = cont;
    }
    
    /**
     * Sets whether the processing should consider directional constraints
     * 
     * @param directional True if processing is directional; false otherwise
     */
    public void setIsDirectional(boolean directional) {
    	isDirectional = directional;
    }
    
    /**
     * Sets the maximum number of hops the query can make,
     * this method is used only if the query goes to specific directions
     * 
     * @param max The maximum number of hops allowed
     */
    public void setMaxJumps(int max) {
    	maxHops = max;
    }
    
    
    /*
     * Setters for FCont continuation
     */
    
    /**
     * Sets whether the processing should flood the network, the query will be send to all directions
     * 
     * @param flooding True if processing should use flooding; false otherwise
     */
    public void setIsFlooding(boolean flooding) {
    	isFlooding = flooding;
    }
    
    /**
     * Sets the maximum distance from the initial position that the query can consider,
     * this method is used only if the query wants to flood the network
     * 
     * @param dist The maximum distance allowed
     */
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
