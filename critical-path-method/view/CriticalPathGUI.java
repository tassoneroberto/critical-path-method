package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import model.Edge;
import model.Node;
import model.TaskGraph;
import util.TaskGraphTools;

public class CriticalPathGUI extends JFrame implements ActionListener,
		KeyListener {
	private static final long serialVersionUID = 2923975805665801740L;
	private static final int WIDTH = 635;
	private static final int HEIGHT = 485;
	GraphPanel graphPanel;
	JPanel container;
	JTextField info;
	AddEdgeFrame addEdgeFrame;
	DeleteEdgeFrame deleteEdgeFrame;
	JButton addNode;
	JButton deleteNode;
	JButton addEdge;
	JButton deleteEdge;
	JButton execute;
	JButton clear;
	TaskGraph taskGraph;
	LinkedList<Edge> criticalPath;

	public CriticalPathGUI() {
		super("Critical Path Method");
		this.setTitle("Critical Path Method");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setSize(WIDTH, HEIGHT);
		this.setFocusable(true);
		this.requestFocusInWindow();
		this.addKeyListener(this);
		this.setLayout(null);
		taskGraph = new TaskGraph();
		container = new JPanel();
		container.setLayout(null);
		container.setBounds(0, 0, WIDTH, HEIGHT);
		this.add(container);
		// GraphPanel
		graphPanel = new GraphPanel();
		container.add(graphPanel);
		graphPanel.setBounds(5, 5, WIDTH - 10, 400);

		// Info
		info = new JTextField();
		container.add(info);
		info.setBounds(0, 430, WIDTH, 30);
		info.setEditable(false);
		// Buttons
		addNode = new JButton("Add new node");
		container.add(addNode);
		addNode.setBounds(5, 410, 100, 20);
		addNode.addActionListener(this);

		deleteNode = new JButton("Delete node");
		container.add(deleteNode);
		deleteNode.setBounds(110, 410, 100, 20);
		deleteNode.addActionListener(this);

		addEdge = new JButton("Add new edge");
		container.add(addEdge);
		addEdge.setBounds(215, 410, 100, 20);
		addEdge.addActionListener(this);

		deleteEdge = new JButton("Delete edge");
		container.add(deleteEdge);
		deleteEdge.setBounds(320, 410, 100, 20);
		deleteEdge.addActionListener(this);

		execute = new JButton("Critical Path");
		container.add(execute);
		execute.setBounds(425, 410, 100, 20);
		execute.addActionListener(this);

		clear = new JButton("Clear");
		container.add(clear);
		clear.setBounds(530, 410, 100, 20);
		clear.addActionListener(this);

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
		this.setLocation(x, y);

		Point positionStart = new Point(50, 200);
		NodeView start = new NodeView(positionStart, "Start");
		graphPanel.nodes.add(start);
		graphPanel.add(start);

		Point positionEnd = new Point(570, 200);
		NodeView end = new NodeView(positionEnd, "End");
		graphPanel.nodes.add(end);
		graphPanel.add(end);

		graphPanel.repaint();

		this.setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		info.setText("");
		JButton source = (JButton) e.getSource();
		String testo = source.getText();
		if (testo.equals("Add new node")) {
			restore();
			int label = 0;
			List<NodeView> nodes = graphPanel.nodes;
			boolean indexFound = false;
			while (!indexFound) {
				label++;
				if (!nodes.contains(new NodeView(new Point(0, 0), "" + label))) {
					indexFound = true;
				}
			}
			String labelString = String.valueOf(label);
			graphPanel.nodes
					.add(new NodeView(new Point(310, 200), labelString));
			info.setText("Added node: [" + label + "]");
			graphPanel.repaint();
		} else if (testo.equals("Delete node")) {
			NodeView.getSelected(graphPanel.nodes, graphPanel.selected);
			if (graphPanel.selected.size() == 0) {
				info.setText("Error: no nodes selected.");
			} else {
				LinkedList<String> deletedNodes = new LinkedList<String>();
				for (NodeView n : graphPanel.selected) {
					if (!(n.getName().equals("Start") || n.getName().equals(
							"End"))) {
						graphPanel.nodes.remove(n);
						deletedNodes.add(n.getName());
						for (int i = graphPanel.edges.size() - 1; i >= 0; i--) {
							EdgeView a = graphPanel.edges.get(i);
							if (a.getFrom().getName().equals(n.getName())
									|| a.getTo().getName().equals(n.getName())) {
								graphPanel.edges.remove(a);
							}
						}
					} else {
						info.setText("Errore: no nodes selected.");
					}
				}
				restore();
				if (deletedNodes.size() != 0) {
					String x = "Deleted nodes: ";
					if (deletedNodes.size() == 1)
						x = "Deleted node: ";
					info.setText(x + deletedNodes);
				} else {
					info.setText("Errore: no nodes to delete.");
				}
				graphPanel.repaint();
			}
		} else if (testo.equals("Add new edge")) {
			disableControls();
			NodeView.getSelected(graphPanel.nodes, graphPanel.selected);
			if (graphPanel.selected.size() != 2) {
				new AddEdgeFrame();
			} else {
				NodeView n1 = graphPanel.selected.get(0);
				NodeView n2 = graphPanel.selected.get(1);
				if (n1.compareTo(n2) < 0) {
					new AddEdgeFrame(n1, n2);
				} else if (n1.compareTo(n2) > 0) {
					new AddEdgeFrame(n2, n1);
				}
			}
		} else if (testo.equals("Delete edge")) {
			if (graphPanel.edges.size() == 0) {
				info.setText("Error: no edges in the graph.");
				return;
			}
			disableControls();
			new DeleteEdgeFrame();
		} else if (testo.equals("Critical Path")) {
			createTaskGraph();
			if (TaskGraphTools.isValid(taskGraph)) {
				criticalPath = TaskGraphTools.findCriticalPath(taskGraph);
				for (Node n : taskGraph.getNodes()) {
					NodeView compare = new NodeView(n.getName());
					int index = graphPanel.nodes.indexOf(compare);
					graphPanel.nodes.get(index).setEarly(n.getEarly());
					graphPanel.nodes.get(index).setLate(n.getLate());
				}
				for (int i = 0; i < criticalPath.size(); i++) {
					Edge edge = criticalPath.get(i);
					EdgeView edgeCompare = new EdgeView(edge);
					int indexCriticalEdge = graphPanel.edges
							.indexOf(edgeCompare);
					graphPanel.edges.get(indexCriticalEdge).setCriticalEdge();
				}
				graphPanel.repaint();
				info.setText("Critical path found.");
			} else {
				info.setText("Error: invalid task graph.");
			}
		} else if (testo.equals("Clear")) {
			graphPanel.clear();

			Point positionStart = new Point(50, 200);
			NodeView start = new NodeView(positionStart, "Start");
			graphPanel.nodes.add(start);

			Point positionEnd = new Point(570, 200);
			NodeView end = new NodeView(positionEnd, "End");
			graphPanel.nodes.add(end);

			graphPanel.repaint();
			taskGraph = new TaskGraph();
			info.setText("Graph cleared.");
		}
	}

	private void createTaskGraph() {
		taskGraph = new TaskGraph();
		for (NodeView n : graphPanel.nodes) {
			if (!n.getName().equals("Start") && !n.getName().equals("End"))
				taskGraph.addNode(new Node(n.getName()));
		}
		for (EdgeView a : graphPanel.edges) {
			taskGraph.addEdge(new Edge(a.getCost(), new Node(a.getFrom()
					.getName()), new Node(a.getTo().getName())));
		}
	}

	private void restore() {
		for (NodeView n : graphPanel.nodes) {
			n.setEarly(-1);
			n.setLate(-1);
			n.restoreColor();
		}
		for (EdgeView e : graphPanel.edges) {
			e.restoreColor();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	private void disableControls() {
		addNode.setEnabled(false);
		deleteNode.setEnabled(false);
		addEdge.setEnabled(false);
		execute.setEnabled(false);
		clear.setEnabled(false);
		deleteEdge.setEnabled(false);
	}

	private void enableControls() {
		addNode.setEnabled(true);
		deleteNode.setEnabled(true);
		addEdge.setEnabled(true);
		execute.setEnabled(true);
		clear.setEnabled(true);
		deleteEdge.setEnabled(true);
	}

	public void deselect() {
		for (NodeView n : graphPanel.nodes) {
			n.setSelected(false);
		}
		graphPanel.repaint();
	}

	public static void main(String[] args) {
		new CriticalPathGUI();
	}

	public class AddEdgeFrame extends JFrame implements ActionListener,
			KeyListener {
		private static final long serialVersionUID = 2923975805665801740L;
		final static int WIDTH = 225;
		final static int HEIGHT = 80;
		JPanel container;
		JButton addButton;
		JButton cancelButton;
		JLabel costLabel;
		JLabel fromLabel;
		JLabel toLabel;
		JTextField costField;
		JComboBox<String> fromComboBox;
		JComboBox<String> toComboBox;

		public AddEdgeFrame() {
			super("Add new edge");
			this.setTitle("Add new edge");
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.setResizable(false);
			this.setUndecorated(true);
			this.setSize(WIDTH, HEIGHT);
			Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
			int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
			int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
			this.setLocation(x, y);
			this.setFocusable(true);
			this.requestFocusInWindow();
			this.addKeyListener(this);
			this.setLayout(null);
			container = new JPanel();
			container.setLayout(null);
			container.setBounds(0, 0, WIDTH, HEIGHT);
			this.add(container);

			costLabel = new JLabel("Task cost:");
			costField = new JTextField("1");
			fromLabel = new JLabel("From:");
			toLabel = new JLabel("To:");
			fromComboBox = new JComboBox<String>();
			toComboBox = new JComboBox<String>();
			addButton = new JButton("Add");
			cancelButton = new JButton("Cancel");

			container.add(costLabel);
			container.add(costField);
			container.add(fromLabel);
			container.add(toLabel);
			container.add(fromComboBox);
			container.add(toComboBox);
			container.add(addButton);
			container.add(cancelButton);

			costLabel.setBounds(5, 5, 100, 20);
			costField.setBounds(70, 5, 150, 20);
			fromLabel.setBounds(5, 30, 40, 20);
			fromComboBox.setBounds(40, 30, 80, 25);
			toLabel.setBounds(120, 30, 25, 20);
			toComboBox.setBounds(140, 30, 80, 25);
			addButton.setBounds(115, 55, 105, 20);
			cancelButton.setBounds(5, 55, 105, 20);

			addButton.addActionListener(this);
			cancelButton.addActionListener(this);

			for (NodeView n : graphPanel.nodes) {
				fromComboBox.addItem(n.getName());
				toComboBox.addItem(n.getName());
			}
			fromComboBox.setSelectedItem("Start");
			toComboBox.setSelectedItem("End");

			addEdgeFrame = this;
			addEdgeFrame.setAlwaysOnTop(true);
			addEdgeFrame.setVisible(true);

		}

		public AddEdgeFrame(NodeView from, NodeView to) {
			this();
			fromComboBox.addItem(from.getName());
			fromComboBox.setSelectedItem(from.getName());
			fromComboBox.setEnabled(false);
			toComboBox.addItem(to.getName());
			toComboBox.setSelectedItem(to.getName());
			toComboBox.setEnabled(false);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JButton source = (JButton) e.getSource();
			String testo = source.getText();
			if (testo.equals("Add")) {
				String from = fromComboBox.getSelectedItem().toString();
				String to = toComboBox.getSelectedItem().toString();
				int cost = 0;
				try {
					cost = Integer.parseInt(costField.getText());
					if (cost <= 0) {
						info.setText("Error: cost must be a positive integer.");
						return;
					}
				} catch (NumberFormatException e1) {
					info.setText("Error: cost must be a positive integer.");
					return;
				}
				if (from.equals(to)) {
					info.setText("Error: select distinct nodes.");
					return;
				}
				if (from.equals("End")) {
					info.setText("Error: [End] node can't have out edges.");
					return;
				}
				if (to.equals("Start")) {
					info.setText("Error: [Start] node can't have in edges.");
					return;
				}
				if (!(from.equals("Start") || to.equals("End"))
						&& from.compareTo(to) > 0) {
					info.setText("Error: respect the order of nodes.");
					return;
				}
				for (EdgeView edge : graphPanel.edges) {
					if (edge.getFrom().getName().equals(from)
							&& edge.getTo().getName().equals(to)
							&& edge.getCost() == cost) {
						info.setText("Error: edge already exists.");
						return;
					}
				}
				NodeView fromNode = null;
				NodeView toNode = null;
				for (NodeView n : graphPanel.nodes) {
					if (n.getName().equals(from))
						fromNode = n;
					if (n.getName().equals(to))
						toNode = n;
				}
				restore();
				graphPanel.edges.add(new EdgeView(cost, fromNode, toNode));
				info.setText("Edge [" + from + "-" + cost + "->" + to
						+ "] added.");
				deselect();
				graphPanel.repaint();
				addEdgeFrame.setVisible(false);
				enableControls();
			}
			if (testo.equals("Cancel")) {
				addEdgeFrame.setVisible(false);
				deselect();
				enableControls();
			}
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyPressed(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) {

		}
	}

	public class DeleteEdgeFrame extends JFrame implements ActionListener,
			KeyListener {
		private static final long serialVersionUID = 2923975805665801740L;
		final static int WIDTH = 180;
		final static int HEIGHT = 85;
		JPanel container;
		JButton deleteButton;
		JButton cancelButton;
		JLabel deleteLabel;
		JComboBox<String> edgesComboBox;

		public DeleteEdgeFrame() {
			super("Delete edge");
			this.setTitle("Delete edge");
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.setResizable(false);
			this.setUndecorated(true);
			this.setSize(WIDTH, HEIGHT);
			Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
			int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
			int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
			this.setLocation(x, y);
			this.setFocusable(true);
			this.requestFocusInWindow();
			this.addKeyListener(this);
			this.setLayout(null);
			container = new JPanel();
			container.setLayout(null);
			container.setBounds(0, 0, WIDTH, HEIGHT);
			this.add(container);

			deleteLabel = new JLabel("Edge to delete:");
			edgesComboBox = new JComboBox<String>();
			deleteButton = new JButton("Delete");
			cancelButton = new JButton("Cancel");

			container.add(deleteLabel);
			container.add(edgesComboBox);
			container.add(deleteButton);
			container.add(cancelButton);

			deleteLabel.setBounds(40, 5, 120, 20);
			edgesComboBox.setBounds(5, 30, 170, 25);
			deleteButton.setBounds(94, 58, 82, 20);
			cancelButton.setBounds(5, 58, 82, 20);

			deleteButton.addActionListener(this);
			cancelButton.addActionListener(this);

			for (EdgeView a : graphPanel.edges) {
				edgesComboBox.addItem(a.toString());
			}
			deleteEdgeFrame = this;
			deleteEdgeFrame.setAlwaysOnTop(true);
			deleteEdgeFrame.setVisible(true);

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JButton source = (JButton) e.getSource();
			String testo = source.getText();
			if (testo.equals("Delete")) {
				String edgeToDelete = edgesComboBox.getSelectedItem()
						.toString();
				StringTokenizer st = new StringTokenizer(edgeToDelete, "-");
				String from = st.nextToken();
				@SuppressWarnings("unused")
				String cost = st.nextToken();
				String to = st.nextToken().replace(">", "");
				for (int i = graphPanel.edges.size() - 1; i >= 0; i--) {
					EdgeView a = graphPanel.edges.get(i);
					if (a.getFrom().getName().equals(from)
							&& a.getTo().getName().equals(to)) {
						graphPanel.edges.remove(a);
						info.setText("Deleted edge: " + edgeToDelete);
					}
				}
				restore();
				deselect();
				graphPanel.repaint();
				deleteEdgeFrame.setVisible(false);
				enableControls();
			}
			if (testo.equals("Cancel")) {
				deselect();
				deleteEdgeFrame.setVisible(false);
				enableControls();
			}
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyPressed(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) {

		}
	}

	public class GraphPanel extends JComponent implements Graphic {

		private static final long serialVersionUID = 4611658053566388891L;
		private List<Graphic> graphicsElements = new ArrayList<Graphic>();
		private List<NodeView> nodes = new ArrayList<NodeView>();
		private List<NodeView> selected = new ArrayList<NodeView>();
		private List<EdgeView> edges = new ArrayList<EdgeView>();
		private Point mousePt = new Point(WIDTH / 2, HEIGHT / 2);
		private Rectangle mouseRect = new Rectangle();
		private boolean selecting = false;

		public GraphPanel() {
			this.setOpaque(true);
			this.addMouseListener(new MouseHandler());
			this.addMouseMotionListener(new MouseMotionHandler());
		}

		public void paint(Graphics g) {
			for (Graphic gn : nodes) {
				gn.paint(g);
			}
			for (Graphic ge : edges) {
				ge.paint(g);
			}
		}

		public void add(Graphic g) {
			graphicsElements.add(g);
		}

		public void remove(Graphic g) {
			if (graphicsElements.contains(g)) {
				graphicsElements.remove(g);
			}
		}

		public Graphic get(int index) {
			if (index < graphicsElements.size()) {
				return graphicsElements.get(index);
			}
			return graphPanel;
		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(WIDTH, HEIGHT);
		}

		@Override
		public void paintComponent(Graphics g) {
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, getWidth(), getHeight());
			for (EdgeView e : edges) {
				e.paint(g);
			}
			for (NodeView n : nodes) {
				n.paint(g);
			}
			if (selecting) {
				g.setColor(Color.darkGray);
				g.drawRect(mouseRect.x, mouseRect.y, mouseRect.width,
						mouseRect.height);
			}
		}

		private class MouseHandler extends MouseAdapter {

			@Override
			public void mouseReleased(MouseEvent e) {
				selecting = false;
				mouseRect.setBounds(0, 0, 0, 0);
				e.getComponent().repaint();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				mousePt = e.getPoint();
				if (e.isShiftDown()) {
					NodeView.selectToggle(nodes, mousePt);
				} else if (NodeView.selectOne(nodes, mousePt)) {
					selecting = false;
				} else {
					NodeView.selectNone(nodes);
					selecting = true;
				}
				e.getComponent().repaint();
			}

		}

		private class MouseMotionHandler extends MouseMotionAdapter {

			Point delta = new Point();

			@Override
			public void mouseDragged(MouseEvent e) {
				if (selecting) {
					mouseRect.setBounds(Math.min(mousePt.x, e.getX()),
							Math.min(mousePt.y, e.getY()),
							Math.abs(mousePt.x - e.getX()),
							Math.abs(mousePt.y - e.getY()));
					NodeView.selectRect(nodes, mouseRect);
				} else {
					delta.setLocation(e.getX() - mousePt.x, e.getY()
							- mousePt.y);
					NodeView.updatePosition(nodes, delta);
					mousePt = e.getPoint();
				}
				e.getComponent().repaint();
			}
		}

		void clear() {
			nodes.clear();
			edges.clear();
			repaint();
		}
	}
}
