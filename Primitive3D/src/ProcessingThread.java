import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;



public class ProcessingThread extends Thread{
	public Vector r, camera ;
	Queue<outputData> dataDone ;
	ArrayList<data> dataList;
	public boolean done= true;
	public boolean outputted= true;
	double globalInc ;
	public Vector globalCamera;
	public  Vector globalR;
	public double globalF;
	public double globalSizeX;
	private int threadNum;
	private int numThreads;
	public boolean isDone() {
		return done;
	}
	public ProcessingThread(int id , int size , Vector r, Vector cam , double f , double sizex) {

		globalCamera = cam;
		globalR = r;
		globalSizeX = sizex;
		globalF = f;
		threadNum = id;
		numThreads = size;


	}
	public void init(int id , int size , Vector r, Vector cam , double f , double sizex) {

		globalCamera = cam;
		globalR = r;
		globalSizeX = sizex;
		globalF = f;
		globalInc = globalSizeX/globalR.x;
		threadNum = id;
		numThreads = size;
	}
	// TODO : Make it so instead of taking pixels take rows and find a way to use the previous result to get the next pixel on the row
	public void giveData(ArrayList<data> queue) {
		dataList = new ArrayList<>(queue);

	}
	public Queue<outputData> getDone() {
		return dataDone;
	}
	public void run() {
		dataDone = new LinkedList<>();
		int size =dataList.size();
		if(!dataList.isEmpty()) {
			for(int i = threadNum ; i < size ; i+=numThreads) {
				data temp = dataList.get(i);
				double tempY = globalSizeX*(-temp.y/globalR.y +0.5) ;
				double tempX = globalSizeX*(temp.x/globalR.x   - 0.5);
				if(temp.y > 0 &&temp.y <globalR.y)
				for(int x =temp.x ; x < temp.xe ; x++  ) {
					if( x>= globalF  &&x< globalR.x ) {
						
						double num =getPixelOnObj(tempX , tempY ,temp.obj  );
						tempX += globalInc;
						if(num>=0)
							dataDone.add(new outputData(x,temp.y ,num,temp.obj.color) );}
				}
				/**/

			}
		}
	}





	//Gets the Distance given a pixel and a obj and the system variables
	public double getPixelOnObj(double x, double tempY, Shape obj  ) {

		Vector PixelPos3D = new Vector (globalF,tempY,x) ;// The 2d plane dist , 
		double magnitude = Math.sqrt(PixelPos3D.x *PixelPos3D.x + PixelPos3D.y *PixelPos3D.y + PixelPos3D.z * PixelPos3D.z);

		//return experimental(new Vector(0,globalCamera.y + PixelPos3D.y,globalCamera.z + PixelPos3D.z ), obj.Cords[0] , obj.getNormal()); //Doesn't work (please look at comment on the function)
		return intersect (globalCamera, PixelPos3D , obj.Cords[1] , obj.getNormal())/ magnitude;
	} 
	double intersect(Vector p , Vector v , Vector n , Vector d) // point , vector , plane point , direction (ikr why did this dumbass place n as the point and not the normal. Apparently this dumbass is me)
	{
		return -(d.dot(p) - n.dot(d)) / d.dot(v);// Line plane intersection
	}double experimental( Vector total, Vector plane , Vector normal) // point , vector , plane point , direction
	{// Implemented cuz web told be so but after careful thinking this is used for orthographic  projection as it's assuming it knows the x and y cordinates
		if(normal.x !=0)
			return -( total.dot(normal) - normal.dot(plane)) / (normal.x);
		return Double.MAX_VALUE;
	}
}
