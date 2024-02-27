package connexion;

import java.util.ArrayList;

import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;

//peut etre ne pas mélanger les deux interfaces dans une
public interface NodeI extends NodeInfoI,ProcessingNodeI {
	ArrayList<SensorDataI> getAllSensors();
}
