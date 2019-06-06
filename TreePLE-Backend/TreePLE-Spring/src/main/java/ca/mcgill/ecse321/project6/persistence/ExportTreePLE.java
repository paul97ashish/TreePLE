package ca.mcgill.ecse321.project6.persistence;

import java.io.File;
import java.io.IOException;

import ca.mcgill.ecse321.project6.model.Tree;
import ca.mcgill.ecse321.project6.model.TreePLE;

import java.io.FileWriter;

public class ExportTreePLE {
	public static boolean exportTreesToCSV(String filename, TreePLE tp) {
		File file = new File(filename);
		if(file.exists()) {
			System.err.println("Export to CSV: "+filename+" already exists");
			return false;
		}
		try {
			file.createNewFile();
			
			FileWriter writer = new FileWriter(file);
			writer.write("ID\tLatitude\tLongitude\tHeight (m)\tCanopy (m)\tSpecies\tStatus\tHealth\tLand Type\tMunicipality\n");
			for(Tree t : tp.getTrees()) {
				writer.write(t.getId()+"\t"+t.getLatitude()+"\t"+
						t.getLongitude()+"\t"+t.getHeightMeters()+"\t"
						+t.getCanopyDiameterMeters()+"\t"+t.getSpecies()+"\t"+
						t.getState().getStatus()+"\t"+t.getState().getHealth()+"\t"+
						t.getLand()+"\t"+t.getMunicipality()+"\n");
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
