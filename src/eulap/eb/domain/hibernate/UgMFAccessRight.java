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
 * Domain class for module function access right.


 */
@Entity
@Table(name = "UG_MF_ACCESS_RIGHT")
public class UgMFAccessRight extends BaseDomain{
	private int userGroupId;
	private int moduleCodeId;
	private int accessRightFlag;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "UG_MF_ACCESS_RIGHT_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Override
	@Column(name = "CREATED_BY")
	public int getCreatedBy() {
		return super.getCreatedBy();
	}

	@Override
	@Column(name = "CREATED_DATE")
	public Date getCreatedDate() {
		return super.getCreatedDate();
	}

	@Override
	@Column(name = "UPDATED_BY")
	public int getUpdatedBy() {
		return super.getUpdatedBy();
	}

	@Override
	@Column(name = "UPDATED_DATE")
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
	}

	/**
	 * Get the Id of the user group associated with the function access right.
	 * @return The user group Id.
	 */
	@Column(name = "USER_GROUP_ID")
	public int getUserGroupId() {
		return userGroupId;
	}

	/**
	 * Set the Id of the user group associated with the function access right.
	 * @param userGroupId
	 */
	public void setUserGroupId(int userGroupId) {
		this.userGroupId = userGroupId;
	}

	/**
	 * Get the Id of the module code associated with the function access right.
	 * @return The module code Id.
	 */
	@Column(name = "MODULE_CODE_ID")
	public int getModuleCodeId() {
		return moduleCodeId;
	}

	/**
	 * Set the Id of the module code associated with the function access right.
	 */
	public void setModuleCodeId(int moduleCodeId) {
		this.moduleCodeId = moduleCodeId;
	}

	/**
	 * Get the flag of the function access right.
	 * @return The access right flag.
	 */
	@Column(name = "ACCESS_RIGHT_FLAG")
	public int getAccessRightFlag() {
		return accessRightFlag;
	}

	/**
	 * Set the flag of the function access right.
	 */
	public void setAccessRightFlag(int accessRightFlag) {
		this.accessRightFlag = accessRightFlag;
	}

	@Override
	public String toString() {
		return "UgMFAccessRight [userGroupId=" + userGroupId
				+ ", moduleCodeId=" + moduleCodeId + ", accessRightFlag="
				+ accessRightFlag + ", getId()=" + getId()
				+ ", getCreatedBy()=" + getCreatedBy() + ", getCreatedDate()="
				+ getCreatedDate() + ", getUpdatedBy()=" + getUpdatedBy()
				+ ", getUpdatedDate()=" + getUpdatedDate() + "]";
	}
}