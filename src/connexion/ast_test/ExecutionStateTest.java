package connexion.ast_test;

import java.util.HashSet;
import java.util.Set;

import connexion.QueryResult;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;

public class ExecutionStateTest implements ExecutionStateI{
	    NodeTest initialNode;
	    ProcessingNodeI currNode;
	    private QueryResultI currResult;
	    
	    private boolean isContinuation = false;
	    private boolean isDirectional = false;
	    private Set<Direction> directions = new HashSet<>();
	    private int hops = 0;
	    private int maxHops = Integer.MAX_VALUE; //Lorsque FCont, maxHops ne compte pas
	    private boolean isFlooding = false;
	    private double maxDistance = Double.MAX_VALUE; //Lorsque DCont, maxDistance ne compte pas 

	    public ExecutionStateTest(NodeTest initialNode ,ProcessingNodeI pn, boolean boolRequest){
	        this.initialNode = initialNode;
	    	currNode = pn;
	        currResult = new QueryResult(boolRequest);
	        
	    }

	    @Override
	    public ProcessingNodeI getProcessingNode() {
	        return currNode;
	    }
	    

	    @Override
	    public void updateProcessingNode(ProcessingNodeI pn) {
	        currNode = pn;
	    }

	    // ---------- ASYNCHRONE
	    @Override
	    public QueryResultI getCurrentResult() {
	        return currResult;
	    }

	    @Override
	    public void addToCurrentResult(QueryResultI result) {

	        currResult.gatheredSensorsValues().addAll(result.gatheredSensorsValues());
	        currResult.positiveSensorNodes().addAll(result.positiveSensorNodes());
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
	        this.hops = this.hops + 1;
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
}
