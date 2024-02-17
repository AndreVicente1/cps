package componentTest;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;
import fr.sorbonne_u.cps.sensor_network.nodes.interfaces.RequestingCI;
import interpreter.Interpreter;
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

        SensorDataI sensordata = new SensorData(false,"0","0",0.0,Direction.NE);
        PositionI position = new Position(2.5, 3.0);
        node = new Node("0", (fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI) sensordata,position,0.0);
        Interpreter interpreter = new Interpreter();
        requesting = new Requesting(interpreter);
    }

    public QueryResultI treatRequest(RequestI request) throws Exception{
        return (QueryResultI) requesting.execute(request);
    }

    @Override
    public void execute() throws Exception{
        super.execute();
        // TODO remplacer print par vrai resultat
        System.out.println("cc");
    }

    @Override
    public void shutdown() throws Exception {
        super.shutdown();
        inp.unpublishPort();
        inp.destroyPort();
    }
}
