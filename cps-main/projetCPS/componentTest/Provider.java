package componentTest;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import interpreter.Interpreter;

import java.util.ArrayList;

import connexion.Node;
import connexion.NodeI;
import connexion.RequestContinuation;
import connexion.Requesting;
import connexion.SensorData;
import ast.position.Position;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestContinuationI;

@OfferedInterfaces(offered={ProviderCI.class})

public class Provider extends AbstractComponent  {
    InboundPortProvider inp;
    RequestingCI requesting;
    NodeI node;
    OutboundPortProvider outp;

    public Provider(int nbThreads, int nbSchedulableThreads,
                       String uri,
                       String uriInPort,
                       String uriOutPort) throws Exception {
        super(uri, nbThreads, nbSchedulableThreads);
        this.inp = new InboundPortProvider(this, uriInPort);
        inp.publishPort();
        
        this.outp = new OutboundPortProvider(this, uriOutPort);
        outp.publishPort();

        //données arbitraires
        SensorDataI sensordata = new SensorData<Double>(30.0,"nodetest","temperature");
        ArrayList<SensorDataI> sensors = new ArrayList<>();
        sensors.add(sensordata);
        PositionI position = new Position(2.5, 3.0);
        node = new Node("nodetest", sensors,position,0.0, null);
        
        Interpreter interpreter = new Interpreter();
        requesting = new Requesting(interpreter, node);
    }

    public QueryResultI treatRequest(RequestI request) throws Exception{
        if (request instanceof RequestContinuationI){
            RequestContinuationI requestcont = (RequestContinuationI) request;
            requestcont.getExecutionState();
        }
    	//local
       	QueryResultI res = requesting.execute(request);
       	
        //envoie de la requete via port sortant
       	ExecutionStateI exec = ((Requesting) requesting).getExecutionState();
        if (!exec.noMoreHops() || exec.withinMaximalDistance(node.getPosition())) {
            //poursuite du calcul
        	RequestContinuationI reqCont = new RequestContinuation(
        			request.isAsynchronous(), 
        			request.requestURI(), 
        			request.getQueryCode(),
        			exec);
        	return (QueryResultI) outp.request(reqCont);
        }
        
        return res;
    }

    @Override
    public void execute() throws Exception{
        super.execute();
        // TODO remplacer print par vrai resultat
        System.out.println("execute provider");
    }

    @Override
    public void shutdown() throws Exception {
        super.shutdown();
        inp.unpublishPort();
        //inp.destroyPort();
    }
}
