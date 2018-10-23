package main;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;



public class CodePixelWindow extends JFrame {
	
	public CodePixelWindow(String string) {
		super (string);
	}
	
	CodePixelPanel cpp;
	int frameSize = 1500;
	int pixelSize = 5;
	int lifetimeLength = 5;
	int breedingAgeLimit = 2;
	boolean shouldRefreshInstantly = true;
	
	public void prepareGUI () {
		cpp = new CodePixelPanel (this);
		this.add(cpp);
		setSize (frameSize, frameSize);
		setResizable (false);
	}
	
	public static synchronized void updatePixels (CodePixelWindow c) throws InterruptedException {
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
				g.fillRect((p.x * c.pixelSize) + c.frameSize/2, (p.y * c.pixelSize) + c.frameSize/2, c.pixelSize, c.pixelSize);
			}
		}
		//if (!c.shouldRefreshInstantly) c.cpp.repaint();
		//Thread.sleep(10);
	}
	
	public static void main(String[] args) {
		JTextField pxSizeField = new JTextField ("5");
		JTextField frameSizeField = new JTextField ("1000");
		JTextField lifeLengthField = new JTextField ("80");
		JTextField ageLimitField = new JTextField ("60");
		JTextField codeField = new JTextField ("agecol evocol smrtbrd");
		JCheckBox refreshInstant = new JCheckBox ("Refresh pixels instantly", true);
		
		final JComponent[] inputs = new JComponent[] {
				new JLabel("Frame size"),
		        frameSizeField,
		        new JLabel("Pixel size"),
		        pxSizeField,
		        new JLabel("Lifetime length"),
		        lifeLengthField,
		        new JLabel("Breeding age limit"),
		        ageLimitField,
		        new JLabel("Starting pixel code"),
		        codeField,
		        refreshInstant
		};		
		
		int newPx;
		int newLife;
		int newAgeLimit;
		int newFrameSize;
		
		while (true) {
			int result = JOptionPane.showConfirmDialog(null, inputs, "Enter Parameters", JOptionPane.OK_CANCEL_OPTION);
			if (result != 0) {
				return;
			}
			try {
				newPx = Integer.parseInt(pxSizeField.getText());
				newLife = Integer.parseInt(lifeLengthField.getText());
				newAgeLimit = Integer.parseInt(ageLimitField.getText());
				newFrameSize = Integer.parseInt(frameSizeField.getText());
				break;
			} catch (Exception e) {
				
			}
		}
		
		CodePixelWindow c = new CodePixelWindow ("CodePixel");
		c.pixelSize = newPx;
		c.lifetimeLength = newLife;
		c.breedingAgeLimit = newAgeLimit;
		c.frameSize = newFrameSize;
		//c.shouldRefreshInstantly = refreshInstant.isSelected();
		String code = codeField.getText().toString();
		//code = "brd";
		c.prepareGUI();
		Pixel starter = new Pixel (c.lifetimeLength, code, 0, 0);
		c.cpp.pixels.add(starter);
		
		Pixel starter2 = new Pixel (c.lifetimeLength, code, 150, 150);
		c.cpp.pixels.add(starter2);
		
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
