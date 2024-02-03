package src.ast.dirs;

import src.ast.exception.EvaluationException;
import src.ast.interfaces.IVisitor;
import src.fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import src.fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public abstract class Dirs {
    public abstract <Result> Result eval(IVisitor<Result> visitor, ExecutionStateI e) throws EvaluationException;
    public Direction getDir() {
        return null;
    }
}
