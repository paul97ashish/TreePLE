package ca.mcgill.ecse321.project6;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ca.mcgill.ecse321.project6.persistence.TestImportTreePLE;
import ca.mcgill.ecse321.project6.persistence.TestPersistence;
import ca.mcgill.ecse321.project6.service.TestTreePLEService;
import ca.mcgill.ecse321.project6.service.authentication.TestAuthentication;

@RunWith(Suite.class)
@SuiteClasses({TestPersistence.class,
	TestTreePLEService.class,
	TestImportTreePLE.class,
	TestAuthentication.class,
	})
public class AllTests {

}
