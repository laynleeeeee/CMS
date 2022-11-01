package bp.web.dto.ar;

import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.UserCompany;

public class UserProfile {
	private User user;
	private UserCompany userCompany;
	
	public UserProfile() {
		
	}
	
	public UserProfile (User user, UserCompany userCompany) {
		this.user = user;
		this.userCompany = userCompany;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public UserCompany getUserCompany() {
		return userCompany;
	}
	
	public void setUserCompany(UserCompany userCompany) {
		this.userCompany = userCompany;
	}

	@Override
	public String toString() {
		return "UserProfile [user=" + user
				+ ", userCompany=" + userCompany + "]";
	}
}
