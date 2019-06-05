package exceptions;

public class SQLParserException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public SQLParserException() {
    }

	public SQLParserException(String errorMessage) {
        super(errorMessage);
    }
}
