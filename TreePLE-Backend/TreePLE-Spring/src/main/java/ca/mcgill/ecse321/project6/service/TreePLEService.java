package ca.mcgill.ecse321.project6.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import org.osgeo.proj4j.util.ProjectionMath;
import org.springframework.stereotype.Service;

import ca.mcgill.ecse321.project6.environment.EnvironmentInformation;
import ca.mcgill.ecse321.project6.model.Address;
import ca.mcgill.ecse321.project6.model.Tree;
import ca.mcgill.ecse321.project6.model.Tree.LandType;
import ca.mcgill.ecse321.project6.model.Tree.Municipality;
import ca.mcgill.ecse321.project6.model.TreeHistory;
import ca.mcgill.ecse321.project6.model.TreePLE;
import ca.mcgill.ecse321.project6.model.TreeState;
import ca.mcgill.ecse321.project6.model.User;
import ca.mcgill.ecse321.project6.model.User.UserType;
import ca.mcgill.ecse321.project6.persistence.IDb;
import ca.mcgill.ecse321.project6.security.Password;
import ca.mcgill.ecse321.project6.service.authentication.AuthenticationException;
import ca.mcgill.ecse321.project6.service.authentication.AuthenticationService;
import ca.mcgill.ecse321.project6.service.authentication.InvalidSessionException;

@Service
public class TreePLEService {
	private TreePLE tp;
	private IDb db;
	private AuthenticationService authService;
	private static final double propertyRadius = 50.0;	// assuming a user's property extends at most 50 m from wherever
														// google places their address
	public TreePLEService(
			TreePLE tp,
			IDb db,
			AuthenticationService authService) {
		this.tp = tp;
		this.db = db;
		this.authService = authService;
	}
	
	public User createUser(String name, String password) throws InvalidInputException {
		return createUser(name, password, User.UserType.Resident);
	}
	
	public User createUser(String name, String password, User.UserType userType) throws InvalidInputException {
		checkUsername(name);
		
		String passwordHash;
		try {
			passwordHash = Password.getSaltedHash(password);
		} catch (Exception e) {
			throw new InvalidInputException("Invalid password.");
		}
		
		User u = new User(name, passwordHash, userType);
		
		if (!tp.addUser(u)) {
		  throw new InvalidInputException("User already exists!");
		}
		
		db.saveToDb(tp);
		return u;
	}
	
	public String login(String username, String password) throws AuthenticationException {
		return authService.login(username, password);
	}
	
	public void logout(String sessionGuid) {
		authService.logout(sessionGuid);
	}
	
	public User getUserByGuid(String sessionGuid) throws InvalidInputException, InvalidSessionException {
		if (sessionGuid == null || sessionGuid.equals("")) {
			throw new InvalidInputException("Session GUID cannot be empty.");
		}
		return authService.getUserBySession(sessionGuid);
	}
	
	public User addAddressToUser(String sessionGuid, int streetNo, String street, String postalCode, double lat, double lon) throws InvalidInputException, InvalidSessionException {
		User u = authService.getUserBySession(sessionGuid);
		String error = "";
		
		if (streetNo <= 0) {
			error += " Must use a valid street number.";
		}
		if (street == null || street.trim().length() == 0) {
			error += " Street name must be specified.";
		}
		postalCode = postalCode.replaceAll("\\s","");
		if(!postalCode.matches("\\p{Alpha}\\p{Digit}\\p{Alpha}\\p{Digit}\\p{Alpha}\\p{Digit}")) {
			error += " Must enter a valid postal code.";
		}
		
		if(error.length() > 0) {
			throw new InvalidInputException(error.trim());
		}
		
		Address a = new Address(streetNo, street, postalCode, lat, lon);
		
		tp.removeUser(u);
		
		u.setAddress(a);
		tp.addUser(u);
		
		db.saveToDb(tp);
		return u;
	}
	
	public Address getAddressByUser(String sessionGuid) throws InvalidSessionException {
		User user = authService.getUserBySession(sessionGuid);
		return user.getAddress();
	}

	public Tree addTree(
			String sessionGuid,
			double latitude,
			double longitude,
			double height,
			double canopy,
			String species,
			Municipality mun,
			LandType land
			) throws InvalidInputException, InvalidSessionException {
		return addTree(sessionGuid, latitude, longitude, height, canopy, species, mun, land,false);
	}
	
	public Tree addTree(
			String sessionGuid,
			double latitude,
			double longitude,
			double height,
			double canopy,
			String species,
			Municipality mun,
			LandType land,
			Boolean overrideAccess // a little override to allow web frontend to always call addTree
			) throws InvalidInputException, InvalidSessionException {
		User user = authService.getUserBySession(sessionGuid);
		
		String error = "";
		if(height < 0 || canopy < 0) {
			error += " Measurements must be nonnegative.";
		}
		if(species == null || species.trim().length() == 0) {
			error += " Tree species must be specified.";
		}
		if(land == null) {
			error += " Land type must be specified.";
		}
		if(mun == null) {
			error += " Municipality must be specified.";
		}
		if(Math.abs(latitude)>90){
			error += "Latitude is out of range.";
		}
		if(Math.abs(longitude)>180){
			error += "Longitude is out of range.";
		}
		if(user.getType().equals(UserType.Resident) && (overrideAccess == null || !overrideAccess)) {
			if(user.getAddress() == null) {
				error += " User does not have an associated address.";
			} else if(coordinateDistanceMeters(
					user.getAddress().getLatitude(), user.getAddress().getLongitude(), latitude, longitude) > propertyRadius) {
				error += " Location does not appear to be on user's property.";
			}
		}
		if(error.length() > 0) {
			throw new InvalidInputException(error.trim());
		}
		TreeState state = new TreeState();
		
		// Necessary umple override - next id is static variable not updated on restart
		if (tp.getTrees().size() > 0)
			Tree.nextId = tp.getTrees().get(tp.getTrees().size()-1).getId()+1;
		
		Tree t = new Tree(latitude, longitude, height, canopy, species, mun, land, state);
		TreeHistory th = new TreeHistory(new Date(Calendar.getInstance().getTimeInMillis()), height, canopy, user);
		t.addEntry(th);

		tp.addTree(t);
		db.saveToDb(tp);
		return t;
	}
	
	private TreeHistory generateTreeLog(User u, Tree t, Date d) {
		TreeHistory th = null;
		th = new TreeHistory(d, t.getHeightMeters(), t.getCanopyDiameterMeters(), u);
		return th;
	}
	
	public Tree updateTreeState(String sessionGuid, int treeId, TreeState ts) throws InvalidInputException, InvalidSessionException {
		User user = authService.getUserBySession(sessionGuid);
		Tree tree = getTrees().get(treeId);
		
		String error = "";
		if (!user.getType().equals(UserType.Municipal)) {
			error += " Only municipal users can do this.";
		}
		error += verifyTreeExists(tp, tree);
		if (ts == null) {
			error += " New state must be specified.";
		}
		if(error.length() > 0) {
			throw new InvalidInputException(error.trim());
		}
		
		tp.removeTree(tree);
		tree.setState(ts);
		tree.addEntry(generateTreeLog(user, tree, new Date(Calendar.getInstance().getTimeInMillis())));
		tp.addTree(tree);
		db.saveToDb(tp);
		return tree;
	}
	
	public Tree updateTreeHeightCanopy(String sessionGuid, int treeId, double heightMeters, double canopyDiameterMeters) throws InvalidInputException, InvalidSessionException {
		User user = authService.getUserBySession(sessionGuid);
		Tree tree = getTrees().get(treeId);
		String error = "";
		if (!user.getType().equals(UserType.Municipal)) {
			error += " Only municipal users can do this.";
		}
		error += verifyTreeExists(tp, tree);
		if (heightMeters < 0 || canopyDiameterMeters < 0) {
			error += " Measurements must be nonnegative.";
		}
		if(error.length() > 0) {
			throw new InvalidInputException(error.trim());
		}
		
		tp.removeTree(tree);
		tree.setHeightMeters(heightMeters);
		tree.setCanopyDiameterMeters(canopyDiameterMeters);
		tree.addEntry(generateTreeLog(user, tree, new Date(Calendar.getInstance().getTimeInMillis())));
		tp.addTree(tree);
		db.saveToDb(tp);
		
		return tree;
	}
	
	/**
	 * Marks tree as cut down, saves to db, and return the resultant tree
	 * @param u
	 * @param t
	 * @return
	 * @throws InvalidInputException
	 * @throws InvalidSessionException 
	 * @throws AuthenticationException 
	 */
	public Tree cutDownTree(String sessionGuid, int treeId) throws InvalidInputException, InvalidSessionException {
		User user = authService.getUserBySession(sessionGuid);
		Tree tree = getTreeById(treeId);
		String error = "";
		if (tree == null) {
			error += " Specified tree no longer exists.";
		} else if (tree.getState().getStatus() == TreeState.Status.CutDown) {
			error += " Specified tree was already cut down.";
		} else if (tree.getState().getStatus() == TreeState.Status.Projected) {
			error += " Projected trees cannot be cut down before they're planted.";
		}
		else if(user.getType().equals(UserType.Resident)) {
			if(user.getAddress() == null) {
				error += " User must have an associated address.";
			} else if(coordinateDistanceMeters(
					user.getAddress().getLatitude(), user.getAddress().getLongitude(),
					tree.getLatitude(), tree.getLongitude()) > propertyRadius) {
				error += " Tree to cut down must be on user's property.";
			}
		}
		if(error.length() > 0) {
			throw new InvalidInputException(error.trim());
		}

		tp.removeTree(tree);
		TreeState ts = tree.getState();
		ts.setStatus(TreeState.Status.CutDown);
		tree.setState(ts);
		tree.addEntry(generateTreeLog(user, tree, new Date(Calendar.getInstance().getTimeInMillis())));
		tp.addTree(tree);
		db.saveToDb(tp);
		return tree;
	}
	
	public HashMap<Integer, User> getUsers() {
		HashMap<Integer, User> users = new HashMap<>();
		for (User u : tp.getUsers()) {
			users.put(u.getId(), u);
		}
		return users;
	}
	
	public HashMap<Integer, Tree> getTrees() {
		HashMap<Integer, Tree> trees = new HashMap<>();
		for (Tree t : tp.getTrees()) {
			trees.put(t.getId(), t);
		}
		return trees;
	}
	
	public Tree getTree(int index) {
		return tp.getTree(index);
	}
	
	public List<Tree> getTreesBySpecies(String species) {
		List<Tree> trees = new ArrayList<Tree>();
		for(Tree t : tp.getTrees()) {
			if(t.getSpecies().equals(species)) {
				trees.add(t);
			}
		}
		return trees;
	}
	
	public List<Tree> getTreesByHealth(TreeState.Health health) {
		List<Tree> result = new ArrayList<Tree>();
		for(Tree t : tp.getTrees()) {
			if(health.equals(t.getState().getHealth())) {
				result.add(t);
			}
		}
		return result;
	}
	
	public List<Tree> getTreesByMark(TreeState.Mark mark) {
		List<Tree> result = new ArrayList<Tree>();
		for(Tree t : tp.getTrees()) {
			if(mark.equals(t.getState().getMark())) {
				result.add(t);
			}
		}
		return result;
	}
	
	public List<Tree> getTreesByStatus(TreeState.Status status) {
		List<Tree> result = new ArrayList<Tree>();
		for(Tree t : tp.getTrees()) {
			if(status.equals(t.getState().getStatus())) {
				result.add(t);
			}
		}
		return result;
	}
	
	public List<Tree> getTreesByState(TreeState state) {
		List<Tree> result = new ArrayList<Tree>();
		for(Tree t : tp.getTrees()) {
			if(state.equals(t.getState())) {
				result.add(t);
			}
		}
		return result;
	}
	
	public Tree getTreeById(int id) {
		return getTrees().get(id);
	}
	
	public double getCarbonOffsetPerYear(Stream<Tree> trees) {
		return EnvironmentInformation.carbonOffsetPerYear(trees);
	}
	
	public Stream<Tree> getTreesNearCoordinates(double longitude, double latitude, double tolerance) {
		return tp.getTrees().stream()
			.filter(t -> coordinateDistanceMeters(latitude, longitude, t.getLatitude(), t.getLongitude()) <= tolerance);
	}
	
	private void checkUsername(String name) throws InvalidInputException {
		if (name == null || name.trim().length() == 0) {
			throw new InvalidInputException("User name cannot be empty.");
		}
		if (getUsers().values().stream()
				.filter(user -> user.getName().equalsIgnoreCase(name))
				.findAny().isPresent()) {
			throw new InvalidInputException("That name is taken!");
		}
	}
	
	private String verifyTreeExists(TreePLE tp, Tree t) {
		String error = "";
		if (t == null) {
			error += " Tree must be specified.";
		} else if (!getTrees().containsKey(t.getId())) {
			error += " Tree does not exist.";
		}
		return error;
	}
	
	/**
	 * Distance between points using Haversine formula
	 * @param lat1 First coordinate's latitude in degrees
	 * @param lon1 First coordinate's longitude in degrees
	 * @param lat2 Second coordinate's latitude in degrees
	 * @param lon2 Second coordinate's longitude in degrees
	 * @return Distance between points in meters
	 */
	private double coordinateDistanceMeters(double lat1, double lon1, double lat2, double lon2) {
		final double R_EARTH = 6371e3;
		return R_EARTH * ProjectionMath.greatCircleDistance(
				Math.toRadians(lon1), Math.toRadians(lat1), Math.toRadians(lon2), Math.toRadians(lat2));
	}
}
