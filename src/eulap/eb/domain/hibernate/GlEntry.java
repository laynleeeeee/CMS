package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

/**
 * A class that represents the GL_ENTRY table.

 *
 */
@Entity
@Table(name = "GL_ENTRY")
public class GlEntry extends BaseFormLine {
	private int generalLedgerId;
	@Expose
	private int accountCombinationId;
	private double amount;
	@Expose
	private String description;
	private boolean isDebit;
	private GeneralLedger generalLedger;
	private AccountCombination accountCombination;
	private int companyId;
	@Expose
	private Integer divisionId;
	@Expose
	private String divisionName;
	@Expose
	private Integer accountId;
	@Expose
	private String accountName;
	private String accountNo;
	@Expose
	private double debitAmount;
	@Expose
	private double creditAmount;
	@Expose
	private String combination;
	
	public enum FIELD {id, generalLedgerId, accountCombinationId, amount, description, debit, generalLedger};
	public static final int OBJECT_TYPE_ID = 140;
	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "GL_ENTRY_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE_ID;
	}
	
	@Override
	@Transient
	public Integer getRefenceObjectId() {
		return null;
	}
	/**
	 * Get the general ledger id.
	 * @return The general ledger id.
	 */
	@Column (name = "GENERAL_LEDGER_ID")
	public int getGeneralLedgerId() {
		return generalLedgerId;
	}

	/**
	 * Set the general ledger id.
	 * @param generalLedgerId The general ledger id.
	 */
	public void setGeneralLedgerId(int generalLedgerId) {
		this.generalLedgerId = generalLedgerId;
	}

	/**
	 * Get the account combination id.
	 * @return The account combination id.
	 */
	@Column (name = "ACCOUNT_COMBINATION_ID")
	public int getAccountCombinationId() {
		return accountCombinationId;
	}

	/**
	 * Set the general id.
	 * @param accountId The account id.
	 */
	public void setAccountCombinationId(int accountCombinationId) {
		this.accountCombinationId = accountCombinationId;
	}

	/**
	 * Get the amount.
	 * @return The amount.
	 */
	@Column (name = "AMOUNT")
	public double getAmount() {
		return amount;
	}

	/**
	 * Set the amount.
	 * @param amount The amount.
	 */
	public void setAmount(double amount) {
		this.amount = amount;
	}

	/**
	 * Get the description.
	 * @return The description.
	 */
	@Column (name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}

	/**
	 * Set the description.
	 * @param description The description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Evaluate if entry is debit.
	 * @return True if debit, otherwise false.
	 */
	@Column (name = "IS_DEBIT")
	public boolean isDebit() {
		return isDebit;
	}

	/**
	 * Set the entry to debit or not.
	 * @param debit True if debit, otherwise false.
	 */
	public void setDebit(boolean isDebit) {
		this.isDebit = isDebit;
	}
	
	/**
	 * Get the general ledger domain object.
	 * @return General ledger domain object.
	 */
	@ManyToOne
	@JoinColumn (name = "GENERAL_LEDGER_ID", insertable=false, updatable=false)
	public GeneralLedger getGeneralLedger() {
		return generalLedger;
	}

	/**
	 * Set the general ledger domain object.
	 * @param generalLedger General ledger domain object.
	 */
	public void setGeneralLedger(GeneralLedger generalLedger) {
		this.generalLedger = generalLedger;
	}
	
	/**
	 * Get the associated account combination.
	 * @return The associated account combination.
	 */
	@ManyToOne
	@JoinColumn(name="ACCOUNT_COMBINATION_ID", insertable=false, updatable=false)
	public AccountCombination getAccountCombination() {
		return accountCombination;
	}

	/**
	 * Set the associated account combination.
	 * @param accountCombination The associated account combination.
	 */
	public void setAccountCombination(AccountCombination accountCombination) {
		this.accountCombination = accountCombination;
	}	
	
	/**
	 * Get the company id.
	 * @return The company id.
	 */
	@Transient
	public int getCompanyId() {
		return companyId;
	}

	/**
	 * Set the company id.
	 * @param companyId The company id.
	 */
	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}
		
	/**
	 * Get the debit amount.
	 * @return The debit amount.
	 */
	@Transient
	public double getDebitAmount() {
		return debitAmount;
	}

	/**
	 * Set the debit amount.
	 * @param debitAmount The debit amount.
	 */
	public void setDebitAmount(double debitAmount) {
		this.debitAmount = debitAmount;
	}

	/**
	 * Get the credit amount.
	 * @return The credit amount.
	 */
	@Transient
	public double getCreditAmount() {
		return creditAmount;
	}

	/**
	 * Set the credit amount.
	 * @param creditAmount The credit amount.
	 */
	public void setCreditAmount(double creditAmount) {
		this.creditAmount = creditAmount;
	}

	@Transient
	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@Transient
	public String getDivisionName() {
		return divisionName;
	}

	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
	}

	@Transient
	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	@Transient
	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	@Transient
	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	@Transient
	public String getCombination() {
		return combination;
	}

	public void setCombination(String combination) {
		this.combination = combination;
	}

	@Override
	public String toString() {
		return "GlEntry [generalLedgerId=" + generalLedgerId
				+ ", accountCombinationId=" + accountCombinationId
				+ ", amount=" + amount + ", description=" + description
				+ ", isDebit=" + isDebit + ", generalLedger=" + generalLedger
				+ ", companyId=" + companyId + ", debitAmount=" + debitAmount
				+ ", creditAmount=" + creditAmount + ", getId()=" + getId()
				+ "]";
	}
}
