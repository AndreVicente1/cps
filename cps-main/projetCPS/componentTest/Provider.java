package componentTest;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import interpreter.Interpreter;

import java.util.ArrayList;

import Connexion.connexion.Node;
import Connexion.connexion.NodeI;
import Connexion.connexion.Requesting;
import Connexion.connexion.SensorData;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import ast.position.Position;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;

@OfferedInterfaces(offered={ProviderCI.class})

public class Provider extends AbstractComponent  {
    InboundPortProvider inp;
    RequestingCI requesting;
    NodeI node;

    protected Provider(int nbThreads, int nbSchedulableThreads,
                       String uri,
                       String uriInPort) throws Exception {
        super(uri, nbThreads, nbSchedulableThreads);
        this.inp = new InboundPortProvider(this, uriInPort);
        inp.publishPort();

        //données arbitraires
        SensorDataI sensordata = new SensorData<Double>(30.0,"nodetest","temperature");
        ArrayList<SensorDataI> sensors = new ArrayList<>();
        sensors.add(sensordata);
        PositionI position = new Position(2.5, 3.0);
        node = new Node("nodetest", sensors,position,0.0);
        
        Interpreter interpreter = new Interpreter();
        requesting = new Requesting(interpreter, node);
    }

    public QueryResultI treatRequest(RequestI request) throws Exception{
        return (QueryResultI) requesting.execute(request);
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
