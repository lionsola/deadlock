package client.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This class (and this package) is supposed to load client-side textual
 * and game-play data. The role might be delegated to another class if
 * there's too little data.
 */
public class Class {
	private static ArrayList<Class> classes;
	
	public static Class get(int id) {
		return classes.get(id);
	}
	
	public static int getClassNo() {
		return classes.size();
	}
	
	private int id;
	private String name;
	private String description; 
	
	public static void init() {
		if (classes==null){
			classes = new ArrayList<Class>();
			Scanner sc = null;
			try {
				sc = new Scanner(new File("resource/character/classInfo"));
				while(sc.hasNext()) {
					Class cs = new Class();
					cs.id = Integer.parseInt(sc.nextLine());
					cs.name = sc.nextLine();
					cs.description = sc.nextLine();
					classes.add(cs);
				}
				sc.close();
			} catch (IOException e) {
				System.err.println("Fail to load class stats");
				System.exit(-1);
				e.printStackTrace();
			} finally {
				if (sc!=null) {
					sc.close();
				}
			}
			
		}
	}
	
	public static void free () {
		classes = null;
	}

	public int getId() { return id; }
	
	public String getName() { return name; }
	
	public String getDescription() { return description; }
}
