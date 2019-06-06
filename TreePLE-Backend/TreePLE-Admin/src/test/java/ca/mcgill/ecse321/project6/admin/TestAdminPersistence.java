package ca.mcgill.ecse321.project6.admin;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mcgill.ecse321.project6.model.Tree;
import ca.mcgill.ecse321.project6.model.TreePLE;
import ca.mcgill.ecse321.project6.model.TreeState;
import ca.mcgill.ecse321.project6.model.User;
import ca.mcgill.ecse321.project6.security.Password;

public class TestAdminPersistence {
	private static String testFilename;
	private TreePLE tp;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		testFilename = "output"+File.separator+"test.xml";
	}

	@Before
	public void setUp() throws Exception {
		tp = new TreePLE();
		Tree t1 = new Tree(0, 0, 10, 10, "Pine", Tree.Municipality.VilleMarie, Tree.LandType.Municipal, new TreeState());
		Tree t2 = new Tree(1, 0, 30, 15, "Oak", Tree.Municipality.MontrealNord, Tree.LandType.Residential, new TreeState());
		
		User u1 = new User("Bob", Password.getSaltedHash("asdfasdf"), User.UserType.Resident);
		User u2 = new User("Bob", Password.getSaltedHash("asdfasdf"), User.UserType.Municipal);
		User u3 = new User("Bob", Password.getSaltedHash("asdfasdf"), User.UserType.Resident);
		
		tp.addTree(t1);
		tp.addTree(t2);
		tp.addUser(u1);
		tp.addUser(u2);
		tp.addUser(u3);
	}

	@After
	public void tearDown() throws Exception {
		tp.delete();
		new File(testFilename).delete();
	}

	@Test
	public void testAdminPersistence() {
		AdminPersistence admin = new AdminPersistence(testFilename);
		assert(admin != null);
	}

	@Test
	public void testSaveToDb() {
		AdminPersistence admin = new AdminPersistence(testFilename);
		assert(admin.saveToDb(tp));
	}
	
	@Test
	public void testLoadFromDb() {
		AdminPersistence admin = new AdminPersistence(testFilename);
		admin.saveToDb(tp);
		tp.delete();
		tp = (TreePLE) admin.loadFromDb();
		
		assert(tp != null);
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
