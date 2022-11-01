package eulap.eb.domain.hibernate;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import eulap.common.domain.BaseDomain;

/**
 * Object that represents ROLE_ACCES_RIGHT table in the database.

 */
@Entity
@Table(name = "ROLE_ACCESS_RIGHT")
public class RoleAccessRight extends BaseDomain{

	private int productKeyId;
	private int moduleKeyId;
	private int roleId;
	private Role role;

	public enum FIELD {id , productKeyId, moduleKeyId, roleId}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "ROLE_ACCESS_RIGHT_ID", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name = "PRODUCT_KEY")
	public int getProductKeyId() {
		return productKeyId;
	}

	public void setProductKeyId(int productKeyId) {
		this.productKeyId = productKeyId;
	}

	@Column(name = "MODULE_KEY")
	public int getModuleKeyId() {
		return moduleKeyId;
	}

	public void setModuleKeyId(int moduleKeyId) {
		this.moduleKeyId = moduleKeyId;
	}

	@Column(name ="ROLE_ID")
	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	@ManyToOne
	@JoinColumn(name = "ROLE_ID", insertable=false, updatable=false)
	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@Override
	public String toString() {
		return "RoleAccessRight [productKeyId=" + productKeyId
				+ ", moduleKeyId=" + moduleKeyId + ", roleId=" + roleId
				+ ", role=" + role + ", getId()=" + getId()
				+ ", getCreatedBy()=" + getCreatedBy() + ", getCreatedDate()="
				+ getCreatedDate() + ", getUpdatedBy()=" + getUpdatedBy()
				+ ", getUpdatedDate()=" + getUpdatedDate() + "]";
	}
}
