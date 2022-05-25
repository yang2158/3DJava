import java.awt.Color;

public class Shaders {// CEL SHADERS
	final int ON_SHADER = 50;
	final int OFF_SHADER = -100;
	public double getAngle(Vector3 a , Vector3 b) {
		double rad = Math.acos(( a.dot(b)/(a.getMagnitude() * b.getMagnitude())) ) ;
		return rad *180/Math.PI;

	}
	public double getTint(Triangle a, light obj) {
		
		Vector3 one = a.getNormal();
		Vector3 two = new Vector3(a.Cords[0].x  -obj.pos.x, a.Cords[0].y -obj.pos.y, a.Cords[0].z -obj.pos.z) ;
		double ang = getAngle(one , two);
		int num = 1;
		int ksad = 4;/*
		int r =(a.trueColor.getGreen()+a.trueColor.getBlue())/ksad + a.color.getRed()/2;
		int g =(a.trueColor.getGreen()+a.trueColor.getRed())/ksad + a.color.getGreen()/2;
		int b = (a.trueColor.getGreen()+a.trueColor.getRed())/ksad + a.color.getBlue()/2;*/
		return ang;
	}
	public double getTint(Vector3 point,Vector3 no ,light obj) {
		
		Vector3 one = no;
		Vector3 two = new Vector3(point.x  -obj.pos.x, point.y -obj.pos.y, point.z -obj.pos.z) ;
		double ang = getAngle(one , two);
		int num = 1;
		int ksad = 4;/*
		int r =(a.trueColor.getGreen()+a.trueColor.getBlue())/ksad + a.color.getRed()/2;
		int g =(a.trueColor.getGreen()+a.trueColor.getRed())/ksad + a.color.getGreen()/2;
		int b = (a.trueColor.getGreen()+a.trueColor.getRed())/ksad + a.color.getBlue()/2;*/
		return ang;
	}
	public Color shade(double ang, Color a ) {

		if( ang <=90) {
			return lerp (a, Color.black ,((90-ang)/90)); 
		}
		
		
		
		return lerp (a, Color.white ,((ang-90)/90)); 
	}
	public int clamp (int l , int h , int num) {
		return Math.min(Math.max(l, num),h);
	}int lerp(int a, int b, double f)
	{
	    return (int)((double)a + f * (double)(b - a));
	}
	Color lerp(Color a, Color b, double f)
	{
	    return new Color ((lerp(a.getRed(), b.getRed(), f)),(lerp(a.getGreen(), b.getGreen(), f)),(lerp(a.getBlue(), b.getBlue(), f)));
	}
}
