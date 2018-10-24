package main;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JPanel;

import java.util.ListIterator;

public class CodePixelPanel extends JPanel {	
	boolean isFirstUpdate = true;
	Pixel singleToRefresh;
	
	CodePixelWindow parent;
	
	public CodePixelPanel (CodePixelWindow cp) {
		super ();
		parent = cp;
	}
	
	@Override
	protected void paintComponent (Graphics g) {
		if (isFirstUpdate) {
			g.setColor(Color.white);
			g.fillRect (0, 0, parent.frameSize, parent.frameSize);
			isFirstUpdate = false;
		}
		if (parent.shouldRefreshInstantly) {
			paintPixel (g, singleToRefresh);
			singleToRefresh = null;
		} else {
//			g.setColor(Color.black);
//			g.fillRect (0, 0, parent.frameSize, parent.frameSize);
//			isFirstUpdate = false;
//			ListIterator<Pixel> it = pixels.listIterator();
//			while (it.hasNext()) {
//				Pixel p = it.next();
//				paintPixel (g, p);
//			}
		}
	}

	public boolean pixelExists (int x, int y) {
		for (Pixel p : pixels) {
			if (p.x == x && p.y == y)
				return true;
		}
		return false;
	}
	
	public int indexOf (int x, int y) {
		int i = 0;
		Pixel[] pixes = pixels.toArray(new Pixel[] {});
		for (Pixel p : pixes) {
			if (p.x == x && p.y == y)
				return i;
			i++;
		}
		return -1;
	}
	
	public void paintPixel (Graphics g, Pixel p) {
		//System.out.println(p);
		if (p == null)
			return;
		 /*new Color (brightness, brightness, brightness);*/
		g.setColor(p.color);
		g.fillRect((p.x * parent.pixelSize) + parent.frameSize/2, (p.y * parent.pixelSize) + parent.frameSize/2, parent.pixelSize, parent.pixelSize);
	}
	
	public ArrayList<Pixel> pixels = new ArrayList<Pixel> ();
}
