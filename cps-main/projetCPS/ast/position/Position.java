package ast.position;

import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;

public class Position implements PositionI {
    private double x;
    private double y;

    public Position(double x, double y){
        this.x = x;
        this.y = y;
    }

    @Override
    public double distance(PositionI p) {
        Position tmp = (Position) p;
        double dx = x - tmp.getX();
        double dy = y - tmp.getY();

        return Math.sqrt(dx*dx + dy*dy);
    }

    @Override
    public Direction directionFrom(PositionI p) {
        if (northOf(p)) {
            if (eastOf(p)) {
                return Direction.NE;
            }
            if (westOf(p))
                return Direction.NW;
        } else {
            if (eastOf(p))
                return Direction.SE;
            if (westOf(p))
                return Direction.SW;
        }
        return null;

    }

    @Override
    public boolean northOf(PositionI p) {
        return y >= ((Position)p).getY();
    }

    @Override
    public boolean southOf(PositionI p) {
        return y <= ((Position)p).getY();
    }

    @Override
    public boolean eastOf(PositionI p) {
        return x >= ((Position)p).getX();
    }

    @Override
    public boolean westOf(PositionI p) {
        return x <= ((Position)p).getX();
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }
    
    /*
     * Debug
     */
    @Override
    public String toString() {
    	return
    			"x = " + x + "\n"
    			+ "y = " + y + "\n";
    }
}
