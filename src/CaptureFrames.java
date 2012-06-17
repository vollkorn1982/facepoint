import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.Observer;

import javax.imageio.ImageIO;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.xuggler.Global;

/**
 * Description: Using {@link IMediaReader}, takes a media container and writes
 * video frames out to a JPG image file..<br>
 * Author: T. Tews <br>
 * Last change: 21.06.2010 <br>
 * Version: 1.0 <br>
 * History: <br>
 * Known Bugs and Restrictions: -none- <br>
 */
public class CaptureFrames extends MediaListenerAdapter {

	// The number of seconds between frames
	public static double SECONDS_BETWEEN_FRAMES = 0.1; //0.012; 25 Frames pro Sekunde
	public static int FILE_NAME_DIGITS = 0;
	private String path = "";
	private int frameNr = 0;
	private final Format int4 = new DecimalFormat("0000");

	DeleteOldFiles deleteOldFiles;
	CollectOutput collectOutput; // Show writing status

	// The number of micro-seconds between frames.
	public static final long MICRO_SECONDS_BETWEEN_FRAMES = (long) (Global.DEFAULT_PTS_PER_SECOND * SECONDS_BETWEEN_FRAMES);

	// Time of last frame write
	private static long mLastPtsWrite = Global.NO_PTS;

	/**
	 * The video stream index, used to ensure we display frames from one and
	 * only one video stream from the media container.
	 */
	private int mVideoStreamIndex = -1;

	public CaptureFrames() {
		super();

		// Observer for the status field of Gui and AFP
		collectOutput = new CollectOutput();

	}

	/**
	 * Select Observer class
	 * 
	 * @param observer
	 */
	public void addObserver(Observer observer) {
		// Listen to the class
		collectOutput.addObserver(observer);
	}

	/**
	 * Construct a DecodeAndCaptureFrames which reads and captures frames from a
	 * video file.
	 * 
	 * @param filename
	 *            the name of the media file to read e.g.
	 *            C:/workspace/aerger.avi
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 */
	public void calcCaptureFrames(String filename, String pa, int fNr)
			throws MalformedURLException, URISyntaxException {
		// reset values
		mLastPtsWrite = 0;
		FILE_NAME_DIGITS = 0;

		frameNr = fNr; // Number of frames
		path = pa; // file path

		// Delete old files
		deleteOldFiles = new DeleteOldFiles();
		deleteOldFiles.deleteOldFiles(filename, path, frameNr);

		// create a media reader for processing video
		IMediaReader reader = ToolFactory.makeReader(filename);

		// stipulate that we want BufferedImages created in BGR 24bit color
		// space
		reader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);

		// note that DecodeAndCaptureFrames is derived from
		// MediaReader.ListenerAdapter and thus may be added as a listener
		// to the MediaReader. DecodeAndCaptureFrames implements
		// onVideoPicture().
		reader.addListener(this);
		
		// Create frame folder
		boolean status;
		status = new File(path + "/frames").exists();

		if (status == false) {
			new File(path + "/frames").mkdir();
		}

		// read out the contents of the media file, note that nothing else
		// happens here. action happens in the onVideoPicture() method
		// which is called when complete video pictures are extracted from
		// the media source
		while (reader.readPacket() == null)
			do {
			} while (false);
	}

	/**
	 * Called after a video frame has been decoded from a media stream.
	 * Optionally a BufferedImage version of the frame may be passed if the
	 * calling {@link IMediaReader} instance was configured to create
	 * BufferedImages.
	 * 
	 * This method blocks, so return quickly.
	 */

	public void onVideoPicture(IVideoPictureEvent event) {
		try {
			// if the stream index does not match the selected stream index,
			// then have a closer look
			if (event.getStreamIndex() != mVideoStreamIndex) {
				// if the selected video stream id is not yet set, go ahead an
				// select this lucky video stream
				if (-1 == mVideoStreamIndex)
					mVideoStreamIndex = event.getStreamIndex();

				// otherwise return, no need to show frames from this video
				// stream
				else
					return;
			}

			// if uninitialized, backdate mLastPtsWrite so we get the very
			// first frame
			if (mLastPtsWrite == Global.NO_PTS)
				mLastPtsWrite = event.getTimeStamp()
						- MICRO_SECONDS_BETWEEN_FRAMES;
			// System.out.println("CF: mLast: " + mLastPtsWrite);
			// System.out.println("CF: TimeStamp: " + event.getTimeStamp());

			// if it's time to write the next frame
			if (event.getTimeStamp() - mLastPtsWrite >= MICRO_SECONDS_BETWEEN_FRAMES) {
				// Create a file
				File file;

				// Write the frame				
				file = new File(path + "/frames" +"/frame_" + int4.format(FILE_NAME_DIGITS) + ".jpeg");

				FILE_NAME_DIGITS = FILE_NAME_DIGITS + 1;

				// write out JPG
				ImageIO.write(event.getImage(), "jpeg", file);

				// indicate file written
				// double seconds = ((double)event.getTimeStamp())
				// / Global.DEFAULT_PTS_PER_SECOND;
				//System.out.println("CF: " + FILE_NAME_DIGITS);

				collectOutput.collectAllData("Wrote File: frame_" + int4.format(FILE_NAME_DIGITS) + ".jpeg");

				// System.out.printf("CF: (elapsed time) %6.3f seconds wrote: %s\n",
				// seconds, file);

				// update last write time
				mLastPtsWrite += MICRO_SECONDS_BETWEEN_FRAMES;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}