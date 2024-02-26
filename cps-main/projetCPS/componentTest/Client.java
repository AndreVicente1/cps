package componentTest;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import connexion.Request;
import ast.cont.ECont;
import ast.cont.ICont;
import ast.gather.FGather;
import ast.gather.Gather;
import ast.query.GQuery;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI;

@RequiredInterfaces(required={RequestingCI.class})
@OfferedInterfaces(offered= {RequestingCI.class})

public class Client extends AbstractComponent {
    OutboundPortClient outc; //le port sortant agit comme un RequestingCI
    QueryResultI result;
    
    protected Client(int nbThreads, int nbSchedulableThreads,
                     String uriClient,
                     String uriOutPort) throws Exception{

        super(uriClient, nbThreads, nbSchedulableThreads);
        this.outc = new OutboundPortClient( this, uriOutPort);
        outc.publishPort();

    }
	
    @Override
    public void execute() throws Exception {
        super.execute();

        //on peut mettre la requete en attribut et lors de la création dans la CVM, on définit cette requête
        ICont econt = new ECont();
        Gather fgather = new FGather("temperature"); //TODO mettre le bon sensor id
        QueryI gquery = new GQuery(fgather,econt);

        RequestI request = new Request(false,"URI_requete", (QueryI) gquery);
        result = this.outc.execute(request);
        
        printResult();
    }

    @Override
    public void shutdown() throws Exception {
        super.shutdown();
        outc.unpublishPort();
        //outc.destroyPort();
    }
    
    public void printResult() {
    	System.out.println(result);
    }
}
