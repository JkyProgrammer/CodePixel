package main;

import java.awt.Color;
import java.awt.Point;
import java.util.Random;

public class Pixel {
	int remainingLifetime;  // Stores the pixel's remaining time alive
	int ageStart;			// Stores the total available lifetime the pixel will have
	int breedingAgeLimit;	// Stores the age limit after which the pixel is not allowed to breed
	int x;					// The horizontal position on the pixel grid
	int y;					// The vertical position on the pixel grid
	
	String code = "";		// The genetic code for this pixel
	Color color;			// The colour of the pixel
	float tint = 0.0f;		// The HSB hue of the pixel
	boolean allowsLeapBreedGene = true;	// Determines whether or not the pixel can produce the leapbrd gene
	
	// Provides a simple description of the pixel
	public String toString () {
		return "Pixel: " + x + ", " + y + ", " + remainingLifetime + ", " + code;
	}
	
	// Constructor
	public Pixel (int lifetime, String complexArgs, int xx, int yy, int ageLim) {
		remainingLifetime = lifetime;
		ageStart = lifetime;
		code = complexArgs;
		color = new Color (100,100,100);
		breedingAgeLimit = ageLim;
		this.x = xx;
		this.y = yy;
	}
	
	// Synchronised function to create a new pixel as a child of the origin pixel
	private synchronized static void newPixel (int targetX, int targetY, Pixel origin, CodePixelWindow cp) {
		Pixel newPixel = new Pixel (origin.ageStart, origin.code.replaceAll("prsistnt", ""), targetX, targetY, origin.breedingAgeLimit);
		// Potentially generate the leapbrd gene, if it is allowed
		if (origin.allowsLeapBreedGene) {
			Random r = new Random ();
			if (r.nextInt (10000) == 0) {
				newPixel.code = "leapbrd " + newPixel.code;
			}
		} else {
			newPixel.allowsLeapBreedGene = false;
		}
		// Pass on the tint
		newPixel.tint = origin.tint;
		// Add the pixel to the hashmap
		cp.addPixel(newPixel);
	}
	
	public Color agecol() {
		int brightness = 255 - (int)((float)remainingLifetime/(float)ageStart*255.0);
		float col = (float)brightness/255f;
		
		return Color.getHSBColor(0f, 0f, col);
	}
	
	// Evaluate a single gene for this pixel
	private void evaluate (String arg, CodePixelWindow cp) {
		Random r = new Random ();
		if (arg.equals("smrtbrd")) { /* Smart Breed gene: the pixel will breed into any neighbouring spaces, 
		as long as this would not create offscreen growth and as long as the pixel is young enough to breed. */
			if ((ageStart - remainingLifetime) <= breedingAgeLimit) {
				
				int newX = (r.nextInt(3) - 1) + x;
				int newY = (r.nextInt(3) - 1) + y;
				if (Math.abs(newX - x) + Math.abs(newY - y) > 1) {
					return;
				}
				int lim = cp.frameSize/(2 * cp.pixelSize);
				
				if (!cp.pixelExists(newX, newY) && !(newX > lim || newX < -lim) && !(newY > lim || newY < -lim)) {
					newPixel (newX, newY, this, cp);
					return;
				}
			}
		} else if (arg.equals("agecol")) { // Age Colour: the pixel's colour is based on a greyscale between black (young) out to white (old).
			color = agecol ();
		} else if (arg.equals("evocol")) { /* Evolutionary Colour: the pixel's colour is tinted with a colour. That colour can mutate and change 
		over time, and the tint is passed down to child pixels. */
			if (r.nextInt(20) == 4) {
				int b = r.nextInt(150);
				if (b < 50) {
					tint -= (0.01);
				} else if (b > 100) {
					tint += (0.01);
				}
			}
			float[] hsb = new float[3];
			Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
			color = Color.getHSBColor(tint, 1f-hsb[2], hsb[2]);;
		} else if (arg.equals("leapbrd")) { // Leap Breed: the pixel will throw out a 'spore' and start an entirely new colony away from itself. The colony base has the same genetic code. The gene can only be used once per pixel. 
			this.code = this.code.replaceAll("leapbrd", "");
			boolean negativeDist = r.nextBoolean();
			int distance = r.nextInt(100) + 30;
			boolean horizontal = r.nextBoolean();
			if (negativeDist) {
				distance = -distance;
			}
			
			int targetX = this.x;
			int targetY = this.y;
			
			if (horizontal) targetX = this.x + distance;
			else targetY = this.y + distance;
			
			if (cp.pixelExists(targetX, targetY)) return;
			
			int lim = cp.frameSize/(2 * cp.pixelSize);
			
			Pixel inheriter = new Pixel (2, this.code.replaceAll("prsistnt", ""), targetX, targetY, 0);
			inheriter.tint = this.tint;
			inheriter.allowsLeapBreedGene = false;
			
			if (horizontal) {
				
				int d = 1;
				if (negativeDist) d = -1;
				
				for (int i = this.x; i != targetX; i+=d) {
					if (!cp.pixelExists(i, targetY)) {
						if (!(i > lim || i < -lim) && !(targetY > lim || targetY < -lim)) {
							newPixel (i, targetY, inheriter, cp);
						} else {
							break;
						}
					}
				}
			} else {
				targetY = this.y + distance;
				int d = 1;
				if (negativeDist) d = -1;
				
				for (int i = this.y; i != targetY; i+=d) {
					if (!cp.pixelExists(targetX, i)) {
						if (!(targetX > lim || targetX < -lim) && !(i > lim || i < -lim)) {
							newPixel (targetX, i, inheriter, cp);
						} else {
							break;
						}
					}
				}
			}
			
			if (!cp.pixelExists(targetX, targetY)) {
				if (!(targetX > lim || targetX < -lim) && !(targetY > lim || targetY < -lim)) {
					newPixel (targetX, targetY, this, cp);
				}
			}
			
			
		} else if (arg.equals("homocidal")) {
			// TODO: New cell code
		} else if (arg.equals ("explsvkill")) {
			// TODO: New cell code
		} else if (arg.equals ("explsvbrd")) {
			// TODO: New cell code
		} else if (arg.equals ("msedistcol")) {
			// TODO: New cell code
		} else if (arg.equals ("logiclbrd")) {
			// TODO: New cell code
		} else if (arg.equals ("prsistnt")) {
			remainingLifetime++;
		} else {
			System.out.println ("Unrecognised instruction " + arg);
		}
	}
	
	// Execute a full cycle of the pixel's life
	public boolean enact (CodePixelWindow cp) {
		remainingLifetime--;
		if (remainingLifetime > 0) {
			for (String arg : code.split(" ")) {
				if (!arg.isEmpty()) evaluate (arg, cp);
			}
			return true;
		}
		return false;
	} 
}
