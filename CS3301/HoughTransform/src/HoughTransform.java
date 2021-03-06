import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.util.*;

// Main class
public class HoughTransform extends Frame implements ActionListener {
	BufferedImage input;
	int width, height, diagonal;
	ImageCanvas source, target;
	TextField texRad, texThres;
	// Constructor
	public HoughTransform(String name) {
		super("Hough Transform");
		// load image
		try {
			input = ImageIO.read(new File("/Users/Ming/Documents/workspace/HoughTransform/src/ellipse.png"));
		}
		catch ( Exception ex ) {
			ex.printStackTrace();
		}
		width = input.getWidth();
		height = input.getHeight();
		diagonal = (int)Math.sqrt(width * width + height * height);
		// prepare the panel for two images.
		Panel main = new Panel();
		source = new ImageCanvas(input);
		target = new ImageCanvas(input);
		main.setLayout(new GridLayout(1, 2, 10, 10));
		main.add(source);
		main.add(target);
		// prepare the panel for buttons.
		Panel controls = new Panel();
		Button button = new Button("Line Transform");
		button.addActionListener(this);
		controls.add(button);
		controls.add(new Label("Threshold:"));
		texThres = new TextField("10", 3);
		controls.add(texThres);
		button = new Button("Circle Transform");
		button.addActionListener(this);
		controls.add(button);
		controls.add(new Label("Radius:"));
		texRad = new TextField("25", 3);
		controls.add(texRad);
		
		// add two panels
		add("Center", main);
		add("South", controls);
		addWindowListener(new ExitListener());
		setSize(diagonal*2+100, Math.max(height,360)+100);
		setVisible(true);
	}
	class ExitListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	}
	// Action listener
	public void actionPerformed(ActionEvent e) {
		// perform one of the Hough transforms if the button is clicked.
		//Line Hough Transform.
		if ( ((Button)e.getSource()).getLabel().equals("Line Transform") ) {
			int threshold = Integer.parseInt(texThres.getText());
			int maxTheta = 360;
			int[][] houghArray = new int[diagonal][maxTheta];
			int[][] ImageMatrix = new int[width][height];
			int[][] g = new int[diagonal][maxTheta];
			
			//Load the pixel value into the matrix
			for ( int x = 0; x < width; x++ ){
				for ( int y = 0; y < height; y++ ){
					ImageMatrix[x][y] = (new Color(source.image.getRGB(x, y))).getRed()<<16 | (new Color(source.image.getRGB(x, y))).getGreen()<<8 | (new Color(source.image.getRGB(x, y))).getBlue();
				}
			}
			
			//calculate the hough transform value
			for ( int theta = 0; theta < maxTheta; theta++ ){
				for ( int x = 0; x < width; x++ ){
					for( int y = 0; y < width; y++ ){
						//pick the pixel that are not white
						if ( (ImageMatrix[x][y] & 0x0000ff ) < 255 ){ 
							int r = (int)(x*Math.cos(Math.toRadians(theta)) + y*Math.sin(Math.toRadians(theta)));
							if(r>0){
								houghArray[r][theta]++;
							}
						}
					}
				}
			}
			
			//calculate the point associated to the hough value. 
			//Use the point to draw line.
			for ( int theta = 0; theta < maxTheta; theta++ ){
				for ( int r =0; r < diagonal; r++ ){
					if ( houghArray[r][theta] > threshold ) {
						int x0 = 0;
						int y0 = (int)((r-x0)/Math.sin(Math.toRadians(theta)));
						int x1 =  width -1;
						int y1 = (int)((r-x1*Math.cos(Math.toRadians(theta)))/Math.sin(Math.toRadians(theta)));
						if ( y1 < 0 || y1 > height ){
							y0 = 0;
							y1 = height - 1;
							x0 = (int)((r-y0)/Math.cos(Math.toRadians(theta)));
							x1 = (int)((r-y1*Math.sin(Math.toRadians(theta)))/Math.cos(Math.toRadians(theta)));; 
						}
						source.addLinePoint(x0,y0,x1,y1);
					}
				}
			}
			source.repaint();
			g = HouTransform(ImageMatrix, g);
			DisplayTransformLine(maxTheta, diagonal, g);
		}
		
		//Circle Hough Transform.
		else if ( ((Button)e.getSource()).getLabel().equals("Circle Transform") ) {
			int[][] houghArray = new int[width][height];
			int radius = Integer.parseInt(texRad.getText());
			int threshold = Integer.parseInt(texThres.getText());
			int[][] ImageMatrix = new int[width][height];
			int maxTheta = 360;
			//Load the pixel value into a matrix.
			for ( int x = 0; x < width; x++ ){
				for ( int y = 0; y < height; y++ ){ 
					ImageMatrix[x][y] = (new Color(source.image.getRGB(y, x))).getRed()<<16 | (new Color(source.image.getRGB(y, x))).getGreen()<<8 | (new Color(source.image.getRGB(y, x))).getBlue();
				}
			}
			//calculate the hough transform value.
			for ( int x = 0; x < width; x++ ){
				for ( int y = 0; y < height; y++ ){
					if ( ( ImageMatrix[x][y] & 0x0000ff ) < 255 ) {
						for ( int theta = 0; theta < maxTheta; theta++ ) {
							int x0 = (int)Math.round((x + radius*Math.cos(Math.toRadians(theta))));//the center x
							int y0 = (int)Math.round((y + radius*Math.sin(Math.toRadians(theta))));//the center y
							if ( x0 < width && x0 > 0 && y0 < height && y0 >0 ){
								houghArray[x0][y0]++;
							}
						}
					}
				}
			}
			
			//Draw the circle
			for ( int x = 0; x < width; x++ ){
				for ( int y = 0; y < height;  y++ ){
					if ( houghArray[x][y] > threshold ){
						source.addCirclePoint(x, y, radius);
					}
				}
			}
			source.repaint();
			DisplayTransform(width, height, houghArray);
		}
	}
	
	// display the spectrum of the transform.
	public void DisplayTransform(int wid, int hgt, int[][] g) {
		target.resetBuffer(wid, hgt);
		for ( int y=0, i=0; y<hgt ; y++ )
			for ( int x=0 ; x<wid ; x++, i++ )
			{	
				
				int value = g[y][x] > 255 ? 255 : g[y][x];
				target.image.setRGB(x, y, new Color(value, value, value).getRGB());
			}
		target.repaint();
	}
	
	// display the spectrum of the transform. [maxTheta][diagonal]
	public void DisplayTransformLine(int wid, int hgt, int[][] g) {
		target.resetBuffer(wid, hgt);
		for ( int y=0, i=0; y<hgt ; y++ )
			for ( int x=0 ; x<wid ; x++, i++ )
			{	
				
				int value = g[y][x] > 255 ? 255 : g[y][x];
				target.image.setRGB(y, x, new Color(value, value, value).getRGB());
			}
		target.repaint();
	}
	
	public int[][] HouTransform(int[][] ImageMatrix, int[][] g){
		int x_center = width/2;
		int y_center = height/2;
		int maxTheta = 360;
		double thetastep = Math.PI/360;
		for ( int theta = 0; theta < maxTheta; theta++ ){
			for ( int x = 0; x < width; x++ ){
				for( int y = 0; y < width; y++ ){
					if ( (ImageMatrix[x][y] & 0x0000ff ) < 255 ){
						int r = (int)Math.round((x-x_center)*Math.cos(theta*thetastep)+((y-y_center)*Math.sin(theta*thetastep)));
						int rscaled = (int)Math.round(r+diagonal/2);
						if ( rscaled < 0) {
							rscaled = Math.abs(rscaled);
						}
						g[rscaled][theta]+=2;
					}
				}
			}
		}
		return g;
	}
	
	public static void main(String[] args) {
		new HoughTransform(args.length==1 ? args[0] : "rectangle.png");
	}
	 
}


