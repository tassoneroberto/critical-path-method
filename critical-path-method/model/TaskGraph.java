package model;

import java.util.LinkedList;

public class TaskGraph extends Graph<Node, Edge> {
	private Node start, end;

	public TaskGraph() {
		super();
		start = new Node("Start");
		start.setEarly(0);
		start.setLate(0);
		end = new Node("End");
		end.setLate(-1);
		nodes = new LinkedList<Node>();
		nodes.add(start);
		nodes.add(end);
		edges = new LinkedList<Edge>();
	}

	public TaskGraph(LinkedList<Node> nodes, LinkedList<Edge> edges) {
		this();
		for (Node n : nodes)
			if (!this.nodes.contains(n))
				this.nodes.add(n);
		for (Edge e : edges)
			if (!this.edges.contains(e))
				this.edges.add(e);
	}

	public void addEdge(Edge e) {
		if (!this.edges.contains(e))
			this.edges.add(e);
		Node compareFrom = e.getFrom();
		Node compareTo = e.getTo();
		int indexFrom = nodes.indexOf(compareFrom);
		int indexTo = nodes.indexOf(compareTo);
		Node from = nodes.get(indexFrom);
		Node to = nodes.get(indexTo);
		from.addEdge(to, e.getCost());
	}

	public void addEdge(int cost, Node from, Node to) {
		Edge e = new Edge(cost, from, to);
		addEdge(e);
	}

	public void addNode(Node n) {
		if (!nodes.contains(n))
			nodes.add(n);
	}

	public Node getStartNode() {
		return start;
	}

	public Node getEndNode() {
		return end;
	}

	public void removeNode(Node node) {
		Node task = null;
		if (nodes.contains(node)) {
			task = nodes.get(nodes.indexOf(node));
			nodes.remove(task);
		}
		for (int i = edges.size() - 1; i >= 0; i--) {
			Edge e = edges.getLast();
			if (e.getFrom().equals(task) || e.getTo().equals(task)) {
				edges.remove(e);
			}
		}
	}

	public void setStartNode(Node start) {
		this.start = start;
	}

	public void setEndNode(Node end) {
		this.end = end;
	}
}
