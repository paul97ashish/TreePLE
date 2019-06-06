package ca.mcgill.ecse321.project6.service.authentication;

public class AuthenticationException extends Exception {

	private static final long serialVersionUID = 4100515846651276243L;

	public AuthenticationException(String errorMessage) {
		super(errorMessage);
	}

	public AuthenticationException(String errorMessage, Exception e) {
		super(errorMessage, e);
	}

}