
	public  class Vector {
		  public double x,y,z;
		  Vector(double xt,double yt,double zt) {
			  x= xt;
			  y= yt;
			  z= zt;
		  }
		  public void Print() {
			  System.out.println("x: "+x + "\ny: "+y + "\nz: "+ z);
		  }
		  public void addWithOrentation(Vector ore , double num) {
			  x+= Math.cos(ore.x)* num;
			  z+= Math.sin(ore.x)* num;
		  }
		  public double dot(Vector other) {
			  return this.x * other.x + this.y * other.y+ this.z * other.z; 
		  }
		  public Vector crossProduct(Vector u) {
			  
				double xTemp=  ( (u.y *z) - (u.z * y )  );
				double yTemp=  ( (u.z * x) - (u.x * z )  );
				double zTemp=  ( (u.x * y) - (u.y * x )  );
				return new Vector(xTemp,yTemp,zTemp);
		  }
		  public Vector muti (double num) {
			  x*= num;
			  y*=num;
			  z*=num;
			  return this;
		  }public Vector add (Vector num) {
			  x+= num.x;
			  y+=num.y;
			  z+=num.z;
			  return this;
		  }public Vector sub (Vector num) {
			  x-= num.x;
			  y-=num.y;
			  z-=num.z;
			  return this;
		  }
	}