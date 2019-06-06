package ca.mcgill.ecse321.project6.admin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import com.thoughtworks.xstream.XStream;

import ca.mcgill.ecse321.project6.model.Tree;
import ca.mcgill.ecse321.project6.model.TreePLE;
import ca.mcgill.ecse321.project6.model.User;

public class AdminPersistence {
	private XStream xstream;
	private String filename;
	
	public AdminPersistence(String filename) {
		this.filename = filename;
		initializeXStream();
	}
	
	private void initializeXStream() {
		xstream = new XStream();
		
		xstream.setMode(XStream.ID_REFERENCES);
		xstream.alias("manager", TreePLE.class);
		xstream.alias("user", User.class);
		xstream.alias("tree", Tree.class);
	}
	
	public Object loadFromDb() {
		try {
			Reader reader = new BufferedReader(new FileReader(filename));
			return xstream.fromXML(reader);
		} catch (IOException e) {
			return null;
		}
	}
	
	private void tryCreateDb() {
		File file = new File(filename);
		
		if (!file.exists()) {
			File parentFile = file.getParentFile();
			if (parentFile != null) {			
				parentFile.mkdirs();
			}
			try {
				file.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException("Could not create db file: " + filename, e);
			}
		}
	}
	
	public boolean saveToDb(Object obj) {
		String xml = xstream.toXML(obj);
		try {
			File file = new File(filename);
			
			tryCreateDb();
			
			try (Writer writer = new BufferedWriter(new FileWriter(file))) {
				writer.write(xml);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
