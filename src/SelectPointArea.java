import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Description: Handles the selection of the search area".<br>
 * Author: T. Tews<br>
 * Last change: 13.12.2011 <br>
 * Version: 1.0 <br>
 * History: <br>
 * Known Bugs and Restrictions: -none- <br>
 */
public class SelectPointArea implements ActionListener, Runnable {

	App_FacePoints aFacPo;
	MouselistenerPanel mouselistnerPanel;
	FaceDetection myFaceDetection; 

	JFrame frame = new javax.swing.JFrame(
			"Select Rectangle (Left Top-Corner and right Down-Corner)");

	private String[] path = { "0", "0" };
	public static BufferedImage img = null;
	private JButton okButton = new JButton("OK");
	private JButton cancelButton = new JButton("Cancel");
	private String[] filename = { "0", "0" };
	private int[] frameNr = { 0, 0 };
	private int clusternumber; // number of clusters
	// x, y, w, h // { 190, 130, 587, 430 }
	Rectangle recValue[] = new Rectangle[4]; // 170,130,327,400
															// x, y, w, h
	private int brokenFrame = 8;

	ImagePanel ip; // Panel for the frames

	public SelectPointArea(String[] pa, String[] fName, int[] fraNr, int cnumber) {
		for (int k = 0; k < pa.length; k++)
			path[k] = pa[k];

		filename[0] = fName[0]; // filenames
		filename[1] = fName[1];
		frameNr[0] = fraNr[0]; // numbers of frame
		frameNr[1] = fraNr[1];
		clusternumber = cnumber;
		//System.out.println("SPA: " + filename[0]); // vp101GF
		//System.out.println("Path: " + path[0]); //C:/Fahrsimulatorvideos/neutral-geschnitten/Vp101n
		myFaceDetection = new FaceDetection(); 
		myFaceDetection.searchOpenCVAreas(path[0], brokenFrame);
	}

	public void selectPointAreaCalc() throws IOException {

		javax.swing.JFrame.setDefaultLookAndFeelDecorated(true);

		MovieSource ms = new MovieSource(path);
		img = ms.readFrame(6, 0); // show the first frame + first video //TODO TENX

		
		// Get the face detection Rectangle size
		recValue = myFaceDetection.getRecValue(); // 
		//System.out.println("SPA:" + faceDetectionRecValue.x);
		//recValue = faceDetectionRecValue;
		ip = new ImagePanel(recValue, path[0]);
		
		ip.setOpaque(true);
		ip.setBackground(Color.black);

		// show first frame
		ip.setImage(img, 6); //TODO TENX

		
		mouselistnerPanel = new MouselistenerPanel(frame, img, recValue[0]);
		mouselistnerPanel.setPreferredSize(new Dimension(img.getWidth(), img
				.getHeight()));
		frame.getContentPane().add(mouselistnerPanel, BorderLayout.NORTH);
		frame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);

		// Create + add buttons
		JPanel buttonPanel = new JPanel();
		okButton.setMnemonic(KeyEvent.VK_O);
		okButton.addActionListener(this);
		cancelButton.setMnemonic(KeyEvent.VK_C);
		cancelButton.addActionListener(this);
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		frame.add(buttonPanel, BorderLayout.SOUTH);

		// Window position
		Dimension frameSize = new Dimension(730, 540);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int top = (screenSize.height - frameSize.height) / 2;
		int left = (screenSize.width - frameSize.width) / 2;
		frame.setSize(frameSize);
		frame.setLocation(left, top);

		// closing the window
		frame.setPreferredSize(new Dimension(img.getWidth(),
				img.getHeight() + 70));
		frame.pack();
		frame.setVisible(true);
	}

	// Handle open, select and cancel buttons
	public void actionPerformed(ActionEvent e) {

		// Handle open button action.
		if (e.getSource() == okButton) {

			// Two points were selected...
			if ((mouselistnerPanel.getPressedX() != 0)
					&& (mouselistnerPanel.getPressedX2() != 0)) { 
				int[] tmp = { mouselistnerPanel.getPressedX(),
						mouselistnerPanel.getPressedY(),
						mouselistnerPanel.getPressedX2(),
						mouselistnerPanel.getPressedY2() };

				// Calc Rectangle
				Rectangle tmpRecValue = new Rectangle(tmp[0], tmp[1], tmp[2]
						- tmp[0], tmp[3] - tmp[1]); 
				
				recValue [0] =  tmpRecValue;
				
				//System.out.println("SPA: " + tmp[0] + ", "+ tmp [1] + ", " + (tmp[2] - tmp[0]) + ", "+ (tmp[3] - tmp[1]));

				// Start Face Point App
				new Thread(new App_FacePoints(path, filename, frameNr,
						recValue, clusternumber, brokenFrame)).start();
				frame.setVisible(false);

				// select default points
			} else if ((mouselistnerPanel.getPressedX() == 0)
					&& (mouselistnerPanel.getPressedX2() == 0)) { 

				// Start Face Point App
				new Thread(
						new App_FacePoints(path, filename, frameNr, recValue, clusternumber, brokenFrame))
						.start();
				//System.out.println("SPA: " + recValue.x + ", "+ recValue.y + ", " + recValue.width + ", "+ recValue.height);

				frame.setVisible(false);
			} else { // Only one mouse click

				frame.setTitle("Please select two Points!");
			}

		} else if (e.getSource() == cancelButton) { // Handle cancel button
			frame.setVisible(false);
		}
	}

	@Override
	public void run() {
		try {
			selectPointAreaCalc();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
