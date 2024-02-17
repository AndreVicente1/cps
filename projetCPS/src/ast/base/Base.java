package ast.base;

import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public abstract class Base {

    public abstract <Result> Result eval(IVisitor<Result> visitor, ExecutionStateI e) throws EvaluationException;
}
