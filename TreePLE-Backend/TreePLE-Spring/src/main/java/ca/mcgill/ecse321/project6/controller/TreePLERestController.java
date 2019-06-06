package ca.mcgill.ecse321.project6.controller;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.mcgill.ecse321.project6.dto.AddressDto;
import ca.mcgill.ecse321.project6.dto.TreeDto;
import ca.mcgill.ecse321.project6.dto.UserDto;
import ca.mcgill.ecse321.project6.model.Address;
import ca.mcgill.ecse321.project6.model.Tree;
import ca.mcgill.ecse321.project6.model.Tree.LandType;
import ca.mcgill.ecse321.project6.model.Tree.Municipality;
import ca.mcgill.ecse321.project6.model.TreeState;
import ca.mcgill.ecse321.project6.model.User.UserType;
import ca.mcgill.ecse321.project6.service.InvalidInputException;
import ca.mcgill.ecse321.project6.service.TreePLEService;
import ca.mcgill.ecse321.project6.service.authentication.AuthenticationException;
import ca.mcgill.ecse321.project6.service.authentication.InvalidSessionException;

@RestController
public class TreePLERestController {
	@Autowired
	private TreePLEService service;
	
	@RequestMapping("/")
	public String index() {
		return "TreePLE REST service, online!";
	}
	
	/**
	 * Attempts to login and returns the session if successful
	 * @param username
	 * @return sessionGuid
	 * @throws AuthenticationException
	 */
	@PostMapping(value = { "/login" })
	public String login(@RequestParam String username, @RequestParam String password) throws AuthenticationException {
		return service.login(username, password);
	}
	
	/**
	 * @param sessionGuid
	 * @throws AuthenticationException
	 */
	@PostMapping(value = { "/logout" })
	public void logout(@RequestParam String sessionGuid) {
		service.logout(sessionGuid);
	}
	
	/**
	 * Creates a new user and returns a valid session
	 * @param username
	 * @return sessionGuid
	 * @throws Exception 
	 */
	@PostMapping(value = { "/signup" })
	public String signup(@RequestParam String username, @RequestParam String password) throws Exception {
		service.createUser(username, password, UserType.Resident);
		try {
			return service.login(username, password);
		} catch (AuthenticationException e) {
			throw new Exception("Integrity issue - this should not occur", e);
		}
	}

	@PostMapping(value = { "/plant", "/plant/" })
	public TreeDto plantTree(@RequestParam double latitude, @RequestParam double longitude, @RequestParam double height,
			@RequestParam double canopy, @RequestParam String species, @RequestParam Municipality mun,
			@RequestParam LandType landType, @RequestParam String sessionGuid, @RequestParam(required = false) Boolean overrideAccess) throws InvalidInputException, InvalidSessionException {
		Tree t = service.addTree(sessionGuid, latitude, longitude, height, canopy, species, mun, landType, overrideAccess);
		return TreeDto.fromDomainObject(t);
	}

	@PostMapping(value = { "/cutdown/{treeId}" })
	public TreeDto cutDownTree(@PathVariable("treeId") int treeId, @RequestParam String sessionGuid) throws InvalidInputException, InvalidSessionException {
		return TreeDto.fromDomainObject(service.cutDownTree(sessionGuid, treeId));
	}
	
	@PostMapping(value = { "/carbon-offset", "/carbon-offset/" })
	public double getCarbonOffset(@RequestParam(name = "treeIds[]") int[] treeIds, @RequestParam int additionalProjected) throws InvalidInputException {
		if (treeIds == null) {
			throw new InvalidInputException("List of trees must be passed for calculations");
		}
		
		Stream<Tree> trees = IntStream.of(treeIds)
			.mapToObj(id -> service.getTreeById(id))
			.filter(tree -> tree != null);
		
		Stream<Tree> additionalTrees = Stream.of(new Tree[0]);
		
		return service.getCarbonOffsetPerYear(Stream.concat(trees, additionalTrees));
	}

	@GetMapping(value = { "/trees", "/trees/" })
	public TreeDto[] getTrees() {
		return service.getTrees().values().stream()
				.map(tree -> TreeDto.fromDomainObject(tree))
				.toArray(TreeDto[]::new);
	}
	
	@GetMapping(value = { "/trees-distance", "/trees-distance/"})
	public TreeDto[] getTreesDistance(@RequestParam double latitude, @RequestParam double longitude, @RequestParam double distance) {
		return service.getTreesNearCoordinates(longitude, latitude, distance)
				.filter(tree -> tree.getState().getStatus().equals(TreeState.Status.Planted))
				.map(tree -> TreeDto.fromDomainObject(tree))
				.toArray(TreeDto[]::new);
	}
	
	@GetMapping(value = { "/trees-planted", "/trees-planted/" })
	public TreeDto[] getTreesPlanted() {
		return service.getTreesByStatus(TreeState.Status.Planted).stream()
				.map(tree -> TreeDto.fromDomainObject(tree))
				.toArray(TreeDto[]::new);
	}
	
	@GetMapping(value = { "/address", "/address/" })
	public AddressDto getUserAddress(@RequestParam String sessionGuid) throws InvalidSessionException, InvalidInputException {
		Address address = service.getAddressByUser(sessionGuid);
		if(address == null) {
			throw new InvalidInputException("There is no address associated with this user.");
		}
		return AddressDto.fromDomainObject(address);
	}
	
	@PostMapping(value = { "/address", "/address/" })
	public UserDto setUserAddress(@RequestParam String sessionGuid, @RequestParam int streetNumber, @RequestParam String streetName,
			@RequestParam String postalCode, @RequestParam double latitude, @RequestParam double longitude) throws InvalidInputException, InvalidSessionException {
		return UserDto.fromDomainObject(service.addAddressToUser(sessionGuid, streetNumber, streetName, postalCode, latitude, longitude));
	}
	
	@GetMapping(value = { "/whoami", "/whoami/" })
	public UserDto getUserInfo(@RequestParam String sessionGuid) throws InvalidInputException, InvalidSessionException {
		return UserDto.fromDomainObject(service.getUserByGuid(sessionGuid));
	}
	
	@PostMapping(value = { "/update-tree-state", "/update-tree-state/" })
	public TreeDto updateTreeState(@RequestParam String sessionGuid, @RequestParam int id, @RequestParam TreeState.Status newStatus,
			@RequestParam TreeState.Health newHealth, @RequestParam TreeState.Mark newMarking) throws InvalidInputException, InvalidSessionException {
		TreeState ts = new TreeState();
		ts.setStatus(newStatus);
		ts.setHealth(newHealth);
		ts.setMark(newMarking);
		return TreeDto.fromDomainObject(service.updateTreeState(sessionGuid, id, ts));
	}
	
	@PostMapping(value = { "/update-height-canopy", "/update-height-canopy/" })
	public TreeDto updateTreeHeightCanopy(@RequestParam String sessionGuid, @RequestParam int id, @RequestParam double newHeight, @RequestParam double newCanopy)
		throws InvalidInputException, InvalidSessionException {
		return TreeDto.fromDomainObject(service.updateTreeHeightCanopy(sessionGuid, id, newHeight, newCanopy));
	}
}
