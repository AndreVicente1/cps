package ast.bexp;

import ast.cexp.CExp;
import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public class CExpBExp extends BExp{
	private final CExp cexp;
	
	public CExpBExp(CExp cexp) {
		this.cexp = cexp;
	}
	
	public CExp getCexp() {
		return cexp;
	}

	@Override
	public <Result> Result eval(IVisitor<Result> visitor, ExecutionStateI e) throws EvaluationException {
		
		return visitor.visit(this, e);
	}
	
	
}
