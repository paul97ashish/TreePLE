package ca.mcgill.ecse321.project6.service;

public class AuthorizationException extends Exception {

	private static final long serialVersionUID = 3100515846651276243L;

	public AuthorizationException(String errorMessage) {
		super(errorMessage);
	}

}