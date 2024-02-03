package src.ast.dirs;

import src.ast.exception.EvaluationException;
import src.ast.interfaces.IVisitor;
import src.fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import src.fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public class FDirs extends Dirs {
	private final Direction dir;
	
	public FDirs(Direction dir) {
		this.dir = dir;
	}

	@Override
	public Direction getDir() {
		return dir;
	}

	@Override
	public <Result> Result eval(IVisitor<Result> visitor, ExecutionStateI e) throws EvaluationException {
		return visitor.visit(this, e);
	}
}
