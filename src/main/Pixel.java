package main;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;
import java.util.Random;

public class Pixel {
	int remainingLifetime;
	int ageStart;
	int ageLimit;
	int x;
	int y;
	
	String code = "";
	Color color;
	float tint = 0.0f;
	
	public String toString () {
		return "Pixel: " + x + ", " + y + ", " + remainingLifetime + ", " + code;
	}
	
	public Pixel (int lifetime, String complexArgs, int xx, int yy, int ageLim) {
		remainingLifetime = lifetime;
		ageStart = lifetime;
		code = complexArgs;
		color = new Color (100,100,100);
		ageLimit = ageLim;
		this.x = xx;
		this.y = yy;
	}
	
	private void newPixel (ListIterator<Pixel> li, int targetX, int targetY) {
		Pixel newPixel = new Pixel (ageStart, code.replaceAll("prsistnt", ""), targetX, targetY, ageLimit);
		Random r = new Random ();
		if (r.nextInt (10000) == 0) {
			newPixel.code = "leapbrd " + newPixel.code;
		}
		newPixel.tint = this.tint;
		li.add(newPixel);
	}
	 
	private void evaluate (String arg, CodePixelWindow cp, ListIterator<Pixel> li) {
		Random r = new Random ();
		if (arg.equals("smrtbrd")) {
			if ((ageStart - remainingLifetime) <= ageLimit) {
				
				int newX = (r.nextInt(3) - 1) + x;
				int newY = (r.nextInt(3) - 1) + y;
				if (Math.abs(newX - x) + Math.abs(newY - y) > 1) {
					return;
				}
				int lim = cp.frameSize/(2 * cp.pixelSize);
				
				if (!cp.cpp.pixelExists(newX, newY) && !(newX > lim || newX < -lim) && !(newY > lim || newY < -lim)) {
					newPixel (li, newX, newY);
					return;
				}
			}
		} else if (arg.equals("agecol")) {
			int brightness = 255 - (int)((float)remainingLifetime/(float)ageStart*255.0);
			//rgbNum = 100; 
			
			float[] hsb = new float[3];
			Color.RGBtoHSB(brightness, brightness, brightness, hsb);
			Color c = Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
			//System.out.println(c);
			color = new Color (c.getRGB());
		} else if (arg.equals("evocol")) {
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
			Color c = Color.getHSBColor(tint, 1.0f, hsb[2]);
			color = new Color (c.getRGB());
		} else if (arg.equals("leapbrd")) {
			this.code = this.code.replaceAll("leapbrd", "");
			boolean negativex = r.nextBoolean();
			int distancex = r.nextInt(100) + 100;
			boolean negativey = r.nextBoolean();
			int distancey = r.nextInt(100) + 100;
			if (negativex) {
				distancex = -distancex;
			}
			if (negativey) {
				distancey = -distancey;
			}
			
			int targetX;
			int targetY;
			
			targetX = this.x + distancex;
			targetY = this.y + distancey;
			int lim = cp.frameSize/(2 * cp.pixelSize);
			if (!cp.cpp.pixelExists(targetX, targetY) && !(targetX > lim || targetX < -lim) && !(targetY > lim || targetY < -lim)) {
				newPixel (li, targetX, targetY);
				return;
			}
		} else if (arg.equals("homocidal")) {
			// TODO: New cell code
		} else if (arg.equals ("explsvkill")) {
			// TODO: New cell code
		} else if (arg.equals ("explsvbrd")) {
			// TODO: New cell code
		} else if (arg.equals ("msedistcol")) {
			// TODO: New cell code
		} else if (arg.equals ("prsistnt")) {
			remainingLifetime++;
			
		}
	}
	
	public void enact (CodePixelWindow cp, ListIterator<Pixel> li) {
		remainingLifetime--;
		ArrayList<String> args = new ArrayList<String> (Arrays.asList(code.split(" ")));
		for (String arg : args) {
			evaluate (arg, cp, li);
		}
	} 
}
