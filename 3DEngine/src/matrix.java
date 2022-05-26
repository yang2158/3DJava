
public class matrix {
	
	public static void main(String[] args) {
		double [][ ] a =  {{1f},{0.7f},{0.4f}};
		double[][] b = {{1,3},{1,1},{1,1}};
		printArr(multiplyMatrices(multiplyMatrices(inverse2x2(multiplyMatrices(transpose(b), b)  ), transpose(b)),a));
		
	}
	public static void printArr(double[][] a) {
		for(int i =0 ; i < a.length;i++) {
			for(int j = 0 ; j <a[0].length ;j++) {
				System.out.print(a[i][j] + " ");
			}
			System.out.println();
		}
	}
	public double[][] vectorConvert(Vector3 p0,Vector3 p1,Vector3 p2){
		double[][] b = {{p0.x -p2.x,p1.x-p2.x},{p0.y-p2.y,p1.y - p2.y},{p0.z-p2.z,p1.z-p2.z}};
		return b;
	}
	public double[][] leastSquares (double[][] a   , double[][] b) {
		return multiplyMatrices(multiplyMatrices(inverse2x2(multiplyMatrices(transpose(a), a)  ), transpose(a)),b);
		
		
	}
	public static double[][] inverse2x2(double[][] a){
		double v = 1/ (a[0][0] * a[1][1] -a[1][0] * a[0][1]);
		double [][] inverted = {
				{v*a[1][1] ,v*(-a[0][1] )},
				{v*(-a[1][0]) ,v*a[0][0] }
		};
		return inverted;
		
	}
	public static double[][] multiplyMatrices(double[][] firstMatrix, double[][] secondMatrix) {
		int r1 = firstMatrix.length;
		int c1 = firstMatrix[0].length;
		int c2 = secondMatrix[0].length;
		
		
        double[][] product = new double[r1][c2];
        for(int i = 0; i < r1; i++) {
            for (int j = 0; j < c2; j++) {
                for (int k = 0; k < c1; k++) {
                    product[i][j] += firstMatrix[i][k] * secondMatrix[k][j];
                }
            }
        }

        return product;
    }
	
	public static double[][] transpose(double[][] a) {
		double[][] b = new double[a[0].length][a.length];
		for ( int i =0 ; i < a.length; i++)
			for (int j = 0; j <a[0].length; j++) {
				b[j][i] = a[i][j];
 			}
		return b;
	}
}
