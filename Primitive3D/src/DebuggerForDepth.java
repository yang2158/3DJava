import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class DebuggerForDepth  extends JPanel implements MouseMotionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int x,y ;
	double[][] display= new double[2000][2000];
	public DebuggerForDepth() {
		setFocusable(true);
		addMouseMotionListener(this);
		
		for( int i = 0 ; i < display.length; i ++) 
			for(int e = 0 ; e < display[i].length ; e++) {
				display[i][e] =Double.MAX_VALUE;
		}
		paint(null);
		JFrame app = new JFrame("App");
		app.add(this, BorderLayout.CENTER);
		app.setSize(1000, 1000);
		app.setLocationRelativeTo(null);
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		app.setVisible(true);
	}
	public void drawMap(double[][] map) {
		display = map;
	}
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D)g ;
		super.paintComponent(g);
		double max = Double.MIN_VALUE;
		double min = Double.MAX_VALUE;
		boolean can =false;
		for( int i = 0 ; i < display.length; i ++) 
			for(int e = 0 ; e < display[i].length ; e++) {
				if(display[i][e]!= Double.MAX_VALUE) {
					min = Math.min(min, display[i][e] );
					max = Math.max(max, display[i][e] );
					can=true;
				}
			}
		if(can)
		for( int i = 0 ; i < display.length; i ++) {
			for(int e = 0 ; e < display[i].length ; e++) {

				if(display[i][e]== Double.MAX_VALUE) {
					g2.setPaint(Color.black);
					g2.drawRect(i, e, 1, 1);
					continue;
				}
				g2.setPaint(new Color ((int)((display[i][e]-min) / (max - min)*200)+55 ,0,0));
				g2.drawRect(i, e, 1, 1);
			}
		}
		g2.setPaint(Color.white);
		g2.drawString("Pos X : " + x+" Y : " + y, 40, 50);

		g2.drawString("Depth : " + display[x][y] , 40, 100);
		g2.drawString("Closest  " + min + "   Furthest "+ max  + " Diff :" + (max -min), 40, 150);
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		repaint();
		x= e.getX();
		y= e.getY();
	}
}
