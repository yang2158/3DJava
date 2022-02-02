import java.awt.Color;

public class outputData {
	public int x;
	public int y;
	public int startX;
	public int endX;
	public double depth;
	public Color color;
	public outputData(int X , int Y , double DEPTH , Color COLOR) {
		x=X;
		y=Y;
		depth = DEPTH;
		color = COLOR;
	}
	public outputData(int X1,int X2 , int Y , double DEPTH , Color COLOR) {
		startX= X1;
		endX= X2;
		depth = DEPTH;
		color = COLOR;
	}
}
