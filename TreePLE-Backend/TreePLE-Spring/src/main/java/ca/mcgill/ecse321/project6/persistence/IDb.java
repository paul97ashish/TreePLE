package ca.mcgill.ecse321.project6.persistence;

public interface IDb {
	public boolean saveToDb(Object obj);
	public Object loadFromDb();
	
	public void clearAllData();
}