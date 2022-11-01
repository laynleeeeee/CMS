package eulap.eb.domain.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

import eulap.common.domain.BaseDomain;

/**
 * Object representation of USER_COMPANY table in the database.


 */
@Entity
@Table(name = "USER_COMPANY")
public class UserCompany extends BaseDomain{
	@Expose
	private int userId;
	@Expose
	private int companyId;
	@Expose
	private boolean active;
	@Expose
	private int userCompanyId;
	private User user;
	private Company company;
	@Expose
	private String companyName;
	private Integer userCompanyHeadId;

	public enum FIELD {id, createdBy, createdDate, updatedBy, 
		updatedDate, userId, companyId, active, user, company, userCompanyHeadId}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "USER_COMPANY_ID", unique = true, nullable = false, insertable = false, updatable = false)
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
	 * Get the user id of user associated with the company.
	 * @return The userId.
	 */
	@Column(name = "USER_ID")
	public int getUserId() {
		return userId;
	}

	/**
	 * Set the user id of user.
	 * @param userId The user id of user associated with the company.
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}

	/**
	 * Get the company id of company associated with the user.
	 * @return The userId.
	 */
	@Column(name = "COMPANY_ID")
	public int getCompanyId() {
		return companyId;
	}

	/**
	 * Set the company id of company.
	 * @param companyId The company id of company associated with the user.
	 */
	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	/**
	 * Get the company id of company associated with the user.
	 * @return The userId.
	 */
	@Column(name = "ACTIVE")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Get the user info.
	 * @return The user info.
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "USER_ID", insertable=false, updatable=false)
	public User getUser() {
		return user;
	}

	/**
	 * Set the user info.
	 * @param user info.
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * Get the  company associated with the user.
	 * @return The user info.
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "COMPANY_ID", insertable = false, updatable = false)
	public Company getCompany() {
		return company;
	}

	/**
	 * Set the company associated with the user.
	 * @param company The company.
	 */
	public void setCompany(Company company) {
		this.company = company;
	}

	@Transient
	public int getUserCompanyId() {
		return userCompanyId;
	}

	public void setUserCompanyId(int userCompanyId) {
		this.userCompanyId = userCompanyId;
	}

	@Transient
	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	@Override
	public String toString() {
		return "UserCompany [userId=" + userId
				+ ", companyId=" + companyId + ", getId()="
				+ getId() + ", getCreatedBy()=" + getCreatedBy()
				+ ", getCreatedDate()=" + getCreatedDate() + ", getUpdatedBy()="
				+ getUpdatedBy() + ", getUpdatedDate()=" + getUpdatedDate() + "]";
	}

	/**
	 * @return the userCompanyHeadId
	 */
	@Column(name = "USER_COMPANY_HEAD_ID", columnDefinition = "int(10)")
	public Integer getUserCompanyHeadId() {
		return userCompanyHeadId;
	}

	/**
	 * @param userCompanyHeadId the userCompanyHeadId to set
	 */
	public void setUserCompanyHeadId(Integer userCompanyHeadId) {
		this.userCompanyHeadId = userCompanyHeadId;
	}

}