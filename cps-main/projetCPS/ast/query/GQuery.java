package ast.query;

import ast.cont.ICont;
import ast.gather.Gather;
import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public class GQuery extends Query{
	private final Gather gather;
	private ICont cont;
	
	public GQuery(Gather gather, ICont cont) {
		super(cont);
		this.gather = gather;
	}

	public Gather getGather(){
		return gather;
	}

	@Override
	public <Result> Result eval(IVisitor<Result> visitor, ExecutionStateI e) throws EvaluationException {
		
		return visitor.visit(this, e);
	}
	
	
}
