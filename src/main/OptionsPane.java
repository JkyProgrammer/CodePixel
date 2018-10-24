package main;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class OptionsPane extends JFrame {
	public JTextField lifeLengthField;
	public JTextField ageLimitField;
	public JTextField codeField;
	
	public OptionsPane (CodePixelWindow cp) {
		super ("Options for New Pixels");
		
		lifeLengthField = new JTextField (Integer.toString((cp.lifetimeLength)));
		ageLimitField = new JTextField (Integer.toString((cp.breedingAgeLimit)));
		codeField = new JTextField (cp.startCode);
		
		JTextArea infoLabel = new JTextArea ("Left Click or drag on the main window to add new pixels. Right click or drag to kill pixels.");
		infoLabel.setFont(infoLabel.getFont().deriveFont(Font.BOLD));
		infoLabel.setLineWrap(true);
		//infoLabel.setEditable(false);
		
		final JComponent[] inputs = new JComponent[] {
				infoLabel,
		        new JLabel("Lifetime length"),
		        lifeLengthField,
		        new JLabel("Breeding age limit"),
		        ageLimitField,
		        new JLabel("Pixel code"),
		        codeField,
		};
		
		this.setLayout(new GridLayout (7, 1));
		for (JComponent c : inputs) {
			this.add(c);
		}
		infoLabel.setEditable(false);
		this.setSize(300, 400);
		this.setResizable(false);
		this.setLocation(cp.frameSize + 1, 0);
	}
}
