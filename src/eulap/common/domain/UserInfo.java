package eulap.common.domain;

/**
 * The logged user's information
 * 

 * 
 */
public class UserInfo {
	private final String userName;
	private final String companyName;
	private final int companyId;
	private final int userId;

	public UserInfo(int userId, String userName, int companyId, 
			String companyName) {
		this.userId = userId;
		this.userName = userName;
		this.companyName = companyName;
		this.companyId = companyId;
	}

	public String getUserName() {
		return userName;
	}

	public String getCompanyName() {
		return companyName;
	}

	public int getCompanyId() {
		return companyId;
	}

	public int getUserId() {
		return userId;
	}
}
