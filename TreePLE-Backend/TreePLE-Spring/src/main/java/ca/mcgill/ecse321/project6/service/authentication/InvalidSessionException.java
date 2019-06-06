package ca.mcgill.ecse321.project6.service.authentication;

public class InvalidSessionException extends Exception {

	private static final long serialVersionUID = 1732273217064781131L;

	public InvalidSessionException() {
		super("Session has expired or is invalid.");
	}
}
