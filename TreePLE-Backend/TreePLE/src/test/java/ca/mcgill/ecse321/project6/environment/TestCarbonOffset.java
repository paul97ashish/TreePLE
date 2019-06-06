package ca.mcgill.ecse321.project6.environment;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.ecse321.project6.model.Tree;
import ca.mcgill.ecse321.project6.model.TreeState;

public class TestCarbonOffset {
	private List<Tree> list = null;

	@Before
	public void setUp() throws Exception {
		list = new ArrayList<Tree>();
		Tree t1 = new Tree(1, 0, 30, 15, "Oak", Tree.Municipality.MontrealNord, Tree.LandType.Residential, new TreeState());
		list.add(t1);
		list.add(t1);
		list.add(t1);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSimple() {
		assert(list != null);
		double kgCOffset = EnvironmentInformation.carbonOffsetPerYear(list.stream());
		assertEquals(57.9 * 0.4535924, kgCOffset, 0.001);
	}

}
