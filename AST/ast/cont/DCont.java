package ast.cont;

import ast.dirs.Dirs;
import exception.EvaluationException;
import interfaces.IVisitor;

public class DCont implements ICont{
	private final Dirs dirs;
	private final int maxJumps;
	
	public DCont(Dirs dir, int maxJumps) {
        this.dirs = dir;
        this.maxJumps = maxJumps;
    }

	@Override
	public <Result> Result eval(IVisitor<Result> visitor) throws EvaluationException {
		// TODO Auto-generated method stub
		return visitor.visit(this);
	}
	
	public Dirs getDirs() {
		return dirs;
	}
	
	public int getMaxJumps() {
		return maxJumps;
	}
	
}
