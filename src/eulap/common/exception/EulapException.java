package eulap.common.exception;

/**
 * Generic eulap exception. 

 *
 */
public class EulapException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private String exceptionMsg;
	private Exception exception;

	public EulapException(String exceptionMsg) {
		this.exceptionMsg = exceptionMsg;
	}
	
	public EulapException (String exceptionMsg, Exception e) {
		this.exception = e;
	}
	
	public String getExceptionMsg(){
		return this.exceptionMsg;
	}
	
	public void setExceptionMsg(String exceptionMsg) {
		this.exceptionMsg = exceptionMsg;
	}
	
	public Exception getException () {
		return exception;
	}
	
	public void setException (Exception e) {
		this.exception = e;
	}
}
