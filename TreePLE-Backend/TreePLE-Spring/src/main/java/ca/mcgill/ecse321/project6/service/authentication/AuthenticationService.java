package ca.mcgill.ecse321.project6.service.authentication;

import java.util.HashMap;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import ca.mcgill.ecse321.project6.model.TreePLE;
import ca.mcgill.ecse321.project6.model.User;
import ca.mcgill.ecse321.project6.security.Password;

public class AuthenticationService {
	
	@Autowired
	private TreePLE tp;
	
	public AuthenticationService(TreePLE tp) {
		this.tp = tp;
	}
	
	private static final HashMap<String, Integer> userBySession = new HashMap<>();
	private static final HashMap<Integer, String> sessionByUser = new HashMap<>();

	public User getUserBySession(String sessionGuid) throws InvalidSessionException {
		Integer userId = userBySession.get(sessionGuid);
		
		if (userId == null) {
			throw new InvalidSessionException();
		}
		User user = findUserById(userId);
		if (user == null) {
			// User no longer exists
			throw new InvalidSessionException();
		}
		return user;
	}
	
	private User findUserById(int id) {
		return tp.getUsers().stream()
			.filter(user -> user.getId() == id)
			.findAny().orElse(null);
	}
	
	public String login(String username, String password) throws AuthenticationException {
		User user;
		try {
			user = tp.getUsers().stream()
				.filter(u -> u.getName().equalsIgnoreCase(username))
				.filter(u -> {
					try {
						return Password.check(password, u.getPasswordHash());
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				})
				.findAny().get();
		} catch (Exception e) {
			throw new AuthenticationException("Your login information is incorrect.", e);
		}
		
		if (sessionByUser.containsKey(user.getId())) {
			// Invalidate old session
			logout(user.getId());
		}
		String sessionGuid = UUID.randomUUID().toString();
		userBySession.put(sessionGuid, user.getId());
		sessionByUser.put(user.getId(), sessionGuid);
		return sessionGuid;
	}
	
	public void logout(String sessionGuid) {
		sessionByUser.remove(userBySession.remove(sessionGuid));
	}
	
	public void logout(int userId) {
		userBySession.remove(sessionByUser.remove(userId));
		
	}
}
