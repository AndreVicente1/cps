package connexion;

import java.lang.reflect.Method;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.cps.sensor_network.interfaces.BCM4JavaEndPointDescriptorI;

public class BCM4JavaEndPointDescriptor implements BCM4JavaEndPointDescriptorI{
	private String portURI;
	private Class<? extends OfferedCI> offeredInterface;
	 
	public BCM4JavaEndPointDescriptor(String portURI, Class<? extends OfferedCI> offeredInterface){
		this.portURI = portURI;
		this.offeredInterface = offeredInterface;
	}
	@Override
	public String getInboundPortURI() {
		return portURI;
	}

	@Override
	public boolean isOfferedInterface(Class<? extends OfferedCI> inter) {
		assert inter != null : "L'interface est null";

        // verifier si l'interface demandée est egale à l'interface offerte
        if (inter.equals(this.offeredInterface)) {
            return true;
        }
 
        // vérifier si toutes les méthodes de l'interface demandée sont présentes dans l'interface offerte
        for (Method method : inter.getMethods()) {
            try {
                this.offeredInterface.getMethod(method.getName(), method.getParameterTypes());
            } catch (NoSuchMethodException e) {
                return false;
            }
        }
        
        return true;
  
	}

}
