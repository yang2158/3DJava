import java.awt.Color;
import java.io.File;

public class Triangle {
	public Vector2[] TextureCords= new Vector2[3];
	public Vector3[] Cords= new Vector3[3];
	public Vector3[] camCords= new Vector3[3];
	Vector3[] normal=  new Vector3[3];
	int imageID =0;
	Color trueColor = new Color(69,8,69); // WHAT YOU SEE IS NOT WHAT YOU EXPECT IS IT NOW
	Color color = new Color( (int)(Math.random()*255) ,(int)(Math.random()*255),(int)(Math.random()*255));
	
	public Triangle( Vector3 p1, Vector3 p2,Vector3 p3) {
		/*if(type.equals("Triangle")) {/*
			addCords(p1);
			addCords(p2);
			addCords(p3);*/
		Cords[0] = p1;
		Cords[1] = p2;
		Cords[2] = p3;

		//}
	}public Triangle( Vector3 p1, Vector3 p2,Vector3 p3, Vector3 n) {
		/*if(type.equals("Triangle")) {/*
		addCords(p1);
		addCords(p2);
		addCords(p3);*/
		Cords[0] = p1;
		Cords[1] = p2;
		Cords[2] = p3;

		//}
	}
	public Triangle( Vector3 p1, Vector3 p2,Vector3 p3, Vector3 n,Vector3 n1,Vector3 n2, Vector2 t1 , Vector2 t2 , Vector2 t3 , int iD ) {
		/*if(type.equals("Triangle")) {/*
		addCords(p1);
		addCords(p2);
		addCords(p3);*/
		Cords[0] = p1;
		Cords[1] = p2;
		Cords[2] = p3;
		normal[0] = n;
		normal[1] = n;
		normal[2] = n;
		TextureCords[0]= t1;
		TextureCords[1]= t2;
		TextureCords[2]= t3;
		imageID = iD;

		//}
	}
	public Vector3 getnormal (double w1,double w2,double w3) {
		return new Vector3(normal[0].x*w1 + normal[1].x*w2 + normal[2].x*w3,normal[0].y*w1 + normal[1].y*w2 + normal[2].y*w3,normal[0].z*w1 + normal[1].z*w2 + normal[2].z*w3);
		
	}
	public Vector3 getPos (double w1,double w2,double w3) {
		return new Vector3(
				Cords[0].x*w1 + Cords[1].x*w2 + Cords[2].x*w3,
				Cords[0].y*w1 + Cords[1].y*w2 + Cords[2].y*w3,
				Cords[0].z*w1 + Cords[1].z*w2 + Cords[2].z*w3
				);
		
	}
	public Triangle(Triangle n) {// copies from Triangle
		Cords[0] = n.Cords[0];
		Cords[1] = n.Cords[1];
		Cords[2] = n.Cords[2];
		color  =n.color;
	}
	public Vector3 getNormal() {// if three points 
		Vector3 u = new Vector3(camCords[1].x - camCords[0].x ,camCords[1].y - camCords[0].y ,camCords[1].z - camCords[0].z );
		Vector3 v = new Vector3(camCords[2].x - camCords[0].x ,camCords[2].y - camCords[0].y ,camCords[2].z - camCords[0].z );
		double x=  ( (u.y * v.z) - (u.z * v.y )  );
		double y=  ( (u.z * v.x) - (u.x * v.z )  );
		double z=  ( (u.x * v.y) - (u.y * v.x )  );


		return new Vector3(x,y,z);

	}
}
