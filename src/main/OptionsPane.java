package main;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class OptionsPane extends JFrame {
	private static final long serialVersionUID = 7884001862052877939L;
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
		
		JButton clearButton = new JButton ("Clear (WARNING, KILLS ALL)");
		clearButton.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				cp.pixelsToRemove.clear();
				//cp.cpp.pixels.clear();
				Pixel[] pixes = cp.cpp.pixels.toArray(new Pixel[] {});
				for (Pixel p : pixes) {
					if (p != null)
						cp.pixelsToRemove.add (new Point (p.x, p.y));
				}
				cp.pixelsToAdd.clear();
			}
		});
		
		final JComponent[] inputs = new JComponent[] {
				infoLabel,
		        new JLabel("Lifetime length"),
		        lifeLengthField,
		        new JLabel("Breeding age limit"),
		        ageLimitField,
		        new JLabel("Pixel code"),
		        codeField,
		        clearButton
		};
		
		this.setLayout(new GridLayout (8, 1));
		for (JComponent c : inputs) {
			this.add(c);
		}
		infoLabel.setEditable(false);
		this.setSize(300, 400);
		this.setResizable(false);
		this.setLocation(cp.frameSize + 1, 0);
	}
}
