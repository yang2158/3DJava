import java.awt.Color;

public class Shape {
	public Vector[] Cords= new Vector[3];
	Color color = new Color( (int)(Math.random()*255) ,(int)(Math.random()*255),(int)(Math.random()*255));
	
	public  void addCords(Vector cords) {
		Vector[] temp = new Vector [Cords.length+1];
		for(int i = 0 ; i < Cords.length; i ++) {
			temp[i]= Cords[i];
		}
		temp[Cords.length]= cords;
		Cords= temp.clone();
		
	}public Shape(String type , Vector p1, Vector p2,Vector p3) {
		/*if(type.equals("Triangle")) {/*
			addCords(p1);
			addCords(p2);
			addCords(p3);*/
			Cords[0] = p1;
			Cords[1] = p2;
			Cords[2] = p3;
			
		//}
	}public Shape(  ) {
		
	}
	public Vector getNormal() {// if three points 
		Vector u = new Vector(Cords[1].x - Cords[0].x ,Cords[1].y - Cords[0].y ,Cords[1].z - Cords[0].z );
		Vector v = new Vector(Cords[2].x - Cords[0].x ,Cords[2].y - Cords[0].y ,Cords[2].z - Cords[0].z );
		double x=  ( (u.y * v.z) - (u.z * v.y )  );
		double y=  ( (u.z * v.x) - (u.x * v.z )  );
		double z=  ( (u.x * v.y) - (u.y * v.x )  );
		
		
		return new Vector(x,y,z);
		
	}
}
