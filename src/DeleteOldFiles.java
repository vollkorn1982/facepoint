import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.Format;

/**
 * Description: Deletes all frames, <br>
 * result + inspection images and excel files ".<br>
 * Author: T. Tews <br>
 * Last change: 30.06.2010 <br>
 * Version: 1.0 <br>
 * History: <br>
 * Known Bugs and Restrictions: -none- <br>
 */
public class DeleteOldFiles {
	
	private static String path;
	final static Format int4 = new DecimalFormat("0000");
	private String excelFileName;

	/**
	 * @param args
	 * @throws MalformedURLException
	 * @throws URISyntaxException
	 */
	public void deleteOldFiles(String filename, String pa, int frameNr) throws MalformedURLException,
			URISyntaxException {

		excelFileName = filename;
		String results[] = excelFileName.split("\\.");
		excelFileName = results[0] + ".xls";
		
		path = pa;
		for (int m = 0; m <= frameNr+2; m++) {
			
			String fileName;
			if(m < frameNr -1) { // Deletes all frames
				fileName = path + "/frames" + "/" + "frame_" + int4.format(m) + ".jpeg";
			} else if (m == frameNr-1) { // Maybe one is forgotten...
				fileName = path + "/frames" + "/" + "frame_" + int4.format(m) + ".jpeg";
			} else if (m == frameNr) { // deletes excel file
				fileName = excelFileName;
			} else if (m == frameNr+1) { // deletes result images
				fileName = path + "/frames" + "/" + "Result.jpg";
			} else {
				fileName = path + "/frames" + "/" + "InsepctionFrame.jpg";
			}
			
			// Set image
			File f = new File(fileName);


			// delete only the existing files 
			if (f.exists()) {
				if (!f.canWrite())
					throw new IllegalArgumentException(
							"Delete: write protected: " + fileName);

				
				//System.out.println("DOF: Deletes " + fileName);

				boolean success = false;

				// Attempt to delete it
				success = f.delete();

				if (!success)
					System.out.println("Delete: deletion failed " + fileName);
			}
		}

	}

}
