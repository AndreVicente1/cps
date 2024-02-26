package tests.astTest;

import interpreter.Interpreter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;

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
import ast.cont.FCont;
import ast.dirs.FDirs;
import ast.exception.EvaluationException;
import ast.gather.FGather;
import ast.interfaces.IVisitor;
import ast.position.Position;
import ast.query.BQuery;
import ast.query.GQuery;
import ast.cexp.CExp;
import ast.bexp.BExp;
import ast.base.*;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ExecutionStateI;
import fr.sorbonne_u.cps.sensor_network.interfaces.Direction;
import fr.sorbonne_u.cps.sensor_network.interfaces.NodeInfoI;
import fr.sorbonne_u.cps.sensor_network.interfaces.SensorDataI;
import fr.sorbonne_u.cps.sensor_network.requests.interfaces.ProcessingNodeI;

public class TestAst {
	Interpreter interpreter = new Interpreter();
	SensorDataI sensor1 = new SensorData<Double>(60.0, "nodetest", "temperature");
	SensorDataI sensor2 = new SensorData<Double>(3.0, "nodetest", "fumee");
	ArrayList<SensorDataI> sensors = new ArrayList<SensorDataI>();
	Node node;
	ExecutionStateI executionState;
	
	
	public TestAst() {
		sensors.add(sensor1);
		sensors.add(sensor2);
		node = new Node("nodetest", sensors, new Position(0.0, 0.0), 0.0, null, null);
	}
	
	/*
	 * Test Query
	 */
	@Test
	public <Result> void testQuery() throws EvaluationException {
		executionState = new ExecutionState(node, node, true);
		
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
	
	
	/*
	 * Test Gather
	 */
	@Test
	public <Result> void testGather() throws EvaluationException{
		executionState = new ExecutionState(node, node, true);
		
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
	
	
	/*
	 * Test Continuation
	 */
	/*@Test
	public <Result> void testCont() throws EvaluationException{
		//execution state sur le noeud de base
		executionState = new ExecutionState(node, node, true);
		
		//on rajoute un nouveau noeud
		SensorDataI sensor3 = new SensorData<Double>(50.0, "nodetest1", "temperature2");
		SensorDataI sensor4 = new SensorData<Double>(2.0, "nodetest1", "fumee2");
		ArrayList<SensorDataI> sensors2 = new ArrayList<SensorDataI>();
		sensors2.add(sensor4);
		sensors2.add(sensor3);
		HashSet<NodeInfoI> neighbors = new HashSet<NodeInfoI>();
		neighbors.add(node);
		Node node2 = new Node("nodetest1", sensors2, new Position(1.0, 3.0), 5.0,neighbors, null /* pas besoin de ports */);
		
		//set the neighbors of the first node
		/*HashSet<NodeInfoI> neighbors2 = new HashSet<NodeInfoI>();
		neighbors2.add(node2);
		node.setNeighbors(neighbors2);
		
		//Base
		Base rbase = new RBase(executionState.getProcessingNode().getPosition());
		
		GQuery query = 
				new GQuery(
						new FGather(
									"temperature"),
						new FCont(rbase, 5.0));
		
		QueryResult res = (QueryResult) query.eval((IVisitor<Result>)interpreter, executionState);
		
		/* Test: Le résultat de la GQuery devrait renvoyer les 2 senseurs d'identifiant de temperature 
		 * Résultat: Les capteurs "temperature" et "temperature2" sont renvoyés dans la QueyrResult
		 */
		/*for (SensorDataI s : res.gatheredSensorsValues())
			System.out.println(s.getSensorIdentifier());
		
		assertEquals(res.gatheredSensorsValues().get(0).getValue(), sensors.get(sensors.indexOf(sensor1)).getValue());
		assertEquals(res.gatheredSensorsValues().get(1).getValue(), sensors2.get(sensors2.indexOf(sensor3)).getValue());
	}*/
}
