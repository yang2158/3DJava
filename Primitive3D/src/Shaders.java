import java.awt.Color;

public class Shaders {// CEL SHADERS
	final int ON_SHADER = 50;
	final int OFF_SHADER = -100;
	public double getAngle(Vector a , Vector b) {
		double rad = Math.acos(( a.dot(b)/(a.getMagnitude() * b.getMagnitude())) ) ;
		return rad *180/Math.PI;

	}
	public Color getTint(Shape a, light obj) {
		Vector one = a.getNormal();
		Vector two = new Vector(a.Cords[0].x  -obj.pos.x, a.Cords[0].y -obj.pos.y, a.Cords[0].z -obj.pos.z) ;
		double ang = getAngle(one , two);
		int num = 1;
		int ksad = 4;/*
		int r =(a.trueColor.getGreen()+a.trueColor.getBlue())/ksad + a.color.getRed()/2;
		int g =(a.trueColor.getGreen()+a.trueColor.getRed())/ksad + a.color.getGreen()/2;
		int b = (a.trueColor.getGreen()+a.trueColor.getRed())/ksad + a.color.getBlue()/2;*/
		int r =(a.trueColor.getGreen()+a.trueColor.getBlue())/ksad + a.color.getRed()/2;
		int g =(a.trueColor.getGreen()+a.trueColor.getRed())/ksad + a.color.getGreen()/2;
		int b = (a.trueColor.getGreen()+a.trueColor.getRed())/ksad + a.color.getBlue()/2;
		if( ang <90) {
			return new Color(clamp(0,255,r),clamp(0,255,g),clamp(0,255,b));
		}
		
		
		
		return new Color(clamp(0,255,a.trueColor.getRed() + ON_SHADER),clamp(0,255,a.trueColor.getGreen() + ON_SHADER),clamp(0,255,a.trueColor.getBlue() + ON_SHADER));
	}
	public int clamp (int l , int h , int num) {
		return Math.min(Math.max(l, num),h);
	}
}
