package test;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import src.Connexion.connexion.Request;
import src.ast.cont.ECont;
import src.ast.cont.ICont;
import src.ast.gather.FGather;
import src.ast.gather.Gather;
import src.ast.query.GQuery;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI;

@RequiredInterfaces(required={RequestCI.class})
public class Client extends AbstractComponent {
    OutboundPortClient outc;
    protected Client(int nbThreads, int nbSchedulableThreads,
                     String uriClient,
                     String uriOutPort) throws Exception{

        super(uriClient, nbThreads, nbSchedulableThreads);
        this.outc = new OutboundPortClient( this, uriOutPort);
        outc.publishPort();

    }

    public QueryResultI sendRequest(RequestI request) throws Exception{
        return this.outc.request(request);
    }

    @Override
    public void execute() throws Exception {
        super.execute();

        ICont econt = new ECont();
        Gather fgather = new FGather("0"); //TODO mettre le bon sensor id
        QueryI gquery = new GQuery(fgather,econt);

        RequestI request = new Request(false,"URI_requete", (fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI) gquery);
        sendRequest(request);
    }

    @Override
    public void shutdown() throws Exception {
        super.shutdown();
        outc.unpublishPort();
        outc.destroyPort();
    }
}
