package main;

import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

public class TimePane extends JFrame {
	private JButton playButton;
	private JButton pauseButton;
	private JButton stepButton;
	
	
	public TimePane (CodePixelWindow cp) {
		super ("Time Control Pane");
		
		playButton = new JButton ("Play");
		playButton.setIcon(new ImageIcon (getClass().getResource("/resources/play.png")));
		playButton.setHorizontalAlignment(SwingConstants.LEFT);
		playButton.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				cp.allowsPixelEnactment = true;
			}
		});
		
		
		pauseButton = new JButton ("Pause");
		pauseButton.setIcon(new ImageIcon (getClass().getResource("/resources/pause.png")));
		pauseButton.setHorizontalAlignment(SwingConstants.LEFT);
		pauseButton.addActionListener(new ActionListener () {
			@Override
			public void actionPerformed(ActionEvent e) {
				cp.allowsPixelEnactment = false;
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
		
		this.getContentPane().setLayout(new GridLayout (3,1));
		
		this.add(playButton);
		this.add(pauseButton);
		this.add(stepButton);
		
		
		this.setResizable(false);
		this.setSize(400, 200);
		this.setLocation(cp.frameSize + 1, cp.optionsPane.getHeight()+24);
		this.setVisible(true);
	}
}
