package eulap.eb.webservice;

import java.io.Serializable;

/**
 * A class that contains the security information for web services

 *
 */
public class WebServiceCredential implements Serializable{
	private String userName;
	// Encrypted
	private String password;
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "WebServiceCredential [userName=" + userName + ", password="
				+ password + "]";
	}
}
