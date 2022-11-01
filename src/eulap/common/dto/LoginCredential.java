package eulap.common.dto;

/**
 * Holds the Credential of the logging in user.

 *
 */
public class LoginCredential {
	private String userName;
	private String password;
	private String newPassword;
	private String confirmPassword;
	private String message;
	
	/**
	 * Get the user name.
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Set the user name 
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Get the password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Set the password.
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * Get the new password.
	 */
	public String getNewPassword() {
		return newPassword;
	}

	/**
	 * Set the new password.
	 */
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	
	/**
	 * Get the confirm password.
	 */
	public String getConfirmPassword() {
		return confirmPassword;
	}
	
	/**
	 * Set the confirm password.
	 */
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	/**
	 * Get the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Set the message 	
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "LoginCredentialAndMessage [userName=" + userName
				+ ", password=" + password + ", message=" + message + "]";
	}
}
