package componentTest;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.connectors.AbstractConnector;
public class CVM extends AbstractCVM {
	//TODO : rework les attributs de classes
	protected static  String uriClient = "URI_Client";
	protected static  String uriProvider = "URI_Provider";
    //ports URI
	protected static final String clientOutURI = "URI_ClientPortTestOut";
	protected static final String providerInURI = "URI_ProviderPortTestIn";


    public static void main(String[] args) throws Exception{
        CVM c = new CVM();
        c.startStandardLifeCycle(10000L); // durée de 10 secondes
        System.exit(0);
    }

    public CVM() throws Exception {

    	super();

      

    }

    //a redefinir pour creer interconexion statique entre composant
    @Override
    public void deploy() throws Exception {
    	 uriClient = AbstractComponent.createComponent(Client.class.getCanonicalName(), new Object[]{1,1, uriClient, clientOutURI});
         uriProvider = AbstractComponent.createComponent(Provider.class.getCanonicalName(), new Object[]{1,1, uriProvider, providerInURI});
        
         //creation connecteur
         AbstractConnector ReqConnector = new RequestConnector();
         
         
        // On peut ensuite les interconnecter statiquement par la méthode doPortConnection
        this.doPortConnection( //requête
                uriClient,
                clientOutURI,
                providerInURI,
                RequestConnector.class.getCanonicalName());
        /*this.doPortConnection( //résultat
                uriProvider,
                providerOutURI,
                clientInURI,
                RequestConnector.class.getCanonicalName());
        */

        super.deploy();
    }

    @Override
    public void execute() throws Exception {
        executeComponent(uriClient);
        executeComponent(uriProvider);
        super.execute();
    }

    @Override
    public void finalise() throws Exception {
        //deconnexion des ports
        this.doPortDisconnection(
                uriClient,
                clientOutURI);
        /*this.doPortDisconnection(
                uriProvider,
                providerOutURI);
        */

        //finilise des composants
        finaliseComponent(uriClient);
        finaliseComponent(uriProvider);

        super.finalise();
    }

    /*@Override
    public void shutdown() throws Exception {
        //depublier les ports sortant
        localUnpublishPort(providerOut);
        localUnpublishPort(clientOut);
        localUnpublishPort(clientIn);
        localUnpublishPort(providerIn);
        super.shutdown();
    }*/
}

