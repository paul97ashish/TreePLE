package ca.mcgill.ecse321.project6.persistence;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "xml")
public class XmlConfig {
	/**
	 * Represents the filename in which the XML database is stored
	 */
	private String dbFilename;
	
	public String getDbFilename() { return dbFilename; }
	public void setDbFilename(String filename) { dbFilename = filename; }
}
