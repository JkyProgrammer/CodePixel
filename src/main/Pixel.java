package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;
import java.util.Random;

public class Pixel {
	int remainingLifetime;
	
	int x;
	int y;
	
	String code = "";
	
	public String toString () {
		return "Pixel: " + x + ", " + y + ", " + remainingLifetime + ", " + code;
	}
	
	public Pixel (int lifetime, String complexArgs, int xx, int yy) {
		remainingLifetime = lifetime;
		code = complexArgs;
		this.x = xx;
		this.y = yy;
	}
	
	public void enact (CodePixel cp, ListIterator<Pixel> li) {
		remainingLifetime--;
		ArrayList<String> args = new ArrayList<String> (Arrays.asList(code.split(" ")));
		for (String arg : args) {
			if (arg == "brd") {
				Random r = new Random ();
				
				int newX = (r.nextInt(3) - 1) + x;
				int newY = (r.nextInt(3) - 1) + y;
				if (!cp.cpp.pixelExists(newX, newY)) {
					Pixel newPixel = new Pixel (cp.lifetimeLength, code, newX, newY);
					li.add(newPixel);
					return;
				}
			}
		}
	} 
}
