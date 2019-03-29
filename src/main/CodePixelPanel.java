package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.HashMap;

import javax.swing.JPanel;

public class CodePixelPanel extends JPanel {	
	private static final long serialVersionUID = -2170386884746336930L;
	
	boolean isFirstUpdate = true;	// Determines if the whole screen needs a repaint
	Pixel singleToRefresh;			// The single pixel that needs refreshing
	
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
		// Paint the single pixel that needs refreshing
		paintPixel (g, singleToRefresh);
		// Clear singleToRefresh
		singleToRefresh = null;
	}

	// Check if a pixel exists at the defined point
	public boolean pixelExists (int x, int y) {
		if (pixels.containsKey(new Point (x, y))) return true;
		return false;
	}
	
	// Paint a single pixel into the graphics environment
	public void paintPixel (Graphics g, Pixel p) {
		if (p == null)
			return;
		g.setColor(p.color);
		g.fillRect((p.x * parent.pixelSize) + parent.frameSize/2, (p.y * parent.pixelSize) + parent.frameSize/2, parent.pixelSize, parent.pixelSize);
	}
	
	public HashMap<Point, Pixel> pixels = new HashMap<Point, Pixel> ();
}
