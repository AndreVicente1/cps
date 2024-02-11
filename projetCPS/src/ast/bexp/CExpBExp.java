package src.ast.bexp;

import src.ast.cexp.CExp;
import src.ast.exception.EvaluationException;
import src.ast.interfaces.IVisitor;
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
		// TODO Auto-generated method stub
		return visitor.visit(this, e);
	}
	
	
}
