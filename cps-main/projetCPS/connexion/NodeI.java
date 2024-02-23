package connexion;

import java.util.ArrayList;

import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.network.interfaces.SensorNodeP2PCI;

public interface NodeI extends NodeInfoI,ProcessingNodeI, SensorNodeP2PCI {
	ArrayList<SensorDataI> getAllSensors();
}
