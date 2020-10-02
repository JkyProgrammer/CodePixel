package main;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import javax.imageio.ImageIO;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;


public class CodePixelWindow extends JFrame {

	private static final long serialVersionUID = -8407839062091833852L;

	public CodePixelWindow(String string) {
		super (string);
	}

	CodePixelPanel cpp;
	int frameSize = 1000;
	int pixelSize = 3;
	boolean isWritingToImageFile = false;
	String imageFilePath = "";
	
	static int lifetimeLength = 100; 					// The default lifetime length for the options pane
	static int breedingAgeLimit = 80;					// The default breeding age limit for the options pane
	static String startCode = "agecol evocol smrtbrd";	// The default starting code for the options pane

	boolean allowsPixelEnactment = true;				// Defines whether or not the pixelUpdate method should be called
	int killBrushRadius = 2;							// Defines the radius around the mouse that is removed when using the kill brush
	int frameNumber = 0;
	BufferedImage currentlyWritingImage = null;
	
	public HashMap<Point, Pixel> pixels = new HashMap<Point, Pixel> ();
	public Queue<Point> pixelQueue = new LinkedList<Point> ();
	
	// Add a new pixel at a point
	public void addDefaultPixel (Point p) {
		if (pixelExists (p.x, p.y)) return;
		Pixel pix = new Pixel (100, "agecol smrtbrd", p.x, p.y, 80);
		synchronized (pixels) {
			pixels.put (p, pix);
		}
		synchronized (pixelQueue) {
			pixelQueue.add(p);
		}
		cpp.paintPixel (pix);
	}
	
	// Add a new pixel at a point
	public void addPixel (Pixel pix) {
		if (pixelExists (pix.x, pix.y)) return;
		Point p = new Point (pix.x, pix.y);
		synchronized (pixels) {
			pixels.put (p, pix);
		}
		synchronized (pixelQueue) {
			pixelQueue.add(p);
		}
		cpp.paintPixel (pix);
	}
	
	// Check if a pixel exists at the defined point
	public boolean pixelExists (int x, int y) {
		if (pixels.containsKey(new Point (x, y))) return true;
		return false;
	}
	
	// Kill a specific pixel
	public void killPixel (Point p) {
//		long startp = System.nanoTime();
		synchronized (pixels) {
			pixels.remove(p);
		}
//		pMan.enter("removep", System.nanoTime()-startp);
		
//		long startc = System.nanoTime();
		cpp.clearPixel(p);
//		pMan.enter("clearp", System.nanoTime()-startc);
	}
	
	// Called to set up the GUI
	public void prepareGUI () {
		// Create a new CodePixelPanel
		cpp = new CodePixelPanel (this);
		this.add(cpp);
		// Prepare the frame
		setSize (frameSize, frameSize);
		setResizable (false);

		// Set up and show the options pane
		//optionsPane = new OptionsPane (this);
		//optionsPane.setVisible (true);
		
		//timePane = new TimePane (this);
		
		//currentlyWritingImage = new BufferedImage (this.frameSize, this.frameSize, BufferedImage.TYPE_INT_ARGB);
		setVisible (true);
		addDefaultPixel (new Point (0,0));
	}

	OptionsPane optionsPane;
	TimePane timePane;
	PerformanceManager pMan = new PerformanceManager();
	
	public void writeFinalImage () {
		try {
		    File outputfile = new File(imageFilePath + "-" + frameNumber + ".png");
		    
		    
		    File directory = new File(outputfile.getAbsolutePath().substring(0, outputfile.getAbsolutePath().lastIndexOf("/")));
		    if (!directory.exists()){
		        directory.mkdirs();
		    }
		    
		    ImageIO.write(currentlyWritingImage, "png", outputfile);
		} catch (Exception e) {
		    JOptionPane.showMessageDialog(this, e.getMessage() + ". You must resolve this before the program can continue.", "File Write Error", JOptionPane.ERROR_MESSAGE);
		    System.exit(1);
		}
		currentlyWritingImage = new BufferedImage (this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
	}
	
	// The main entry point for the CodePixelWindow
	public void updateMain () {
		while (true) {
			if (allowsPixelEnactment) { // Run a pixel update only if we're allowed to here
				Pixel p;
				synchronized (pixelQueue) {
					synchronized (pixels) {
						p = pixels.get(pixelQueue.poll());
					}
				}
				if (p == null) continue;
				
//				long start = System.nanoTime();
				boolean res = p.enact(this);
//				pMan.enter("enact", System.nanoTime()-start);
				
				if (res) {
					synchronized (pixelQueue) {
						pixelQueue.add(new Point (p.x, p.y));
					}
					
					cpp.paintPixel(p);
				} else {
					killPixel (new Point (p.x, p.y));
				}
			} else {
				System.out.println("Waiting...");
			}
		}
	}
	
	
	
	public static void main(String[] args) {
		// Get setup input
		JTextField pxSizeField = new JTextField ("3");
		JTextField frameSizeField = new JTextField ("1000");
		JCheckBox fileWrite = new JCheckBox ("Write each frame to image file", false);
		JTextField fileWritePath = new JTextField (System.getProperty("user.home") + "/Documents/CodePixel/Frame");
		
		final JComponent[] inputs = new JComponent[] {
				new JLabel("Frame size"),
				frameSizeField,
				new JLabel("Pixel size"),
				pxSizeField,
				fileWrite,
				new JLabel ("Image file path prefix"),
				fileWritePath
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
				if ((fileWrite.isSelected() && !fileWritePath.getText().equals("")) || !fileWrite.isSelected())
					break;
			} catch (Exception e) {

			}
		}

		// Set up the main window
		CodePixelWindow c = new CodePixelWindow ("CodePixel");
		c.setDefaultCloseOperation(EXIT_ON_CLOSE);
		c.pixelSize = newPx;
		c.frameSize = newFrameSize;
		c.imageFilePath = fileWritePath.getText();
		c.isWritingToImageFile = fileWrite.isSelected();
		c.prepareGUI();
		c.setVisible (true);
		
		// Call into the entry point on every thread
		ArrayList<Thread> ts = new ArrayList<Thread> ();
		for (int i = 0; i < 2; i++) {
			Thread t = new Thread () {
				public void run () {
					c.updateMain();
				}
			};
			ts.add(t);
			t.start();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
//		try {
//			Thread.sleep(20000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		System.out.println ("Average time for enacting: " + c.pMan.averageForCode("enact"));
//		System.out.println ("Average time for removing: " + c.pMan.averageForCode("removep"));
//		System.out.println ("Average time for clearing: " + c.pMan.averageForCode("clearp"));
//		System.exit(0);
	}

}
