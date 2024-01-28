package ast.query;

import ast.cont.ICont;
import ast.gather.Gather;
import exception.EvaluationException;
import interfaces.IVisitor;

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
	public <Result> Result eval(IVisitor<Result> visitor) throws EvaluationException {
		// TODO Auto-generated method stub
		return visitor.visit(this);
	}
	
	
}
