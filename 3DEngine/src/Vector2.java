import java.util.Comparator;

public  class Vector2 {
		  public double x,y;
		  Vector2(double xt,double yt) {
			  x= xt;
			  y=yt;
		  }
		  public void Print() {
			  System.out.println("x: "+x + "\ny: "+y );
		  }class comp implements Comparator<Vector2> {
			  
			    // Method
			    // Sorting in ascending order of name
			    public int compare(Vector2 a, Vector2 b)
			    {
			        if( a.x > b.x) {
			        	return 1;
			        	
			        }if( a.x < b.x) {
			        	return -1;
			        }
			        return 0;
			    }
			}
	}