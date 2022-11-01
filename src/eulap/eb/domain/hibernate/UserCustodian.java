package eulap.eb.domain.hibernate;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.common.domain.BaseDomain;

/**
 * A class that represents the USER_CUSTODIAN table.

 */

@Entity
@Table (name="USER_CUSTODIAN")
public class UserCustodian extends BaseDomain {
	private Integer companyId;
	private Integer divisionId;
	private Integer custodianAccountId;
	private boolean active;
	private List<UserCustodianLines> userCustodianLines;
	private String userCustodianLinesJson;
	private Company company;
	private Division division;
	private CustodianAccount custodianAccount;
	private String errorMessage;

	public enum FIELD {id, companyId, divisionId, custodianAccountId, active};

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "USER_CUSTODIAN_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Override
	@Column(name = "CREATED_BY", columnDefinition = "int(10)")
	public int getCreatedBy() {
		return super.getCreatedBy();
	}
	@Override
	@Column(name = "CREATED_DATE", columnDefinition = "timestamp")
	public Date getCreatedDate() {
		return super.getCreatedDate();
	}

	@Override
	@Column(name = "UPDATED_BY", columnDefinition = "int(10)")
	public int getUpdatedBy() {
		return super.getUpdatedBy();
	}
	@Override
	@Column(name = "UPDATED_DATE", columnDefinition = "timestamp")
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
	}

	@Column(name = "COMPANY_ID", columnDefinition = "int(10)")
	public Integer getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@OneToOne
	@JoinColumn(name = "COMPANY_ID", insertable = false, updatable = false)
	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}

	@Column(name = "DIVISION_ID", columnDefinition = "int(10)")
	public Integer getDivisionId() {
		return divisionId;
	}
	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@OneToOne
	@JoinColumn(name = "DIVISION_ID", insertable = false, updatable = false)
	public Division getDivision() {
		return division;
	}
	public void setDivision(Division division) {
		this.division = division;
	}

	@Column(name = "CUSTODIAN_ACCOUNT_ID", columnDefinition = "int(10)")
	public Integer getCustodianAccountId() {
		return custodianAccountId;
	}
	public void setCustodianAccountId(Integer custodianAccountId) {
		this.custodianAccountId = custodianAccountId;
	}

	@OneToOne
	@JoinColumn(name = "CUSTODIAN_ACCOUNT_ID", insertable = false, updatable = false)
	public CustodianAccount getCustodianAccount() {
		return custodianAccount;
	}
	public void setCustodianAccount(CustodianAccount custodianAccount) {
		this.custodianAccount = custodianAccount;
	}

	@Column(name = "ACTIVE", columnDefinition = "tinyint(1)")
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}

	@Transient
	public String getUserCustodianLinesJson() {
		return userCustodianLinesJson;
	}
	public void setUserCustodianLinesJson(String userCustodianLinesJson) {
		this.userCustodianLinesJson = userCustodianLinesJson;
	}

	@Transient
	public void serializeUserCustodianLines() {
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		userCustodianLinesJson = gson.toJson(userCustodianLines);
	}
	public void deSerializeUserCustodianLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<UserCustodianLines>>(){}.getType();
		userCustodianLines = gson.fromJson(userCustodianLinesJson, type);
	}

	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "USER_CUSTODIAN_LINES_ID", insertable=false, updatable=false)
	public List<UserCustodianLines> getUserCustodianLines() {
		return userCustodianLines;
	}
	public void setUserCustodianLines(List<UserCustodianLines> userCustodianLines) {
		this.userCustodianLines = userCustodianLines;
	}

	@Transient
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public String toString() {
		return "UserCustodian [companyId=" + companyId + ", divisionId=" + divisionId + ", custodianAccountId="
				+ custodianAccountId + ", active=" + active + ", userCustodianLines=" + userCustodianLines
				+ ", userCustodianLinesJson=" + userCustodianLinesJson + ", errorMessage=" + errorMessage + "]";
	}
}
