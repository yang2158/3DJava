import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class Main extends JPanel implements KeyListener, ActionListener{

	double sizex = 1;
	boolean[] held = new boolean[256];
	ArrayList<Shape> obj = new ArrayList<Shape>();
	static double[] xCords= new double[0];
	static double [] yCords= new double[0];
	static double [] zCords= new double[0];
	static Vector CPos= new Vector(0,0,0);	
	 Vector COre= new Vector(0,0,0);
	ScreenImage[] screen = new ScreenImage[2];
	//static Vector EPos= new Vector(1,0.01,0.01);	// Pinhole	
	double f=0.5;
	Quaternion quaternion = new Quaternion();
	static Vector r= new Vector(600,600,.1);	// Pinhole	
	Vector p = new Vector(1,0,1);
	
	public static void addCords(Vector cords) {
		double[] temp = new double [xCords.length+1];
		for(int i = 0 ; i < xCords.length; i ++) {
			temp[i]= xCords[i];
		}
		temp[xCords.length]= cords.x;
		xCords = temp.clone();
		for(int i = 0 ; i < yCords.length; i ++) {
			temp[i]= yCords[i];
		}
		
		temp[yCords.length]= cords.y;
		yCords = temp.clone();
		for(int i = 0 ; i < zCords.length; i ++) {
			temp[i]= zCords[i];
		}
		temp[zCords.length]= cords.z;
		zCords = temp.clone();
	}

	double  xx ,yy;
	public Main() {
		//quaternion.rotate(new Vector(1,0,1), new Vector(1,0,2), quaternion.degToRadian(180), 0).Print();
		
		Timer time = new Timer(10,this);
		
		addCords(new Vector(1,0,1));
		
		obj.add(new Shape("Triangle",
		(new Vector(1,0,2)),
		(new Vector(1,1,2)),
		(new Vector(1,1,1))));
//*/
/**/
		obj.add(new Shape("Triangle",
		(new Vector(1,0,2)),
		(new Vector(2,1,2)),
		(new Vector(1,1,1))));//*/
		
/**/
		obj.add(new Shape("Triangle",
		(new Vector(1,1,2)),
		(new Vector(2,1,2)),
		(new Vector(1,1,1))));
		//*/
/**/
		obj.add(new Shape("Triangle",
		(new Vector(1,1,2)),
		(new Vector(2,1,2)),
		(new Vector(1,0,2))));
		//*/
		paint(null);
		setFocusable(true);
		addKeyListener(this);
		time.start();
	}
	
	public void add(double x, double y) {
		xx=x;
		yy=y;
		repaint();
		
	}
	public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if( screen[0] ==null) {

    		screen[0] = new ScreenImage(this.getWidth(), this.getHeight());
        }screen[0].reset();
		Graphics2D g2 = (Graphics2D) g;
		for(int i = 0 ; i < obj.size(); i++) {
			
			//obj.get(i).getNormal().Print();
			double[] xC = new double[obj.get(i).Cords.length];
			double[] yC = new double[obj.get(i).Cords.length];
			int[] xCi = new int[obj.get(i).Cords.length];
			int[] yCi = new int[obj.get(i).Cords.length];
			int j ;
			Vector rotatedProduct;
			Shape newObj = new Shape();
			newObj.color  =obj.get(i).color;
			newObj.Cords=new Vector[obj.get(i).Cords.length];
			for (j=0; j < obj.get(i).Cords.length; j++) {
				rotatedProduct= quaternion.rotate(CPos, obj.get(i).Cords[j], quaternion.degToRadian(-COre.x), quaternion.degToRadian(COre.y));

				newObj.Cords[j]= (rotatedProduct);
				Vector result = Prespective(rotatedProduct);
				xC[j]=  result.x;
				yC[j]=  result.y;
				xCi[j]=  (int)result.x;
				yCi[j]= (int) result.y;
				g2.setPaint(Color.RED);
			}
			g2.setPaint(obj.get(i).color);
			
			screen[0].fillPolygon(xC, yC, newObj, CPos, r, f, sizex);

			Graphics graph = screen[0].getGraphics();
			graph.setColor(Color.red);
			graph.drawPolygon(xCi,yCi, yCi.length);
			
		}

		screen[0].processImage();
        g2.drawImage(screen[0], 0, 0, null);
		g2.drawString("Pos X : " + CPos.x+" Y : " + CPos.y+" Z : " + CPos.z, 40, 50);
		g2.drawString("Ore X : " + COre.x+" Y : " + COre.y+" Z : " + COre.z, 40, 100);
	}
	public static void main(String[] args ) {
		 Main pane = new Main();
		JFrame app = new JFrame("App");
		app.add(pane, BorderLayout.CENTER);
		app.setSize(1000, 1000);
		r.x=1000;
		r.y=1000;
		app.setLocationRelativeTo(null);
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		app.setVisible(true);
		System.out.println(Arrays.toString(xCords));
		System.out.println(Arrays.toString(yCords));


	}
	public Vector Prespective(Vector point) {
		//Triangle similarity To solve projection on 2D plane given line
		//TODO Use Line and Plane Intersection instead of Triangle Simularity to get Pixel
		double f1= (point.x-CPos.x);
		double z1= (point.z-CPos.z);
		double x = (f* z1)/f1;
		double b2 = Math.sqrt(f*f + x*x);
		double b1 = Math.sqrt( f1*f1 +z1*z1 );
		double y = (b2 *(point.y-CPos.y))/b1; 
		x=((x/sizex)*r.x)+r.x/2;
		y=((y/sizex)*r.y)+r.y/2;
		y= r.y - y;// Inverts it because java draws top to down not bottom up like a graph
		
		
		return (new Vector(x,y,0));
	}
	Vector intersect(Vector p , Vector v , Vector n , Vector d) // point , vector , plane point , direction
	{
		
		
		
	    // dot products
	    double dot1 = d.dot(v);             // a*Vx + b*Vy + c*Vz
	    double dot2 = d.dot(p);             // a*x1 + b*y1 + c*z1

	    // if denominator=0, no intersect
	    /*if(dot1 == 0)
	        return null; // return NaN point
*/
	    // find t = -(a*x1 + b*y1 + c*z1 + d) / (a*Vx + b*Vy + c*Vz)
	    double t = -(dot2 - n.dot(d)) / dot1;
	    // find intersection point
	   return new Vector ( p.x+(t* v.x), p.y+(t* v.y), p.z+(t* v.z));
	}
	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	@Override
	public void keyPressed(KeyEvent e) {
		held[e.getKeyCode()]= true;
		if( e.getKeyCode() == KeyEvent.VK_ENTER) {
			
			int i= 0;
			for (int j=0; j < obj.get(i).Cords.length; j++) {
				Vector result = Prespective(obj.get(i).Cords[j]);
				
				System.out.println("\n " + j + " : \n");
				obj.get(i).Cords[j].Print();
				System.out.println("x: "+result.x+"  y: "+result.y);

			}

		}
	}
	@Override
	public void keyReleased(KeyEvent e) {

		held[e.getKeyCode()]= false;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

		final double change = 0.05;
		if(held[KeyEvent.VK_LEFT]) {
			COre.x -= 5;
		}
		if(held[KeyEvent.VK_RIGHT]) {
			COre.x += 5;
		}
		if(held[KeyEvent.VK_UP]) {
			COre.y -= 5;
		}if(held[KeyEvent.VK_DOWN]) {
			COre.y += 5;
		}
		if(held[KeyEvent.VK_W]) {

			CPos.add(quaternion.rotate( new Vector(0,0,0), new Vector(change,0,0), quaternion.degToRadian(COre.x), 0));
			
		}if(held[KeyEvent.VK_A]) {
			CPos.add(quaternion.rotate( new Vector(0,0,0), new Vector(0,0,-change), quaternion.degToRadian(COre.x), 0));
			
		}if(held[KeyEvent.VK_D]) {
			CPos.add(quaternion.rotate( new Vector(0,0,0), new Vector(0,0,change), quaternion.degToRadian(COre.x), 0));
			
		}if(held[KeyEvent.VK_S]) {

			CPos.add(quaternion.rotate( new Vector(0,0,0), new Vector(-change,0,0), quaternion.degToRadian(COre.x), 0));

		}
		if(held[KeyEvent.VK_SPACE]) {

			CPos.y+=change;
		}if(held[KeyEvent.VK_SHIFT]) {

			CPos.y-=change;
		}
	
		repaint();
	}

}
