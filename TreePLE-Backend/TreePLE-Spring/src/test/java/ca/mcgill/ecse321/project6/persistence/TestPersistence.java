package ca.mcgill.ecse321.project6.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.ecse321.project6.model.Tree;
import ca.mcgill.ecse321.project6.model.TreePLE;
import ca.mcgill.ecse321.project6.model.TreeState;
import ca.mcgill.ecse321.project6.model.User;

public class TestPersistence {
	
	public static final XmlConfig testXmlConfig = new XmlConfig();
	
	static {
		testXmlConfig.setDbFilename("output"+File.separator+"test.xml");
	}
	
	private TreePLE tp;

	@Before
	public void setUp() throws Exception {
		tp = new TreePLE();
		Tree t1 = new Tree(0, 0, 10, 10, "Pine", Tree.Municipality.VilleMarie, Tree.LandType.Municipal, new TreeState());
		Tree t2 = new Tree(1, 0, 30, 15, "Oak", Tree.Municipality.MontrealNord, Tree.LandType.Residential, new TreeState());
		
		User u1 = new User("Bob", "dummyhash", User.UserType.Resident);
		User u2 = new User("Bob", "dummyhash", User.UserType.Municipal);
		User u3 = new User("Bob", "dummyhash", User.UserType.Resident);
		
		tp.addTree(t1);
		tp.addTree(t2);
		tp.addUser(u1);
		tp.addUser(u2);
		tp.addUser(u3);
	}

	@After
	public void tearDown() throws Exception {
		tp.delete();
		new File(testXmlConfig.getDbFilename()).delete();
	}

	@Test
	public void testDbXml() {
		testDb(new DbXml(testXmlConfig));
	}
	
	/*
	@Test
	public void testDbMySql() {
		testDb(new DbMySql());
	}
	*/
	
	private void testDb(IDb db) {
		//save current model
		if(!db.saveToDb(tp)) {
			fail("Could not save file.");
		}
		
		assert(!tp.getUser(0).equals(tp.getUser(2)));
		
		tp.delete();
		
		tp = (TreePLE) db.loadFromDb();
		if(tp == null) {
			fail("Could not load object from file.");
		}
		
		assertEquals(2, tp.getTrees().size());
		assertEquals(3, tp.getUsers().size());
		assertEquals(Tree.Municipality.VilleMarie, tp.getTree(0).getMunicipality());
		assertEquals(Tree.Municipality.MontrealNord, tp.getTree(1).getMunicipality());
		assert(!tp.getTree(0).getSpecies().equals(tp.getTree(1).getSpecies()));
		
		assertEquals(tp.getUser(0).getName(), tp.getUser(1).getName());
		assertEquals(User.UserType.Resident, tp.getUser(0).getType());
		assertEquals(User.UserType.Municipal, tp.getUser(1).getType());
		assertEquals(User.UserType.Resident, tp.getUser(2).getType());
		assertEquals(tp.getUser(2).getName(), tp.getUser(0).getName());
		assert(tp.getUser(0).getId() != tp.getUser(2).getId());
	}

}
