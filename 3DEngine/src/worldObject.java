import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class worldObject {
	public Vector3 pos= new Vector3(0,0,0);
	public Vector3 rot= new Vector3(0,0,0);
	LinkedList <Vector2> loadedTexCords = new LinkedList<Vector2>();
	
	LinkedList <Vector3> loadedVectors = new LinkedList<Vector3>();
	
	LinkedList <Vector3> loadedNormals = new LinkedList<Vector3>();
	public ArrayList <Triangle> loadedShapes = new ArrayList<Triangle>();
	
	public boolean loadFile(String file , Main a)  {
		
		HashMap<String,Integer> imageIDs= new HashMap<String,Integer>() ;
		try {
			Scanner mtl = new Scanner(new File(file.substring(0, file.length()-4) + ".mtl"));
			String obj = "~~~";
			String fln = "~~~";
			while (true) {
				if(!mtl.hasNextLine()) {
					System.out.println("Created Material "+ obj + " | "+ fln);
					imageIDs.put(obj, a.imageID(ImageIO.read(new File(fln))));
					break;
				}
				String content = mtl.nextLine();
				String[] con= content.split(" ");
				
					if(con[0].trim().equals("newmtl")) {
						if(obj != "~~~"&&fln != "~~~") {
							System.out.println("Created Material "+ obj + " | "+ fln);
							imageIDs.put(obj, a.imageID(ImageIO.read(new File(fln))));
						}
						obj = con[1].trim();
						fln = "~~~";
						
					}
					if(con[0].trim().equals("map_Kd")) {
						fln = content.split("Kd ")[1].trim();
					}
					
				
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

		BufferedImage img = null;
		
		try {
		    img = ImageIO.read(new File("texture.png"));
		} catch (IOException e) {
		}
		

		try {
			String curObj =" asdasda";
			String[] line = {"Random " , " Stuff " , "Temp"};
			while (buffReader.ready()) {
				line =buffReader.readLine().split(" ");
				if(line[0].equals("usemtl")) {
					curObj =line[1].trim();
				}
				if(line[0].equals("v")) {
					loadedVectors.add(new Vector3(Double.parseDouble(line[1]),Double.parseDouble(line[2]),Double.parseDouble(line[3])));

				}
				if(line[0].equals("vn")) {
					loadedNormals.add(new Vector3(Double.parseDouble(line[1]),Double.parseDouble(line[2]),Double.parseDouble(line[3])));

				}if(line[0].equals("vt")) {
					loadedTexCords.add(new Vector2(Double.parseDouble(line[1]),Double.parseDouble(line[2])));

				}
				if(line[0].equals("f")) {
						int top= line.length -1;
						int[] ninx =new int[top];
						int[] indexes = new int[top];
						int[] Textureindexes = new int[top];
						for(int i = 1 ; i <=top ;i++) {
							String[] content = line[i].split("/");
							
							ninx[i-1]= Integer.parseInt(content[2])-1;
							indexes[i-1] = Integer.parseInt(content[0])-1;
							Textureindexes[i-1] = Integer.parseInt(content[1])-1;
						}
						for(int i = 0 ; i <top-2;i++) {
							if(imageIDs.get(curObj)== null) {
								System.out.println(curObj);
							}
							loadedShapes.add(new Triangle(loadedVectors.get(indexes[0]),
									loadedVectors.get(indexes[i+1]),
									loadedVectors.get(indexes[i+2]),
									loadedNormals.get(ninx[0]),
									loadedNormals.get(ninx[i+1]),
									loadedNormals.get(ninx[i+2]),
									loadedTexCords.get(Textureindexes[0]),
									loadedTexCords.get(Textureindexes[i+1]),
									loadedTexCords.get(Textureindexes[i+2]),
									imageIDs.get(curObj.trim())
									
									));
						}

				}
				
				
			}
			buffReader.close();
			fileReader.close();
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return true;
		} 
		System.out.println(loadedVectors.size());
		return true;
	}
	public ArrayList<Triangle> getShapes() {
		return loadedShapes;
	}
}
