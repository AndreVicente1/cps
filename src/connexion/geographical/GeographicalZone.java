package connexion.geographical;

import ast.position.Position;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;

/*
 * Represents a geographical zone in the sensor network.
 * This geographical zone is a rectangle that can or can not contain nodes.
 * The Client component will use this geographical zone to send its request to one node in that area
 */
public class GeographicalZone implements GeographicalZoneI{
	private static final long serialVersionUID = 1L;
	
	/** Bottom left corner of the rectangle */
	private double xMin, yMin;
	/**  Top right corner of the rectangle */
    private double xMax, yMax; 
	
    /**
     * Creates a new GeographicalZone instance with the specified coordinates
     * @param xMin The x-coordinate of the bottom left corner of the rectangle
     * @param yMin The y-coordinate of the bottom left corner of the rectangle
     * @param xMax The x-coordinate of the top right corner of the rectangle
     * @param yMax The y-coordinate of the top right corner of the rectangle
     */
	public GeographicalZone(double xMin, double yMin, double xMax, double yMax) {
        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;
    }
    
	 /**
     * Checks if the given position is inside this geographical zone
     * @param p The position to check
     * @return True if the position is inside the zone, false otherwise
     */
	@Override
	public boolean in(PositionI p) {
		Position position = (Position) p;
		return position.getX() >= xMin && position.getX() <= xMax && 
               position.getY() >= yMin && position.getY() <= yMax;
	}
	

}
