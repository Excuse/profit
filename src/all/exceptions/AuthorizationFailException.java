package all.exceptions;

@SuppressWarnings("serial")
public class AuthorizationFailException extends Exception {
	public AuthorizationFailException(String message) {
		super(message);
	}
}
