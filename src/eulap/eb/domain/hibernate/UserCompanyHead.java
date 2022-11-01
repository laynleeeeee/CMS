package eulap.eb.domain.hibernate;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.common.domain.BaseDomain;

/**
 * Object representation of USER_COMPANY_HEAD table in the database.

 */
@Entity
@Table(name = "USER_COMPANY_HEAD")
public class UserCompanyHead extends BaseDomain{
	private boolean active;
	private List<UserCompany> userCompanies;
	private String userCompaniesJson;
	private String userName;
	private String errorMessage;
	private Integer usrId;

	public enum Field {id, createdBy, createdDate, updatedBy, active}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "USER_COMPANY_HEAD_ID", unique = true, nullable = false, insertable = false, updatable = false)
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
	 * @return the active
	 */
	@Column(name = "ACTIVE", columnDefinition = "tinyint(1)")
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	@OneToMany
	@JoinColumn(name = "USER_COMPANY_ID", insertable = false, updatable = false)
	public List<UserCompany> getUserCompanies() {
		return userCompanies;
	}

	public void setUserCompanies(List<UserCompany> userCompanies) {
		this.userCompanies = userCompanies;
	}

	@Transient
	public String getUserCompaniesJson() {
		return userCompaniesJson;
	}

	@Transient
	public void setUserCompaniesJson(String userCompaniesJson) {
		this.userCompaniesJson = userCompaniesJson;
	}

	@Transient
	public void serializeUserCompanies() {
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		userCompaniesJson = gson.toJson(userCompanies);
	}

	public void deSerializeUserCompanies() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<UserCompany>>(){}.getType();
		userCompanies = gson.fromJson(userCompaniesJson, type);
	}

	@Transient
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Transient
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Transient
	public Integer getUsrId() {
		return usrId;
	}

	public void setUsrId(Integer usrId) {
		this.usrId = usrId;
	}

	@Override
	public String toString() {
		return "UserCompanyHead [active=" + active + ", userCompaniesJson="
				+ userCompaniesJson + ", userName=" + userName + ", errorMessage=" + errorMessage + ", getId()="
				+ getId() + "]";
	}
}