package ca.mcgill.ecse321.project6.dto;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ca.mcgill.ecse321.project6.model.Tree;
import ca.mcgill.ecse321.project6.model.Tree.LandType;
import ca.mcgill.ecse321.project6.model.Tree.Municipality;
import ca.mcgill.ecse321.project6.model.TreeState;
import ca.mcgill.ecse321.project6.model.User;
import ca.mcgill.ecse321.project6.model.User.UserType;

public class TestDtoConversion {
	@Test
	public void testConvertTree() {
		Tree tree = new Tree(12, 12, 12, 12, "Treefam", Municipality.AhuntsicCartierville, LandType.Institutional, new TreeState());
		TreeDto dto = TreeDto.fromDomainObject(tree);
		
		assertEquals(tree.getId(), dto.getId());
		assertEquals(tree.getLatitude(), dto.getLatitude(), 0.01);
		assertEquals(tree.getHeightMeters(), dto.getHeightMeters(), 0.01);
		assertEquals(tree.getCanopyDiameterMeters(), dto.getCanopyDiameterMeters(), 0.01);
		assertEquals(tree.getSpecies(), dto.getSpecies());
		assertEquals(tree.getMunicipality(), dto.getMunicipality());
		assertEquals(tree.getLand(), dto.getLandType());
		assertEquals(tree.getState(), dto.getState());
	}
	
	@Test
	public void testConvertUser() {
		User user = new User("Bob", "dummyhash", UserType.Resident);
		UserDto dto = UserDto.fromDomainObject(user);
		
		assertEquals(user.getId(), dto.getId());
		assertEquals(user.getName(), dto.getName());
		assertEquals(user.getType(), dto.getUserType());
		assertEquals(user.getAddress(), dto.getAddress());
	}
}
