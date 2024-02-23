package tests.astTest;

import interpreter.Interpreter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import connexion.ExecutionState;
import connexion.Node;
import connexion.SensorData;
import connexion.NodeI;
import connexion.QueryResult;
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
import ast.gather.FGather;
import ast.interfaces.IVisitor;
import ast.position.Position;
import ast.query.BQuery;
import ast.query.GQuery;
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
	ArrayList<SensorDataI> sensors = new ArrayList<SensorDataI>();
	NodeI node;
	ExecutionStateI executionState;
	
	
	public TestAst() {
		sensors.add(sensor1);
		sensors.add(sensor2);
		node = new Node("nodetest", sensors, new Position(3.0, 2.0), 5.0, null);
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
		executionState = new ExecutionState(node, true);
		
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
		
		for (SensorDataI s : sensors) {
			System.out.println("reg sensor = " + s.getValue());
		}
		
		QueryResult res = (QueryResult) query.eval((IVisitor<Result>)interpreter, executionState);
		for (SensorDataI sensor : res.gatheredSensorsValues()) {
			System.out.println("sensor = " + sensor.getValue());
		}
		
		/* Test: Le résultat de la BQuery devrait renvoyer les senseurs qui répondent aux deux conditions:
		 * Un senseur du noeud d'identifiant "temperature" a comme valeur >= 50.0
		 * Un senseur du noeud d'identifiant "fumee" a comme valeur >= 3.0
		 * Résultat: Les deux capteurs du noeuds sont renvoyés dans la QueryResult
		 */
		assertEquals(res.gatheredSensorsValues(), sensors);
		
		System.out.println("Test passed");
	}
	
	//test gather, continuation, direction, base, et les autres expressions

	@Test
	public <Result> void testGather() throws EvaluationException{
		executionState = new ExecutionState(node, true);
		
		GQuery query = 
				new GQuery(
						new FGather(
									"temperature"),
						new ECont());
		
		QueryResult res = (QueryResult) query.eval((IVisitor<Result>)interpreter, executionState);
		
		/* Test: Le résultat de la GQuery devrait renvoyer le senseur d'identifiant "temperature" 
		 * Résultat: Le capteur du noeud "temperature" est renvoyé dans la QueyrREsult
		 */
		for (SensorDataI s : res.gatheredSensorsValues())
			System.out.println(s.getSensorIdentifier());
		
		assertEquals(res.gatheredSensorsValues().get(0).getValue(), sensors.get(sensors.indexOf(sensor1)).getValue());
	}
}
