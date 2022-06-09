import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Main extends JPanel implements KeyListener ,ActionListener {

	/*Example Objects:
	 * Elf01_posed.obj
	 * us-c-130-hercules-airplane.obj
	 * sign.obj
	 * model_mesh.obj //Doesn't work cause no normal 
	 */
	//Untested Settings ( Settings that if changed might not work)
	public double FOV = 1;// You don wanna change this 



	//SETTINGS 
	public int sampleSize =2;
	public String loadOBJ_FILENAME = "Elf01_posed.obj";// loads a obj file with it's mtl file
	public double f = 1;// Plane / Intersection / Close clipping plane
	public Vector3 CPos= new Vector3(0,0,-150);//Starting positions
	public Vector3 COre= new Vector3(90,0,0);// Starting rotation
	public Vector2 sD = new Vector2 (1000,1000);


	//Constants 
	final Vector3 zero = new Vector3(0,0,0);
	final Vector3 forwardFace =new Vector3(1,0,0);
	boolean[] held = new boolean[1000]; // WHICH BUTTONS ARE HELD (just makes it easier to make the movement)


	//Variables
	WritableRaster deprecatedRaster;// Deprecated drawing of buffer to image
	ReturnData reData= new ReturnData();
	worldObject plane;

	//My Libraries
	matrix mat= new matrix();
	Quaternion quaternion = new Quaternion();// not quaternion just rotation matrix 
	Shaders shade = new Shaders();




	//DATA
	ArrayList<BufferedImage> textures = new ArrayList<BufferedImage>();
	ArrayList<worldObject> world = new ArrayList<worldObject>();
	ArrayList < light> lights= new ArrayList<light>();




	int[] backBuffer = new int [(int) (sD.x * sD.y)];

	BufferedImage displayBuffer = new BufferedImage((int)sD.x , (int)sD.y, BufferedImage.TYPE_INT_RGB);
	Color[][] sampleBuffer =new Color [(int) (sD.x*sampleSize)][(int) (sD.y * sampleSize)];
	double[][] depthBuffer = new double[(int) (sD.x*sampleSize)][(int) (sD.y * sampleSize)];



	public worldObject loadObj(String filename) {
		worldObject obj = new worldObject();
		obj.loadFile(filename,this);
		world.add(obj);
		return obj;

	}
	public void blankBuffer () {

		for (int i = 0 ; i < (int) (sD.x*sampleSize) ;i++) {
			for (int j = 0; j < (int) (sD.y * sampleSize); j++) {
				depthBuffer[i][j] =Double.MAX_VALUE;
			}
		}
	}
	public void setDisplayRGB(int x , int y , int rgb) {
		backBuffer[(int) (y * sD.x + x)]= rgb;
	}
	public int imageID(BufferedImage newImage) {
		textures.add(newImage);
		return textures.size()-1;
	}
	public void convertBuffer () {
		for (int i = 0 ; i < sD.x *sampleSize;i+=sampleSize) {
			for (int j = 0; j < sD.y*sampleSize; j+=sampleSize) {
				int r=0;
				int g = 0;
				int b =0 ;
				for (int a = 0 ; a < sampleSize; a++) 

					for (int q = 0 ; q < sampleSize; q++) {
						if(sampleBuffer[i+a][j+q] == null||depthBuffer[i+a][j+q] == Double.MAX_VALUE) {
						}else {
							r+= sampleBuffer[i+a][j+q].getRed()*sampleBuffer[i+a][j+q].getRed();
							g+= sampleBuffer[i+a][j+q].getGreen()*sampleBuffer[i+a][j+q].getGreen();
							b+= sampleBuffer[i+a][j+q].getBlue()*sampleBuffer[i+a][j+q].getBlue();
						}

					}
				r/=(sampleSize * sampleSize);
				g/=(sampleSize * sampleSize);
				b/=(sampleSize * sampleSize);
				setDisplayRGB(i/sampleSize,j/sampleSize ,rgbToInt(new Color((int)Sqrt(r),(int)Sqrt(g),(int)Sqrt(b))));
			}

		}
	}public static double Sqrt(double x) {
		double xhalf = 0.5d * x;
		long i = Double.doubleToLongBits(x);
		i = 0x5fe6ec85e7de30daL - (i >> 1);
		x = Double.longBitsToDouble(i);
		x *= (1.5d - xhalf * x * x);
		return 1/x;
	}
	//Draws a triangle 
	void drawTriangle(Vector2 v0, Vector2 v1 , Vector2 v2, Triangle obj)
	{

		int minX= (int) Math.min(v0.x, Math.min(v1.x, v2.x));
		int maxX= (int) (Math.max(v0.x, Math.max(v1.x, v2.x)));// add 1 so it always rounds up
		int minY= (int) Math.min(v0.y, Math.min(v1.y, v2.y));
		int maxY= (int) (Math.max(v0.y, Math.max(v1.y, v2.y)));// add 1 so it always rounds up

		double a01 = -(v0.y - v1.y)/sampleSize, b01 = -(v1.x - v0.x)/sampleSize;
		double a12 = -(v1.y - v2.y)/sampleSize, b12 = -(v2.x - v1.x)/sampleSize;
		double a20 = -(v2.y - v0.y)/sampleSize, b20 = -(v0.x - v2.x)/sampleSize;
		
		Vector2 p = new Vector2((double)(minX*sampleSize-1)/sampleSize, (double)(minY*sampleSize-1)/sampleSize);
		double rowW0 = edgeFunction(v1, v2, p);
		double rowW1 = edgeFunction(v2, v0, p);
		double rowW2 = edgeFunction(v0, v1, p);

		double area3 = edgeFunction(obj.camCords[0],obj.camCords[1], obj.camCords[2]); 
		for (int j = minY*sampleSize-1; j <= maxY*sampleSize+1; ++j) { 
			double w0 = rowW0;
			double w1 = rowW1;
			double w2 = rowW2;
			for (int i = minX*sampleSize-1; i <= maxX*sampleSize+1; ++i) { 
				//tempX += inc;
				p = new Vector2( ((double) i)/sampleSize , ((double)j)/sampleSize );
				double tempY = (sD.y *FOV /sD.x )/2 -(p.y*FOV/sD.x) ;
				double tempX = FOV*(p.x/sD.x   - 0.5);
				//*/
				if (w0 >= 0 && w1 >= 0 && w2 >= 0) { 

					getPixelOnObj(tempX , tempY ,obj, reData  );
					double num = reData.dist;

					if(num>=f) {
						Vector3 p3d= reData.a;



						//setRGB(i,j , Color.red);

						double wc0 = edgeFunction(obj.camCords[1], obj.camCords[2],p3d ); 
						double wc1 = edgeFunction(obj.camCords[2], obj.camCords[0], p3d); 
						double wc2 = edgeFunction(obj.camCords[0], obj.camCords[1], p3d); 
						wc0 /= area3; 
						wc1 /= area3; 
						wc2 /= area3;
						double x =wc0 * obj.TextureCords[0].x + wc1 * obj.TextureCords[1].x + wc2 * obj.TextureCords[2].x; 
						double y =wc0 * obj.TextureCords[0].y + wc1 * obj.TextureCords[1].y + wc2 * obj.TextureCords[2].y; 
						double tint =0;
						for (int h = 0; h < lights.size(); h++) {
							tint = Math.max(tint, shade.getTint(p3d, obj.getcamnormal(wc0, wc1, wc2),lights.get(h) ));
						}
						Color a=new Color((textures.get(obj.imageID).getRGB((int)((x%1)*textures.get(obj.imageID).getWidth()), (int)((1-(y%1))*textures.get(obj.imageID).getHeight()))));
						a= shade.shade(tint, a);
						setSample(i, j,a ,num );
					}else {
						//System.out.println(num);
					}
				}
				w0 += a12;
				w1 += a20;
				w2 += a01;
			}
			rowW0 += b12;
			rowW1 += b20;
			rowW2 += b01;
		}
	}
	private void setSample(int x, int y, Color color, double depth) {// Sets the sample buffer and depth buffer
		if(   x >=0 && x< sD.x * sampleSize &&y >=0 && y< sD.y * sampleSize && depthBuffer[x][y]> depth){
			depthBuffer[x][y] = depth;
			setRGB(x, y, color);
		}
	}
	//2D cross product
	double edgeFunction(double[] v1,double[] v2, double[] p) 
	{ return (p[0] - v1[0]) * (v2[1] - v1[1]) - (p[1] - v1[1]) * (v2[0] - v1[0]); } 
	double edgeFunction(Vector2 v1,Vector2 v2, Vector2 p) 
	{ return (p.x - v1.x) * (v2.y - v1.y) - (p.y - v1.y) * (v2.x - v1.x); } 
	double edgeFunction(double v1x,double v1y,double v2x,double v2y, double px,double py) 
	{ return (px - v1x) * (v2y - v1y) - (py - v1y) * (v2x - v1x); } 

	//3D Cross product's Magnitude /2 
	double edgeFunction(Vector3 v1,Vector3 v2, Vector3 p)
	{ 

		// This works because the magnitude of the Cross produce is the area of a parrallelagram with the vectors
		// If you divide a parrallegram by 2 you get a triangle
		// Do this in 3d and you have the 3d area of a triangle
		Vector3 one = new Vector3(v1.x -p.x ,v1.y -p.y ,v1.z -p.z  );
		Vector3 two = new Vector3(v2.x -p.x ,v2.y -p.y ,v2.z -p.z  );
		double i = (one.y*two.z - one.z *two.y ) ;
		double j = (one.z*two.x - one.x *two.z ) ;
		double k = (one.x*two.y - one.y *two.x ) ;
		return Sqrt(i* i + j * j + k *k) / 2 ;


	} 

	public void displayBack() {// Displays back buffer
		// Converts the sample buffer to a image and displays it
		convertBuffer();
		//deprecatedRaster.setDataElements(0, 0, (int)sD.x,(int)sD.y, backBuffer);
		displayBuffer.setRGB(0, 0, (int)sD.x,(int)sD.y, backBuffer,0,(int)sD.x);
	}

	public int rgbToInt(Color rgb) { // Converts from colour to int
		return 65536 * rgb.getRed() + 256 * rgb.getGreen() + rgb.getBlue();
	}

	public void setRGB(int x , int y , Color rgb) {// sets sample
		if(x>=0 && x < sD.x*sampleSize && y >= 0 && y < sD.y*sampleSize )
			sampleBuffer[x][y]=  (rgb);
	}

	public static void main(String[] args ) {
		Main pane = new Main();
		JFrame app = new JFrame("App");
		app.add(pane, BorderLayout.CENTER);
		app.setSize( (int)pane.sD.x, (int)pane.sD.y);

		app.setLocationRelativeTo(null);
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		app.setVisible(true);


	}

	void init() {//initializes everything
		plane =loadObj(loadOBJ_FILENAME);
		lights.add( new light (new Vector3(1000,1000,1000)));
		deprecatedRaster = displayBuffer.getRaster();
		for (int i = 0 ; i < (int) (sD.x*sampleSize) ;i++) {
			for (int j = 0; j < (int) (sD.y * sampleSize); j++) {
				setRGB(i,j, Color.BLACK);
				depthBuffer[i][j] =Double.MAX_VALUE;
			}
		}
	}

	public Main() {
		init();
		Timer time = new Timer(0,this);
		paint(null);
		setFocusable(true);
		addKeyListener(this);
		time.start();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		long startTime = System.currentTimeMillis();
		Graphics2D g2 = (Graphics2D) g;
		blankBuffer();
		// DO STUFF HERE
		g.setColor(Color.red);
		for(int i = 0; i < world.size();i++) {
			ArrayList<Triangle> list = world.get(i).loadedShapes;
			for (int objID = 0; objID < list.size();objID++) {

				//list.get(objID).getNormal().Print();
				Vector2[] cords = new Vector2[list.get(objID).Cords.length];

				int stuf = 0;
				if( list.get(objID).Cords.length ==3) {
					int j;
					for (j=0; j < list.get(objID).Cords.length; j++) {
						//rotatedProduct= quaternion.rotate(CPos, list.get(objID).Cords[j], quaternion.degToRadian(-COre.x), quaternion.degToRadian(COre.y)); 
						Vector3 pos = list.get(objID).Cords[j].clone();
						pos=quaternion.rotate(zero, pos, quaternion.degToRadian(-world.get(i).rot.x), quaternion.degToRadian(world.get(i).rot.y));
						pos.add(world.get(i).pos);
						
						list.get(objID).camCords[j]= quaternion.rotate(CPos, pos, quaternion.degToRadian(-COre.x), quaternion.degToRadian(COre.y));
						list.get(objID).camnormal[j] = quaternion.rotate(zero, list.get(objID).normal[j], quaternion.degToRadian(-world.get(i).rot.x), quaternion.degToRadian(world.get(i).rot.y));
						Vector2 result = Prespective(list.get(objID).camCords[j]);

						cords[j]=  result;
						if(list.get(objID).camCords[j].x - CPos.x >f &&cords[j].x >=0 && cords[j].x < sD.x) {
							stuf++;

						}



					}
					if(stuf >0) {

						drawTriangle(cords[0] , cords[1], cords[2] , list.get(objID));
					}







				}
			}
		}
		displayBack();
		g2.drawImage(displayBuffer, 0, 0, null);
		g.setColor(Color.white);
		//g2.drawImage(displayBuffer, 0, 0, null);
		g2.drawString("Pos X : " + CPos.x+" Y : " + CPos.y+" Z : " + CPos.z, 40, 50);
		g2.drawString("Ore X : " + COre.x+" Y : " + COre.y+" Z : " + COre.z, 40, 80);
		long delay = System.currentTimeMillis()-startTime;
		g2.drawString("Delay : " + delay + " FPS: " + (1000/delay), 40, 110);

	}
	Vector3 PixelPos3D = new Vector3 (f,0,0) ;
	public void getPixelOnObj(double x, double tempY, Triangle obj, ReturnData re ) {

		PixelPos3D.set(f,tempY,x) ;
		// The 2d plane dist , 

		double magnitude = Sqrt(PixelPos3D.x *PixelPos3D.x + PixelPos3D.y *PixelPos3D.y + PixelPos3D.z * PixelPos3D.z);

		//return experimental(new Vector3(0,globalCamera.y + PixelPos3D.y,globalCamera.z + PixelPos3D.z ), obj.Cords[0] , obj.getNormal()); //Doesn't work (please look at comment on the function)
		double num =intersect(CPos, PixelPos3D , obj.camCords[0] , obj.getNormal()) ;
		Vector3 vre = new Vector3(0,0,0);
		vre.add(PixelPos3D);
		vre.muti(num);
		vre.add(CPos);
		re.setReturnData( vre, num/ magnitude) ;
	} 
	Vector3 pointNormal= new Vector3(0,0,0);
	Vector3 centerFront = (new Vector3 (CPos.x+f, CPos.y , CPos.z));
	public Vector2 Prespective(Vector3 point) {/*
		//Triangle similarity To solve projection on 2D plane given line
		//TODO Use Line and Plane Intersection instead of Triangle Simularity to get Pixel
		double f1= (point.x-CPos.x);
		double z1= (point.z-CPos.z);
		double x = (f* z1)/f1;
		double b2 = Sqrt(f*f + x*x);
		double b1 = Sqrt( f1*f1 +z1*z1 );
		double y = (b2 *(point.y-CPos.y))/b1; 
	 */
		double x,y;
		centerFront.set (CPos.x+f, CPos.y , CPos.z);
		pointNormal.set((point.x - CPos.x ),(point.y - CPos.y),(point.z - CPos.z) );
		double unitsAway =  intersect (CPos,pointNormal, centerFront,forwardFace);


		x=unitsAway*(point.z - CPos.z);
		y=  unitsAway*(point.y - CPos.y);

		x=(sD.x/2 + sD.x*x/FOV )   ;
		y=((sD.y *FOV /sD.x )/2 -y)* ( sD.x / FOV);

		return (new Vector2(x,y));//*/
		//*/

		//return experimental(new Vector3(0,globalCamera.y + PixelPos3D.y,globalCamera.z + PixelPos3D.z ), obj.Cords[0] , obj.getNormal()); //Doesn't work (please look at comment on the function)
		//return new Vector3(CPos.x + t*(point.x - CPos.x) ,);
	}
	
	public Vector3 offScreen(Vector3 point, Vector3 other) {// Interpolates triangles to screen to imitate clipping without using depth buffers (deprecated) 
		//REPLACED BY DEPTH BUFFER
		double x,y;
		Vector3 direction = new Vector3(point.x - other.x ,point.y - other.y,point.z - other.z );
		double unitsAway = intersect (other,  direction, (new Vector3 (CPos.x+f, CPos.y , CPos.z)) ,new Vector3(1,0,0));
		x=other.z+(unitsAway*direction.z);
		y=other.y+(unitsAway*direction.y);
		x-= CPos.z;
		y-= CPos.y;
		x=((x/FOV)*sD.x)+sD.x/2;
		y=((y/FOV)*sD.y)+sD.y/2;
		y= sD.y - y;// Inverts it because java draws top to down not bottom up like a graph

		return (new Vector3(x,y,0));//*/
		//*/

		//return experimental(new Vector3(0,globalCamera.y + PixelPos3D.y,globalCamera.z + PixelPos3D.z ), obj.Cords[0] , obj.getNormal()); //Doesn't work (please look at comment on the function)
		//return new Vector3(CPos.x + t*(point.x - CPos.x) ,);
	}

	public void keyPressed(KeyEvent e) {
		held[e.getKeyCode()]= true;
	}
	double intersect(Vector3 p , Vector3 v , Vector3 n , Vector3 d) // point , Vector3 , plane point , direction (ikr why did this dumbass place n as the point and not the normal. Apparently this dumbass is me)
	{
		return -(d.dot(p) - n.dot(d)) / d.dot(v);// Line plane intersection
	}
	@Override
	public void keyReleased(KeyEvent e) {

		held[e.getKeyCode()]= false;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

		double change = 1;
		if( held[KeyEvent.VK_CONTROL]) {
			change = 3;

		}
		if(held[KeyEvent.VK_LEFT]) {
			COre.x = (COre.x + 355)%360;
		}
		if(held[KeyEvent.VK_RIGHT]) {COre.x = (COre.x + 365)%360;
		}
		if(held[KeyEvent.VK_UP]) {
			COre.y =(COre.y + 355)%360;;
		}if(held[KeyEvent.VK_DOWN]) {
			COre.y =(COre.y + 365)%360;;
		}
		if(held[KeyEvent.VK_R]) {
			plane.pos.z+=3;
		}
		if(held[KeyEvent.VK_T]) {
			plane.rot.x = (plane.rot.x + 355)%360;
		}
		if(held[KeyEvent.VK_W]) {

			CPos.add(quaternion.rotate(zero, new Vector3(change,0,0), quaternion.degToRadian(COre.x), 0));

		}if(held[KeyEvent.VK_A]) {
			CPos.add(quaternion.rotate(zero, new Vector3(0,0,-change), quaternion.degToRadian(COre.x), 0));

		}if(held[KeyEvent.VK_D]) {
			CPos.add(quaternion.rotate( zero, new Vector3(0,0,change), quaternion.degToRadian(COre.x), 0));

		}if(held[KeyEvent.VK_S]) {

			CPos.add(quaternion.rotate( zero, new Vector3(-change,0,0), quaternion.degToRadian(COre.x), 0));

		}
		if(held[KeyEvent.VK_SPACE]) {

			CPos.add(quaternion.rotate( zero, new Vector3(0,change,0), quaternion.degToRadian(COre.x), 0));
		}if(held[KeyEvent.VK_SHIFT]) {

			CPos.add(quaternion.rotate( zero, new Vector3(0,-change,0), quaternion.degToRadian(COre.x), 0));
		}

		repaint();
	}
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}
}
