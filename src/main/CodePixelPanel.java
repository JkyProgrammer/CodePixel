package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.AbstractQueue;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.SynchronousQueue;

import javax.swing.JPanel;

public class CodePixelPanel extends JPanel {	
	private static final long serialVersionUID = -2170386884746336930L;
	
	boolean isFirstUpdate = true;	// Determines if the whole screen needs a repaint

	CodePixelWindow parent;
	
	public CodePixelPanel (CodePixelWindow cp) {
		super ();
		parent = cp;
	}
	
	// Paint the component
	@Override
	protected void paintComponent (Graphics g) {
		if (isFirstUpdate) {
			// Repaint the entire screen
			g.setColor(Color.white);
			g.fillRect (0, 0, parent.frameSize, parent.frameSize);
			isFirstUpdate = false;
		}
	}
	
	// Paint a single pixel into the graphics environment
	public void paintPixel (Point point) {
		Pixel p = parent.pixels.get(point);
		Graphics g = getGraphics().create();
		if (p == null)
			g.setColor(Color.white);
		g.setColor(p.color);
		g.fillRect((point.x * parent.pixelSize) + parent.frameSize/2, (point.y * parent.pixelSize) + parent.frameSize/2, parent.pixelSize, parent.pixelSize);
	}

}
