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
 * Domain class for user group access right.


 */
@Entity
@Table(name = "UG_M_ACCESS_RIGHT")
public class UgMAccessRight extends BaseDomain{
	private int userGroupId;
	private int productCodeId;
	private int accessRightFlag;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "UG_M_ACCESS_RIGHT_ID", unique = true, nullable = false, insertable = false, updatable = false)
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
	 * Get the Id of the user group associated with the user group access right.
	 * @return The user group Id.
	 */
	@Column(name = "USER_GROUP_ID")
	public int getUserGroupId() {
		return userGroupId;
	}

	/**
	 * Set the user group Id associated with the user group access right.
	 */
	public void setUserGroupId(int userGroupId) {
		this.userGroupId = userGroupId;
	}

	/**
	 * Get the Id of the product code associated with the user group access right.
	 * @return The product code Id.
	 */
	@Column(name = "PRODUCT_CODE_ID")
	public int getProductCodeId() {
		return productCodeId;
	}

	/**
	 * Set the Id of the product code associated with the user group access right.
	 */
	public void setProductCodeId(int productCodeId) {
		this.productCodeId = productCodeId;
	}

	/**
	 * Get the flag of the user group access right.
	 * @return The access right flag.
	 */
	@Column(name = "ACCESS_RIGHT_FLAG")
	public int getAccessRightFlag() {
		return accessRightFlag;
	}

	/**
	 * Set the flag of the user group access right.
	 */
	public void setAccessRightFlag(int accessRightFlag) {
		this.accessRightFlag = accessRightFlag;
	}

	@Override
	public String toString() {
		return "UgMAccessRight [userGroupId=" + userGroupId
				+ ", productCodeId=" + productCodeId + ", accessRightFlag="
				+ accessRightFlag + ", getId()=" + getId()
				+ ", getCreatedBy()=" + getCreatedBy() + ", getCreatedDate()="
				+ getCreatedDate() + ", getUpdatedBy()=" + getUpdatedBy()
				+ ", getUpdatedDate()=" + getUpdatedDate() + "]";
	}
}