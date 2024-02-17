package Connexion.connexion;

import java.util.ArrayList;

import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;

public interface NodeI extends NodeInfoI,ProcessingNodeI {
	ArrayList<SensorDataI> getAllSensors();
}
