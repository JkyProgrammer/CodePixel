package main;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import javax.swing.JFrame;



public class CodePixel extends JFrame {

	static int frameSize = 500;
	static int pixelSize = 10;
	static int lifetimeLength = 10;
	static boolean shouldRefreshInstantly = true;
	
	boolean isFirstUpdate = true;
	Pixel singleToRefresh;
	
	public CodePixel(String string) {
		super (string);
	}

	public void prepareGUI () {
		setSize (frameSize, frameSize);
		setResizable (false);
		setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
	}
	
	@Override
	public void paintComponents (Graphics g) {
		if (isFirstUpdate) {
			g.setColor(Color.black);
			g.fillRect (0, 0, frameSize, frameSize);
		}
		if (shouldRefreshInstantly) {
			paintPixel (g, singleToRefresh);
			singleToRefresh = null;
		} else {
			for (Pixel p : pixels) {
				paintPixel (g, p);
			}
		}
	}
	
	public boolean pixelExists (int x, int y) {
		for (Pixel p : pixels) {
			if (p.x == x && p.y == y)
				return true;
		}
		return false;
	}
	
	public void paintPixel (Graphics g, Pixel p) {
		if (p == null)
			return;
		int rgbNum = 255 - (int) ((p.remainingLifetime/lifetimeLength)*255.0);
		  
		g.setColor(new Color (rgbNum,rgbNum,rgbNum));
		g.fillRect(p.x * pixelSize, p.y * pixelSize, pixelSize, pixelSize);
	}
	
	public ArrayList<Pixel> pixels = new ArrayList<Pixel> ();
	
	public static void main(String[] args) {
		CodePixel c = new CodePixel ("CodePixel");
		c.prepareGUI();
		Pixel starter = new Pixel (c.lifetimeLength, "brd", 250, 250);
		c.pixels.add(starter);
		Thread t = new Thread () {
			public void run () {
				while (true) {
					try {
						Pixel[] pixes = c.pixels.toArray(new Pixel[] {});
						for (Pixel p : pixes) {
							p.enact(c);
							if (c.shouldRefreshInstantly)
								c.singleToRefresh  = p;
								c.repaint();
						}
						if (!c.shouldRefreshInstantly)
							c.repaint();
					} catch (ConcurrentModificationException e) {
						e.printStackTrace();
					}
				}
			}
		};
		t.start();
		
		c.setVisible (true);
	}

}
