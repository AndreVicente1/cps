package connexion;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.cps.sensor_network.interfaces.BCM4JavaEndPointDescriptorI;

public class BCM4JavaEndPointDescriptor implements BCM4JavaEndPointDescriptorI{
	private String portURI;
	private Set<Class<? extends OfferedCI>> offeredInterfaces = new HashSet<>();
	//asynchrone uri du client, mettre port entrant au client
	 
	public BCM4JavaEndPointDescriptor(String portURI){
		this.portURI = portURI;
	}
	@Override
	public String getInboundPortURI() {
		return portURI;
	}

	@Override
	public boolean isOfferedInterface(Class<? extends OfferedCI> inter) {
		assert inter != null : "L'interface est null";

        return offeredInterfaces.contains(inter);
  
	}
	
	@Override
	public String toString() {
		return portURI;
	}

}
