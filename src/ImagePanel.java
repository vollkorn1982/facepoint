import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Description: An ImagePanel holds and displays image an frame image and
 * additional graphical elements, e.g. the set of points denoting a "blue mark".<br>
 * Author: T. Tews  & H. Faasch <br>
 * Last change: 03.04.2010 <br>
 * Version: 1.2 <br>
 * History: <br>
 * Known Bugs and Restrictions: -none- <br>
 */
class ImagePanel extends JPanel {
  /**
	 * 
	 */
	FaceDetection fD = new FaceDetection(); //pa, frame
	private static final long serialVersionUID = 1L;
BufferedImage image; // Actual image
private String path = "";
  int seq; // Actual frame sequence number
  int selectedVersion = 0;
  Rectangle [] recValue; //Face Area
  List<PointCluster> regions; // The list of PointClusters each representing a
  // blue mark
  Set<Point> trackPoints = new HashSet<Point>();// Set of Tracks to follow up

  // the movement of blue marks
  // (if displayed)
  
  public ImagePanel(Rectangle rcValue [], String pa ) {

	  		path = pa;
			recValue = new Rectangle[rcValue.length];
	  		//System.out.println("test " + rcValue.length + ", " + rcValue[1].x + rcValue[1].y + rcValue[1].width + rcValue[1].height);
	  		for(int k = 0; k < rcValue.length; k++) {
	  			recValue[k] = new Rectangle(rcValue[k].x, rcValue[k].y, rcValue[k].width, rcValue[k].height);
	  		}
  }

  /**
   * Set the actual image and repaint the graphics.
   * @param image Image to be displayed.
   * @param seq Number of frame, which will show up in the upper left corner.
   */
  void setImage(BufferedImage image, int seq) {
    this.image = image;
    this.seq = seq;
    repaint();
  }
  
  

  public void addMarks(List<PointCluster> regions) {
    this.regions = regions;
    repaint();
  }

  public void addTrack(List<PointCluster> regions) {
    for (PointCluster pr : regions)
      trackPoints.addAll(pr.track);
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);

    g.drawImage(image, 0, 0, this);

    g.setColor(Color.GREEN);
    g.drawString("" + seq, 10, 10);

    if (regions != null)
      for (PointCluster reg : regions) {
        Rectangle r = reg.getRectangle();
        if(seq != 0){    // dots
        	g.drawRect(r.x, r.y, r.width, r.height);
        	g.drawString("" + regions.indexOf(reg), r.x, r.y - 2);
        }
        
		g.setColor(java.awt.Color.GREEN);
        // draw Rect
        //g.drawRect(recValue.x, recValue.y, recValue.width, recValue.height); 
		for(int k = 0; k < recValue.length; k++) {//face //eyes
			//faceDetection.searchOpenCVAreas(path, frame); //TODO
			//System.out.println("IP: path:" + path + ", "+ seq + ", " + recValue.length);
			//fD.searchOpenCVAreas(path, seq);TODO YYY
			
			//recValue = fD.getRecValue();
			//recValue[k] = fD.getRecValue()[k]; TODO YYY
			
			//g.drawRect(fD.getRecValue()[k].x, fD.getRecValue()[k].y,fD.getRecValue()[k].width, fD.getRecValue()[k].height); 
			g.drawRect(recValue[k].x, recValue[k].y, recValue[k].width, recValue[k].height); 		
			//System.out.println("IP:" + recValue[k].x+","+ recValue[k].y);
			
		}
      }
    
    
  }

  /**
   * Output an ImagePanel to a file
   * @param fileName Name of JPEG-fiel containing the image
   */
  public void write(String fileName, int sV) {
    BufferedImage result = new BufferedImage(720, 576,
        BufferedImage.TYPE_INT_RGB);
    Graphics2D gc = result.createGraphics();
    selectedVersion = sV;
    gc.drawImage(image, 0, 0, null);
    gc.setColor(Color.white);
    if (selectedVersion == 0)
    for (PointCluster reg : regions) {
      Rectangle r = reg.getRectangle();
      gc.drawRect(r.x, r.y, r.width, r.height);
      gc.drawString("" + regions.indexOf(reg), r.x, r.y - 2);
    }
    else {
    	//gc.drawRect(recValue.x, recValue.y, recValue.width, recValue.height); // face detection with OpenCV
    	//gc.setColor(java.awt.Color.RED);
    	for(int k = 0; k < recValue.length; k++)
        	gc.drawRect(recValue[k].x, recValue[k].y, recValue[k].width, recValue[k].height);	
    	
    	//gc.setColor(java.awt.Color.GREEN);
    }


    // Frame output
    try {
      FileOutputStream file = new FileOutputStream(new File(fileName));
      ImageIO.write(result, "JPG", file);
      file.close();
    } catch (Exception exc) {	
      exc.printStackTrace();
    }
  }

}
