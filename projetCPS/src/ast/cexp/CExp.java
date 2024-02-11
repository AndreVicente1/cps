package src.ast.cexp;

import src.ast.rand.Rand;
import src.ast.exception.EvaluationException;
import src.ast.interfaces.IVisitor;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;

public abstract class CExp {
	protected Rand rand1;
    protected Rand rand2;

    public CExp(Rand rand1, Rand rand2) {
        this.rand1 = rand1;
        this.rand2 = rand2;
    }

    public Rand getRand1() {
        return rand1;
    }

    public Rand getRand2() {
        return rand2;
    }

    public abstract <Result> Result eval(IVisitor<Result> visitor, ExecutionStateI e) throws EvaluationException;
}
