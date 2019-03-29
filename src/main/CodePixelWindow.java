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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


public class CodePixelWindow extends JFrame {

	private static final long serialVersionUID = -8407839062091833852L;
	public boolean hasStarted = false;

	public void addPixel (Point at) {
		int x = (at.x-this.frameSize/2)/this.pixelSize;
		int y = (at.y-this.frameSize/2)/this.pixelSize;
		y -= 5;


		Pixel newPixel = new Pixel (Integer.parseInt(optionsPane.lifeLengthField.getText()), optionsPane.codeField.getText(), x, y, Integer.parseInt(optionsPane.ageLimitField.getText()));
		//newPixel.color = Color.getHSBColor((float)((float)(optionsPane.colourSlider.getValue())/(float)255), 1f, 0.5f);
		newPixel.tint = (float)((float)(optionsPane.colourSlider.getValue())/(float)255);
		if (!optionsPane.enableLeapBrdBox.isSelected()) newPixel.allowsLeapBreedGene = false;
		pixelsToAdd.add(newPixel);
		//it.add(newPixel);
		this.cpp.singleToRefresh = newPixel;
		this.cpp.paintComponent(this.cpp.getGraphics().create());
	}

	ArrayList<Pixel> pixelsToAdd = new ArrayList<Pixel> ();
	ArrayList<Point> pixelsToRemove = new ArrayList<Point> ();

	public CodePixelWindow(String string) {
		super (string);
	}

	CodePixelPanel cpp;
	int frameSize = 1000;
	int pixelSize = 3;
	int lifetimeLength = 40;
	int breedingAgeLimit = 8;
	boolean shouldRefreshInstantly = true;
	public String startCode;
	boolean allowsPixelEnactment = true;
	boolean isMidUpdate = false;
	
	boolean verboseTimerLogging = false;
	
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
				allowsPixelEnactment = true;
				if (SwingUtilities.isLeftMouseButton(e)) {
					addPixel (new Point (e.getX(), e.getY()));
				} else {
					int x = (e.getX()-frameSize/2)/pixelSize;
					int y = (e.getY()-frameSize/2)/pixelSize;
					y -= 5;
					int topLeftX = (e.getX()-frameSize/2)/pixelSize - 2;
					int topLeftY = (e.getY()-frameSize/2)/pixelSize - 7;

					for (int b = topLeftY; b <= topLeftY + 4; b++) {
						for (int a = topLeftX; a <= topLeftX + 4; a++) {
							Point o = new Point (a, b);
							pixelsToRemove.add (o);
							pixelsToAdd.remove(o);
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
				allowsPixelEnactment = true;
				if (SwingUtilities.isLeftMouseButton(e)) {
					addPixel (new Point (e.getX(), e.getY()));
				} else {
					int x = (e.getX()-frameSize/2)/pixelSize;
					int y = (e.getY()-frameSize/2)/pixelSize;
					y -= 5;
					int topLeftX = (e.getX()-frameSize/2)/pixelSize - 2;
					int topLeftY = (e.getY()-frameSize/2)/pixelSize - 7;

					for (int b = topLeftY; b <= topLeftY + 4; b++) {
						for (int a = topLeftX; a <= topLeftX + 4; a++) {
							Point o = new Point (a, b);
							pixelsToRemove.add (o);
							pixelsToAdd.remove(o);
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
	public void updatePixels () {
		long start = System.nanoTime();
		Point[] indices = pixelsToRemove.toArray (new Point[] {});
		pixelsToRemove.clear();
		for (Point p : indices) {
			if (p != null) {
				int index = cpp.indexOf(p.x, p.y);
				if (index > -1)
					this.cpp.pixels.get(index).remainingLifetime = 0;
			}
		}
		
		if (verboseTimerLogging) System.out.println((System.nanoTime() - start) + " millis for removal.");
		start = System.nanoTime();
		
		Pixel[] pixes = pixelsToAdd.toArray(new Pixel[] {});
		pixelsToAdd.clear();
		for (Pixel p : pixes) {
			if (p != null) {
				int i = this.cpp.indexOf(p.x, p.y);
				if (i > -1)
					this.cpp.pixels.set(i, p);
				else 
					this.cpp.pixels.add(p);
			}
		}
		
		if (verboseTimerLogging) System.out.println((System.nanoTime() - start) + " millis for adding.");
		start = System.nanoTime();

		
		isMidUpdate = true;
		it = this.cpp.pixels.listIterator();
		while (it.hasNext()) {
			Pixel p = it.next();
			if (p.remainingLifetime > 0) {
				if (verboseTimerLogging) System.out.println((System.nanoTime() - start) + " millis for setup.");
				start = System.nanoTime();
				enact(p);
				if (verboseTimerLogging) System.out.println((System.nanoTime() - start) + " millis for enactment.");
				start = System.nanoTime();
				cpp.singleToRefresh  = p;
				cpp.paintComponent(cpp.getGraphics().create());
				if (verboseTimerLogging) System.out.println((System.nanoTime() - start) + " millis for painting.");
				start = System.nanoTime();
			} else {
				if (verboseTimerLogging) System.out.println((System.nanoTime() - start) + " millis for setup.");
				start = System.nanoTime();
				it.remove();
				Graphics g = this.cpp.getGraphics().create();
				g.setColor(Color.WHITE);
				g.fillRect((p.x * this.pixelSize) + this.frameSize/2, (p.y * this.pixelSize) + this.frameSize/2, this.pixelSize, this.pixelSize);
				if (verboseTimerLogging) System.out.println((System.nanoTime() - start) + " millis for killing.");
				start = System.nanoTime();
			}
		}
		isMidUpdate = false;
	}

	public void enact (Pixel p) {
		p.enact (this, it);
	}
	
	ExecutorService service;
	public void updateMain () {
		service = Executors.newFixedThreadPool(3);
		while (true) {
			//try {
			
			if (allowsPixelEnactment) {
				updatePixels ();
			} else {
				System.out.println("Pixel update unavailable.");
			}
//			} catch (ConcurrentModificationException | InterruptedException e) {
//				e.printStackTrace();
//				//return;
//			}
		}
	}
	
	public static void main(String[] args) {
		JTextField pxSizeField = new JTextField ("3");
		JTextField frameSizeField = new JTextField ("1000");
		JTextField lifeLengthField = new JTextField ("40");
		JTextField ageLimitField = new JTextField ("8");
		JTextField codeField = new JTextField ("agecol evocol smrtbrd");

		final JComponent[] inputs = new JComponent[] {
				new JLabel("Frame size"),
				frameSizeField,
				new JLabel("Pixel size"),
				pxSizeField,
				//new JLabel("Lifetime length"),
				//lifeLengthField,
				//new JLabel("Breeding age limit"),
				//ageLimitField,
				//new JLabel("Starting pixel code"),
				//codeField,
				//refreshInstant
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
				//newLife = Integer.parseInt(lifeLengthField.getText());
				//newAgeLimit = Integer.parseInt(ageLimitField.getText());
				newFrameSize = Integer.parseInt(frameSizeField.getText());
				break;
			} catch (Exception e) {

			}
		}

		CodePixelWindow c = new CodePixelWindow ("CodePixel");
		c.setDefaultCloseOperation(EXIT_ON_CLOSE);
		c.pixelSize = newPx;
		//c.lifetimeLength = newLife;
		//c.breedingAgeLimit = newAgeLimit;
		c.frameSize = newFrameSize;
		String code = codeField.getText().toString();
		c.startCode = code;
		c.shouldRefreshInstantly = true; //refreshInstant.isSelected();
		c.prepareGUI();
		c.setVisible (true);
		c.updateMain();
	}

}
