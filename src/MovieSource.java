import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.text.*;

import javax.imageio.*;

/**
 * Description: MovieSource implements an interface to a sequence of frames of a
 * movie. The frames are located in a single directory and have 4-digit numbered
 * filenames starting with "<movieName>nnnn.jpeg"<br>
 * Author: H. Faasch <br>
 * Last change: 03.04.2010 <br>
 * Version: 1.1 <br>
 * History: <br>
 * Known Bugs and Restrictions: -none- <br>
 */
public class MovieSource {
  private String [] pathName = {"0", "0"};
  final String fileext = ".jpeg";
  final Format int4 = new DecimalFormat("0000");
  URL url;
  BufferedImage img;

  public MovieSource(String [] pName) {
		for (int k = 0; k < pName.length; k++)
			pathName[k] = pName[k];
  }

  /**
   * Read an image from the data source.
   * @param frameNumber Number in Sequence which will be appended to the
   *          filename as a 4-digit string
   * @return The buffered image if accessable
   * @throws IOException If file can not be found
   */
  public BufferedImage readFrame(int frameNumber, int pathNr) throws IOException {
    URL url = new URL("file:" + pathName[pathNr] + "/frames" + "/" + "frame_"
        + int4.format(frameNumber) + ".jpeg");
    
    //System.out.println("MS: " + url);
    img = ImageIO.read(url);
    
    return img;
  }

}
