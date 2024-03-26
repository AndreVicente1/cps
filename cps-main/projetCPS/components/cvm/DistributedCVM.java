package components.cvm;

import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;

public class DistributedCVM extends AbstractDistributedCVM {
	//ajouter les ports + URI des composants JVM
	public DistributedCVM(String[] args) throws Exception {
		super(args);
	}

	public static void main(String[] args) {
		try {
			DistributedCVM c = new DistributedCVM(args);
			c.startStandardLifeCycle(10000L); // durée de 10 secondes
	        Thread.sleep(10000L);
	        System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void instantiateAndPublish() throws Exception {
		String jvmURI = AbstractCVM.getThisJVMURI();
		if (jvmURI.equals( )) { // Compo 1
			//Create compo 1
			//si besoin d'un attribut (par exemple uri d'un compo après l'avoir créer, le mettre en atrribut
		} else if (jvmURI.equals( )) { // Compo 2
			//Create compo 2
		} else {
			System.out.println("Unknown JVM URI: " + jvmURI);
		}
		super.instantiateAndPublish();
	}

	@Override
	public void interconnect() throws Exception {
		//Connexion ports
		String jvmURI = AbstractCVM.getThisJVMURI();
		if (jvmURI.equals( )) { // Compo 1
			
		} else if (jvmURI.equals( )) { // Compo 2
			
		} else {
			System.out.println("Unknown JVM URI: " + jvmURI);
		}
		super.interconnect();
	}

}
