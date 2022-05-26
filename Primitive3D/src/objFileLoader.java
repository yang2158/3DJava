import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class objFileLoader {

	LinkedList <Vector> loadedVectors = new LinkedList<Vector>();

	LinkedList <Vector> loadedNormals = new LinkedList<Vector>();
	Queue <Shape> loadedShapes = new LinkedList<Shape>();
	public boolean loadFile(String file )  {
		FileReader fileReader;
		try {
			fileReader = new FileReader(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		// Convert fileReader to
		// bufferedReader
		BufferedReader buffReader = new BufferedReader(fileReader);


		try {
			String[] line = {"Random " , " Stuff " , "Temp"};
			while (buffReader.ready()) {
				line =buffReader.readLine().split(" ");
				if(line[0].equals("v")) {
					loadedVectors.add(new Vector(Double.parseDouble(line[1]),Double.parseDouble(line[2]),Double.parseDouble(line[3])));

				}if(line[0].equals("vn")) {
					loadedNormals.add(new Vector(Double.parseDouble(line[1]),Double.parseDouble(line[2]),Double.parseDouble(line[3])));

				}
				if(line[0].equals("f")) {
						int top= line.length -1;
						int ninx =0;
						int[] indexes = new int[top];
						for(int i = 1 ; i <=top ;i++) {
							String[] content = line[i].split("/");
							ninx= Integer.parseInt(content[2])-1;
							indexes[i-1] = Integer.parseInt(content[0])-1;
						}
						for(int i = 0 ; i <top-2;i++) {

							loadedShapes.add(new Shape(loadedVectors.get(indexes[0]),
									loadedVectors.get(indexes[i+1]),
									loadedVectors.get(indexes[i+2]),loadedNormals.get(ninx)
									));
						}

				}
				
				
			}
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return true;
		} 
		System.out.println(loadedVectors.size());
		return true;
	}
	public Queue<Shape> getShapes() {
		return loadedShapes;
	}
}
