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
 * Object representation of USER_ACCESS_RIGHT table in the database.

 */
@Entity
@Table (name="USER_ACCESS_RIGHT")
public class UserAccessRight extends BaseDomain{
	private int productKey;
	private int moduleKey;
	private int userId;
	
	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "USER_ACCESS_RIGHT_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column (name="PRODUCT_KEY")
	public int getProductKey() {
		return productKey;
	}

	public void setProductKey(int productKey) {
		this.productKey = productKey;
	}
	
	@Column (name="MODULE_KEY")
	public int getModuleKey() {
		return moduleKey;
	}

	public void setModuleKey(int moduleKey) {
		this.moduleKey = moduleKey;
	}
	
	@Column (name="USER_ID")
	public int getUserId() {
		return userId;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
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

	@Override
	public String toString() {
		return "UserAccessRight [productKey=" + productKey + ", moduleKey="
				+ moduleKey + ", userId=" + userId + "]";
	}
}
