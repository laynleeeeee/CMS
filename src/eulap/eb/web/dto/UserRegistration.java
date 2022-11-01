package eulap.eb.web.dto;

import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.User;

/**
 * Container class for User Registration form

 *
 */
public class UserRegistration {
	private Company company;
	private User user;
	private String reEnteredPassword;
	private String keyCode;

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getReEnteredPassword() {
		return reEnteredPassword;
	}

	public void setReEnteredPassword(String reEnteredPassword) {
		this.reEnteredPassword = reEnteredPassword;
	}

	public String getKeyCode() {
		return keyCode;
	}

	public void setKeyCode(String keyCode) {
		this.keyCode = keyCode;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserRegistrationDto [company=");
		builder.append(company);
		builder.append(", user=");
		builder.append(user);
		builder.append(", reEnteredPassword=");
		builder.append(reEnteredPassword);
		builder.append(", keyCode=");
		builder.append(keyCode);
		builder.append("]");
		return builder.toString();
	}

}
