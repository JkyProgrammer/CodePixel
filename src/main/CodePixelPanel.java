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
	
	Thread drawThread1;
	Thread drawThread2;
	Thread clearThread;
	
	boolean isFirstRun = true;
	
	public CodePixelPanel (CodePixelWindow cp) {
		super ();
		parent = cp;
		drawThread1 = new Thread () {
			public void run () {
				drawThreadMain();
			}
		};
		
		drawThread2 = new Thread () {
			public void run () {
				drawThreadMain();
			}
		};
		
		clearThread = new Thread () {
			public void run () {
				Graphics g = getGraphics();
				while (true) {
					Point point;
					synchronized (pixelClearQueue) {
						point = pixelClearQueue.poll();
					}
					if (point == null) continue;
					g.setColor(Color.white);
					g.fillRect((point.x * parent.pixelSize) + xOffset, (point.y * parent.pixelSize) + yOffset, parent.pixelSize, parent.pixelSize);
				}
			}
		};
		
	}
	
	@Override
	public void paint (Graphics g) {
		if (isFirstRun) {
			drawThread1.start();
			drawThread2.start();
			clearThread.start();
			isFirstRun = false;
		}
		
		// Repaint the entire screen
		System.out.println ("Redrawing");
		g.setColor(Color.white);
		g.fillRect (0, 0, parent.getWidth(), parent.getHeight());
		parent.allowsPixelEnactment = false;
		xOffset = parent.getWidth()/2;
		yOffset = parent.getHeight()/2;
		synchronized (parent.pixels) {
			for (Point point : parent.pixels.keySet()) {
				Pixel p = parent.pixels.get(point);
				if (p == null) g.setColor(Color.white);
				else g.setColor(p.color);
				g.fillRect((point.x * parent.pixelSize) + xOffset, (point.y * parent.pixelSize) + yOffset, parent.pixelSize, parent.pixelSize);
			}
		}
		parent.allowsPixelEnactment = true;
	}
	
	int xOffset = 0;
	int yOffset = 0;
	
	Queue<Pixel> pixelDrawQueue = new LinkedList<Pixel> ();
	Queue<Point> pixelClearQueue = new LinkedList<Point> ();
		
	// Clear a single pixel in the graphics environment
	public void clearPixel (Point point) {
		synchronized (pixelClearQueue) {
			pixelClearQueue.add(point);
		}
	}
	
	// Paint a single pixel into the graphics environment
	public void paintPixel (Pixel pixel) {
		synchronized (pixelDrawQueue) {
			pixelDrawQueue.add(pixel);
		}
	} 
	
	private void drawThreadMain () {
		Graphics g = getGraphics();
		while (true) {
			Pixel pixel;
			synchronized (pixelDrawQueue) {
				pixel = pixelDrawQueue.poll();
			}
			if (pixel == null) continue;
			g.setColor(pixel.color);
			g.fillRect((pixel.x * parent.pixelSize) + xOffset, (pixel.y * parent.pixelSize) + yOffset, parent.pixelSize, parent.pixelSize);
		}
	}
}
