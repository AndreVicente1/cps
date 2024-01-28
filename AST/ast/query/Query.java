package ast.query;

import ast.cont.ICont;
import exception.EvaluationException;
import interfaces.IVisitor;

public abstract class Query {
	
	protected ICont cont;
	
    public Query(ICont cont) {
        this.cont = cont;
    }

    public abstract <Result> Result eval(IVisitor<Result> visitor) throws EvaluationException;

    public ICont getCont() {
        return cont;
    }

    public void setCont(ICont cont) {
        this.cont = cont;
    }
}

