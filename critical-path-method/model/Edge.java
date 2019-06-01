package model;

public class Edge {
	private int cost;
	private Node from;
	private Node to;

	public Edge(int cost, Node from, Node to) {
		this.cost = cost;
		this.from = from;
		this.to = to;
	}

	public Edge(Node from, Node to) {
		this.from = from;
		this.to = to;
	}

	public Edge(Edge e) {
		this.cost = e.getCost();
		this.from = e.getFrom();
		this.to = e.getTo();
	}

	public int getCost() {
		return cost;
	}

	public Node getFrom() {
		return from;
	}

	public Node getTo() {
		return to;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Edge other = (Edge) obj;
		if (!from.equals(other.getFrom()))
			return false;
		if (!to.equals(other.getTo()))
			return false;
		return true;
	}

	public String toString() {
		return from.getName() + "-" + cost + "->" + to.getName();
	}

}