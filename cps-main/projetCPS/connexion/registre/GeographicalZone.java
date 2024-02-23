package connexion.registre;

import ast.position.Position;
import fr.sorbonne_u.cps.sensor_network.interfaces.GeographicalZoneI;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;

/*
 * On suppose que la zone géographique est rectangulaire 
 */
public class GeographicalZone implements GeographicalZoneI{
	private double xMin, yMin; // Coin inférieur gauche du rectangle
    private double xMax, yMax; // Coin supérieur droit du rectangle
	
	public GeographicalZone(double xMin, double yMin, double xMax, double yMax) {
        this.xMin = xMin;
        this.yMin = yMin;
        this.xMax = xMax;
        this.yMax = yMax;
    }
    
	@Override
	public boolean in(PositionI p) {
		Position position = (Position) p;
		return position.getX() >= xMin && position.getX() <= xMax && 
               position.getY() >= yMin && position.getY() <= yMax;
	}
	

}
