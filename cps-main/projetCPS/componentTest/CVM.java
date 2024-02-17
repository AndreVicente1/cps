package componentTest;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.connectors.AbstractConnector;
public class CVM extends AbstractCVM {
    //composants
    private String uriClient = "URI_Client";
    private String uriProvider = "URI_Provider";
    //ports URI
    private String clientOutURI = "URI_ClientPortTestOut";
    private String providerInURI = "URI_ProviderPortTestIn";
    /*private String clientInURI = "URI_ClientPortTestIn";
    private String providerOutURI = ;
    //ports
    private PortI clientOut;
    private PortI clientIn;
    private PortI providerOut;
    private PortI providerIn;*/


    public static void main(String[] args) throws Exception{
        CVM c = new CVM();
        c.startStandardLifeCycle(10000L); // durée de 10 secondes
        System.exit(0);
    }

    public CVM() throws Exception {

        String uriClient = AbstractComponent.createComponent(Client.class.getCanonicalName(), new Object[]{1, 0});
        String uriProvider = AbstractComponent.createComponent(Provider.class.getCanonicalName(), new Object[]{1, 0});

        //creation port
        ComponentI client = (ComponentI) new Client(1,1, uriClient, clientOutURI);
        ComponentI provider = (ComponentI) new Provider(1,1, uriProvider, providerInURI);

        //creation connecteur
        AbstractConnector ReqConnector = new RequestConnector();

        /*clientOutURI =clientOut.getPortURI();
        clientInURI = clientIn.getPortURI();
        providerOutURI = providerOut.getPortURI();
        providerInURI = providerIn.getPortURI();*/

    }

    //a redefinir pour creer interconexion statique entre composant
    @Override
    public void deploy() throws Exception {

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

