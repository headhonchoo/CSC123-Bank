

public class AccountClosedException extends Exception {

	public AccountClosedException() {

	}

	public AccountClosedException(String message) {
		super(message);

	}

	public AccountClosedException(Throwable cause) {
		super(cause);

	}

	public AccountClosedException(String message, Throwable cause) {
		super(message, cause);

	}

	public AccountClosedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);

	}

}
