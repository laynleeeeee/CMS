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

import eulap.common.domain.BaseDomain;

/**
 * A class that represents the DIRECT_PAYMENT_LINE table in the database.

 *
 */
@Entity
@Table(name="DIRECT_PAYMENT_LINE")
public class DirectPaymentLine extends BaseDomain {
	private int directPaymentId;
	private int accountCombinationId;
	private double amount;
	private String description;
	private boolean active;
	private String companyNumber;
	private String divisionNumber;
	private String accountNumber;
	private AccountCombination accountCombination;
	private Integer companyId;
	private Integer divisionId;
	private Integer accountId;
	private String invoiceNumber;
	private String accountName;
	private Double debit;
	private Double credit;
	private String particular;

	public enum FIELD {
		aPInvoiceId, accountCombinationId, amount, description, directPaymentId, active
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "DIRECT_PAYMENT_LINE_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "DIRECT_PAYMENT_ID", columnDefinition = "INT(10)")
	public int getDirectPaymentId() {
		return directPaymentId;
	}

	public void setDirectPaymentId(int directPaymentId) {
		this.directPaymentId = directPaymentId;
	}

	@Column(name = "ACCOUNT_COMBINATION_ID", columnDefinition = "INT(10)")
	public int getAccountCombinationId() {
		return accountCombinationId;
	}

	public void setAccountCombinationId(int accountCombinationId) {
		this.accountCombinationId = accountCombinationId;
	}

	@Column(name = "DESCRIPTION", columnDefinition = "TEXT")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "AMOUNT", columnDefinition = "DOUBLE")
	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	@Transient
	public String getCompanyNumber() {
		return companyNumber;
	}

	public void setCompanyNumber(String companyNumber) {
		this.companyNumber = companyNumber;
	}

	@Transient
	public String getDivisionNumber() {
		return divisionNumber;
	}

	public void setDivisionNumber(String divisionNumber) {
		this.divisionNumber = divisionNumber;
	}

	@Transient
	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	@ManyToOne
	@JoinColumn (name = "ACCOUNT_COMBINATION_ID", insertable=false, updatable=false)
	public AccountCombination getAccountCombination() {
		return accountCombination;
	}

	public void setAccountCombination(AccountCombination accountCombination) {
		this.accountCombination = accountCombination;
	}

	@Column(name = "ACTIVE", columnDefinition = "TINYINT(1)")
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

	@Transient
	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	@Transient
	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	@Transient
	public Double getDebit() {
		return debit;
	}

	public void setDebit(Double debit) {
		this.debit = debit;
	}

	@Transient
	public Double getCredit() {
		return credit;
	}

	public void setCredit(Double credit) {
		this.credit = credit;
	}

	@Transient
	public String getParticular() {
		return particular;
	}

	public void setParticular(String particular) {
		this.particular = particular;
	}

	@Override
	public String toString() {
		return "DirectPaymentLine [directPaymentId=" + directPaymentId + ", accountCombinationId="
				+ accountCombinationId + ", amount=" + amount + ", description=" + description + ", active=" + active
				+ ", companyNumber=" + companyNumber + ", divisionNumber=" + divisionNumber + ", accountNumber="
				+ accountNumber + ", companyId=" + companyId + ", divisionId=" + divisionId + ", accountId=" + accountId
				+ ", invoiceNumber=" + invoiceNumber + ", accountName=" + accountName + ", debit=" + debit + ", credit="
				+ credit + ", particular=" + particular + "]";
	}

}
