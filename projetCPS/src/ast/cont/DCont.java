package ast.cont;

import ast.dirs.Dirs;
import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public class DCont implements ICont{
	private final Dirs dirs;
	private final int maxJumps;
	
	public DCont(Dirs dir, int maxJumps) {
        this.dirs = dir;
        this.maxJumps = maxJumps;
    }

	@Override
	public <Result> Result eval(IVisitor<Result> visitor, ExecutionStateI e) throws EvaluationException {
		// TODO Auto-generated method stub
		return visitor.visit(this, e);
	}
	
	public Dirs getDirs() {
		return dirs;
	}
	
	public int getMaxJumps() {
		return maxJumps;
	}
	
}
