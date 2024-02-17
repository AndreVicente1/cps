package tests.astTest;

import interpreter.Interpreter;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import Connexion.connexion.ExecutionState;
import Connexion.connexion.Node;
import Connexion.connexion.SensorData;
import Connexion.connexion.NodeI;
import ast.bexp.AndBExp;
import ast.bexp.CExpBExp;
import ast.bexp.NotBExp;
import ast.rand.CRand;
import ast.rand.SRand;
import ast.bexp.OrBExp;
import ast.bexp.SBExp;
import ast.cexp.GEqExp;
import ast.cont.DCont;
import ast.cont.ECont;
import ast.dirs.FDirs;
import ast.exception.EvaluationException;
import ast.interfaces.IVisitor;
import ast.position.Position;
import ast.query.BQuery;
import ast.cexp.CExp;
import ast.bexp.BExp;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;

public class TestAst {
	Interpreter interpreter = new Interpreter();
	SensorDataI sensor1 = new SensorData<Double>(60.0, "nodetest", "temperature");
	SensorDataI sensor2 = new SensorData<Double>(3.0, "nodetest", "fumee");
	NodeI node;
	ExecutionStateI executionState;
	
	
	public TestAst() {
		ArrayList<SensorDataI> sensors = new ArrayList<SensorDataI>();
		sensors.add(sensor1);
		sensors.add(sensor2);
		node = new Node("nodetest", sensors, new Position(3.0, 2.0), 5.0);
	}
	
	/*@Test
    public void testBExp() {
        
        
        SBExp data = new SBExp("sensortest");
        SBexp
        
        AndBExp andExp = new AndBExp();
        NotBExp notExp = new NotBExp(andExp);
        OrBExp orExp = new OrBExp(notExp, sensor3);

        // Evaluate the AST
        try {
            Object result = orExp.eval(interpreter, executionState);
            System.out.println("Evaluation Result: " + result);
        } catch (EvaluationException e) {
            System.err.println("Evaluation failed: " + e.getMessage());
        }
    }*/
	
	@Test
	public <Result> void testQuery() throws EvaluationException {
		System.out.println("EXECUTION STATE");
		executionState = new ExecutionState(node, true);
		
		System.out.println("QUERY");
		BQuery query = 
		new BQuery(
				new AndBExp(
					new CExpBExp(
						new GEqExp(
								new SRand("temperature"), //temperature >= 50.0?
								new CRand(50.0))),
					new CExpBExp(
						new GEqExp(
								new SRand("fumee"), //fumee >= 3.0
								new CRand(3.0)))),
				new ECont());
		
		System.out.println(sensor1.getValue());
		assertTrue((boolean) query.eval((IVisitor<Result>)interpreter, executionState));
		System.out.println("Test passed");
	}
}
