
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;
public class ScreenImage extends BufferedImage{
	public double[][] depthMap = new double[1][1];
	DebuggerForDepth debug = new DebuggerForDepth();
	private Shape globalObj;
	private Vector globalCamera;
	private Vector globalR;
	private double globalF;
	private double globalSizeX;
	
    ArrayList<data> dataList = new ArrayList<>();
	
	public ScreenImage(int width, int height) {
		
		super(width, height, BufferedImage.TYPE_INT_RGB);
		depthMap = new double[width][height];

		for(int i = 0 ; i < width ; i ++) {
			for( int e = 0 ;  e < height ; e++) {
				depthMap[i][e] =Double.MAX_VALUE;
				this.setRGB(i, e, rgbToInt(Color.green));
			}
		}
	}
	public boolean setPixel(int x , int y, Color color) {
		this.setRGB(x, y, rgbToInt(color));
		return false;

	}
	public void processImage() {
		//long time =System.currentTimeMillis();
		
		int numth = 6;
		ProcessingThread[] threads = new ProcessingThread[numth] ;
		for(int i = 0 ; i< numth ; i ++) {
			threads[i] = new ProcessingThread(i, numth , globalR , globalCamera,globalF , globalSizeX );
			threads[i].giveData(dataList);
		}
		for(int i = 0 ; i< numth ; i ++) {
			threads[i] .start();
		}
		boolean[] done = new boolean[numth] ;
		Arrays.fill(done, true);
		boolean processing = true;
		while (processing) {
			processing = false;
			
			for(int i = 0 ; i< numth ; i ++) {
				if(threads[i].isAlive()) {
					processing = true;
					
				}else {
					if(done[i]) {
						done[i]= false;
						Queue<outputData> processedData =  threads[i].getDone();
						//System.out.print(processedData.size() + " " );
						while(!processedData.isEmpty()) {
							outputData dat = processedData.remove();
							setPixel(dat.x , dat.y ,dat.color, dat.depth );
							
						}
					}
				}
				
			}
		}//System.out.println("  ; "+ (System.currentTimeMillis()-time) + "ms");
	}
	public void reset() {
		dataList.clear();
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
		/*// Old code 
		double yMin= r.y ;
		double yMax=0;
		double xMin= r.x ;
		double xMax=0;
		for( int cords = 0 ; cords< xCords.length; cords++) {
			yMin = Math.min(yMax, yCords[cords]);
			yMax= Math.max(yMax, yCords[cords]);
			xMin = Math.min(xMin, xCords[cords]);
			xMax= Math.max(xMax, xCords[cords]);
		}
		ArrayList<Double> xCordinate = new ArrayList<Double>();
		for( double cords = yMin; cords< yMax; cords++) {
			for( int lines = 0 ; lines< xCords.length; lines++) {
				double min = Math.min(yCords[lines], yCords[(lines+1)% xCords.length]);
				double max = Math.max(yCords[lines], yCords[(lines+1)% xCords.length]);

				double dx =xCords[(lines+1)% xCords.length]-xCords[lines];
				double dy =yCords[(lines+1)% xCords.length]-yCords[lines];
				double m = dy/dx;

				double b= yCords[lines]-  ((m)*xCords[lines]) ;
				if(dx == 0) {
					xCordinate.add(xCords[lines]);
				}else
					if(dy == 0) {
						continue;
					}
					else
						if(   m!=0) {
							if(cords< max && cords > min) {
								xCordinate.add((cords-b)/m);
							}
						}
			}

			Collections.sort(xCordinate);
			if(xCordinate.size()==3) {
				for( double x = Math.min(xCordinate.get(1),xCordinate.get(0)); x < Math.max(xCordinate.get(1),xCordinate.get(0)); x++ ) {
					//this.setPixel((int)Math.round(x),(int)Math.round(cords), color);
					this.setPixel((int)Math.round(x),(int)Math.round(cords), color, getPixelOnObj((int)x , (int)cords ,obj , camera , r , f , sizex));
				}for( double x = Math.min(xCordinate.get(1),xCordinate.get(2)); x < Math.max(xCordinate.get(1),xCordinate.get(2)); x++ ) {
					//this.setPixel((int)Math.round(x),(int)Math.round(cords), color);
					this.setPixel((int)Math.round(x),(int)Math.round(cords), color, getPixelOnObj((int)x , (int)cords ,obj , camera , r , f , sizex));
				}	

			}else
				if(xCordinate.size()!=1) 
					for(int i = 0 ; i < xCordinate.size() ; i +=2) {
						for( double x = Math.min(xCordinate.get(i),xCordinate.get(i+1)); x < Math.max(xCordinate.get(i),xCordinate.get(i+1)); x++ ) {
							//this.setPixel((int)Math.round(x),(int)Math.round(cords), color);
							this.setPixel((int)Math.round(x),(int)Math.round(cords), color, getPixelOnObj((int)x , (int)cords ,obj , camera , r , f , sizex));
						}
					}


			xCordinate.clear();
		}*/
		debug.drawMap(depthMap);
		return true;
	}
	void fillBottomFlatTriangle(Vector v1, Vector v2, Vector v3)
	{
		double invslope1 = (v2.x - v1.x) / (v2.y - v1.y);
		double invslope2 = (v3.x - v1.x) / (v3.y - v1.y);

		double curx1 = v1.x;
		double curx2 = v1.x;
		int max = (int) Math.min( v2.y, globalR.y);
		for (int scanlineY = (int) v1.y; scanlineY <= max; scanlineY++)
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

		int max = (int) Math.max( v1.y, 0);
		for (int scanlineY = (int) v3.y; scanlineY > max; scanlineY--)
		{
			drawLineOnY((int)curx1,  (int)curx2, scanlineY);
			curx1 -= invslope1;
			curx2 -= invslope2;
		}
	}
	private void drawLineOnY(int curx1, int curx2, int scanlineY) {
		int end = (int) Math.min(Math.max(curx1, curx2),globalR.x);
		int start = Math.max(Math.min(curx1, curx2),0);
		for(int x =start ; x < end ; x++  ) {
			if( x>= globalF  &&x< globalR.x && scanlineY > 0 &&scanlineY <globalR.y)
			dataList.add(new data(x,scanlineY , globalObj));
			
		}
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
	public boolean setPixel(int x , int y, Color color, double depth) {
		if(  depth >=0 &&x >=0 && x< depthMap.length &&y >=0 && y< depthMap[0].length&& depth <= depthMap[x][y] ){
			depthMap[x][y] = depth;
			this.setRGB(x, y, rgbToInt(color));
			return true;
		}
		//System.out.printf("Pixel %d %d not on screen\n", x,y);
		return true;

	}
	public int rgbToInt(Color rgb) {
		return 65536 * rgb.getRed() + 256 * rgb.getGreen() + rgb.getBlue();
	}

}
