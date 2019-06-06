package ca.mcgill.ecse321.project6.service.authentication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.ecse321.project6.model.TreePLE;
import ca.mcgill.ecse321.project6.model.User;
import ca.mcgill.ecse321.project6.model.User.UserType;
import ca.mcgill.ecse321.project6.persistence.DbXml;
import ca.mcgill.ecse321.project6.persistence.IDb;
import ca.mcgill.ecse321.project6.persistence.TestPersistence;
import ca.mcgill.ecse321.project6.service.InvalidInputException;
import ca.mcgill.ecse321.project6.service.TreePLEService;

public class TestAuthentication {
	
	private TreePLE tp;
	private IDb db;
	
	private TreePLEService service;
	private AuthenticationService authService;
	
	private User bob;
	
	@AfterClass
	public static void tearDownAfterClass() {
		File file = new File(TestPersistence.testXmlConfig.getDbFilename());
		file.delete();
	}
	
	@Before
	public void setUp() throws Exception {
		db = new DbXml(TestPersistence.testXmlConfig);
		db.saveToDb(tp = new TreePLE());

		authService = new AuthenticationService(tp);
		service = new TreePLEService(tp, db, authService);
		
		// Set up bob
		try {
			bob = service.createUser("Bob", "asdfasdf", UserType.Resident);
		} catch (InvalidInputException e) {
			fail("Users could not be created.");
		}
		
	}

	@After
	public void tearDown() throws Exception {
		tp.delete();
		db.clearAllData();
	}
	
	@Test
	public void testLogin() {
		assertEquals(1, tp.getUsers().size());
		
		try {
			String oldSession = authService.login(bob.getName(), "asdfasdf");
			
			// Login twice, new session
			String bobSession = authService.login(bob.getName(), "asdfasdf");
			assertNotEquals(oldSession, bobSession);
			
			// Ensure old session is invalid
			try {
				authService.getUserBySession(oldSession);
				fail("Invalidated session, no exception thrown");
			} catch (InvalidSessionException e) {
				// Expected
			}
			assertEquals(bob.getId(), authService.getUserBySession(bobSession).getId());
		} catch (AuthenticationException e) {
			fail("Bob login failed: "  + e.getMessage());
			return;
		} catch (InvalidSessionException e) {
			fail("Bob session invalid: "  + e.getMessage());
			return;
		}
	}
	
	@Test
	public void testLogout() {
		assertEquals(1, tp.getUsers().size());
		
		try {
			// First login to get the session
			String bobSession = authService.login(bob.getName(), "asdfasdf");
			
			// Then logout to invalidate the session
			authService.logout(bobSession);
			
			// Usage of the invalidated session should fail
			try {
				authService.getUserBySession(bobSession);
				fail("Invalidated session, no exception thrown");
			} catch (InvalidSessionException e) {
				// Expected
			}
		} catch (AuthenticationException e) {
			fail(e.getMessage());
			return;
		}
	}
}
