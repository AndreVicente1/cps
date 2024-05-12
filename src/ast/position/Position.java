package ast.position;

import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;

/**
 * Represents a position in the two-dimensional space sensor network
 */
public class Position implements PositionI {
    private static final long serialVersionUID = 1L;
	private double x;
    private double y;

    /**
     * Constructs a position with the given x and y coordinates
     * @param x The x coordinate
     * @param y The y coordinate
     */
    public Position(double x, double y){
        this.x = x;
        this.y = y;
    }

    /**
     * @see fr.sorbonne_u.cps.sensor_network.interfaces.PositionI#distance(PositionI)
     */
    @Override
    public double distance(PositionI p) {
        Position tmp = (Position) p;
        double dx = x - tmp.getX();
        double dy = y - tmp.getY();

        return Math.sqrt(dx*dx + dy*dy);
    }

    /**
     * @see fr.sorbonne_u.cps.sensor_network.interfaces.PositionI#directionFrom(PositionI)
     */
    @Override
    public Direction directionFrom(PositionI p) {
    	Position other = (Position) p;
        boolean north = northOf(other);
        boolean south = southOf(other);
        boolean east = eastOf(other);
        boolean west = westOf(other);

        if (north && east) return Direction.NE;
        if (north && west) return Direction.NW;
        if (south && east) return Direction.SE;
        if (south && west) return Direction.SW;

        return null;
    }

    /**
     * @see fr.sorbonne_u.cps.sensor_network.interfaces.PositionI#northOf(PositionI)
     */
    @Override
    public boolean northOf(PositionI p) {
        return y >= ((Position)p).getY();
    }

    /**
     * @see fr.sorbonne_u.cps.sensor_network.interfaces.PositionI#southOf(PositionI)
     */
    @Override
    public boolean southOf(PositionI p) {
        return y <= ((Position)p).getY();
    }

    /**
     * @see fr.sorbonne_u.cps.sensor_network.interfaces.PositionI#eastOf(PositionI)
     */
    @Override
    public boolean eastOf(PositionI p) {
        return x >= ((Position)p).getX();
    }

    /**
     * @see fr.sorbonne_u.cps.sensor_network.interfaces.PositionI#westOf(PositionI)
     */
    @Override
    public boolean westOf(PositionI p) {
        return x <= ((Position)p).getX();
    }

    /**
     * Returns the x coordinate of this position
     * @return The x coordinate
     */
    public double getX(){
        return x;
    }

    /**
     * Returns the y coordinate of this position
     * @return The y coordinate
     */
    public double getY(){
        return y;
    }
    
    /*
     * Debug
     */
    @Override
    public String toString() {
    	return
    			"x = " + x
    			+ ", y = " + y;
    }
}
