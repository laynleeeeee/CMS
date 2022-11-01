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
 * A class that represents the bank accounts table in the CBS database.


 */
@Entity
@Table (name = "BANK_ACCOUNT")
public class BankAccount extends BaseDomain{
	private String name; // bank account name
	private int companyId;
	private Integer cashInBankAcctId;
	private Integer cashReceiptClearingAcctId;
	private Integer cashPaymentClearingAcctId;
	private Integer cashReceiptsPdcAcctId;
	private Integer cashPaymentsPdcAcctId;
	private boolean active;
	private int serviceLeaseKeyId;
	private Integer bankId;
	private String bankAccountNo;
	// Associatied Objects
	private Company company;
	private AccountCombination cashInBank;
	private AccountCombination cashReceiptClearingAcct;
	private AccountCombination cashPaymentClearingAcct;
	private AccountCombination cashReceiptsPdcAcct;
	private AccountCombination cashPaymentsPdcAcct;
	private Bank bank;
	//Transient
	private int inBADivisionId;
	private int inBAAccountId;
	private int cRCADivisionId;
	private int cRCAAccountId;
	private int cPCADivisionId;
	private int cPCAAccountId;
	private int cRPdcDivisionId;
	private int cRPdcAccountId;
	private int cPPdcDivisionId;
	private int cPPdcAccountId;

	public final static int MAX_NAME_CHAR = 100;
	public final static int MAX_NUMBER_CHAR = 50;

	public enum FIELD {id, name, companyId, cashInBankAcctId, cashReceiptClearingAcctId, cashPaymentClearingAcctId, 
		cashReceiptsPdcAcctId, cashPaymentsPdcAcctId, active, serviceLeaseKeyId, bankId}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column (name = "BANK_ACCOUNT_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
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
	 * Get the name of the bank account
	 * @return The name
	 */
	@Column(name = "NAME")
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the bank account
	 * @param name The name of the bank account
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
	 * Check if the bank account is active or not
	 * @return True if active, otherwise false
	 */
	@Column(name = "ACTIVE")
	public boolean isActive() {
		return active;
	}

	/**
	 * Set the bank account active or inactive
	 * @param active True if active, otherwise false
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	@Column(name = "EB_SL_KEY_ID")
	public int getServiceLeaseKeyId() {
		return serviceLeaseKeyId;
	}

	public void setServiceLeaseKeyId(int serviceLeaseKeyId) {
		this.serviceLeaseKeyId = serviceLeaseKeyId;
	}

	/**
	 * Get the formatted number and name of the bank account
	 */
	@Transient
	public String getNumberAndName () {
		return name + " - " + bankAccountNo;
	}

	@Transient
	public int getInBADivisionId() {
		return inBADivisionId;
	}

	public void setInBADivisionId(int inBADivisionId) {
		this.inBADivisionId = inBADivisionId;
	}

	@Transient
	public int getInBAAccountId() {
		return inBAAccountId;
	}

	public void setInBAAccountId(int inBAAccountId) {
		this.inBAAccountId = inBAAccountId;
	}

	@Transient
	public int getcRCADivisionId() {
		return cRCADivisionId;
	}

	public void setcRCADivisionId(int cRCADivisionId) {
		this.cRCADivisionId = cRCADivisionId;
	}

	@Transient
	public int getcRCAAccountId() {
		return cRCAAccountId;
	}

	public void setcRCAAccountId(int cRCAAccountId) {
		this.cRCAAccountId = cRCAAccountId;
	}

	@Transient
	public int getcPCADivisionId() {
		return cPCADivisionId;
	}

	public void setcPCADivisionId(int cPCADivisionId) {
		this.cPCADivisionId = cPCADivisionId;
	}

	@Transient
	public int getcPCAAccountId() {
		return cPCAAccountId;
	}

	public void setcPCAAccountId(int cPCAAccountId) {
		this.cPCAAccountId = cPCAAccountId;
	}

	/**
	 * Get the cash in bank account id
	 * @return The cash in bank account id
	 */
	@Column(name = "CASH_IN_BANK_ACCT_ID")
	public Integer getCashInBankAcctId() {
		return cashInBankAcctId;
	}

	/**
	 * Set the cash in bank account id
	 * @param cashInBankAcctId The cash in bank account id
	 */
	public void setCashInBankAcctId(Integer cashInBankAcctId) {
		this.cashInBankAcctId = cashInBankAcctId;
	}

	/**
	 * Get the cash receipt clearing account id
	 * @return The cash receipt clearing account id
	 */
	@Column(name = "CASH_RECEIPTS_CLEARING_ACCT_ID")
	public Integer getCashReceiptClearingAcctId() {
		return cashReceiptClearingAcctId;
	}

	/**
	 * Set the cash receipt clearing account id
	 * @param cashReceiptClearingAcctId The cash receipt clearing account id 
	 */
	public void setCashReceiptClearingAcctId(Integer cashReceiptClearingAcctId) {
		this.cashReceiptClearingAcctId = cashReceiptClearingAcctId;
	}

	/**
	 * Get the cash payment clearing account id
	 * @return The cash payment clearing account id
	 */
	@Column(name = "CASH_PAYMENTS_CLEARING_ACCT_ID")
	public Integer getCashPaymentClearingAcctId() {
		return cashPaymentClearingAcctId;
	}

	/**
	 * Set the cash payment clearing account id
	 * @param cashPaymentClearingAcctId The cash payment clearing account id
	 */
	public void setCashPaymentClearingAcctId(Integer cashPaymentClearingAcctId) {
		this.cashPaymentClearingAcctId = cashPaymentClearingAcctId;
	}
	
	/**
	 * Get the cash receipts post dated check account id
	 * @return The cash receipts post dated check account id
	 */
	@Column(name = "CASH_RECEIPTS_PDC_ACCT_ID")
	public Integer getCashReceiptsPdcAcctId() {
		return cashReceiptsPdcAcctId;
	}
	
	public void setCashReceiptsPdcAcctId(Integer cashReceiptsPdcAcctId) {
		this.cashReceiptsPdcAcctId = cashReceiptsPdcAcctId;
	}
	
	/**
	 * Get the cash payments post dated check account id
	 * @return The cash payments post dated check account id
	 */
	@Column(name = "CASH_PAYMENTS_PDC_ACCT_ID")
	public Integer getCashPaymentsPdcAcctId() {
		return cashPaymentsPdcAcctId;
	}
	
	public void setCashPaymentsPdcAcctId(Integer cashPaymentsPdcAcctId) {
		this.cashPaymentsPdcAcctId = cashPaymentsPdcAcctId;
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
	@JoinColumn(name = "CASH_IN_BANK_ACCT_ID", insertable = false, updatable = false)
	public AccountCombination getCashInBank() {
		return cashInBank;
	}
	
	public void setCashInBank(AccountCombination cashInBank) {
		this.cashInBank = cashInBank;
	}
	
	@OneToOne
	@JoinColumn(name = "CASH_RECEIPTS_CLEARING_ACCT_ID", insertable = false, updatable = false)
	public AccountCombination getCashReceiptClearingAcct() {
		return cashReceiptClearingAcct;
	}
	
	public void setCashReceiptClearingAcct(
			AccountCombination cashReceiptClearingAcct) {
		this.cashReceiptClearingAcct = cashReceiptClearingAcct;
	}
	
	@OneToOne
	@JoinColumn(name = "CASH_PAYMENTS_CLEARING_ACCT_ID", insertable = false, updatable = false)
	public AccountCombination getCashPaymentClearingAcct() {
		return cashPaymentClearingAcct;
	}
	
	public void setCashPaymentClearingAcct(
			AccountCombination cashPaymentClearingAcct) {
		this.cashPaymentClearingAcct = cashPaymentClearingAcct;
	}
	
	@OneToOne
	@JoinColumn(name = "CASH_RECEIPTS_PDC_ACCT_ID", insertable = false, updatable = false)
	public AccountCombination getCashReceiptsPdcAcct() {
		return cashReceiptsPdcAcct;
	}
	
	public void setCashReceiptsPdcAcct(AccountCombination cashReceiptsPdcAcct) {
		this.cashReceiptsPdcAcct = cashReceiptsPdcAcct;
	}
	
	@OneToOne
	@JoinColumn(name = "CASH_PAYMENTS_PDC_ACCT_ID", insertable = false, updatable = false)
	public AccountCombination getCashPaymentsPdcAcct() {
		return cashPaymentsPdcAcct;
	}
	
	public void setCashPaymentsPdcAcct(AccountCombination cashPaymentsPdcAcct) {
		this.cashPaymentsPdcAcct = cashPaymentsPdcAcct;
	}
	
	@Transient
	public int getcRPdcDivisionId() {
		return cRPdcDivisionId;
	}

	public void setcRPdcDivisionId(int cRPdcDivisionId) {
		this.cRPdcDivisionId = cRPdcDivisionId;
	}

	@Transient
	public int getcRPdcAccountId() {
		return cRPdcAccountId;
	}

	public void setcRPdcAccountId(int cRPdcAccountId) {
		this.cRPdcAccountId = cRPdcAccountId;
	}

	@Transient
	public int getcPPdcDivisionId() {
		return cPPdcDivisionId;
	}

	public void setcPPdcDivisionId(int cPPdcDivisionId) {
		this.cPPdcDivisionId = cPPdcDivisionId;
	}

	@Transient
	public int getcPPdcAccountId() {
		return cPPdcAccountId;
	}

	public void setcPPdcAccountId(int cPPdcAccountId) {
		this.cPPdcAccountId = cPPdcAccountId;
	}

	@Column(name = "BANK_ID")
	public Integer getBankId() {
		return bankId;
	}

	public void setBankId(Integer bankId) {
		this.bankId = bankId;
	}

	@Column(name = "ACCOUNT_NUMBER")
	public String getBankAccountNo() {
		return bankAccountNo;
	}

	public void setBankAccountNo(String bankAccountNo) {
		this.bankAccountNo = bankAccountNo;
	}

	@OneToOne
	@JoinColumn(name = "BANK_ID", insertable = false, updatable = false)
	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BankAccount [name=").append(name).append(", companyId=").append(companyId)
				.append(", cashInBankAcctId=").append(cashInBankAcctId).append(", cashReceiptClearingAcctId=")
				.append(cashReceiptClearingAcctId).append(", cashPaymentClearingAcctId=")
				.append(cashPaymentClearingAcctId).append(", cashReceiptsPdcAcctId=").append(cashReceiptsPdcAcctId)
				.append(", cashPaymentsPdcAcctId=").append(cashPaymentsPdcAcctId).append(", active=").append(active)
				.append(", serviceLeaseKeyId=").append(serviceLeaseKeyId).append(", bankId=").append(bankId)
				.append(", bankAccountNo=").append(bankAccountNo).append("]");
		return builder.toString();
	}
}
