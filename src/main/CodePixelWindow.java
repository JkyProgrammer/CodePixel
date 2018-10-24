package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.ListIterator;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;


public class CodePixelWindow extends JFrame {
	
	private static final long serialVersionUID = -8407839062091833852L;
	public boolean hasStarted = false;
	
	public void addPixel (Point at) {
		int x = (at.x-this.frameSize/2)/this.pixelSize;
		int y = (at.y-this.frameSize/2)/this.pixelSize;
		y -= 5;
		
		
		Pixel newPixel = new Pixel (Integer.parseInt(optionsPane.lifeLengthField.getText()), optionsPane.codeField.getText(), x, y, Integer.parseInt(optionsPane.ageLimitField.getText()));
		pixelsToAdd.add(newPixel);
		//it.add(newPixel);
		this.cpp.singleToRefresh  = newPixel;
		this.cpp.paintComponent(this.cpp.getGraphics().create());
	}
	
	ArrayList<Pixel> pixelsToAdd = new ArrayList<Pixel> ();
	ArrayList<Point> pixelsToRemove = new ArrayList<Point> ();
	
	public CodePixelWindow(String string) {
		super (string);
	}
	
	CodePixelPanel cpp;
	int frameSize = 1500;
	int pixelSize = 5;
	int lifetimeLength = 5;
	int breedingAgeLimit = 2;
	boolean shouldRefreshInstantly = true;
	public String startCode;
	
	public void prepareGUI () {
		cpp = new CodePixelPanel (this);
		this.add(cpp);
		setSize (frameSize, frameSize);
		setResizable (false);
		this.addMouseListener(new MouseListener () {

			@Override
			public void mouseClicked(MouseEvent e) {
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					addPixel (new Point (e.getX(), e.getY()));
				} else {
					int x = (e.getX()-frameSize/2)/pixelSize;
					int y = (e.getY()-frameSize/2)/pixelSize;
					y -= 5;
					int topLeftX = (e.getX()-frameSize/2)/pixelSize - 2;
					int topLeftY = (e.getY()-frameSize/2)/pixelSize - 7;
					
					for (int b = topLeftY; b <= topLeftY + 4; b++) {
						for (int a = topLeftX; a <= topLeftX + 4; a++) {
							pixelsToRemove.add (new Point (a, b));
						}
					}
					
					Graphics g = cpp.getGraphics().create();
					g.setColor(Color.WHITE);
					g.fillRect(((x-2) * pixelSize) + frameSize/2, ((y-2) * pixelSize) + frameSize/2, pixelSize * 5, pixelSize * 5);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {
			}
			
		});
		this.addMouseMotionListener(new MouseMotionListener () {

			@Override
			public void mouseDragged(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					addPixel (new Point (e.getX(), e.getY()));
				} else {
					int x = (e.getX()-frameSize/2)/pixelSize;
					int y = (e.getY()-frameSize/2)/pixelSize;
					y -= 5;
					int topLeftX = (e.getX()-frameSize/2)/pixelSize - 2;
					int topLeftY = (e.getY()-frameSize/2)/pixelSize - 7;
					
					for (int b = topLeftY; b <= topLeftY + 4; b++) {
						for (int a = topLeftX; a <= topLeftX + 4; a++) {
							pixelsToRemove.add (new Point (a, b));
						}
					}
					
					Graphics g = cpp.getGraphics().create();
					g.setColor(Color.WHITE);
					g.fillRect(((x-2) * pixelSize) + frameSize/2, ((y-2) * pixelSize) + frameSize/2, pixelSize * 5, pixelSize * 5);
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}
			
		});
	
		optionsPane = new OptionsPane (this);
		optionsPane.setVisible (true);
	}
	
	OptionsPane optionsPane;
	boolean mouseIsDown = false;
	public ListIterator<Pixel> it;
	public void updatePixels () throws InterruptedException {
		Pixel[] pixes = pixelsToAdd.toArray(new Pixel[] {});
		pixelsToAdd.clear();
		for (Pixel p : pixes) {
			int i = this.cpp.indexOf(p.x, p.y);
			if (i > -1)
				this.cpp.pixels.set(i, p);
			else 
				this.cpp.pixels.add(p);
		}
		
		Point[] indices = pixelsToRemove.toArray (new Point[] {});
		pixelsToRemove.clear();
		for (Point p : indices) {
			int index = cpp.indexOf(p.x, p.y);
			if (index > -1)
				this.cpp.pixels.get(index).remainingLifetime = 0;
		}
		
		
		it = this.cpp.pixels.listIterator();
		while (it.hasNext()) {
			Pixel p = it.next();
			if (p.remainingLifetime > 0) {
			p.enact(this, it);
			if (this.shouldRefreshInstantly) {
				this.cpp.singleToRefresh  = p;
				this.cpp.paintComponent(this.cpp.getGraphics().create());
			}
			} else {
				it.remove();
				Graphics g = this.cpp.getGraphics().create();
				g.setColor(Color.WHITE);
				g.fillRect((p.x * this.pixelSize) + this.frameSize/2, (p.y * this.pixelSize) + this.frameSize/2, this.pixelSize, this.pixelSize);
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
		String code = codeField.getText().toString();
		c.startCode = code;
		c.shouldRefreshInstantly = refreshInstant.isSelected();
		c.prepareGUI();
		//Pixel starter = new Pixel (c.lifetimeLength, code, 0, 0);
		//c.cpp.pixels.add(starter);
		
		//Pixel starter2 = new Pixel (c.lifetimeLength, code, 150, 150);
		//c.cpp.pixels.add(starter2);
		
		c.setVisible (true);
		while (true) {
			try {
				c.updatePixels ();
			} catch (ConcurrentModificationException | InterruptedException e) {
				e.printStackTrace();
				//return;
			}
		}
	}

}
