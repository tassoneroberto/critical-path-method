package util;

import java.util.LinkedList;

import model.Edge;
import model.Node;
import model.TaskGraph;

public class TaskGraphTools {

	public static boolean isValid(TaskGraph g) {
		Node start = g.getStartNode();
		Node end = g.getEndNode();
		if (start.getOutEdges().size() < 1) {
			// info.setText("Grafo non valido: il nodo start non ha archi uscenti");
			return false;
		}
		if (start.getInEdges().size() > 1) {
			// info.setText("Grafo non valido: il nodo start ha archi entranti");
			return false;
		}
		if (end.getInEdges().size() < 1) {
			// info.setText("Grafo non valido: il nodo end non ha archi entranti");
			return false;
		}
		if (end.getOutEdges().size() > 1) {
			// info.setText("Grafo non valido: il nodo end ha archi uscenti");
			return false;
		}
		if (!TaskGraphTools.isConnected(g)) {
			// info.setText("Grafo non valido: sconnesso");
			return false;
		}
		if (!TaskGraphTools.isAcyclic(g)) {
			// info.setText("Grafo non valido: ciclico");
			return false;
		}
		return true;
	}

	public static boolean isConnected(TaskGraph g) {
		LinkedList<Node> nodes = g.getNodes();
		Node start = g.getStartNode();
		Node end = g.getEndNode();
		for (Node n : nodes) {
			if (!n.equals(start) && !n.equals(end)) {
				if (n.getInEdges().size() == 0 || n.getOutEdges().size() == 0) {
					// info.setText("Grafo non valido: " + n
					// + " scollega il grafo.");
					return false;
				}
			}
		}
		return true;
	}

	public static boolean isAcyclic(TaskGraph g) {
		// Creo una copia di nodi e archi per non alterare il grafo
		LinkedList<Node> replicaNodes = new LinkedList<Node>();
		for (Node n : g.getNodes()) {
			replicaNodes.add(new Node(n));
		}
		LinkedList<Edge> replicaEdges = new LinkedList<Edge>();
		for (Edge e : g.getEdges()) {
			replicaEdges.add(new Edge(e));
		}
		LinkedList<Node> topologicalSorted = new LinkedList<Node>();
		LinkedList<Node> nodesNoInEdges = new LinkedList<Node>();
		for (Node n : replicaNodes) {
			if (n.getInEdges().size() == 0) {
				nodesNoInEdges.add(n);
			}
		}
		while (!nodesNoInEdges.isEmpty()) {
			Node n = nodesNoInEdges.removeFirst();
			topologicalSorted.add(n);
			if (!n.getOutEdges().isEmpty()) {
				for (int i = n.getOutEdges().size() - 1; i >= 0; i--) {
					Edge e = n.getOutEdges().get(i);
					Node compare = e.getTo();
					int index = replicaNodes.indexOf(compare);
					Node m = replicaNodes.get(index);
					replicaEdges.remove(e);
					m.deleteEdge(e);
					if (m.getInEdges().isEmpty()) {
						if (!nodesNoInEdges.contains(m)) {
							nodesNoInEdges.add(m);
						}
					}
				}
			}
		}
		boolean cycle = false;
		for (Node n : replicaNodes) {
			if (!n.getInEdges().isEmpty()) {
				cycle = true;
				break;
			}
		}
		return !cycle;
	}

	public static LinkedList<Edge> findCriticalPath(TaskGraph g) {
		setEarlyLate(g);
		Node start = g.getStartNode();
		LinkedList<Edge> criticalPath = new LinkedList<Edge>();
		LinkedList<Edge> outEdges = start.getOutEdges();
		LinkedList<Node> possibleNextTask = new LinkedList<Node>();
		for (Edge e : outEdges) {
			if (!possibleNextTask.contains(e.getTo()))
				possibleNextTask.add(e.getTo());
		}
		boolean endTaskFound = false;
		while (!endTaskFound) {
			for (Node t : possibleNextTask)
				for (Edge e : t.getInEdges()) {
					if ((e.getTo().getEarly() == e.getTo().getLate())
							&& (e.getFrom().getEarly() == e.getFrom().getLate())
							&& (t.getLate() == e.getCost()
									+ e.getFrom().getLate())) {
						criticalPath.add(e);
						if (t.getName().equals("End")) {
							endTaskFound = true;
							break;
						}
						outEdges = t.getOutEdges();
						possibleNextTask = new LinkedList<Node>();
						for (Edge e2 : outEdges) {
							if (!possibleNextTask.contains(e2.getTo()))
								possibleNextTask.add(e2.getTo());
						}
						if (t.getName().equals("End"))
							endTaskFound = true;
					}
				}
		}
		return criticalPath;
	}

	public static void setEarlyLate(TaskGraph g) {
		if (!TaskGraphTools.isValid(g)) {
			throw new IllegalStateException("Il grafo non è valido.");
		}
		setEarly(g);
		setLate(g);
	}

	private static void setEarly(TaskGraph g) {
		Node start = g.getStartNode();
		start.setEarly(0);
		LinkedList<Edge> outEdges = start.getOutEdges();
		LinkedList<Node> currentNodes = new LinkedList<Node>();
		for (Edge e : outEdges) {
			if (!currentNodes.contains(e.getTo()))
				currentNodes.add(e.getTo());
		}
		while (!currentNodes.isEmpty()) {
			for (int i = currentNodes.size() - 1; i >= 0; i--) {
				int finalEarly = -1;
				int tempEarly = -1;
				Node n = currentNodes.get(i);
				LinkedList<Edge> inEdges = n.getInEdges();
				for (Edge e : inEdges) {
					if ((e.getFrom()).getEarly() != -1) {
						tempEarly = (e.getFrom()).getEarly() + e.getCost();
					} else {
						break;
					}
					if (tempEarly > finalEarly)
						finalEarly = tempEarly;
					if (n.getEarly() < finalEarly) {
						n.setEarly(finalEarly);
					}
					currentNodes.remove(n);
					LinkedList<Edge> outEdgesTemp = n.getOutEdges();
					for (Edge edgeTemp : outEdgesTemp) {
						if (!currentNodes.contains(edgeTemp.getTo())) {
							currentNodes.add(edgeTemp.getTo());
						}
					}
				}
			}
		}
	}

	private static void setLate(TaskGraph g) {
		Node start = g.getStartNode();
		start.setLate(0);
		Node end = g.getEndNode();
		for (Edge e : end.getInEdges()) {
			int late = (e.getFrom()).getEarly() + e.getCost();
			if (late > end.getLate()) {
				end.setLate(late);
			}
		}
		LinkedList<Edge> inEdges = end.getInEdges();
		LinkedList<Node> currentNodes = new LinkedList<Node>();
		for (Edge a : inEdges) {
			if (!currentNodes.contains(a.getFrom()))
				currentNodes.add(a.getFrom());
		}
		while (!currentNodes.isEmpty()) {
			for (int i = currentNodes.size() - 1; i >= 0; i--) {
				int finalLate = Integer.MAX_VALUE;
				int tempLate = Integer.MAX_VALUE;
				Node n = currentNodes.get(i);
				LinkedList<Edge> uscenti = n.getOutEdges();
				for (Edge a : uscenti) {
					if ((a.getTo()).getLate() != Integer.MAX_VALUE) {
						tempLate = (a.getTo()).getLate() - a.getCost();
					} else {
						break;
					}
					if (tempLate < finalLate)
						finalLate = tempLate;
					if (n.getLate() > finalLate) {
						n.setLate(finalLate);
					}
					currentNodes.remove(n);

					LinkedList<Edge> inEdgesTemp = n.getInEdges();
					for (Edge edgeTemp : inEdgesTemp) {
						if (!currentNodes.contains(edgeTemp.getFrom())) {
							currentNodes.add(edgeTemp.getFrom());
						}
					}
				}
			}
		}
	}
}
