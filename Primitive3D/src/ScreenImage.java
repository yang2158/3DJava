
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Queue;
public class ScreenImage extends BufferedImage{
	public double[][] depthMap = new double[1][1];
	final int WHITE =16777215;
	public int[] pixelMap = new int[1];
	DebuggerForDepth debug = new DebuggerForDepth();
	private Shape globalObj;
	private Vector globalCamera;
	private Vector globalR;
	private double globalF;
	private double globalSizeX;
	Graphics graphics = this.getGraphics();
	final int numth = 12; // # of threads
	ArrayList<data> dataList = new ArrayList<>();
	ProcessingThread[] threads = new ProcessingThread[numth] ;
	public ScreenImage(int width, int height) {

		super(width, height, BufferedImage.TYPE_INT_RGB);
		depthMap = new double[width][height];
		pixelMap = new int[width*height];


		for(int i = 0 ; i < width ; i ++) {
			for( int e = 0 ;  e < height ; e++) {
				depthMap[i][e] =Double.MAX_VALUE;
				setRGB(i, e, -1);
			}
		}
	}
	public boolean setPixel(int x , int y, Color color) {
		this.setRGB(x, y, rgbToInt(color));
		return false;

	}
	public void processImage() {
		for(int i = 0 ; i< numth ; i ++) {

			threads[i] = new ProcessingThread(i, numth , globalR , globalCamera,globalF , globalSizeX );
			
			threads[i].init(i, numth , globalR , globalCamera,globalF , globalSizeX );
			threads[i].giveData(dataList);threads[i].start();
		}
		//long time =System.currentTimeMillis();
		boolean processing = true;
		while (processing) {
			processing = false;

			for(int i = 0 ; i< numth ; i ++) {
				if( threads[i].isAlive()) {
					processing = true;
				}else {
					
						Queue<outputData> processedData =  threads[i].getDone();
						//System.out.print(processedData.size() + " " );
						while(!processedData.isEmpty()) {// Offload processed data
							outputData dat = processedData.remove();
							setPixel(dat.x , dat.y ,dat.color, dat.depth );
							//TODO make it so that it only processed overlapping pixels

						}
					
				}

			}
		}
		imagify();
		//debug.drawMap(depthMap);

		//System.out.println("  ; "+ (System.currentTimeMillis()-time) + "ms");
	}
	public void reset() {
		dataList.clear();
		graphics.fillRect(0, 0, this.getWidth(), this.getHeight());

		for(int i = 0 ; i < this.getWidth() ; i ++) {
			for( int e = 0 ;  e < this.getHeight() ; e++) {
				depthMap[i][e] =Double.MAX_VALUE;

				this.setRGB(i, e, rgbToInt(Color.white));
			}
		}
	}
	public boolean fillPolygon(double[] xCords , double[] yCords,Shape obj ,  Vector camera , Vector r, double f, double sizex) {// obj for depth and xy cordinates 
		globalObj = obj;
		globalCamera = camera;
		globalR = r;
		globalF = f;
		globalSizeX = sizex;
		drawTriangle( xCords , yCords);
		//debug.drawMap(depthMap);
		return true;
	}
	void fillBottomFlatTriangle(Vector v1, Vector v2, Vector v3)
	{
		double invslope1 = (v2.x - v1.x) / (v2.y - v1.y);
		double invslope2 = (v3.x - v1.x) / (v3.y - v1.y);

		double curx1 = v1.x;
		double curx2 = v1.x;
		int max = (int) Math.min( v2.y, globalR.y);
		int scanlineY = (int) v1.y;
		if( v1.y<0) {
			scanlineY= 0;
			curx1 += (-v1.y)* (invslope1);
			curx2 += (-v1.y)* (invslope2);
		}
		for (scanlineY = scanlineY; scanlineY <= max; scanlineY++)
		{
			drawLineOnY((int)curx1, (int)curx2, scanlineY);
			curx1 += invslope1;
			curx2 += invslope2;
		}
	}void fillTopFlatTriangle(Vector v1, Vector v2, Vector v3)
	{
		double invslope1 = (v3.x - v1.x) / (v3.y - v1.y);
		double invslope2 = (v3.x - v2.x) / (v3.y - v2.y);

		double curx1 = v3.x;
		double curx2 = v3.x;
		int scanlineY = (int) v3.y;
		int max = (int) Math.max( v1.y, 0);
		if( v3.y> globalR.y) {
			scanlineY=(int) globalR.y;
			curx1 -= (v3.y- globalR.y)* (invslope1);
			curx2 -= (v3.y- globalR.y)* (invslope2);
		}
		for (scanlineY= scanlineY; scanlineY > max; scanlineY--)
		{
			drawLineOnY((int)curx1,  (int)curx2, scanlineY);
			curx1 -= invslope1;
			curx2 -= invslope2;
		}
	}
	private void drawLineOnY(int curx1, int curx2, int scanlineY) {
		int end = (int) Math.min(Math.max(curx1, curx2),globalR.x);
		int start = Math.max(Math.min(curx1, curx2),0);
		dataList.add(new data(start, end,scanlineY , globalObj));
		/*

		for(int x =start ; x < end ; x++  ) {
			if( x>= globalF  &&x< globalR.x && scanlineY > 0 &&scanlineY <globalR.y)
			dataList.add(new data(x,scanlineY , globalObj));

		}*/
	}void drawTriangle(double[] xCords , double[] yCords)
	{
		Vector v1= null;
		Vector v2= null;
		Vector v3= null;
		/* at first sort the three vertices by y-coordinate ascending so v1 is the topmost vertice */
		if (yCords[0] >= yCords[1]){ //In the three responses below, y is always before x.  
			if (yCords[1] >= yCords[2]) {// z = 2 ; y = 1 ; x = 0
				v1= new Vector(xCords[2] ,yCords[2],0);
				v2= new Vector(xCords[1] ,yCords[1],0);
				v3= new Vector(xCords[0] ,yCords[0],0);
			}
			else if  (yCords[2] >= yCords[0]) {//yxz
				v1= new Vector(xCords[1] ,yCords[1],0);
				v2= new Vector(xCords[0] ,yCords[0],0);
				v3= new Vector(xCords[2] ,yCords[2],0);

			}
			else if (yCords[0] > yCords[2])// yzx

			{
				v1= new Vector(xCords[1] ,yCords[1],0);
				v2= new Vector(xCords[2] ,yCords[2],0);
				v3= new Vector(xCords[0] ,yCords[0],0);
			}

		}

		if (yCords[1] > yCords[0]){// In the three responses below, x is always before y
			if (yCords[2] >= yCords[1])
			{

				v1= new Vector(xCords[0] ,yCords[0],0);
				v2= new Vector(xCords[1] ,yCords[1],0);
				v3= new Vector(xCords[2] ,yCords[2],0);
			}
			else if (yCords[2] >= yCords[0])
				//System.out.print("In order " + y + " " + x + " " + z); //In this case, z has to be smaller than y.  The order was off
			{

				v1= new Vector(xCords[0] ,yCords[0],0);
				v2= new Vector(xCords[2] ,yCords[2],0);
				v3= new Vector(xCords[1] ,yCords[1],0);
			}
			else if (yCords[0] > yCords[2]) {
				//System.out.print("In order " + y + " " + z + " " + x);
				v1= new Vector(xCords[2] ,yCords[2],0);
				v2= new Vector(xCords[0] ,yCords[0],0);
				v3= new Vector(xCords[1] ,yCords[1],0);}
		}


		/* here we know that v1.y <= v2.y <= v3.y */
		/* check for trivial case of bottom-flat triangle */
		if (v2.y == v3.y)
		{
			fillBottomFlatTriangle(v1, v2, v3);
		}
		/* check for trivial case of top-flat triangle */
		else if (v2.y == v1.y)
		{
			fillTopFlatTriangle( v1, v2, v3);
		}
		else
		{

			/* general case - split the triangle in a topflat and bottom-flat one */
			Vector v4 = new Vector(
					(v1.x + ((double)(v2.y - v1.y) / (double)(v3.y - v1.y)) * (v3.x - v1.x)), v2.y, 0);
			fillBottomFlatTriangle( v1, v2, v4);
			fillTopFlatTriangle( v2, v4, v3);
		}
	}
	public void attemptPixel(int x, int y , int color) {

		if(pixelMap[y * getWidth() + x] == -1) {
			setRGB(x, y, color);
			//return true;
		}
		if(pixelMap[y * getWidth() + x] != color) {
			
		}
	}
	public boolean setPixel(int x , int y, Color color, double depth) {
		if(  depth >=0 &&x >=0 && x< depthMap.length &&y >=0 && y< depthMap[0].length&& depth <= depthMap[x][y] ){
			depthMap[x][y] = depth;
			this.setRGB(x, y, rgbToInt(color));
			return true;
		}
		//System.out.printf("Pixel %d %d not on screen\n", x,y);
		return true;

	}
	@Override
	public void setRGB(int x , int y , int rgb) {
		if( rgb == -1 )
			pixelMap[y * getWidth() + x]= WHITE;//if no color set to white
		pixelMap[y * getWidth() + x]= rgb;
	}
	public void imagify() {
		WritableRaster rast = getRaster();
		rast.setDataElements(0, 0, getWidth(), getHeight(), pixelMap);
	}




	public int rgbToInt(Color rgb) {
		return 65536 * rgb.getRed() + 256 * rgb.getGreen() + rgb.getBlue();
	}

}
