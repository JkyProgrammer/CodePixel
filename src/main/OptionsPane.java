package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class OptionsPane extends JFrame {
	private static final long serialVersionUID = 7884001862052877939L;
	
	public JTextField lifeLengthField;
	public JTextField ageLimitField;
	public JTextField codeField;
	public JSlider colourSlider;
	private JPanel colourBox = new JPanel ();
	public JCheckBox enableLeapBrdBox;
	
	public OptionsPane (CodePixelWindow cp) {
		super ("Options for New Pixels");
		
		// Initialise all the fields
		lifeLengthField = new JTextField (Integer.toString((CodePixelWindow.lifetimeLength)));
		ageLimitField = new JTextField (Integer.toString((CodePixelWindow.breedingAgeLimit)));
		codeField = new JTextField (CodePixelWindow.startCode);
		enableLeapBrdBox = new JCheckBox ("Enable leap breed mutation");
		enableLeapBrdBox.setSelected(true);
		
		JTextArea infoLabel = new JTextArea ("Left Click or drag on the main window to add new pixels. Right click or drag to kill pixels. All the options below affect the characteristics of newly drawn pixels.");
		infoLabel.setFont(infoLabel.getFont().deriveFont(Font.BOLD));
		infoLabel.setLineWrap(true);
		
		JButton clearButton = new JButton ("Clear (WARNING, KILLS ALL)");
		clearButton.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Disable pixel updating on main thread
				cp.allowsPixelEnactment = false;
				// Wait for updates to finish
				while (cp.isMidUpdate) {System.out.println("Delay due to updating.");}
				// Clear all pixels
				cp.pixelsToRemove.clear();
				cp.pixelsToAdd.clear();
				cp.cpp.pixels.clear();
				// Re-enable everything, and repaint
				cp.allowsPixelEnactment = true;
				cp.cpp.isFirstUpdate = true;
				cp.cpp.repaint();
			}
		});
		
		// Set up colour options
		colourBox.setOpaque(true);
		colourSlider = new JSlider (JSlider.HORIZONTAL, 0, 255, 100);
		colourSlider.addChangeListener(new ChangeListener () {
			@Override
			public void stateChanged(ChangeEvent e) {
				System.out.println("Change");
				System.out.println((float)((float)(colourSlider.getValue())/(float)255));
				colourBox.setBackground(Color.getHSBColor((float)((float)(colourSlider.getValue())/(float)255), 1f, 0.5f));
			}
		});
		colourBox.setBackground(Color.getHSBColor((float)((float)(colourSlider.getValue())/(float)255), 1f, 0.5f));
		
		// Add all components
		final JComponent[] inputs = new JComponent[] {
				infoLabel,
		        new JLabel("Lifetime length"),
		        lifeLengthField,
		        new JLabel("Breeding age limit"),
		        ageLimitField,
		        new JLabel("Pixel code"),
		        codeField,
		        colourBox,
		        colourSlider,
		        enableLeapBrdBox,
		        clearButton
		};
		
		this.setLayout(new GridLayout (11, 1));
		for (JComponent c : inputs) {
			this.add(c);
		}
		infoLabel.setEditable(false);
		
		// Display the frame
		this.setSize(400, 500);
		this.setResizable(false);
		this.setLocation(cp.frameSize + 1, 0);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
}
