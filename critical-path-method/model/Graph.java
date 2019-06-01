package model;

import java.util.LinkedList;

public abstract class Graph<V, E> {
	protected LinkedList<V> nodes;
	protected LinkedList<E> edges;

	public Graph() {
		nodes = new LinkedList<V>();
		edges = new LinkedList<E>();
	}

	public Graph(LinkedList<V> nodes, LinkedList<E> edges) {
		this();
		for (V n : nodes)
			if (!this.nodes.contains(n))
				this.nodes.add(n);
		for (E e : edges)
			if (!this.edges.contains(e))
				this.edges.add(e);
	}

	public void addNode(V n) {
		if (!this.nodes.contains(n))
			this.nodes.add(n);
	}

	public abstract void addEdge(E e);

	public abstract void addEdge(int cost, V from, V to);

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Nodes:\n");
		for (V n : nodes) {
			sb.append(n.toString());
			sb.append("\n");
		}
		sb.append("\nEdges:\n");
		for (E a : edges) {
			sb.append(a.toString());
			sb.append("\n");
		}
		return sb.toString();
	}

	public LinkedList<V> getNodes() {
		return nodes;
	}

	public LinkedList<E> getEdges() {
		return edges;
	}
}
