package eulap.eb.domain.hibernate;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Object representation of USER_GROUP table in the database.


 */
@Entity
@Table(name = "USER_GROUP")
public class UserGroup extends BaseDomain {
	private String name;
	private String description;
	private boolean active;
	private boolean hasAdminAccess;
	private List<UserGroupAccessRight> userGroupAccessRights;
	
	public enum FIELD {
		id, name, description, active
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "USER_GROUP_ID", unique = true, nullable = false, insertable = false, updatable = false)
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
	 * Get the user group name.
	 * @return The user group name.
	 */
	@Column(name = "NAME", columnDefinition="varchar(20)")
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the user group.
	 * @param name The name of the user group.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the user group description.
	 * @return The user group description.
	 */
	@Column(name = "DESCRIPTION", columnDefinition="varchar(150)")
	public String getDescription() {
		return description;
	}

	/**
	 * Set the description of the user group.
	 * @param name The description of the user group.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Validate if the user group is active.
	 * @return True if the user group is active, otherwise false.
	 */
	@Column(name = "ACTIVE", columnDefinition="tinyint(1)")
	public boolean isActive() {
		return active;
	}

	/**
	 * Set the user group status.
	 * @param active True if the user group is active, otherwise false.
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
	
	@Transient
	public boolean isHasAdminAccess() {
		return hasAdminAccess;
	}

	public void setHasAdminAccess(boolean hasAdminAccess) {
		this.hasAdminAccess = hasAdminAccess;
	}

	@Fetch(FetchMode.SELECT)
	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn (name = "USER_GROUP_ID", insertable=false, updatable=false)
	public List<UserGroupAccessRight> getUserGroupAccessRights() {
		return userGroupAccessRights;
	}
	
	public void setUserGroupAccessRights(
			List<UserGroupAccessRight> userGroupAccessRights) {
		this.userGroupAccessRights = userGroupAccessRights;
	}

	@Override
	public String toString() {
		return "UserGroup [name=" + name + ", description=" + description
				+ ", active=" + active + ", getId()="
				+ getId() + ", getCreatedBy()=" + getCreatedBy()
				+ ", getCreatedDate()=" + getCreatedDate()
				+ ", getUpdatedBy()=" + getUpdatedBy() + ", getUpdatedDate()="
				+ getUpdatedDate() + "]";
	}
}