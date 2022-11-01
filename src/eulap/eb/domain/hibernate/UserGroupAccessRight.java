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
 * A class that represents the USER_GROUP_ACCESS_RIGHT table.

 *
 */
@Entity
@Table(name="USER_GROUP_ACCESS_RIGHT")
public class UserGroupAccessRight extends BaseDomain{
	private Integer userGroupId;
	private Integer productKey;
	private Integer moduleKey;

	
	public enum FIELD {
		id, userGroupId, productKey, moduleKey
	}
	
	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "USER_GROUP_ACCESS_RIGHT_ID", unique = true, nullable = false, insertable = false, updatable = false)
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
	 * Get the unique id of the user group.
	 * @return The user group id.
	 */
	@Column(name = "USER_GROUP_ID")
	public Integer getUserGroupId() {
		return userGroupId;
	}
	
	public void setUserGroupId(Integer userGroupId) {
		this.userGroupId = userGroupId;
	}
	
	/**
	 * Get the product key.
	 * @return The product key.
	 */
	@Column(name = "PRODUCT_KEY")
	public Integer getProductKey() {
		return productKey;
	}
	
	public void setProductKey(Integer productKey) {
		this.productKey = productKey;
	}
	
	/**
	 * Get the module key.
	 * @return The module key.
	 */
	@Column(name = "MODULE_KEY")
	public Integer getModuleKey() {
		return moduleKey;
	}
	
	public void setModuleKey(Integer moduleKey) {
		this.moduleKey = moduleKey;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != this)
			return super.equals(obj);
		UserGroupAccessRight other = (UserGroupAccessRight) obj;
		if (other.userGroupId != this.userGroupId)
			return false;
		if (other.productKey != this.productKey)
			return false;
		if (other.moduleKey != this.moduleKey)
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		return userGroupId.hashCode() + productKey.hashCode() + moduleKey.hashCode();
	}
	@Override
	public String toString() {
		return "UserGroupAccessRight [userGroupId=" + userGroupId
				+ ", productKey=" + productKey + ", moduleKey=" + moduleKey
				+ ", getId()=" + getId() + ", getCreatedBy()=" + getCreatedBy()
				+ ", getCreatedDate()=" + getCreatedDate()
				+ ", getUpdatedBy()=" + getUpdatedBy() + ", getUpdatedDate()="
				+ getUpdatedDate() + "]";
	}
}
