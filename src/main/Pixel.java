package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Pixel {
	int remainingLifetime;
	
	int x;
	int y;
	
	String code = "";
	
	public Pixel (int lifetime, String complexArgs, int x, int y) {
		remainingLifetime = lifetime;
		code = complexArgs;
	}
	
	public void enact (CodePixel cp) {
		remainingLifetime--;
		ArrayList<String> args = new ArrayList<String> (Arrays.asList(code.split(" ")));
		for (String arg : args) {
			if (arg == "brd") {
				Random r = new Random ();
				
				int newX = (r.nextInt(3) - 1) + x;
				int newY = (r.nextInt(3) - 1) + y;
				if (!cp.pixelExists(newX, newY)) {
					Pixel newPixel = new Pixel (cp.lifetimeLength, code, newX, newY);
					cp.pixels.add(newPixel);
					return;
				}
			}
		}
	} 
}
