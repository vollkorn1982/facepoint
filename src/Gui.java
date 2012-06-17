import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.Properties;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Observable;
import java.util.Observer;


import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Description: Creates the start window<br>
 * with the possibility to select a  neutral + compare video.<br>
 * It also shows the status of the frame class.
 * Author: T. Tews <br>
 * Last change: 21.06.2010 <br>
 * Version: 1.0 <br>
 * History: <br>
 * Known Bugs and Restrictions: -none- <br>
 */
public class Gui extends JPanel implements ActionListener {
	/**
	 * Instance of captureFrame, capture Duration and SelectPointArea Class
	 */
	CaptureFrames capFrames;

	SelectPointArea selectPointArea;
	CaptureVideoDuration capDuration = new CaptureVideoDuration();
	Gui gui;

	private static final long serialVersionUID = 1L;
	
	// default values
	
	private String[] path = { "C:/Fahrsimulatorvideos/neutral-geschnitten/Vp101n", "C:/Videos" };
	private String[] filename = { "vp101GAE2", "vp041GF2b" };
	private String[] filenamePath = { path[0] + "/" + filename[0],
			path[1] + "/" + filename[1] }; // Input Video default
	public JButton openCompareButton, openOriginButton, cancelButton, okButton;
	private JTextField orginFileURLfield, comparFileURLfield,
			outputFileURLfield;                               // URL Textfields...
	private JTextField originFrameNrField, compareFrameNrField, clusterNumberField;

	private JLabel originFrameLabel, compareFrameLabel;
	private JPanel buttonPanel, comparePanel, originPanel;
	
	private Font font = new Font("Serif", Font.ITALIC, 15);

	private JFileChooser fc;
	private JFrame frame;

	private int[] frameNr = { 30, 30 }; // default frame length
	private int frameNrDur = 1;
	private int clusternumber = 10;

	public Gui() {
		super(new BorderLayout());
		createGui();
	}

	/**
	 * Creates the start window gui with two url and frame fields
	 * and show the status field 
	 */
	public void createGui() {

		// Create the log first, because the action listeners
		// need to refer to it.
		orginFileURLfield = new JTextField(20);
		orginFileURLfield.setMargin(new Insets(5, 5, 5, 5));
		orginFileURLfield.setEditable(false);
		orginFileURLfield.setText(filenamePath[0]);

		comparFileURLfield = new JTextField(20);
		comparFileURLfield.setMargin(new Insets(5, 5, 5, 5));
		comparFileURLfield.setEditable(false);
		comparFileURLfield.setText(filenamePath[1]);
		
		outputFileURLfield = new JTextField(20);
		outputFileURLfield.setText("Waiting...");
		outputFileURLfield.setMargin(new Insets(5, 5, 5, 5));
		outputFileURLfield.setEditable(false);

		// Create a file chooser
		fc = new JFileChooser();
		
		clusterNumberField = new JTextField(20);
		clusterNumberField.setText("" + clusternumber);
		clusterNumberField.setMargin(new Insets(5, 5, 5, 5));
		clusterNumberField.setEditable(true);

		// Create the buttons.
		okButton = new JButton("OK");
		okButton.setMnemonic(KeyEvent.VK_O); // (ALT + O) Shortcut
		okButton.addActionListener(this);
		cancelButton = new JButton("Cancel");
		cancelButton.setMnemonic(KeyEvent.VK_C);
		cancelButton.addActionListener(this);


		// this.addKeyListener(this);
		openOriginButton = new JButton("Open the neutral File...");
		openOriginButton.addActionListener(this);
		openCompareButton = new JButton("Open the compare File...");
		openCompareButton.addActionListener(this);


		// For layout purposes, put the buttons in a separate panel
		comparePanel = new JPanel(); // use FlowLayout#
		originPanel = new JPanel();
		buttonPanel = new JPanel();
		originFrameLabel = new JLabel(" Frames: ");
		compareFrameLabel = new JLabel(" Frames: ");
		compareFrameLabel.setFont(font);
		originFrameLabel.setFont(font);

		originFrameNrField = new JTextField("0" + frameNr[0]);
		originFrameNrField.setMargin(new Insets(5, 10, 5, 10));
		originFrameNrField.setEditable(false);

		compareFrameNrField = new JTextField("0" + frameNr[1]);
		compareFrameNrField.setMargin(new Insets(5, 10, 5, 10));
		compareFrameNrField.setEditable(false);

		originPanel.add(orginFileURLfield);
		originPanel.add(openOriginButton);
		originPanel.add(originFrameLabel);
		originPanel.add(originFrameNrField);

		comparePanel.add(comparFileURLfield);
		comparePanel.add(openCompareButton);
		comparePanel.add(compareFrameLabel);
		comparePanel.add(compareFrameNrField);

		buttonPanel.add(outputFileURLfield);
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		buttonPanel.add(clusterNumberField);

		// Add the buttons and the log to this panel.
		add(originPanel, BorderLayout.NORTH);
		//add(comparePanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}

	/*
	 * Replace slash with backslash
	 * 
	 * @param File file
	 * 
	 * @return String filePath
	 */
	public String reverseSlash(File file, int fileNr) {

		// Replace slash with backslash
		String filePath = file.getPath();
		path[fileNr] = file.getParent();

		for (int h = 0; h < path.length; h++) {
			path[h] = path[h].replace('\\', '/');
		}

		filePath = filePath.replace('\\', '/');
		// System.out.println("Gui: " + filePath);

		return filePath;
	}

	// Handle open, select and cancel buttons
	public void actionPerformed(ActionEvent e) {
		
		// Handle open button action.
		if (e.getSource() == openOriginButton) {
			

			fc.setCurrentDirectory(new File(path[0])); // Last video-path aerger-geschnitten
			
			int returnVal = fc.showOpenDialog(Gui.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) { // Open new file ->
				// OK
				File file = fc.getSelectedFile();
				// This is where a real application would open the file.
				orginFileURLfield.setText(file.getPath());
				filename[0] =  file.getName();

				// Replace slash with backslash
				filenamePath[0] = reverseSlash(file, 0);

				// Calculate frames and show it in the textfield
				frameNrDur = (int) capDuration.getVideoDur(filenamePath[0]);
				originFrameNrField.setText("" + frameNrDur);
				frameNr[0] = frameNrDur;

			} else { // Open new file -> Cancel

				orginFileURLfield.setText(filenamePath[0]);
			}
			orginFileURLfield.setCaretPosition(orginFileURLfield.getDocument()
					.getLength());

		} else if (e.getSource() == openCompareButton) {
			fc.setCurrentDirectory(new File(path[0]));
			int returnVal = fc.showOpenDialog(Gui.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) { // Open new file ->
				// OK
				File file = fc.getSelectedFile();
				// This is where a real application would open the file.
				comparFileURLfield.setText(file.getPath());	
				filename[1] =  file.getName();

				// Replace slash with backslash
				filenamePath[1] = reverseSlash(file, 1);

				// Calculate frames and show it in the textfield
				frameNrDur = (int) capDuration.getVideoDur(filenamePath[1]);
				compareFrameNrField.setText("" + frameNrDur);
				frameNr[1] = frameNrDur;
			}
			orginFileURLfield.setCaretPosition(orginFileURLfield.getDocument()
					.getLength());

		} else if (e.getSource() == okButton) { // Handle ok button action.
			
			outputFileURLfield.setText("Deletes old files...Please wait...");	
			workWithCapFrames();
			
			if(Integer.parseInt(clusterNumberField.getText()) != 17) {
				try {
					clusternumber = Integer.parseInt(clusterNumberField.getText());
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
			

		} else if (e.getSource() == cancelButton) { // Handle cancel button
			// action.
			System.exit(0);
		}
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event dispatch thread.
	 */
	public void createAndShowGUI() {

		// Create and set up the window.
		frame = new JFrame("Select Video File");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Add content to the window.
		frame.add(new Gui());

		// place the window in the middle of the screen
		Dimension frameSize = new Dimension(730, 540);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int top = (screenSize.height - frameSize.height) / 2;
		int left = (screenSize.width - frameSize.width) / 2;
		frame.setSize(frameSize);
		frame.setLocation(left, top);

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	private void workWithCapFrames() {

		// Capture Frames
		capFrames = new CaptureFrames();
		capFrames.addObserver(new Observer() {

			@Override
			public void update(Observable observable, Object object) {
				if (object instanceof String) {
					// Show the Capture Frame feedback
					outputFileURLfield.setText((String) object);		
				}
			}

		});	
			

		// Starting a new Thread for the users feedback
		Thread t = new Thread() {
			@Override
			public void run() {
				// super.run();
				try {
					capFrames.calcCaptureFrames(filenamePath[0], path[0],
							frameNr[0]);
					new Thread(new SelectPointArea(path, filename, frameNr, clusternumber))
							.start();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		t.start();
	}

}
