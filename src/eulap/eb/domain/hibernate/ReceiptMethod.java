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
import eulap.common.util.NumberFormatUtil;

/**
 * A class that represents the receipt method table in the CBS database

 */
@Entity
@Table (name = "RECEIPT_METHOD")
public class ReceiptMethod extends BaseDomain{
	private String name;
	private int companyId;
	private Integer debitAcctCombinationId;
	private Integer creditAcctCombinationId;
	private Integer bankAccountId;
	boolean active;
	// Associatied Objects
	private AccountCombination debitAcctCombination;
	private AccountCombination creditAcctCombination;
	private BankAccount bankAccount;
	private Company company;
	//Transient
	private int dbACDivisionId;
	private int dbACAccountId;
	private int crACDivisionId;
	private int crACAccountId;

	public enum FIELD {id, name, companyId, debitAcctCombinationId, creditAcctCombinationId, bankAccountId, active}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column (name = "RECEIPT_METHOD_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId(){
	    return super.getId();
	}

	@Override
	@Column(name = "CREATED_BY", columnDefinition = "INT(10)")
	public int getCreatedBy() {
	    return super.getCreatedBy();
	}

	@Override
	@Column(name = "CREATED_DATE", columnDefinition = "TIMESTAMP")
	public Date getCreatedDate() {
	    return super.getCreatedDate();
	}

	@Override
	@Column(name = "UPDATED_BY", columnDefinition = "INT(10)")
	public int getUpdatedBy() {
	    return super.getUpdatedBy();
	}

	@Override
	@Column(name = "UPDATED_DATE", columnDefinition = "TIMESTAMP")
	public Date getUpdatedDate() {
	    return super.getUpdatedDate();
	}

	/**
	 * Get the name of the receipt
	 * @return The name
	 */
	@Column(name = "NAME")
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the receipt
	 * @param name The name of the receipt
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the company id
	 * @return The company id
	 */
	@Column(name = "COMPANY_ID")
	public int getCompanyId() {
		return companyId;
	}

	/**
	 * Set the company id
	 * @param companyId The company id
	 */
	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	/**
	 * Get the debit account combination id
	 * @return The debit account combination id
	 */
	@Column (name = "DEBIT_ACCOUNT_COMBINATION_ID")
	public Integer getDebitAcctCombinationId() {
		return debitAcctCombinationId;
	}

	/**
	 * Set the debit account combination id
	 * @param debitAcctCombinationId The debit account combination id
	 */
	public void setDebitAcctCombinationId(Integer debitAcctCombinationId) {
		this.debitAcctCombinationId = debitAcctCombinationId;
	}

	/**
	 * Get the credit account combination id
	 * @return The credit account combination id
	 */
	@Column (name = "CREDIT_ACCOUNT_COMBINATION_ID")
	public Integer getCreditAcctCombinationId() {
		return creditAcctCombinationId;
	}

	/**
	 * Set the credit account combination id
	 * @param creditAcctCombinationId The credit account combination id
	 */
	public void setCreditAcctCombinationId(Integer creditAcctCombinationId) {
		this.creditAcctCombinationId = creditAcctCombinationId;
	}

	/**
	 * Get the bank account id.
	 * @return The bank account id.
	 */
	@Column (name = "BANK_ACCOUNT_ID")
	public Integer getBankAccountId() {
		return bankAccountId;
	}

	/**
	 * Set the bank account id.
	 * @param bankAccountId Get the bank account id.
	 */
	public void setBankAccountId(Integer bankAccountId) {
		this.bankAccountId = bankAccountId;
	}

	/**
	 * Check if receipt is active or not
	 * @return True if active, otherwise false
	 */
	@Column (name = "ACTIVE")
	public boolean isActive() {
		return active;
	}

	/**
	 * Set the receipt active or inactive
	 * @param active True if active, otherwise false
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Get the formatted number and name of the receipt method
	 */
	@Transient
	public String getNumberAndName () {
		return NumberFormatUtil.formatTo10Digit(getId()) + " - " + name;
	}

	@Transient
	public int getDbACDivisionId() {
		return dbACDivisionId;
	}

	public void setDbACDivisionId(int dbACDivisionId) {
		this.dbACDivisionId = dbACDivisionId;
	}

	@Transient
	public int getDbACAccountId() {
		return dbACAccountId;
	}

	public void setDbACAccountId(int dbACAccountId) {
		this.dbACAccountId = dbACAccountId;
	}

	@Transient
	public int getCrACDivisionId() {
		return crACDivisionId;
	}

	public void setCrACDivisionId(int crACDivisionId) {
		this.crACDivisionId = crACDivisionId;
	}

	@Transient
	public int getCrACAccountId() {
		return crACAccountId;
	}

	public void setCrACAccountId(int crACAccountId) {
		this.crACAccountId = crACAccountId;
	}

	@OneToOne
	@JoinColumn(name = "COMPANY_ID", insertable = false, updatable = false)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@OneToOne
	@JoinColumn(name = "DEBIT_ACCOUNT_COMBINATION_ID", insertable = false, updatable = false)
	public AccountCombination getDebitAcctCombination() {
		return debitAcctCombination;
	}

	public void setDebitAcctCombination(AccountCombination debitAcctCombination) {
		this.debitAcctCombination = debitAcctCombination;
	}

	@OneToOne
	@JoinColumn(name = "CREDIT_ACCOUNT_COMBINATION_ID", insertable = false, updatable = false)
	public AccountCombination getCreditAcctCombination() {
		return creditAcctCombination;
	}

	public void setCreditAcctCombination(AccountCombination creditAcctCombination) {
		this.creditAcctCombination = creditAcctCombination;
	}

	@OneToOne
	@JoinColumn(name = "BANK_ACCOUNT_ID", insertable = false, updatable = false)
	public BankAccount getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(BankAccount bankAccount) {
		this.bankAccount = bankAccount;
	}

	@Override
	public String toString() {
		return "ReceiptMethod [name=" + name + ", companyId=" + companyId
				+ ", debitAcctCombinationId=" + debitAcctCombinationId
				+ ", creditAcctCombinationId=" + creditAcctCombinationId
				+ ", bankAccountId=" + bankAccountId + ", active=" + active
				+ ", debitAcctCombination=" + debitAcctCombination
				+ ", bankAccount=" + bankAccount + ", company=" + company
				+ ", dbACDivisionId=" + dbACDivisionId + ", dbACAccountId="
				+ dbACAccountId + ", crACDivisionId=" + crACDivisionId
				+ ", crACAccountId=" + crACAccountId + ", getId()=" + getId()
				+ ", getCreatedBy()=" + getCreatedBy() + ", getCreatedDate()="
				+ getCreatedDate() + ", getUpdatedBy()=" + getUpdatedBy()
				+ ", getUpdatedDate()=" + getUpdatedDate() + "]";
	}

}
