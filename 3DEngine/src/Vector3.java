
	public  class Vector3 {
		  public double x,y,z;
		  Vector3(double xt,double yt,double zt) {
			  x= xt;
			  y= yt;
			  z= zt;
		  }
		  public void set(double xt,double yt,double zt) {
			  x= xt;
			  y= yt;
			  z= zt;
		  }
		  @Override
		  public Vector3 clone() {
			  return new Vector3(x,y,z);
		  }
		  public void Print() {
			  System.out.println("x: "+x + "\ny: "+y + "\nz: "+ z);
		  }
		  public void addWithOrentation(Vector3 ore , double num) {
			  x+= Math.cos(ore.x)* num;
			  z+= Math.sin(ore.x)* num;
		  }
		  public double dot(Vector3 other) {
			  return this.x * other.x + this.y * other.y+ this.z * other.z; 
		  }public double getMagnitude() {
			  return Math.sqrt(x*x + y*y + z*z);
			  
		  }
		  public Vector3 crossProduct(Vector3 u) {
			  
				double xTemp=  ( (u.y *z) - (u.z * y )  );
				double yTemp=  ( (u.z * x) - (u.x * z )  );
				double zTemp=  ( (u.x * y) - (u.y * x )  );
				return new Vector3(xTemp,yTemp,zTemp);
		  }
		  public Vector3 muti (double num) {
			  x*= num;
			  y*=num;
			  z*=num;
			  return this;
		  }public Vector3 add (Vector3 num) {
			  x+= num.x;
			  y+=num.y;
			  z+=num.z;
			  return this;
		  }public Vector3 sub (Vector3 num) {
			  x-= num.x;
			  y-=num.y;
			  z-=num.z;
			  return this;
		  }
	}