package eulap.eb.exception;

/**
 * An exception class that can have suppressable stack trace. 
 * <br> Suppressing the stack trace is optional.
 * <br> The reason for suppressing stack trace is to remove the stack trace when throwing exception
 * <br> for the end user.

 *
 */
public class SuppressableStacktraceException extends Exception {
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean suppressStacktrace = false;

	/**
	 * 
	 * @param message The message to display.
	 * @param suppressStacktrace True to suppress the stack trace, otherwise false.
	 */
	public SuppressableStacktraceException(String message, boolean suppressStacktrace) {
		super(message, null, suppressStacktrace, !suppressStacktrace);
		this.suppressStacktrace = suppressStacktrace;
	}

	@Override
	public String toString() {
		if (suppressStacktrace) {
			return getLocalizedMessage();
		} else {
			return super.toString();
		}
	}

}
