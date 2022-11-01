package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 *  Domain object for IS_AT_SETUP table.

 *
 */
@Entity
@Table(name = "IS_AT_SETUP")
public class IsAtSetup extends BaseDomain{
	private Integer accountTypeId;
	private Integer isClassSetupId;
	private boolean isPositive;
	private Integer sequenceOrder;
	private AccountType accountType;
	
	public static final int AT_SETUP_SALES = 1;
	public static final int AT_SETUP_BEG_BALANCES = 2; // Beginning BalanceS
	public static final int AT_SETUP_PURCHASES = 3;
	public static final int AT_SETUP_END_BALANCES = 4;
	public static final int AT_SETUP_OE = 5; // Operating Expenses
	
	public enum FIELD {
		id, accountTypeId, isClassSetupId, isPositive, sequenceOrder, accountType
	}
	
	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "IS_AT_SETUP_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}
	
	@Column(name = "ACCOUNT_TYPE_ID")
	public Integer getAccountTypeId() {
		return accountTypeId;
	}

	public void setAccountTypeId(Integer accountTypeId) {
		this.accountTypeId = accountTypeId;
	}

	@Column(name = "IS_CLASS_SETUP_ID")
	public Integer getIsClassSetupId() {
		return isClassSetupId;
	}

	public void setIsClassSetupId(Integer isClassSetupId) {
		this.isClassSetupId = isClassSetupId;
	}

	@Column(name = "IS_POSITIVE")
	public boolean isPositive() {
		return isPositive;
	}

	public void setPositive(boolean isPositive) {
		this.isPositive = isPositive;
	}

	@Column(name = "SEQUENCE_ORDER")
	public Integer getSequenceOrder() {
		return sequenceOrder;
	}

	public void setSequenceOrder(Integer sequenceOrder) {
		this.sequenceOrder = sequenceOrder;
	}
	
	@OneToOne
	@JoinColumn(name = "ACCOUNT_TYPE_ID", insertable = false, updatable = false)
	public AccountType getAccountType() {
		return accountType;
	}
	
	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	@Override
	public String toString() {
		return "IsAtSetup [accountTypeId=" + accountTypeId
				+ ", isClassSetupId=" + isClassSetupId + ", isPositive="
				+ isPositive + ", sequenceOrder=" + sequenceOrder + "]";
	}
}
