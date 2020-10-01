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
	
	CodePixelWindow parent;
	
	public CodePixelPanel (CodePixelWindow cp) {
		super ();
		parent = cp;
	}
	
	@Override
	public void paint (Graphics g) {
		// Repaint the entire screen
		System.out.println ("Redrawing");
		g.setColor(Color.white);
		g.fillRect (0, 0, parent.getWidth(), parent.getWidth());
		parent.allowsPixelEnactment = false;
		synchronized (parent.pixels) {
			for (Point point : parent.pixels.keySet()) {
				Pixel p = parent.pixels.get(point);
				if (p == null) g.setColor(Color.white);
				else g.setColor(p.color);
				g.fillRect((point.x * parent.pixelSize) + parent.getWidth()/2, (point.y * parent.pixelSize) + parent.getHeight()/2, parent.pixelSize, parent.pixelSize);
			}
		}
		parent.allowsPixelEnactment = true;
	}
	
	// Clear a single pixel in the graphics environment
	public void clearPixel (Point point) {
		Graphics g = getGraphics().create();
		g.setColor(Color.white);
		g.fillRect((point.x * parent.pixelSize) + parent.getWidth()/2, (point.y * parent.pixelSize) + parent.getHeight()/2, parent.pixelSize, parent.pixelSize);
	}

	// Paint a single pixel into the graphics environment
	public void paintPixel (Pixel pixel) {
		Graphics g = getGraphics().create();
		g.setColor(pixel.color);
		g.fillRect((pixel.x * parent.pixelSize) + parent.getWidth()/2, (pixel.y * parent.pixelSize) + parent.getHeight()/2, parent.pixelSize, parent.pixelSize);
	}
}
