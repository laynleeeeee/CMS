package eulap.eb.domain.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Object representation of UserLogInStatus table in the database.


 */
@Entity
@Table(name = "USER_LOGIN_STATUS")
public class UserLoginStatus extends BaseDomain {
	private int userId;
	private int successfulLoginAttempt;
	private int failedLoginAttempt;
	private boolean blockUser;
	private Date lastLogin;
	
	public enum FIELD {
		userId, blockUser
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "USER_LOGIN_STATUS_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	/**
	 * Get the Id of the user associated with the login status.
	 * @return The Id of the user.
	 */
	@Column(name = "USER_ID")
	public int getUserId() {
		return userId;
	}

	/**
	 * Set the Id of the user.
	 * @param userId The user Id.
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}

	/**
	 * Get the number of successful login attempts.
	 * @return The number of successful login attempts.
	 */
	@Column(name = "SUCCESSFUL_LOGIN_ATTEMPT")
	public int getSuccessfulLoginAttempt() {
		return successfulLoginAttempt;
	}

	/**
	 * Set the number of successful login attempts.
	 * @param successfulLoginAttempt The number of successful login attempts.
	 */
	public void setSuccessfulLoginAttempt(int successfulLoginAttempt) {
		this.successfulLoginAttempt = successfulLoginAttempt;
	}

	/**
	 * Get the number of failed login attempts.
	 * @return The number of failed login attempts.
	 */
	@Column(name = "FAILED_LOGIN_ATTEMPT")
	public int getFailedLoginAttempt() {
		return failedLoginAttempt;
	}

	/**
	 * Set the number of failed login attempts.
	 * @param failedLoginAttempt The number of failed login attempts.
	 */
	public void setFailedLoginAttempt(int failedLoginAttempt) {
		this.failedLoginAttempt = failedLoginAttempt;
	}

	/**
	 * Verify if the user is blocked.
	 * @return True if the user is blocked, otherwise False.
	 */
	@Column(name = "BLOCK_USER")
	public boolean isBlockUser() {
		return blockUser;
	}

	/**
	 * Set the status of the user to blocked.
	 * @param blockUser Set to true if blocked, otherwise false.
	 */
	public void setBlockUser(boolean blockUser) {
		this.blockUser = blockUser;
	}

	/**
	 * Get the latest date the user logged in.
	 * @return The last login.
	 */
	@Column(name = "LAST_LOGIN")
	public Date getLastLogin() {
		return lastLogin;
	}

	/**
	 * Set the latest date the user successfully logged in.
	 * @param lastLogin The last login.
	 */
	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	@Override
	public String toString() {
		return "UserLoginStatus [userId=" + userId
				+ ", successfulLoginAttempt=" + successfulLoginAttempt
				+ ", failedLoginAttempt=" + failedLoginAttempt + ", blockUser="
				+ blockUser + ", lastLogin=" + lastLogin + ", getId()="
				+ getId() + "]";
	}
}