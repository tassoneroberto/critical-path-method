package model;

import java.util.LinkedList;

import junit.framework.TestCase;
import util.TaskGraphTools;

public class TestTaskGraph extends TestCase {

	TaskGraph g;

	public void setUp() {
		g = new TaskGraph();
		Node start = g.getStartNode();
		Node end = g.getEndNode();
		Node uno = new Node("1");
		Node due = new Node("2");
		Node tre = new Node("3");
		Node quattro = new Node("4");
		g.addNode(uno);
		g.addNode(due);
		g.addNode(tre);
		g.addNode(quattro);
		g.addEdge(new Edge(4, start, uno));
		g.addEdge(new Edge(5, start, due));
		g.addEdge(new Edge(9, uno, quattro));
		g.addEdge(new Edge(7, uno, tre));
		g.addEdge(new Edge(11, due, quattro));
		g.addEdge(new Edge(4, quattro, end));
		g.addEdge(new Edge(2, tre, end));
	}

	public TestTaskGraph(String nome) {
		super(nome);
	}

	public void testAcyclic() {
		assertTrue(TaskGraphTools.isAcyclic(g));
	}

	public void testConnected() {
		assertTrue(TaskGraphTools.isAcyclic(g));
	}

	public void testTlateEndNode() {
		TaskGraphTools.setEarlyLate(g);
		assertTrue(g.getEndNode().getLate() == 20);
	}

	public void testCriticalPath() {
		LinkedList<Edge> criticalPath = TaskGraphTools.findCriticalPath(g);
		boolean lateEqualsToEarly = true;
		for (int i = 0; i < criticalPath.size(); i++) {
			Edge edge = criticalPath.get(i);
			if ((edge.getFrom().getEarly() != edge.getFrom().getLate())
					|| edge.getTo().getEarly() != edge.getTo().getLate())
				lateEqualsToEarly = false;
		}
		assertTrue(lateEqualsToEarly);
	}
}