package main;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;

import javax.swing.JFrame;
import javax.swing.JPanel;



public class CodePixel extends JFrame {
	
	public CodePixel(String string) {
		super (string);
	}
	
	CodePixelPanel cpp;
	static int frameSize = 1500;
	static int pixelSize = 5;
	static int lifetimeLength = 8;
	static boolean shouldRefreshInstantly = true;
	
	public void prepareGUI () {
		cpp = new CodePixelPanel (this);
		this.add(cpp);
		setSize (frameSize, frameSize);
		setResizable (false);
	}
	
	public static synchronized void updatePixels (CodePixel c) throws InterruptedException {
		ListIterator<Pixel> it = c.cpp.pixels.listIterator();
		while (it.hasNext()) {
			Pixel p = it.next();
			if (p.remainingLifetime > 0) {
			p.enact(c, it);
			if (c.shouldRefreshInstantly) {
				c.cpp.singleToRefresh  = p;
				//System.out.println("repaint " + ((p.x * c.pixelSize) + 250) + ", " + ((p.y * c.pixelSize) + 250)  + ", " + c.pixelSize  + ", " +  c.pixelSize);
				c.cpp.paintComponent(c.cpp.getGraphics().create());
				//c.cpp.repaint((p.x * c.pixelSize) + 250, (p.y * c.pixelSize) + 250, c.pixelSize, c.pixelSize);
			}
			} else {
				it.remove();
				Graphics g = c.cpp.getGraphics().create();
				g.setColor(Color.WHITE);
				g.fillRect((p.x * pixelSize) + frameSize/2, (p.y * pixelSize) + frameSize/2, pixelSize, pixelSize);
			}
		}
		//if (!c.shouldRefreshInstantly) c.cpp.repaint();
		//Thread.sleep(10);
	}
	
	public static void main(String[] args) {
		CodePixel c = new CodePixel ("CodePixel");
		c.prepareGUI();
		Pixel starter = new Pixel (c.lifetimeLength, "brd", 0, 0);
		c.cpp.pixels.add(starter);
		
		c.setVisible (true);
		while (true) {
			try {
				updatePixels (c);
			} catch (ConcurrentModificationException | InterruptedException e) {
				e.printStackTrace();
				return;
			}
		}
	}

}
