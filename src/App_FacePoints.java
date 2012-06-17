import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Description: Detect and follow up "Blue Face Points".<br>
 * Author: T. Tews & Faasch <br>
 * Last change: 02.07.2010 <br>
 * Version: 2.2 <br>
 * History: <br>
 * Known Bugs and Restrictions: -none- <br>
 */
public class App_FacePoints extends JFrame implements Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ConvertExcelFile convertExcel = new ConvertExcelFile();
	SortRegions sortRegions; // Sort cluster marks
	CaptureFrames capFrames; // Capture Frames
	ImagePanel ip;
	MovieSource ms; // Get File URL
	BlueMarks blueMarks;

	private JPanel cp; // main panel
	private BufferedImage img = null; // main image
	private int startThreshhold = 180; // Cluster threshold
	private int minimumRegions = 17; // Number of cluster
	private int lock = 0; // Number of video selection
	private Rectangle frameWindow = new Rectangle(); // search area
	private Rectangle [] recValueArray = new Rectangle[4];
	private int[] lastFrameNumber = { 0, 0 }; // Maximum number of frames
	private String[] path = { "0", "0" }; // frame location
	private String filenamePath;
	private String[] excelfilename = { "0", "0" };
	private int clusterid = 0; // Clusterid
	// Array with the x, y point values
	private double[][] dataAr;
	

//	Punkte im Gesicht:
//0............3	
//...1......2....	
//...............
//..4........6...
//.......5.......	
//..7........8...
//.......9.......	
	
	// Flaechenmasse und distanzmasse
	private final String[] functionsLabel = { // x axes labels for the excelfile
			"Area A (7_8_9)", "Area B (5_8_7)", "Area C (4_6_5)", "Area D (0_3_2_1)", "Dist. 1 (7_8)","Dist. 2 (4_6)", "Dist. 3 (0_3)" };
			// Calculate the distance between these points
			private final int[][] pointlist = new int[][] {{7,8,9},{5,8,7},{4,6,5},{0,3,2,1},{7,8},{4,6},{0,3}};
			
	int countClick = 0; // Mouse click counter
	private final int[][] sortRegionValues = { { 0, 1 }, { 2, 5 }, { 6, 9 }}; // sort the cluster numbers

	private List<PointCluster> cluster; // Cluster array
	private JTextField log; // Status area
	private int borkenFrame;

	/**
	 * Constructor for main application window
	 * 
	 * @param title
	 * @param cols
	 * @param rows
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public App_FacePoints(String[] pa, String filename[], int[] frameN,
			Rectangle rValue [], int cnumber, int bFrame) {
		super("Face Points Detection");

		for (int k = 0; k < pa.length; k++)
			path[k] = pa[k];

		filenamePath = path[1] + "/" + filename[1]; // Select filename of the
		// compare file for the frame class

		for (int k = 0; k < 2; k++) { // Get the name of the file
			String results[] = filename[k].split("\\.");
			excelfilename[k] = path[k] + "/" + results[0] + ".xls";
		}
		borkenFrame = bFrame;

		minimumRegions = cnumber; // number of cluster
		lastFrameNumber[0] = frameN[0]; // No. of frames of video neutral
		lastFrameNumber[1] = frameN[1]; // and compare
		
		//System.out.println("AFP: " + lastFrameNumber[0] +", " + lastFrameNumber[1] + "###############################################");
		
		frameWindow = rValue[0]; // Search area
		recValueArray = rValue;
		
		if (lastFrameNumber[0] > lastFrameNumber[1]) {
		    dataAr = new double[lastFrameNumber[0]+200][lastFrameNumber[0]+200];
		} else {
			dataAr = new double[lastFrameNumber[1]+200][lastFrameNumber[1]+200];
		}

	}

	/**
	 * Main function: Detect and follow up "Blue Face Points".
	 * 
	 * @param args
	 *            No program arguments
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws URISyntaxException
	 */
	public void startAppFacePoints() throws IOException, InterruptedException,
			URISyntaxException {

		cp = (JPanel) getContentPane();

		// Status field
		log = new JTextField("Deletes old files...");
		log.setMargin(new Insets(5, 5, 5, 5));
		log.setEditable(false);

		// get URL and select the first image
		ms = new MovieSource(path);
		img = ms.readFrame(borkenFrame, lock); //TODO TENX

		// Select the size and the position of the JFrame window
		Dimension frameSize = new Dimension(img.getWidth(),
				img.getHeight() + 65);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int top = (screenSize.height - frameSize.height) / 2;
		int left = (screenSize.width - frameSize.width) / 2;
		setSize(frameSize);
		setLocation(left, top);

		// Panel for the frames
		ip = new ImagePanel(recValueArray, path[0]);
		ip.setOpaque(true);
		ip.setBackground(Color.black);

		// closing and set the size of the window
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		ip
				.setPreferredSize(new Dimension(img.getWidth(),
						img.getHeight() + 30));

		// Show
		setVisible(true);

		while (lock < 1) { // run two times, because we have two videos //TODO nur ein video

			if (lock == 1)
				img = ms.readFrame(borkenFrame, lock); // Select the second video frame //TODO TENX

			// show first frame
			ip.setImage(img, borkenFrame); //TODO TENX
			cp.add(log, BorderLayout.SOUTH); // add status field
			cp.add(ip, BorderLayout.NORTH);

			// Locate the blue marks in the first frame
			blueMarks = new BlueMarks();

			blueMarks.setBlueMarksValues(img, startThreshhold, minimumRegions,
					frameWindow, blueMarks);
			cluster = blueMarks.firstLocate(); // auffinden der Punkte
			
			
			//System.out.println("AFP: Test");
			// Sort cluster marks
			sortRegions = new SortRegions(); //TODO Sortregion
			cluster = sortRegions.sortRegionsCalc(minimumRegions, sortRegionValues, cluster);
			
			//System.out.println("AFP: Test2");

			// Assign each cluster an permanent index-number after sorting.
			for (int n = 0; n < cluster.size(); n++)
				cluster.get(n).nr = n;

			// Add the marks to the ImagePanel and write the image on the disk.
			// This image is just for inspection.
			ip.addMarks(cluster);
			ip.write(path[lock] + "/frames" + "/InsepctionFrame" + lock + ".jpg", 0); // 0 == normal, 1 == face Dect

			// Print the cluster for inspection
			for (PointCluster pr : cluster) {
				System.out.println(pr.toString());
			}

			// Read frame sequence until the limit is reached or no more frames
			// are available
			for (int seq = borkenFrame; seq < lastFrameNumber[lock]; seq++) { //TODO TENX
				// System.out.println("AFP: " + seq + ", " + lastFrameNumber);
				try {
					img = ms.readFrame(seq, lock);
				} catch (IOException exc) {
					System.out
							.println("AFP: Terminating; last valid frame number: "
									+ (seq - 1));
					break; // Leave loop if no more frames are available.
				}

				// Set image panel and the blueMarks instance to next frame
				ip.setImage(img, seq);
				blueMarks.setImage(img);

				// Track all cluster in actual frame
				for (int n = 0; n < cluster.size(); n++) {
					blueMarks.follow(cluster.get(n));
				}

			} // for

			ip.addTrack(cluster);
			ip.write(path[lock] + "/frames" + "/Result" + lock + ".jpg", 0); // 0 = normal, 1 = face Detection

			// cluster feedback
			for (PointCluster pr : cluster) {
				System.out.println("AFP: " + pr.toString());
			}

			// Convert Date inside an Excel File
			convertExcel.startConvertExcelFile(excelfilename[lock], cluster, //TODO Excelfile
					clusterid, dataAr, functionsLabel, pointlist, minimumRegions, lock);

			// Capture Frames of the compare video file
			if (lock == 0) {
				capFrames = new CaptureFrames();
				capFrames.addObserver(new Observer() {

					public void update(Observable observable, Object object) {
						if (object instanceof String) {

							// Show the status
							log.setText((String) object);
							repaint();
						}
					}

				});

//				 Capture Frames of the compare video
//				capFrames.calcCaptureFrames(filenamePath, path[1], // TODO ein video
//						lastFrameNumber[1]);
			}

			lock++;
		} // While end

	}

	// For starting a new Thread
	@Override
	public void run() {
		try {
			startAppFacePoints();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
