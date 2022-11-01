package eulap.eb.web.dto;

import eulap.eb.domain.hibernate.UserGroupAccessRight;

/**
 * DTO for handling UG access rights of forms.

 *
 */
public class UGAccessRightFormDto {
	private String formName;
	private boolean add;
	private boolean edit;
	private boolean search;
	private boolean approval;
	private UserGroupAccessRight userGroupAccessRight;
	
	public static final int ADD = 1;
	public static final int EDIT = 2;
	public static final int SEARCH = 4;
	public static final int APPROVAL = 8;
	
	public UGAccessRightFormDto () {
		
	}
	
	public UGAccessRightFormDto (String formName, 
			boolean add, boolean edit, boolean search, boolean approval, 
			UserGroupAccessRight userGroupAccessRight) {
		this.formName = formName;
		this.add = add;
		this.edit = edit;
		this.search = search;
		this.approval = approval;
		this.userGroupAccessRight = userGroupAccessRight;
	}
	
	public String getFormName() {
		return formName;
	}
	
	public void setFormName(String formName) {
		this.formName = formName;
	}
	
	public boolean isAdd() {
		return add;
	}
	
	public void setAdd(boolean add) {
		this.add = add;
	}
	
	public boolean isEdit() {
		return edit;
	}
	
	public void setEdit(boolean edit) {
		this.edit = edit;
	}
	
	public boolean isSearch() {
		return search;
	}
	
	public void setSearch(boolean search) {
		this.search = search;
	}
	
	public boolean isApproval() {
		return approval;
	}
	
	public void setApproval(boolean approval) {
		this.approval = approval;
	}
	
	public UserGroupAccessRight getUserGroupAccessRight() {
		return userGroupAccessRight;
	}
	
	public void setUserGroupAccessRight(UserGroupAccessRight userGroupAccessRight) {
		this.userGroupAccessRight = userGroupAccessRight;
	}

	@Override
	public String toString() {
		return "UGAccessRightFormDto [formName=" + formName + ", add=" + add
				+ ", edit=" + edit + ", search=" + search + ", approval="
				+ approval + ", userGroupAccessRight=" + userGroupAccessRight
				+ "]";
	}
}
