package cheat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class LoadSave {
        char cells[][];
        String tiles;
        
	public void load(){
		cells = new char[Screen.CELL_COUNT][Screen.CELL_COUNT];
		File f = new File("ScrableData.txt");
                if(f.exists()==false)return;
		
		try {
			Scanner in = new Scanner(new FileInputStream(f));
			for (int i = 0; i < Screen.CELL_COUNT; i++) {
				String s = in.nextLine();
				for (int j = 0; j < s.length(); j++) {
					cells[i][j] = s.charAt(j);
				}
			}
                        tiles = in.nextLine();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void save(char [][] cells, String userTiles){
		File f = new File("ScrableData.txt");		
		try {
			PrintWriter out = new PrintWriter(f);
			for(int i = 0;i<Screen.CELL_COUNT;i++){
				for(int j = 0;j<Screen.CELL_COUNT;j++){
					out.write(cells[i][j]);
				}
				out.write("\n");
			}
                        out.write(userTiles+"\n");
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
