package ast.dirs;

import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public abstract class Dirs {
    public abstract Object eval(IVisitor visitor, ExecutionStateI e) throws EvaluationException;
    public Direction getDir() {
        return null;
    }
}
