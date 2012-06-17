import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JPanel;

/**
 * Description: Creates the "SelectPointArea" rectangle <br>
 * and handles the mouselistener.<br>
 * Author: T. Tews & Faasch <br>
 * Last change: 21.06.2010 <br>
 * Version: 2.1 <br>
 * History: <br>
 * Known Bugs and Restrictions: -none- <br>
 */
class MouselistenerPanel extends JPanel implements MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int pressedX; // first Mouseclick X
	public int pressedY;
	public int pressedX2; // second Mouseclick X
	public int pressedY2;
	Rectangle recValue;
	int countClick = 0;
	final javax.swing.JFrame frame;
	BufferedImage img = null; // first video frame image

	// static int[] recSize;

	MouselistenerPanel(javax.swing.JFrame frame1, BufferedImage ig,
			Rectangle rcValue) throws IOException {
		this.img = ig;

		recValue = new Rectangle(rcValue.x, rcValue.y, rcValue.width,
				rcValue.height);

		final int width = 220;
		final int height = 40;
		this.setPreferredSize(new java.awt.Dimension(width, height));
		this.addMouseListener(this);
		frame = frame1;
		if (getPressedX() == 0)
			repaint();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		g.drawImage(img, 0, 0, this);
		g.setColor(Color.GREEN);

		if (getPressedX() == 0) { // draw default Rect
			g.drawRect(recValue.x, recValue.y, recValue.width, recValue.height);
		}
		if (getPressedX2() != 0) { // draw two points
			g.drawRect(getPressedX(), getPressedY(), getPressedX2()
					- getPressedX(), getPressedY2() - getPressedY());
		}

	}

	// Handle Mouse events
	public void mousePressed(java.awt.event.MouseEvent e) {
		if (countClick == 0) { // first mouse click
			setPressedX(e.getX());
			setPressedY(e.getY());
			setPressedX2(0);
			setPressedY2(0);
			repaint();

			countClick++;
		} else { // second mouse click
			setPressedX2(e.getX());
			setPressedY2(e.getY());
			repaint();
			countClick--;
		}

		System.out.println("MP: (" + e.getX() + ", " + e.getY() + ")");
	}

	public int getPressedX() {
		return pressedX;
	}

	private void setPressedX(int pressedX) {
		this.pressedX = pressedX;
	}

	public int getPressedY() {
		return pressedY;
	}

	private void setPressedY(int pressedY) {
		this.pressedY = pressedY;
	}

	public int getPressedX2() {
		return pressedX2;
	}

	private void setPressedX2(int pressedX2) {
		this.pressedX2 = pressedX2;
	}

	public int getPressedY2() {
		return pressedY2;
	}

	private void setPressedY2(int pressedY2) {
		this.pressedY2 = pressedY2;
	}

	public void mouseReleased(java.awt.event.MouseEvent e) {
	}

	public void mouseClicked(java.awt.event.MouseEvent e) {
	}

	public void mouseEntered(java.awt.event.MouseEvent e) {
	}

	public void mouseExited(java.awt.event.MouseEvent e) {
	}
}
