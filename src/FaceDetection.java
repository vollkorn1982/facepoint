import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.Format;

import javax.imageio.ImageIO;

/**
 * Description: Search for the eyes and the face with the OpenCV Face Detection
 * Lib.<br>
 * Author: T. Tews<br>
 * Last change: 30.04.2012 <br>
 * Version: 1.0 <br>
 * History: <br>
 * Known Bugs and Restrictions: -none- <br>
 */
class JNIOpenCV {
	static {
		System.loadLibrary("JNI2OpenCV");
	}

	public native int[] detectFace(int minFaceWidth, int minFaceHeight,
			String cascade, String filename);
}

/**
 * Description: Detect the Face of a single frame.<br>
 * Author: T. Tews & Faasch <br>
 * Last change: 13.12.2011 <br>
 * Version: 2.2 <br>
 * History: <br>
 * Known Bugs and Restrictions: -none- <br>
 */
public class FaceDetection {
	private JNIOpenCV myJNIOpenCV = new JNIOpenCV();; // Face Detection class
	private ImagePanel ip;
	private Rectangle[] recValue;
	private int counter = 0;
	private String filename = "";
	final Format int4 = new DecimalFormat("0000"); //style of number
	private int numFaces = 0;
	private int selectedFrame = 0;
	String[] cascade = { "haarcascade_frontalface_alt.xml",
	"haarcascade_eye.xml" };// , "haarcascade_mcs_mouth.xml" };


	public Rectangle[] getRecValue() {
		return recValue;
	}

	private void setRecValue(Rectangle recV) {

		this.recValue[counter] = recV;
		counter = counter + 1;
	}

	private BufferedImage img;
	// private BufferedImage img = null; // main image

	private String path;

	
	/**
	 * Searching for the openCV Areas
	 * 
	 * @return
	 */
	public void searchOpenCVAreas(String pa, int frame) {
		
		//System.out.println("FD: " + pa + ", " + frame);
		path = pa;
		selectedFrame = frame; // Selected frame
		
		filename = path + "/frames/frame_" + int4.format(selectedFrame) + ".jpeg"; // Start with the selected frame
		
		recValue = new Rectangle[cascade.length + 1]; // four detecting areas
		//counter = 0; TODO: YYY
		for (int c = 0; c < cascade.length; c++) {

			int[] detectedFaces = myJNIOpenCV.detectFace(40, 40, cascade[c],
					filename);

			numFaces = detectedFaces.length / 4;

			if (numFaces > 2) // detect only two eyes. ;)
				numFaces = 2;

			
			for (int k = 0; k < numFaces; k++) {

				// System.out.println("numFaces = " + numFaces);

//				System.out.println("Face " + k + ": "
//						+ detectedFaces[4 * k + 0] + " "
//						+ detectedFaces[4 * k + 1] + " "
//						+ detectedFaces[4 * k + 2] + " "
//						+ detectedFaces[4 * k + 3]);

				setRecValue(new Rectangle(detectedFaces[4 * k + 0],
						detectedFaces[4 * k + 1], detectedFaces[4 * k + 2],
						detectedFaces[4 * k + 3])); // w, h add high to the face
													// detection

			}

		}

		ip = new ImagePanel(recValue, path);

		try {
			img = ImageIO.read(new File(filename));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ip.setImage(img, 0); // TODO TENX

		ip.write(path + "/frames/FaceDetectionImage.jpg", 1); // 0 = normal, 1 =
																// face
																// detection
	}
	
	/**
	 * Searching for the eyes and the face areas
	 * 
	 * @param pa Path of the frames
	 * @param frame //selected frame
	 */
	public FaceDetection() {
		// TODO: Leer
	}
	
	
	
}