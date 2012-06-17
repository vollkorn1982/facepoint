import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.xuggler.IContainer;

/**
 * Description:Using {@link IMediaReader}, calculates the duration of the selected video (e.g., 119 => 11.9 sec.)<br>
 * Author: T. Tews <br>
 * Last change: 21.06.2010 <br>
 * Version: 1.0 <br>
 * History: <br>
 * Known Bugs and Restrictions: -none- <br>
 * 
 * @param String filename<br>
 */
public class CaptureVideoDuration extends MediaListenerAdapter {
	
	  private long videoDur;
	/**
	 * calculates the duration of the selected video
	 * 
	 * @param filename
	 * @return
	 */
	 public long getVideoDur(String filename) {
		  
			// Create a Xuggler container object
			    IContainer container = IContainer.make();
			    
			    // Open up the container
			    if (container.open(filename, IContainer.Type.READ, null) < 0)
			      throw new IllegalArgumentException("could not open file: " + filename);

			    // calculates the duration of the selected video
			    videoDur = (long) (container.getDuration()/100000);//*2.5);
			return videoDur;
		}

}
