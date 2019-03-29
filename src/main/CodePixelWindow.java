package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


public class CodePixelWindow extends JFrame {

	private static final long serialVersionUID = -8407839062091833852L;
	
	// Handle adding a new pixel at a point, with characteristics as defined in the options pane
	public void addPixel (Point at) {
		// Work out the internal position of the new pixel
		int x = (at.x-this.frameSize/2)/this.pixelSize;
		int y = (at.y-this.frameSize/2)/this.pixelSize;
		y -= 10;


		// Initialise the pixel based on the characteristics defined in the options pane
		Pixel newPixel = new Pixel (Integer.parseInt(optionsPane.lifeLengthField.getText()), optionsPane.codeField.getText(), x, y, Integer.parseInt(optionsPane.ageLimitField.getText()));
		newPixel.tint = (float)((float)(optionsPane.colourSlider.getValue())/(float)255);
		if (!optionsPane.enableLeapBrdBox.isSelected()) newPixel.allowsLeapBreedGene = false;
		
		// Queue the pixel to be added
		pixelsToAdd.add(newPixel);
		
		// Paint the pixel
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
	
	static int lifetimeLength = 40; 					// The default lifetime length for the options pane
	static int breedingAgeLimit = 8;					// The default breeding age limit for the options pane
	static String startCode = "agecol evocol smrtbrd";	// The default starting code for the options pane
	static boolean verboseTimerLogging = false;			// Determines whether or not to display operation timing data on the console

	boolean allowsPixelEnactment = true;				// Defines whether or not the pixelUpdate method should be called
	boolean isMidUpdate = false;						// Defines whether or not the pixelUpdate method is currently enacting/drawing pixels
	
	
	// Called to set up the GUI
	public void prepareGUI () {
		// Create a new CodePixelPanel
		cpp = new CodePixelPanel (this);
		this.add(cpp);
		// Prepare the frame
		setSize (frameSize, frameSize);
		setResizable (false);
		// Add a sensor to detect clicking events
		this.addMouseListener(new MouseListener () {
			@Override
			public void mouseClicked(MouseEvent e) {
				//allowsPixelEnactment = true;
				if (SwingUtilities.isLeftMouseButton(e)) {
					// Add a new pixel under the mouse
					addPixel (new Point (e.getX(), e.getY()));
				} else {
					// Remove the pixels under the mouse
					int x = (e.getX()-frameSize/2)/pixelSize;
					int y = (e.getY()-frameSize/2)/pixelSize;
					y -= 10;
					
					int topLeftX = x - 2;
					int topLeftY = y - 2;

					// Iterate over the pixels and queue them to be removed
					for (int b = topLeftY; b <= topLeftY + 4; b++) {
						for (int a = topLeftX; a <= topLeftX + 4; a++) {
							Point o = new Point (a, b);
							pixelsToRemove.add (o);
							pixelsToAdd.remove(o);
						}
					}
					
					// Draw over the pixels that are being removed
					Graphics g = cpp.getGraphics().create();
					g.setColor(Color.WHITE);
					g.fillRect(((x-2) * pixelSize) + frameSize/2, ((y-2) * pixelSize) + frameSize/2, pixelSize * 5, pixelSize * 5);
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
		});
		
		// Add a listener to detect mouse drag events
		this.addMouseMotionListener(new MouseMotionListener () {
			@Override
			public void mouseDragged(MouseEvent e) {
				//allowsPixelEnactment = true;
				if (SwingUtilities.isLeftMouseButton(e)) {
					// Add a new pixel under the mouse
					addPixel (new Point (e.getX(), e.getY()));
				} else {
					// Remove pixels under the mouse
					int x = (e.getX()-frameSize/2)/pixelSize;
					int y = (e.getY()-frameSize/2)/pixelSize;
					y -= 10;
					int topLeftX = x - 2;
					int topLeftY = y - 2;

					// Iterate over the pixels and queue them to be removed
					for (int b = topLeftY; b <= topLeftY + 4; b++) {
						for (int a = topLeftX; a <= topLeftX + 4; a++) {
							Point o = new Point (a, b);
							pixelsToRemove.add (o);
							pixelsToAdd.remove(o);
						}
					}
					
					// Draw over the pixels being removed
					Graphics g = cpp.getGraphics().create();
					g.setColor(Color.WHITE);
					g.fillRect(((x-2) * pixelSize) + frameSize/2, ((y-2) * pixelSize) + frameSize/2, pixelSize * 5, pixelSize * 5);
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {}
		});

		// Set up and show the options pane
		optionsPane = new OptionsPane (this);
		optionsPane.setVisible (true);
		
		timePane = new TimePane (this);
	}

	OptionsPane optionsPane;
	TimePane timePane;
	
	// Runs a complete update of all listed pixels
	public void updatePixels () {
		long start = System.nanoTime(); // Start the timer
		
		// Cache the pixelsToRemove array, clear out the pixelsToRemove array, then remove the pixels that need to be removed
		Point[] indices = pixelsToRemove.toArray (new Point[] {});
		pixelsToRemove.clear();
		for (Point p : indices) {
			cpp.pixels.remove (p);
		}
		
		if (verboseTimerLogging) System.out.println((System.nanoTime() - start) + " millis for removal.");
		start = System.nanoTime();
		
		// Cache the pixelsToAdd array, clear out the pixelsToAdd array, then add the pixels that need to be added
		Pixel[] pixes = pixelsToAdd.toArray(new Pixel[] {});
		pixelsToAdd.clear();
		for (Pixel p : pixes) {
			if (p != null) {
				cpp.pixels.put(new Point (p.x, p.y), p);
			}
		}
		
		if (verboseTimerLogging) System.out.println((System.nanoTime() - start) + " millis for adding.");
		start = System.nanoTime();

		// Tell other threads that the pixels array is now in use
		isMidUpdate = true;
		// Cache the current list of pixels
		Pixel[] iteratingPixels = cpp.pixels.values().toArray(new Pixel[0]);
		// Iterate over the cached list of pixels
		for (Pixel p : iteratingPixels) {
			if (p.remainingLifetime > 0) { // Check if the pixel has any time left
				if (verboseTimerLogging) System.out.println((System.nanoTime() - start) + " millis for setup.");
				start = System.nanoTime();
				// Enact the pixel
				enact(p);
				if (verboseTimerLogging) System.out.println((System.nanoTime() - start) + " millis for enactment.");
				start = System.nanoTime();
				// Paint the pixel
				cpp.singleToRefresh  = p;
				cpp.paintComponent(cpp.getGraphics().create());
				if (verboseTimerLogging) System.out.println((System.nanoTime() - start) + " millis for painting.");
				start = System.nanoTime();
			} else {
				if (verboseTimerLogging) System.out.println((System.nanoTime() - start) + " millis for setup.");
				start = System.nanoTime();
				// Remove the dead pixel from the pixels array
				cpp.pixels.remove(new Point (p.x, p.y));
				// Paint over it
				Graphics g = this.cpp.getGraphics().create();
				g.setColor(Color.WHITE);
				g.fillRect((p.x * this.pixelSize) + this.frameSize/2, (p.y * this.pixelSize) + this.frameSize/2, this.pixelSize, this.pixelSize);
				if (verboseTimerLogging) System.out.println((System.nanoTime() - start) + " millis for killing.");
				start = System.nanoTime();
			}
		}
		// Tell other threads that the pixels array is now free again
		isMidUpdate = false;
	}

	// Convenience for calling from inside contexts where 'this' doesn't point to the right object
	public void enact (Pixel p) {
		p.enact (this);
	}
	
	// The main entry point for the CodePixelWindow
	public void updateMain () {
		while (true) {
			if (allowsPixelEnactment) { // Run a pixel update only if we're allowed to here
				updatePixels ();
			} else {
				System.out.println("Waiting...");
				
			}
		}
	}
	
	public static void main(String[] args) {
		// Get setup input
		JTextField pxSizeField = new JTextField ("3");
		JTextField frameSizeField = new JTextField ("1000");
		
		final JComponent[] inputs = new JComponent[] {
				new JLabel("Frame size"),
				frameSizeField,
				new JLabel("Pixel size"),
				pxSizeField,
		};		

		int newPx;
		int newFrameSize;

		// Repeat until user puts in acceptable values
		while (true) {
			int result = JOptionPane.showConfirmDialog(null, inputs, "Enter Parameters", JOptionPane.OK_CANCEL_OPTION);
			if (result != 0) {
				return;
			}
			try {
				newPx = Integer.parseInt(pxSizeField.getText());
				newFrameSize = Integer.parseInt(frameSizeField.getText());
				break;
			} catch (Exception e) {

			}
		}

		// Set up the main window
		CodePixelWindow c = new CodePixelWindow ("CodePixel");
		c.setDefaultCloseOperation(EXIT_ON_CLOSE);
		c.pixelSize = newPx;
		c.frameSize = newFrameSize;
		c.prepareGUI();
		c.setVisible (true);
		// Call into the entry point
		c.updateMain();
	}

}
