package ast.query;

import ast.cont.ICont;
import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public abstract class Query implements fr.sorbonne_u.cps.sensor_network.requests.interfaces.QueryI {
	
	protected ICont cont;
	
    public Query(ICont cont) {
        this.cont = cont;
    }

    public abstract <Result> Result eval(IVisitor<Result> visitor, ExecutionStateI e) throws EvaluationException;

    public ICont getCont() {
        return cont;
    }

    public void setCont(ICont cont) {
        this.cont = cont;
    }
}

