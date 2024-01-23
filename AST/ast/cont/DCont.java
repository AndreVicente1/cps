package ast.cont;

import ast.dirs.Dirs;
import interfaces.IVisitor;

public class DCont implements ICont{
	private Dirs dirs;
	private int maxJumps;
	
	public DCont(Dirs dir, int maxJumps) {
        this.dirs = dirs;
        this.maxJumps = maxJumps;
    }

	@Override
	public <Result> Result eval(IVisitor<Result> visitor) {
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
