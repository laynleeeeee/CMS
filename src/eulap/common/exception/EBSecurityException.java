package eulap.common.exception;

/**
 * Elasticbooks Exception

 */
public class EBSecurityException  extends RuntimeException{
	private static final long serialVersionUID = 1L;

	private String exceptionMsg;
	private Exception exception;

	public EBSecurityException(String exceptionMsg) {
		this.exceptionMsg = exceptionMsg;
	}

	public EBSecurityException(String exceptionMsg, Exception e) {
		this.exception = e;
	}
	
	public String getExceptionMsg() {
		return exceptionMsg;
	}

	public void setExceptionMsg(String exceptionMsg) {
		this.exceptionMsg = exceptionMsg;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}
}
