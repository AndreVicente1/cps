package ast.dirs;

import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public class RDirs extends Dirs{
	private final Direction dir; //NE || NW || SE || SW
	private final Dirs dirs;
	
	public RDirs(Direction dir, Dirs dirs) {
		this.dir = dir;
		this.dirs = dirs;
	}
	@Override
	public Direction getDir() {
		return dir;
	}
	
	public Dirs getDirs() {
		return dirs;
	}

	@Override
	public <Result> Result eval(IVisitor<Result> visitor, ExecutionStateI e) throws EvaluationException {
		return visitor.visit(this, e);
	}
}
