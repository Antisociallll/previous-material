import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

// Canvas for image display
class ImageCanvas extends Canvas {
	BufferedImage image;

	// initialize the image and mouse control
	public ImageCanvas(BufferedImage input) {
		image = input;
		addMouseListener(new ClickListener());
	}
	public ImageCanvas(int width, int height) {
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		addMouseListener(new ClickListener());
	}

	// redraw the canvas
	public void paint(Graphics g) {
		// draw boundary
		g.setColor(Color.gray);
		g.drawRect(0, 0, getWidth()-1, getHeight()-1);
		// compute the offset of the image.
		int xoffset = (getWidth() - image.getWidth()) / 2;
		int yoffset = (getHeight() - image.getHeight()) / 2;
		g.drawImage(image, xoffset, yoffset, this);
	}

	// reset an empty image
	public void resetBuffer(int width, int height) {
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		repaint();
	}
	// reset image based on the input
	public void copyImage(BufferedImage input) {
		Graphics2D g2D = image.createGraphics();
		g2D.drawImage(input, 0, 0, null);
		repaint();
	}

	// listen to mouse click
	class ClickListener extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if ( e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON3 )
				try {
					ImageIO.write(image, "png", new File("saved.png"));
				} catch ( Exception ex ) {
					ex.printStackTrace();
				}
		}
	}
}
