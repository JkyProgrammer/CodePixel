package main;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;
import java.util.Random;

public class Pixel {
	int remainingLifetime;
	
	int x;
	int y;
	
	String code = "";
	float tint = 40f;
	
	public String toString () {
		return "Pixel: " + x + ", " + y + ", " + remainingLifetime + ", " + code;
	}
	
	public Pixel (int lifetime, String complexArgs, int xx, int yy) {
		remainingLifetime = lifetime;
		code = complexArgs;
		this.x = xx;
		this.y = yy;
	}
	
	public void enact (CodePixelWindow cp, ListIterator<Pixel> li) {
		remainingLifetime--;
		ArrayList<String> args = new ArrayList<String> (Arrays.asList(code.split(" ")));
		for (String arg : args) {
			if (arg.equals("brd")) {
				Random r = new Random ();
				int newX = (r.nextInt(3) - 1) + x;
				int newY = (r.nextInt(3) - 1) + y;
				if (Math.abs(newX - x) + Math.abs(newY - y) > 1) {
					return;
				}
				if (!cp.cpp.pixelExists(newX, newY)) {
					Pixel newPixel = new Pixel (cp.lifetimeLength, code, newX, newY);
					li.add(newPixel);
					return;
				}
			}
		}
	} 
}
