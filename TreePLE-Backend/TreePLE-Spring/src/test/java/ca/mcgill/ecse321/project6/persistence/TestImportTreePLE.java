package ca.mcgill.ecse321.project6.persistence;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.ecse321.project6.model.Tree;

public class TestImportTreePLE {
	private final String filename = "input"+File.separator+"test_city_data.csv";
	private final String fake_filename = "input"+File.separator+"not_a_file.csv";
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		List<Tree> list = null;
		try {
			list = ImportTreePLE.importFromMtlCSV(filename);
		} catch (IOException e) {
			fail(e.getMessage());
		}
		assertEquals(list.size(), 50);
		for(Tree t : list) {
			assertEquals(t.getMunicipality(), Tree.Municipality.AhuntsicCartierville);
			assert(t.getLongitude() != 0);
			assert(t.getLatitude() != 0);
		}
		
		try {
			list = ImportTreePLE.importFromMtlCSV(fake_filename);
		} catch (IOException e) {
			fail(e.getMessage());
		}
		assert(list.isEmpty());
	}
	
	
}
