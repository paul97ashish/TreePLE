package ca.mcgill.ecse321.project6.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.BiConsumer;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.ecse321.project6.model.Address;
import ca.mcgill.ecse321.project6.model.Tree;
import ca.mcgill.ecse321.project6.model.TreePLE;
import ca.mcgill.ecse321.project6.model.TreeState;
import ca.mcgill.ecse321.project6.model.User;
import ca.mcgill.ecse321.project6.model.User.UserType;
import ca.mcgill.ecse321.project6.persistence.DbXml;
import ca.mcgill.ecse321.project6.persistence.IDb;
import ca.mcgill.ecse321.project6.persistence.TestPersistence;
import ca.mcgill.ecse321.project6.service.authentication.AuthenticationException;
import ca.mcgill.ecse321.project6.service.authentication.AuthenticationService;
import ca.mcgill.ecse321.project6.service.authentication.InvalidSessionException;

public class TestTreePLEService {
	
	private TreePLE tp;
	private IDb db;
	
	private TreePLEService service;
	
	@AfterClass
	public static void tearDownAfterClass() {
		File file = new File(TestPersistence.testXmlConfig.getDbFilename());
		file.delete();
	}
	
	@Before
	public void setUp() throws Exception {
		db = new DbXml(TestPersistence.testXmlConfig);
		db.saveToDb(tp = new TreePLE());
		
		service = new TreePLEService(tp, db, new AuthenticationService(tp));
	}

	@After
	public void tearDown() throws Exception {
		tp.delete();
		db.clearAllData();
	}
	
	@Test
	public void testCreateUser() {
		assertEquals(0, tp.getUsers().size());
		try {
			service.createUser("Bob", "asdfasdf", UserType.Resident);
		} catch (InvalidInputException e) {
			fail("Users could not be created.");
		}
		assertEquals(tp.getUser(0).getName(), "Bob");
		assertEquals(tp.getUser(0).getType(), User.UserType.Resident);
	}
	
	@Test
	public void testCreateUserFail() {
		assertEquals(0, tp.getUsers().size());
		
		String error = null;
		
		try {
			service.createUser("", "asdfasdf", UserType.Resident);
		} catch (InvalidInputException e) {
			error = e.getMessage();
		}
		
		assert(error != null);
		
		error = null;
		
		try {
			service.createUser(" ", "asdfasdf", UserType.Resident);
		} catch (InvalidInputException e) {
			error = e.getMessage();
		}
		assert(error != null);

		assertEquals(0, tp.getUsers().size());
	}
	
	@Test
	public void testCreateMunicipal(){
		assertEquals(0, tp.getUsers().size());
		try {
			service.createUser("Bob", "asdfasdf", UserType.Municipal);
		} catch (Exception e) {
			fail("Users could not be created.");
		}
		assertEquals(tp.getUser(0).getName(), "Bob");
		assertEquals(tp.getUser(0).getType(), User.UserType.Municipal);
	}

	@Test
	public void testCreateMunicipalFail(){assertEquals(0, tp.getUsers().size());
		
		String error = null;
	
		try {
			service.createUser("", "asdfasdf", UserType.Municipal);
		} catch (Exception e) {
			error = e.getMessage();
		}
		
		assert(error != null);
		
		error = null;
		try {
			service.createUser(" ", "asdfsadf", UserType.Municipal);
		} catch (Exception e) {
			error = e.getMessage();
		}
		assert(error != null);

		assertEquals(0, tp.getUsers().size());
	}

	@Test
	public void testGetUsers() {
		assertEquals(0, tp.getUsers().size());
		String[] names = {"Bill", "Bob", "Some Other Person"};
		
		HashMap<Integer, String> userIdToName = new HashMap<>();
		
		for(String name : names) {
			try {
				userIdToName.put(service.createUser(name, "asdfasdf", UserType.Resident).getId(), name);
			} catch(InvalidInputException e) {
				fail();
			}
		}
		
		HashMap<Integer, User> users = service.getUsers();
		assertEquals(users.size(), names.length);
		userIdToName.forEach(new BiConsumer<Integer, String>() {
			@Override
			public void accept(Integer id, String name) {
				assertEquals(userIdToName.get(id), users.get(id).getName());
			}
		});
	}

	@Test
	public void testGetTrees() {
		assertEquals(0, tp.getTrees().size());
		User u = null;
		Tree t1, t2, t3;
		try{
			u = service.createUser("Bob", "asdfasdf", UserType.Municipal);
			String sessionGuid = service.login(u.getName(), "asdfasdf");
			t1 = service.addTree(sessionGuid, 3, 3, 3, 3, "Oak", Tree.Municipality.VilleMarie, Tree.LandType.Municipal);
			t2 = service.addTree(sessionGuid, 3, 3, 3, 3, "Maple", Tree.Municipality.Anjou, Tree.LandType.Residential);
			t3 = service.addTree(sessionGuid, 3, 3, 3, 3, "Pine", Tree.Municipality.LaSalle, Tree.LandType.Park);
		}
		catch(Exception e){
			fail(e.getMessage());
			return;
		}
		assertEquals(3, service.getTrees().size());
		assertEquals(t1.getSpecies(), service.getTrees().get(t1.getId()).getSpecies());
		assertEquals(t2.getSpecies(), service.getTrees().get(t2.getId()).getSpecies());
		assertEquals(t3.getSpecies(), service.getTrees().get(t3.getId()).getSpecies());
	}
	
	@Test
	public void testAddAddress() {
		assertEquals(0, tp.getUsers().size());
		
		User u;
		String sessionGuid;
		try {
			u = service.createUser("Bob", "asdfasdf");
			sessionGuid = service.login(u.getName(), "asdfasdf");
		} catch(InvalidInputException | AuthenticationException e) {
			fail(e.getMessage());
			return;
		}
		
		Address a = new Address(123, "Fake Street", "A1A1A1", 7, 42);
		try {
			u = service.addAddressToUser(sessionGuid, a.getStreetNumber(), a.getStreetName(), a.getPostalCode(), a.getLatitude(), a.getLongitude());
		} catch (Exception e) {
			fail(e.getMessage());
		}
		assertEquals(u.getAddress(), a);

	}
	
	@Test 
	public void testGetAddress() {
		assertEquals(0, tp.getUsers().size());
		
		User u;
		try {
			u = service.createUser("Bob", "asdfasdf", UserType.Resident);
		} catch(InvalidInputException e) {
			fail();
			return;
		}
		Address a = new Address(123, "Fake Street", "A1A1A1", 7, 42);
		try {
			String sessionGuid = service.login(u.getName(), "asdfasdf");
			service.addAddressToUser(sessionGuid, a.getStreetNumber(), a.getStreetName(), a.getPostalCode(), a.getLatitude(), a.getLongitude());
		} catch (InvalidInputException e) {
			fail("Could not add address");
		} catch (AuthenticationException e) {
			fail("Could not authenticate user");
		} catch (InvalidSessionException e) {
			// TODO Auto-generated catch block
			fail("User authenticated but session invalid");
		}
		User u2 = service.getUsers().get(u.getId());
		assertEquals(u2.getAddress(), a);
		
	}
	@Test
	public void testAddAddressFail(){
		assertEquals(0, tp.getUsers().size());
		
		User u;
		String sessionGuid;
		try {
			u = service.createUser("Bob", "asdfasdf", UserType.Resident);
			sessionGuid = service.login(u.getName(), "asdfasdf");
		} catch(InvalidInputException e) {
			fail(e.getMessage());
			return;
		} catch (AuthenticationException e) {
			fail(e.getMessage());
			return;
		}

		Address a = new Address(123, "Fake Street", "A1A1A1", 7, 42);

		boolean[] exceptionsThrown = {false, false, false};
		try {
			service.addAddressToUser(sessionGuid, -5, a.getStreetName(), a.getPostalCode(), a.getLatitude(), a.getLongitude());
		} catch (Exception e) {
			assert(e.getMessage().equals("Must use a valid street number."));
			exceptionsThrown[0] = true;
		}
		
		try {
			service.addAddressToUser(sessionGuid, a.getStreetNumber(), null, a.getPostalCode(), a.getLatitude(), a.getLongitude());
		} catch (Exception e) {
			assert(e.getMessage().equals("Street name must be specified."));
			exceptionsThrown[1] = true;
		}
		
		try {
			service.addAddressToUser(sessionGuid, a.getStreetNumber(), a.getStreetName(), "YXUKKLM", a.getLatitude(), a.getLongitude());
		} catch (Exception e) {
			assert(e.getMessage().equals("Must enter a valid postal code."));
			exceptionsThrown[2] = true;
		}
		
		for(boolean b : exceptionsThrown) {	// verify that all exceptions were thrown
			assert(b);
		}	
	}

	@Test
	public void testAddTree() {
		assertEquals(0, tp.getUsers().size());
		assertEquals(0, tp.getTrees().size());
		
		User u = null;
		Tree t = null;
		
		try {
			u = service.createUser("Bob", "asdfasdf", UserType.Resident);
			String sessionGuid = service.login(u.getName(), "asdfasdf");
			service.addAddressToUser(sessionGuid, 123, "Fake Street", "X1X 1X1", 3, 3);
			t = service.addTree(sessionGuid, 3, 3, 3, 3, "Oak", Tree.Municipality.VilleMarie, Tree.LandType.Municipal);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		assertEquals(1, t.getEntries().size());
		assertEquals(1, tp.getTrees().size());
		assertEquals("Oak", tp.getTree(0).getSpecies());
	}
	
	@Test
	public void testAddTreeFail() {
		assertEquals(0, tp.getUsers().size());
		assertEquals(0, tp.getTrees().size());
		User u = null;
		User u2 = null;
		String sessionGuid;
		String sessionGuid2; // Electric Boogaloo
		
		try {
			u = service.createUser("Bob", "asdfasdf", UserType.Municipal);
			sessionGuid = service.login(u.getName(), "asdfasdf");
			u2 = service.createUser("Bono", "asdfasdf", UserType.Resident);
			sessionGuid2 = service.login(u2.getName(), "asdfasdf");
		} catch (Exception e) {
			fail(e.getMessage());
			return; // return will not be reached, just here to avoid initialization warnings 
		}
		
		try { 
			try {
				service.addTree(sessionGuid, 3, 3, 3, -3, "Oak", Tree.Municipality.LaSalle, Tree.LandType.Residential);
				fail("Expected exception not thrown");
			} catch (InvalidInputException e) {
				assert(e.getMessage().equals("Measurements must be nonnegative."));
			}
			try {
				service.addTree(sessionGuid, 3, 3, 3, 3, null, Tree.Municipality.LaSalle, Tree.LandType.Residential);
				fail("Expected exception not thrown");
			} catch (InvalidInputException e) {
				assert(e.getMessage().equals("Tree species must be specified."));
			}
			try {
				service.addTree(sessionGuid, 3, 3, 3, 3, "", Tree.Municipality.LaSalle, Tree.LandType.Residential);
				fail("Expected exception not thrown");
			} catch (InvalidInputException e) {
				assert(e.getMessage().equals("Tree species must be specified."));
			}
			try {
				service.addTree(sessionGuid, 3, 3, 3, 3, "Oak", Tree.Municipality.LaSalle, null);
				fail("Expected exception not thrown");
			} catch (InvalidInputException e) {
				assert(e.getMessage().equals("Land type must be specified."));
			}
			try {
				service.addTree(sessionGuid, 91, 3, 3, 3, "Oak", Tree.Municipality.LaSalle, Tree.LandType.Residential);
				fail("Expected exception not thrown");
			} catch (InvalidInputException e) {
				assert(e.getMessage().equals("Latitude is out of range."));
			}
			try {
				service.addTree(sessionGuid, 3, 181, 3, 3, "Oak", Tree.Municipality.LaSalle, Tree.LandType.Residential);
				fail("Expected exception not thrown");
			} catch (InvalidInputException e) {
				assert(e.getMessage().equals("Longitude is out of range."));
			}
			try {
				service.addTree(null, 14, 43, 2, 3, "Maple", Tree.Municipality.Anjou, Tree.LandType.Park);
				fail("Expected exception not thrown");
			} catch (InvalidInputException e) {
				fail();
			} catch (InvalidSessionException e) {
				// Expected
			}
			try {
				service.addTree(sessionGuid, 14, 43, -4, 3, "Maple", Tree.Municipality.Anjou, Tree.LandType.Park);
			} catch (InvalidInputException e) {
				assert(e.getMessage().equals("Measurements must be nonnegative."));
			}
			try {
				service.addTree(sessionGuid, 14, 43, 2, 3, "Maple", null, Tree.LandType.Park);
				fail("Expected exception not thrown");
			} catch (InvalidInputException e) {
				assert(e.getMessage().equals("Municipality must be specified."));
			}
			try {
				service.addTree(sessionGuid2, 3, 4, 2, 3, "Maple", Tree.Municipality.Anjou, Tree.LandType.Park);
				fail("Expected exception not thrown");
			} catch (InvalidInputException e) {
				assert(e.getMessage().equals("User does not have an associated address."));
			}
			try {
				service.addAddressToUser(sessionGuid2, 123, "Fake Street", "X1X 1X1", 3, 3);
				service.addTree(sessionGuid2, 3, 4, 2, 3, "Maple", Tree.Municipality.Anjou, Tree.LandType.Park);
				fail("Expected exception not thrown");
			} catch (InvalidInputException e) {
				assert(e.getMessage().equals("Location does not appear to be on user's property."));
			}
		} catch (InvalidSessionException e) {
			fail();
		}
	}
	
	@Test
	public void testUpdateTreeStatus() {
		assertEquals(0, tp.getTrees().size());
		assertEquals(0, tp.getUsers().size());
		
		User u = null;
		String sessionGuid;
		Tree t = null;
		
		try {
			u = service.createUser("Bob", "asdfasdf", UserType.Municipal);
			sessionGuid = service.login(u.getName(), "asdfasdf");
			t = service.addTree(sessionGuid, 14, 43, 2, 3, "Maple", Tree.Municipality.Anjou, Tree.LandType.Park);
		} catch (Exception e) {
			fail();
			return;
		}
		assertEquals(TreeState.Health.Healthy, t.getState().getHealth());
		TreeState ts = new TreeState();
		ts.setHealth(TreeState.Health.Dead);
		try {
			service.updateTreeState(sessionGuid, t.getId(), ts);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		Tree t2 = tp.getTree(0);
		assertEquals(t2.getId(), t.getId());
		assertEquals(2, t2.getEntries().size());
		assertEquals(t2.getState(), ts);
		assertEquals(TreeState.Health.Dead, t2.getState().getHealth());
	}
	
	@Test
	public void testUpdateTreeStatusFail() {
		assertEquals(0, tp.getTrees().size());
		assertEquals(0, tp.getUsers().size());
		User u = null;
		String sessionGuid;
		User u2 = null;
		String sessionGuid2;
		Tree t = null;
		
		try {
			u = service.createUser("Bob", "asdfasdf", UserType.Municipal);
			sessionGuid = service.login(u.getName(), "asdfasdf");
			u2 = service.createUser("Bill", "asdfasdf", UserType.Resident);
			sessionGuid2 = service.login(u2.getName(), "asdfasdf");
			t = service.addTree(sessionGuid, 14, 43, 2, 3, "Maple", Tree.Municipality.Anjou, Tree.LandType.Park);
		} catch (Exception e) {
			fail();
			return;
		}
		
		TreeState ts = new TreeState();
		
		try {
			try {
				service.updateTreeState(sessionGuid2, t.getId(), ts);
				fail("Expected exception not thrown");
			} catch (InvalidInputException e) {
				assert(e.getMessage().equals("Only municipal users can do this."));
			}
			try {
				service.updateTreeState(sessionGuid, t.getId(), null);
				fail("Expected exception not thrown");;
			} catch (InvalidInputException e) {
				assert(e.getMessage().equals("New state must be specified."));
			}
		} catch (InvalidSessionException e) {
			fail();
		}
	}
	
	@Test
	public void testCutDownTree() {
		assertEquals(0, tp.getTrees().size());
		assertEquals(0, tp.getUsers().size());
		
		User u = null;
		String sessionGuid;
		Tree t = null;
		
		try {
			u = service.createUser("Bob", "asdfasdf", UserType.Resident);
			sessionGuid = service.login(u.getName(), "asdfasdf");
			service.addAddressToUser(sessionGuid, 123, "Fake Street", "X1X 1X1", 14, 43);
			t = service.addTree(sessionGuid, 14, 43, 2, 3, "Maple", Tree.Municipality.Anjou, Tree.LandType.Park);
		} catch (Exception e) {
			fail(e.getMessage());
			return;
		}
		
		assertEquals(1, tp.getTrees().size());
		assertEquals(t.getState().getStatus(), TreeState.Status.Planted);
		
		try {
			service.cutDownTree(sessionGuid, t.getId());
		} catch(Exception e) {
			fail();
		}
		assertEquals(TreeState.Status.CutDown, service.getTreeById(t.getId()).getState().getStatus());
	}
	
	@Test
	public void testCutDownTreeFail() {
		assertEquals(0, tp.getTrees().size());
		assertEquals(0, tp.getUsers().size());
		
		User u = null;
		String sessionGuid;
		Tree t = null;
		try {
			u = service.createUser("Bob", "asdfasdf", UserType.Resident);
			sessionGuid = service.login(u.getName(), "asdfasdf");
			service.addAddressToUser(sessionGuid, 123, "Fake Street", "X1X 1X1", 14, 43);
			t = service.addTree(sessionGuid, 14, 43, 2, 3, "Maple", Tree.Municipality.Anjou, Tree.LandType.Park);
		} catch (Exception e) {
			fail();
			return;
		}
		
		Tree t2 = new Tree(14, 43, 2, 3, "Maple", Tree.Municipality.LaSalle, Tree.LandType.Institutional, new TreeState());
		try {
			service.cutDownTree(sessionGuid, t2.getId());
			fail("Exception expected");
		} catch (InvalidInputException e) {
			assertEquals("Specified tree no longer exists.", e.getMessage());
		} catch (InvalidSessionException e) {
			fail("Invalid input, but session should be fine");
		}
		try {
			service.cutDownTree(null, t.getId());
			fail("Exception expected");
		} catch(InvalidSessionException e) {
			// Expected behavior
		} catch (InvalidInputException e) {
			fail("Input is valid, session exception should be thrown");
		}
	}
	
	@Test(expected=InvalidInputException.class)
	public void testCutDownTreeAlreadyCutDown() throws InvalidInputException, InvalidSessionException {
		User u = null;
		String sessionGuid;
		Tree t = null;
		TreeState ts = new TreeState();
		ts.setStatus(TreeState.Status.CutDown);
		try {
			u = service.createUser("Bob", "asdfasdf", User.UserType.Resident);
			sessionGuid = service.login(u.getName(), "asdfasdf");
			tp.addTree(t = new Tree(14.0, 43.0, 2.0, 3.0, "Maple", Tree.Municipality.Anjou, Tree.LandType.Park, ts));
		} catch (Exception e) {
			fail();
			return;
		}
		service.cutDownTree(sessionGuid, t.getId());
	}
	
	@Test(expected=InvalidInputException.class)
	public void testCutDownTreeProjected() throws InvalidInputException, InvalidSessionException {
		User u = null;
		String sessionGuid;
		Tree t = null;
		TreeState ts = new TreeState();
		ts.setStatus(TreeState.Status.Projected);
		try {
			u = service.createUser("Bob", "asdfasdf");
			sessionGuid = service.login(u.getName(), "asdfasdf");
			tp.addTree(t = new Tree(14.0, 43.0, 2.0, 3.0, "Maple", Tree.Municipality.Anjou, Tree.LandType.Park, ts));
		} catch (Exception e) {
			fail();
			return;
		}
		service.cutDownTree(sessionGuid, t.getId());
	}
	
	@Test
	public void testGetTreesByX() {
		assertEquals(0, tp.getTrees().size());
		assertEquals(0, tp.getUsers().size());
		String sessionGuid;
		User u = null;
		try {
			u = service.createUser("Bob", "asdfasdf", UserType.Municipal);
			sessionGuid = service.login(u.getName(), "asdfasdf");
			service.addTree(sessionGuid, 14, 43, 2, 3, "Maple", Tree.Municipality.Anjou, Tree.LandType.Park);
			service.addTree(sessionGuid, 14, 43, 2, 3, "Pine", Tree.Municipality.LaSalle, Tree.LandType.Municipal);
		} catch (Exception e) {
			fail();
			return;
		}
		assertEquals(1, service.getTreesBySpecies("Maple").size());
		assertEquals(0, service.getTreesBySpecies("Baobab").size());
		
		Tree t = tp.getTrees().get(0);
		TreeState ts = t.getState();
		ts.setHealth(TreeState.Health.Dead);
		ts.setMark(TreeState.Mark.MarkedToCutDown);
		
		Tree t2 = tp.getTrees().get(1);
		TreeState ts2 = t2.getState();
		ts2.setStatus(TreeState.Status.Projected);
		
		try {
			service.updateTreeState(sessionGuid, t.getId(), ts);
			service.updateTreeState(sessionGuid, t2.getId(), ts2);
		} catch (InvalidInputException e) {
			fail(e.getMessage());
		} catch (InvalidSessionException e) {
			fail(e.getMessage());
		}
		
		assertEquals(1, service.getTreesByHealth(TreeState.Health.Dead).size());
		assertEquals(0, service.getTreesByHealth(TreeState.Health.Diseased).size());
		
		assertEquals(1, service.getTreesByMark(TreeState.Mark.MarkedToCutDown).size());
		
		assertEquals(1, service.getTreesByStatus(TreeState.Status.Projected).size());
		assertEquals(1, service.getTreesByStatus(TreeState.Status.Planted).size());
		assertEquals(0, service.getTreesByStatus(TreeState.Status.CutDown).size());
		
		assertEquals(1, service.getTreesByState(ts).size());
		
		assertEquals(2, service.getTreesNearCoordinates(43.000001, 14.00000001, 100).count());
	}
	
	@Test
	public void testGetTreeById() {
		assertEquals(0, tp.getTrees().size());
		assertEquals(0, tp.getUsers().size());
		
		User u = null;
		Tree t = null;
		try {
			u = service.createUser("Bob", "asdfasdf", UserType.Municipal);
			String sessionGuid = service.login(u.getName(), "asdfasdf");
			t = service.addTree(sessionGuid, 14, 43, 2, 3, "Maple", Tree.Municipality.Anjou, Tree.LandType.Park);
		} catch (Exception e) {
			fail();
		}
		
		assertEquals(null, service.getTreeById(t.getId()+1));
	}
	
	@Test
	public void testGetTree() {
		Tree t;
		tp.addTree(t = new Tree(14.0, 43.0, 2.0, 3.0, "Maple", Tree.Municipality.Anjou, Tree.LandType.Park, new TreeState()));
		assert(service.getTree(0).equals(t));
	}
	
	@Test(expected=IndexOutOfBoundsException.class)
	public void testGetTreeFail() {
		service.getTree(0);
	}
	
	@Test
	public void testGetAddressByUser() {
		User u = null;
		String sessionGuid = null;
		try {
			u = service.createUser("Bob", "asdfasdf");
			sessionGuid = service.login(u.getName(), "asdfasdf");
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		try {
			assert(service.getAddressByUser(sessionGuid) == null);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test(expected=InvalidSessionException.class)
	public void failGetAddressByUser() throws InvalidSessionException {
		User u = null;
		String sessionGuid = null;
		try {
			u = service.createUser("Bob", "asdfasdf");
			sessionGuid = service.login(u.getName(), "asdfasdf");
		} catch (Exception e) {
			fail(e.getMessage());
		}
		String fakeGuid = UUID.randomUUID().toString();
		assert(!sessionGuid.equals(fakeGuid));	// this should not happen
		service.getAddressByUser(fakeGuid);
	}
}
