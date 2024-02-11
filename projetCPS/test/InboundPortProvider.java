package test;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.cps.sensor_network.interfaces.QueryResultI;
import fr.sorbonne_u.cps.sensor_network.interfaces.RequestI;

public class InboundPortProvider extends AbstractInboundPort implements ProviderCI {

    public InboundPortProvider(ComponentI owner, String uri) throws Exception{
        super(uri, ProviderCI.class, owner);

        assert owner instanceof Provider;

    }

    @Override
    public QueryResultI treatRequest(RequestI request) throws Exception {
        return this.getOwner().handleRequest(
                new AbstractComponent.AbstractService<QueryResultI>() {
                    @Override
                    public QueryResultI call() throws Exception{
                        return (QueryResultI) ((Provider)this.getServiceOwner()).treatRequest(request);
                    }
                });
    }
}
