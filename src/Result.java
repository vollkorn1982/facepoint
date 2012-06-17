import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.jfree.ui.RefineryUtilities;

/**
 * Description: Shows the result of the video.<br>
 * Author: T. Tews <br>
 * Last change: 04.10.2010 <br>
 * Version: 1.0 <br>
 * History: <br>
 * Known Bugs and Restrictions: -none- <br>
 */
public class Result extends Panel implements Runnable {
	
	static JFrame frame;
	private static String resultString = "";
	BufferedImage  image;
	private static double [] emotionResult = new double [8]; // Neutral, Freude, Aerger
	final static String [] category = {"Anger OM", "Anger CM", "Happiness", "Frightend", "Surprise", "Sadness", "Disgust", "Neutral"};
	
	
	public Result(String str, double [] emotionRes) {
		resultString = str;	
		
		for(int k = 0; k < emotionRes.length; k++) // copy the results
        	emotionResult[k] = emotionRes[k];
	}

	  static void displayJFrame()
	  {  
	    frame = new JFrame("Result");



	    // display our jdialog when the jbutton is pressed
	    JButton showDialogButton = new JButton("OK");
	    JLabel label = new JLabel(resultString);
	    JPanel panel = new JPanel();
	    
	    
	    
	    //label.setIcon(icon);
	    panel.add(label);
	    
	    showDialogButton.addActionListener(new ActionListener()
	    {
	      public void actionPerformed(ActionEvent e)
	      {
	    	  System.exit(0);
	      }
	    });
	    
	    
	    // Show Result Chart Bar =)
	    BarChart bar = new BarChart("Emotion-Results", emotionResult, category);
	    bar.pack();
		RefineryUtilities.centerFrameOnScreen(bar);
		bar.setVisible(true);
		
//		LineChart line = new LineChart("Dynamic Areas", category);
//	    line.pack();
//		RefineryUtilities.centerFrameOnScreen(line);
//		line.setVisible(true);

	    // put the button on the frame
	    frame.getContentPane().setLayout(new BorderLayout());
	    label.setBounds(50, 50, 50, 50);
	    frame.add(panel, BorderLayout.NORTH);
	    frame.add(showDialogButton, BorderLayout.SOUTH);

	    // set up the jframe, then display it
	    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    frame.setPreferredSize(new Dimension(300, 300));
	    frame.pack();
	    frame.setLocationRelativeTo(null);
	    frame.setVisible(true);
	  }
	  
	  
	  public void paint(Graphics g) {
		  g.drawImage( image, 0, 0, null);
		  }

	@Override
	public void run() {
		displayJFrame();
		
	}
	}


	
