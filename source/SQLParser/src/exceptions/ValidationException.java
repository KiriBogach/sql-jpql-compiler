package exceptions;

public class ValidationException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public ValidationException() {
    }

	public ValidationException(String errorMessage) {
        super(errorMessage);
    }
}
