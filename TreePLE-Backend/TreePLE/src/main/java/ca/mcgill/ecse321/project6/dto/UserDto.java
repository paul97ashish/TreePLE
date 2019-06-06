package ca.mcgill.ecse321.project6.dto;

import ca.mcgill.ecse321.project6.model.User;
import ca.mcgill.ecse321.project6.model.Address;

public class UserDto {
	
	private int id;
	private String name;
	private User.UserType type;
	private Address addr;
	
	public UserDto() {
	}
	
	public UserDto(int id, String name) {
		this(id, name, User.UserType.Resident, null);
	}
	
	public UserDto(int id, String name, User.UserType type, Address addr) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.addr = addr;
	}
	
	public String getName() {
		return name;
	}
	
	public int getId() {
		return id;
	}
	
	public User.UserType getUserType() {
		return type;
	}
	
	public Address getAddress() {
		return addr;
	}

	public static UserDto fromDomainObject(User user) {
		return new UserDto(user.getId(), user.getName(), user.getType(), user.getAddress());
	}
}
