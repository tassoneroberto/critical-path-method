package view;

import java.awt.Graphics;

public interface Graphic {
	
	public void add(Graphic g);

	public void remove(Graphic g);

	public Graphic get(int index);

	public void paint(Graphics g);
}