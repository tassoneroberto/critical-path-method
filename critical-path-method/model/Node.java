package model;

import java.util.LinkedList;

public class Node {
	private int early, late;
	protected LinkedList<Edge> inEdges;
	protected LinkedList<Edge> outEdges;
	protected String name;

	public Node() {
		inEdges = new LinkedList<Edge>();
		outEdges = new LinkedList<Edge>();
		early = -1;
		late = Integer.MAX_VALUE;
	}

	public Node(String name) {
		this();
		this.name = name;
	}

	public Node(Node n) {
		this(n.getName());
		this.early = n.getEarly();
		this.late = n.getLate();
		for (Edge in : n.inEdges) {
			this.inEdges.add(new Edge(in));
		}
		for (Edge out : n.outEdges) {
			this.outEdges.add(new Edge(out));
		}
	}

	public Node(int label) {
		this(Integer.toString(label));
	}

	public void addEdge(Edge e) {
		if (!e.getFrom().equals(this))
			throw new IllegalArgumentException();
		if (!outEdges.contains(e)) {
			outEdges.add(e);
		}
		if (!e.getTo().inEdges.contains(e))
			e.getTo().inEdges.add(e);
	}

	public void addEdge(Node n, int cost) {
		Edge e = new Edge(cost, this, n);
		addEdge(e);
	}

	public void deleteEdge(Edge e) {
		if (outEdges.contains(e))
			outEdges.remove(e);
		if (inEdges.contains(e))
			inEdges.remove(e);
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (!name.equals(other.getName()))
			return false;
		return true;
	}

	public LinkedList<Edge> getInEdges() {
		return inEdges;
	}

	public LinkedList<Edge> getOutEdges() {
		return outEdges;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name + " {TE=" + early + "}{TL=" + late + "}");
		if (!inEdges.isEmpty()) {
			sb.append(" (InEdges: ");
			for (Edge inEdges : inEdges) {
				sb.append(inEdges);
				sb.append(" ");
			}
			sb.append(")");
		}
		if (!outEdges.isEmpty()) {
			sb.append(" (OutEdges: ");
			for (Edge outEdges : outEdges) {
				sb.append(outEdges);
				sb.append(" ");
			}
			sb.append(")");
		}
		return sb.toString();
	}

	public void setEarly(int early) {
		this.early = early;
	}

	public void setLate(int late) {
		this.late = late;
	}

	public int getEarly() {
		return early;
	}

	public int getLate() {
		return late;
	}
}
