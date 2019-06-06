package ca.mcgill.ecse321.project6.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.springframework.stereotype.Repository;

import com.thoughtworks.xstream.XStream;

import ca.mcgill.ecse321.project6.model.Tree;
import ca.mcgill.ecse321.project6.model.TreePLE;
import ca.mcgill.ecse321.project6.model.User;

@Repository
public class DbXml implements IDb {

	private XStream xstream;
	private XmlConfig xmlConfig;

	public DbXml(XmlConfig xmlConfig) {
		this.xmlConfig = xmlConfig;
		initializeXStream();
	}

	private void tryCreateDb() {
		File file = new File(xmlConfig.getDbFilename());
		
		if (!file.exists()) {
			File parentFile = file.getParentFile();
			if (parentFile != null) {			
				parentFile.mkdirs();
			}
			try {
				file.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException("Could not create db file: " + xmlConfig.getDbFilename(), e);
			}
		}
	}

	private void initializeXStream() {
		xstream = new XStream();

		xstream.setMode(XStream.ID_REFERENCES);

		xstream.alias("manager", TreePLE.class);
		xstream.alias("user", User.class);
		xstream.alias("tree", Tree.class);

		File file = new File(xmlConfig.getDbFilename());
		if (!file.exists())
			saveToDb(new TreePLE());
	}

	public boolean saveToDb(Object obj) {
		String xml = xstream.toXML(obj);

		try {
			File file = new File(xmlConfig.getDbFilename());
			
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

	public Object loadFromDb() {
		try {
			Reader reader = new BufferedReader(new FileReader(xmlConfig.getDbFilename()));
			return xstream.fromXML(reader);
		} catch (IOException e) {
			return null;
		}
	}

	public void clearAllData() {
		File clear = new File(xmlConfig.getDbFilename());
		if (!clear.exists()) {
			return;
		}

		FileWriter clearer;
		try {
			clearer = new FileWriter(clear, false);
			clearer.write("");
			clearer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
