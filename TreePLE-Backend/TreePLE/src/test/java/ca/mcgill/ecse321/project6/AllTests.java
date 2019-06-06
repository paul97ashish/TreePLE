package ca.mcgill.ecse321.project6;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ca.mcgill.ecse321.project6.dto.TestDtoConversion;
import ca.mcgill.ecse321.project6.environment.TestCarbonOffset;

@RunWith(Suite.class)
@SuiteClasses({
	TestDtoConversion.class,
	TestCarbonOffset.class})
public class AllTests {

}
