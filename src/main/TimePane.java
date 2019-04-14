package main;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class TimePane extends JFrame {
	private static final long serialVersionUID = 1L;
	private JButton playButton;
	private JButton pauseButton;
	private JButton stepButton;
	private JButton exportImage;
	
	public JLabel pixelCounter;
	public JLabel frameCounter;
	
	public TimePane (CodePixelWindow cp) {
		super ("Time Control Pane");
		
		playButton = new JButton ("Play");
		playButton.setIcon(new ImageIcon (getClass().getResource("/resources/play.png")));
		playButton.setHorizontalAlignment(SwingConstants.LEFT);
		playButton.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				cp.allowsPixelEnactment = true;
				exportImage.setEnabled(false);
			}
		});
		
		
		pauseButton = new JButton ("Pause");
		pauseButton.setIcon(new ImageIcon (getClass().getResource("/resources/pause.png")));
		pauseButton.setHorizontalAlignment(SwingConstants.LEFT);
		pauseButton.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				cp.allowsPixelEnactment = false;
				exportImage.setEnabled(true);
			}
		});
		
		stepButton = new JButton ("Step");
		stepButton.setIcon(new ImageIcon (getClass().getResource("/resources/step.png")));
		stepButton.setHorizontalAlignment(SwingConstants.LEFT);
		stepButton.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!cp.allowsPixelEnactment) cp.updatePixels();
			}
		});
		
		exportImage = new JButton ("Export Image");
		exportImage.setIcon(new ImageIcon (getClass().getResource("/resources/camera.png")));
		exportImage.setHorizontalAlignment(SwingConstants.LEFT);
		exportImage.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				cp.writeFinalImage();
			}
		});
		exportImage.setEnabled(false);

		pixelCounter = new JLabel ("Pixels: 0");
		
		frameCounter = new JLabel ("Frame: 0");
		
		this.getContentPane().setLayout(new GridLayout (6,1));
		
		this.add(playButton);
		this.add(pauseButton);
		this.add(stepButton);
		this.add(exportImage);
		this.add(pixelCounter);
		this.add(frameCounter);
		
		this.setResizable(false);
		this.setSize(400, 350);
		this.setLocation(cp.frameSize + 1, cp.optionsPane.getHeight()+24);
		this.setVisible(true);
	}
}
