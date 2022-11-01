package bp.web.dto.ar;

import java.util.Collection;

import eulap.common.util.Page;
import eulap.eb.domain.hibernate.UserGroup;

public class UserGroupDto {
	private Collection<UserGroup> userGroups;
	private String userGroupName;
	private UserGroup userGroup;
	private Page<UserGroup> page;
	
	public void setPage(Page<UserGroup> page) {
		this.page = page;
	}

	public Page<UserGroup> getPage() {
		return page;
	}
	public void setUserGroups(Collection<UserGroup> userGroups) {
		this.userGroups = userGroups;
	}
	
	public Collection<UserGroup> getUserGroups() {
		return userGroups;
	}
	
	public void setuserGroupName(String userGroupName) {
		this.userGroupName = userGroupName;
	}

	public String getUserGroupName() {
		return userGroupName;
	}

	public void setUserGroup(UserGroup userGroup) {
		this.userGroup = userGroup;
	}

	public UserGroup getUserGroup() {
		return userGroup;
	}

	@Override
	public String toString() {
		return "UserGroupDto [userGroup=" + userGroup + ", userGroupName="
				+ userGroupName + ", userGroups=" + userGroups + ", page=" + page
				+ "]";
	}
}
