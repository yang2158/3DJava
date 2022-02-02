public class data{
		public int x,xe,y;
		public Shape obj;
		public data(int xCor, int yCor , Shape inputedObject) {
			
			x = xCor ;
			y= yCor;
			obj = inputedObject;
		}

		public data(int xCor,int xeC, int yCor , Shape inputedObject) {
			xe = xeC;
			x = xCor ;
			
			y= yCor;
			obj = inputedObject;
		}
	}