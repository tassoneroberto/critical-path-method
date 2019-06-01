package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

public class NodeView implements Comparable<NodeView>, Graphic {

	private String name;
	private int early, late;
	private Point p;
	private static final int r = 20;
	private Color color = Color.BLACK;
	private boolean selected = false;
	private Rectangle b = new Rectangle();

	public NodeView(Point p, String name) {
		this.early = -1;
		this.late = -1;
		this.name = name;
		this.p = p;
		setBoundary(b);
	}

	public NodeView(String name) {
		this(new Point(150, 150), name);
	}

	private void setBoundary(Rectangle b) {
		b.setBounds(p.x - r, p.y - r, 2 * r, 2 * r);
	}

	public void draw(Graphics g) {
		// g.setColor(color);
		// g.setFont(new Font("default", Font.BOLD, 16));
		// g.drawOval(b.x, b.y, b.width, b.height);
		// int textPositionY = (int) p.getY() + 5;
		// int textPositionX = (int) p.getX() - 5;
		// if (name.equals("Start")) {
		// textPositionX -= 13;
		// }
		// if (name.equals("End")) {
		// textPositionX -= 10;
		// }
		// int timePositionX = (int) p.getX() - 14;
		// g.drawString(name, textPositionX, textPositionY);
		// if (early > -1)
		// g.drawString(early + "/" + late, timePositionX, textPositionY - 27);
		// if (selected) {
		// g.setColor(Color.GREEN);
		// g.drawRect(b.x - 1, b.y - 1, b.width + 2, b.height + 2);
		// }
	}

	public Point getLocation() {
		return p;
	}

	public boolean contains(Point p) {
		return b.contains(p);
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public static void getSelected(List<NodeView> list, List<NodeView> selected) {
		selected.clear();
		for (NodeView n : list) {
			if (n.isSelected()) {
				selected.add(n);
			}
		}
	}

	public static void selectNone(List<NodeView> list) {
		for (NodeView n : list) {
			n.setSelected(false);
		}
	}

	public static boolean selectOne(List<NodeView> list, Point p) {
		for (NodeView n : list) {
			if (n.contains(p)) {
				if (!n.isSelected()) {
					NodeView.selectNone(list);
					n.setSelected(true);
				}
				return true;
			}
		}
		return false;
	}

	public static void selectRect(List<NodeView> list, Rectangle r) {
		for (NodeView n : list) {
			n.setSelected(r.contains(n.p));
		}
	}

	public static void selectToggle(List<NodeView> list, Point p) {
		for (NodeView n : list) {
			if (n.contains(p)) {
				n.setSelected(!n.isSelected());
			}
		}
	}

	public static void updatePosition(List<NodeView> list, Point d) {
		for (NodeView n : list) {
			if (n.isSelected()) {
				n.p.x += d.x;
				n.p.y += d.y;
				n.setBoundary(n.b);
			}
		}
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
		NodeView other = (NodeView) obj;
		if (!name.equals(other.getName()))
			return false;
		return true;
	}

	@Override
	public int compareTo(NodeView o) {
		if (name.equals("Start"))
			return -1;
		if (o.name.equals("Start"))
			return 1;
		if (name.equals("End"))
			return 1;
		if (o.name.equals("End"))
			return -1;
		if (name.equals(o.name))
			return 0;
		if (name.compareTo(o.name) > 0)
			return 1;
		if (name.compareTo(o.name) < 0)
			return -1;
		return 0;
	}

	public String toString() {
		return name;
	}

	public void setEarly(int early) {
		this.early = early;
	}

	public void setLate(int late) {
		this.late = late;
	}

	public void setCriticalNode() {
		this.color = Color.RED;
	}

	public void restoreColor() {
		color = Color.BLACK;
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
		g.setColor(color);
		g.setFont(new Font("default", Font.BOLD, 16));
		g.drawOval(b.x, b.y, b.width, b.height);
		int textPositionY = (int) p.getY() + 5;
		int textPositionX = (int) p.getX() - 5;
		if (name.equals("Start")) {
			textPositionX -= 13;
		}
		if (name.equals("End")) {
			textPositionX -= 10;
		}
		int timePositionX = (int) p.getX() - 14;
		g.drawString(name, textPositionX, textPositionY);
		if (early > -1)
			g.drawString(early + "/" + late, timePositionX, textPositionY - 27);
		if (selected) {
			g.setColor(Color.GREEN);
			g.drawRect(b.x - 1, b.y - 1, b.width + 2, b.height + 2);
		}
	}
}
