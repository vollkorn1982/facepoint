import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Description: Set of points representing a blue mark.<br>
 * Author: T. Tews, H. Faasch <br>
 * Last change: 06.03.2010 <br>
 * Version: 1.1 <br>
 * History: <br>
 * Known Bugs and Restrictions: -none- <br>
 */
public class PointCluster {
	final int nbws = 4;// Neighbour window size 2 Pixel TODO: anzahl vielleicht
	// 3??
	int nr;
	int missing = 0;// Number of frames where the cluster could not be tracked
	int minRow, maxRow; // TODO Should be changed to class Point
	int minCol, maxCol;// TODO Should be changed to class Point
	int minSig, maxSig;
	int frameid = 0;
	int zlock = 0;
	static Point zeroClusterCenter = new Point(0,0);
	static Point oneClusterCenter = new Point(0,0);
	Point distanceZeroCluster = new Point();
	Point distanceOneCluster = new Point();

	Point predictCenter;

	private HashSet<Point> points = new HashSet<Point>(); // Set of points
	// belonging to cluster
	List<Point> track = new ArrayList<Point>();// Track points of cluster center

	/**
	 * Constructor: Create a PointCluster and add the first single point to the
	 * cluster and the track.
	 * 
	 * @param p
	 *            First point
	 * @param sig
	 *            Significance of point
	 */
	public PointCluster(Point p, int sig) {
		minCol = p.x;
		maxCol = p.x;
		minRow = p.y;
		maxRow = p.y;
		minSig = sig;
		maxSig = sig;
		points.add(p);
		track.add(p);
	}

	/**
	 * Reconstruct an existing cluster after it has been located in the next
	 * frame. If the cluster can't be located the missing counter will be
	 * incremented and a message is printed on the console.
	 * 
	 * @param pc
	 *            Next PointCluster
	 */
	public void modify(PointCluster pc) {
		if (pc == null) {
			points.clear();
			track.add(track.get(track.size() - 1)); // TODO: track??
			missing++;

			System.out
					.println("------------> Cluster " + nr + " has no points "
							+ getCenter() + ", Missing: " + missing);

		} else {
			minCol = pc.minCol;
			maxCol = pc.maxCol;
			minRow = pc.minRow;
			maxRow = pc.maxRow;
			// minSig = pc.minSig;
			maxSig = pc.maxSig;
			points.clear();
			points.addAll(pc.points);
			track.add(getCenter());
		}
	}

	/**
	 * Check if a detected point is located in the neighborhood of a point
	 * already detected.
	 * 
	 * @param x
	 * @param y
	 * @return True if the point is in the neighborhood.
	 */
	public boolean isNeighbor(int x, int y) {
		for (Point p : points)
			for (int row = p.y - nbws; row < p.y + nbws; row++)
				for (int col = p.x - nbws; col < p.x + nbws; col++)
					if (x == col & y == row) {
						return true;
					}
		return false;
	}

	/**
	 * Insert a point into an existing cluster. The border coordinates will be
	 * updated accordingly.
	 * 
	 * @param x
	 * @param y
	 * @param sig
	 */
	public void insert(int x, int y, int sig) {
		points.add(new Point(x, y));

		if (y < minRow)
			minRow = y;
		if (y > maxRow)
			maxRow = y;
		if (x < minCol)
			minCol = x;
		if (x > maxCol)
			maxCol = x;
		if (sig < minSig)
			minSig = sig;
		if (sig > maxSig)
			maxSig = sig;

		track.remove(track.size() - 1);
		track.add(getCenter());
	}

	public int getSize() {
		return points.size();
	}

	/**
	 * Compute the center of the cluster
	 * 
	 * @return Center of cluster
	 */
	Point getCenter() {
		Point point = new Point();
		point.x = (maxCol + minCol) / 2;
		point.y = (maxRow + minRow) / 2;

		// modify the distance between 0 + 1 cluster
		setDistanceToStaticCluster(point);//TODO: here Distance
		//System.out.println("PC: " + point.x);

		return point;
	}

	/**
	 * Predict the next center of the up coming cluster if the center is moving.
	 * 
	 * @return Predicted center.
	 */
	Point getPredictCenter() {
		if (track.size() < 2)
			return getCenter();
		else {
			int lastIndex = track.size() - 1;
			Point b = track.get(lastIndex);
			Point a = track.get(lastIndex - 1);
			Point predict = new Point(b);
			int deltaX = b.x - a.x; // calc first order derivative
			int deltaY = b.y - a.y;
			predict.translate(deltaX, deltaY);
			// System.out.println("PC: Delta: " + deltaX + " " + deltaY);
			// System.out.println("PC:" + nr);

			minCol += deltaX;
			maxCol += deltaX;
			minRow += deltaY;
			maxRow += deltaY;
			return predict;
		}
	}

	/*
	 * Cluster rectangle
	 */
	public Rectangle getRectangle() {
		return new Rectangle(minCol, minRow, maxCol - minCol, maxRow - minRow);
		// TODO: Vielleicht hier Rec?
	}

	public void join(PointCluster pc) {
		points.addAll(pc.points);
	}

	@Override
	public String toString() {
		Point p = getCenter();

		// first set of points
		setDistanceToStaticCluster(p);

		return "" + nr + ": " + points.size() + " (" + p.x + "," + p.y + ") "
				+ minSig + "-" + maxSig + ", missing: " + missing;
	}

	/**
	 * Calc distance to zero and first cluster
	 * 
	 * @param p
	 */
	public void setDistanceToStaticCluster(Point p) {

		if (nr == 0) { // save distance to zero cluster
			zeroClusterCenter.x = p.x;
			zeroClusterCenter.y = p.y;
			//System.out.println("PC: " + nr +" (" + p.x + ", " + p.y + ")");
		}
		if (nr == 1) { // save distance to the first cluster
			oneClusterCenter.x = p.x;
			oneClusterCenter.y = p.y;
			//System.out.println("PC: " + nr +" (" + p.x + ", " + p.y + ")");
		}

		if (nr != 0 && nr != 1) { // set distance to cluster 0 and 1
			distanceZeroCluster = new Point(p.x - zeroClusterCenter.x, p.y
				- zeroClusterCenter.y);
//		System.out.println("PC3: "+  nr + ": "+ p.x + ", " + zeroClusterCenter.x);
//		System.out.println("PC3: " +  nr + ": "+ p.y + ", " + zeroClusterCenter.y);
			distanceOneCluster = new Point(p.x - oneClusterCenter.x, p.y
				- oneClusterCenter.y);
		}

	}



}
