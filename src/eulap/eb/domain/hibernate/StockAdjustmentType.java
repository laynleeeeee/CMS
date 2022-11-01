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
 * Object representation of STOCK_ADJUSTMENT_TYPE table.

 *
 */
@Entity
@Table(name = "STOCK_ADJUSTMENT_TYPE")
public class StockAdjustmentType extends BaseDomain{
	private Integer accountCombiId;
	private String name;
	private boolean active;
	private AccountCombination acctCombi;
	private Integer companyId;
	private Integer divisionId;
	private Integer accountId;

	public enum FIELD {
		id, name, active, accountCombiId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "STOCK_ADJUSTMENT_TYPE_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name="ACCOUNT_COMBINATION_ID", columnDefinition="int(10)")
	public Integer getAccountCombiId() {
		return accountCombiId;
	}

	public void setAccountCombiId(Integer accountCombiId) {
		this.accountCombiId = accountCombiId;
	}

	@Column(name="NAME", columnDefinition="varchar(100)")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name="ACTIVE", columnDefinition="tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@OneToOne
	@JoinColumn (name = "ACCOUNT_COMBINATION_ID", insertable=false, updatable=false)
	public AccountCombination getAcctCombi() {
		return acctCombi;
	}

	public void setAcctCombi(AccountCombination acctCombi) {
		this.acctCombi = acctCombi;
	}

	@Override
	public String toString() {
		return "StockAdjustmentType [accountCombiId=" + accountCombiId
				+ ", name=" + name + ", active=" + active + ", companyId="
				+ companyId + ", divisionId=" + divisionId + ", accountId="
				+ accountId + "]";
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
}
