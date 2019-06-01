package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

import model.Edge;

public class EdgeView implements Graphic {

	private Color color = Color.LIGHT_GRAY;
	private NodeView from;
	private NodeView to;
	private int cost;

	public EdgeView(int cost, NodeView from, NodeView to) {
		this.cost = cost;
		this.from = from;
		this.to = to;
	}

	public EdgeView(NodeView from, NodeView to) {
		this(0, from, to);
	}

	public EdgeView(Edge edge) {
		this.from = new NodeView(edge.getFrom().getName());
		this.to = new NodeView(edge.getTo().getName());
		this.cost = edge.getCost();
	}

	public NodeView getFrom() {
		return from;
	}

	public NodeView getTo() {
		return to;
	}

	public int getCost() {
		return cost;
	}

	public String toString() {
		return from + "-" + cost + "->" + to;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EdgeView other = (EdgeView) obj;
		if (!from.equals(other.getFrom()))
			return false;
		if (!to.equals(other.getTo()))
			return false;
		return true;
	}

	public void restoreColor() {
		color = Color.LIGHT_GRAY;
	}

	public void setCriticalEdge() {
		color = Color.RED;
	}

	@Override
	public void add(Graphic g) {

	}

	@Override
	public void remove(Graphic g) {

	}

	@Override
	public Graphic get(int index) {
		return null;
	}

	@Override
	public void paint(Graphics g) {
		Point p1 = from.getLocation();
		Point p2 = to.getLocation();
		g.setColor(color);
		int x1 = (int) p1.getX();
		int y1 = (int) p1.getY();
		int x2 = (int) p2.getX();
		int y2 = (int) p2.getY();
		double d = 30;
		double h = 10;
		double distanceX = x2 - x1;
		double distanceY = y2 - y1;
		double distance = Math.sqrt(distanceX * distanceX + distanceY
				* distanceY);
		double xm = distance - d;
		double xn = xm;
		double ym = h;
		double yn = -h;
		double x;
		double sin = distanceY / distance;
		double cos = distanceX / distance;
		x = xm * cos - ym * sin + x1;
		ym = xm * sin + ym * cos + y1;
		xm = x;
		x = xn * cos - yn * sin + x1;
		yn = xn * sin + yn * cos + y1;
		xn = x;
		int[] xpoints = { x2, (int) xm, (int) xn };
		int[] ypoints = { y2, (int) ym, (int) yn };
		g.drawLine(x1, y1, x2, y2);
		g.fillPolygon(xpoints, ypoints, 3);
		g.setFont(new Font("default", Font.BOLD, 12));
		g.setColor(Color.BLACK);
		g.drawString(String.valueOf(cost),
				((int) p1.getX() + (int) p2.getX()) / 2,
				((int) p1.getY() + (int) p2.getY()) / 2 - 5);
	}

}