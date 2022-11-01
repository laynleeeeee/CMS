package eulap.eb.domain.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * Object representation of SERVICE_SETTING table.

 *
 */
@Entity
@Table(name="SERVICE_SETTING")
public class ServiceSetting extends BaseDomain{
	private String name;
	private Integer accountCombinationId;
	private Integer companyId;
	private Integer divisionId;
	private Integer accountId;
	private AccountCombination accountCombination;
	private boolean active;

	public static final int MAX_NAME_CHAR = 100;

	public enum FIELD {
		id, name, accountCombinationId, active, companyId, divisionId, accountId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "SERVICE_SETTING_ID", columnDefinition = "int(10)", unique = true, nullable = false, insertable = false, updatable = false)
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

	/**
	 * Get the name of the AR Line.
	 * @return The name of the AR Line.
	 */
	@Column(name="NAME", columnDefinition="varchar(100)")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the Id of the Account Combination of the AR Line.
	 * @return The Id of the Account Combination.
	 */
	@Column(name="ACCOUNT_COMBINATION_ID", columnDefinition="int(10)")
	public Integer getAccountCombinationId() {
		return accountCombinationId;
	}

	public void setAccountCombinationId(Integer accountCombinationId) {
		this.accountCombinationId = accountCombinationId;
	}

	/**
	 * Validate if the AR Line Setup is active.
	 * @return True if active, false if inactive.
	 */
	@Column(name="ACTIVE", columnDefinition="tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Transient
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@Transient
	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@Transient
	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	@OneToOne
	@JoinColumn(name = "ACCOUNT_COMBINATION_ID", insertable = false, updatable = false)
	public AccountCombination getAccountCombination() {
		return accountCombination;
	}

	public void setAccountCombination(AccountCombination accountCombination) {
		this.accountCombination = accountCombination;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Service [name=").append(name).append(", accountCombinationId=").append(accountCombinationId)
				.append(", companyId=").append(companyId).append(", divisionId=").append(divisionId)
				.append(", accountId=").append(accountId).append(", active=").append(active).append("]");
		return builder.toString();
	}
}
