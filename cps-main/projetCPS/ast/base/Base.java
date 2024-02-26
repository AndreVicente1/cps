package ast.base;

import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.interfaces.PositionI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public abstract class Base {
	protected PositionI position;
	
	Base(PositionI pos){
		position = pos;
	}

    public abstract <Result> Result eval(IVisitor<Result> visitor, ExecutionStateI e) throws EvaluationException;
}
