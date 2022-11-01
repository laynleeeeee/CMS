package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

/**
 * A class that represents the AP_LINE table in CBS database.

 */
@Entity
@Table(name = "AP_LINE")
public class APLine extends BaseFormLine {
	@Expose
	private int aPInvoiceId;
	@Expose
	private int accountCombinationId;
	@Expose
	private double amount;
	@Expose
	private String description;
	@Expose
	private String companyNumber;
	@Expose
	private String divisionNumber;
	@Expose
	private String accountNumber;
	@Expose
	private Integer companyId;
	@Expose
	private Integer divisionId;
	@Expose
	private Integer accountId;
	@Expose
	private Integer taxTypeId;
	@Expose
	private Double vatAmount;
	@Expose
	private Double grossAmount;
	private APInvoice aPInvoice;
	private AccountCombination accountCombination;
	@Expose
	private String acctCombinationName;
	@Expose
	private String companyName;
	@Expose
	private String divisionName;
	@Expose
	private String accountName;
	private TaxType taxType;
	@Expose
	private Integer loanChargeTypeId;
	private LoanChargeType loanChargeType;

	public enum FIELD {
		id, aPInvoiceId, accountCombinationId, amount, description, taxTypeId, vatAmount, grossAmount
	}

	/**
	 * Object type id of AP_LINE = 156
	 */
	public static final int AP_LINE_OBJECT_TYPE = 156;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "AP_LINE_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "AP_INVOICE_ID", columnDefinition = "INT(10)")
	public int getaPInvoiceId() {
		return aPInvoiceId;
	}

	public void setaPInvoiceId(int aPInvoiceId) {
		this.aPInvoiceId = aPInvoiceId;
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
	@JoinColumn (name = "AP_INVOICE_ID", insertable=false, updatable=false)
	public APInvoice getaPInvoice() {
		return aPInvoice;
	}

	public void setaPInvoice(APInvoice aPInvoice) {
		this.aPInvoice = aPInvoice;
	}

	@ManyToOne
	@JoinColumn (name = "ACCOUNT_COMBINATION_ID", insertable=false, updatable=false)
	public AccountCombination getAccountCombination() {
		return accountCombination;
	}

	public void setAccountCombination(AccountCombination accountCombination) {
		this.accountCombination = accountCombination;
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

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return AP_LINE_OBJECT_TYPE;
	}

	@Override
	@Transient
	public Integer getRefenceObjectId() {
		return null;
	}

	@Column(name = "TAX_TYPE_ID")
	public Integer getTaxTypeId() {
		return taxTypeId;
	}

	public void setTaxTypeId(Integer taxTypeId) {
		this.taxTypeId = taxTypeId;
	}

	@Column(name = "VAT_AMOUNT")
	public Double getVatAmount() {
		return vatAmount;
	}

	public void setVatAmount(Double vatAmount) {
		this.vatAmount = vatAmount;
	}

	@Column(name = "GROSS_AMOUNT")
	public Double getGrossAmount() {
		return grossAmount;
	}

	public void setGrossAmount(Double grossAmount) {
		this.grossAmount = grossAmount;
	}

	@Transient
	public String getAcctCombinationName() {
		return acctCombinationName;
	}

	public void setAcctCombinationName(String acctCombinationName) {
		this.acctCombinationName = acctCombinationName;
	}

	@Transient
	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	@Transient
	public String getDivisionName() {
		return divisionName;
	}

	public void setDivisionName(String divisionName) {
		this.divisionName = divisionName;
	}

	@Transient
	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	@OneToOne
	@JoinColumn (name = "TAX_TYPE_ID", insertable=false, updatable=false)
	public TaxType getTaxType() {
		return taxType;
	}

	public void setTaxType(TaxType taxType) {
		this.taxType = taxType;
	}

	@Column(name = "LOAN_CHARGE_TYPE_ID")
	public Integer getLoanChargeTypeId() {
		return loanChargeTypeId;
	}

	public void setLoanChargeTypeId(Integer loanChargeTypeId) {
		this.loanChargeTypeId = loanChargeTypeId;
	}

	@OneToOne
	@JoinColumn (name = "LOAN_CHARGE_TYPE_ID", insertable=false, updatable=false)
	public LoanChargeType getLoanChargeType() {
		return loanChargeType;
	}

	public void setLoanChargeType(LoanChargeType loanChargeType) {
		this.loanChargeType = loanChargeType;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("APLine [aPInvoiceId=").append(aPInvoiceId).append(", accountCombinationId=")
				.append(accountCombinationId).append(", amount=").append(amount).append(", description=")
				.append(description).append(", companyNumber=").append(companyNumber).append(", divisionNumber=")
				.append(divisionNumber).append(", accountNumber=").append(accountNumber).append(", companyId=")
				.append(companyId).append(", divisionId=").append(divisionId).append(", accountId=").append(accountId)
				.append(", taxTypeId=").append(taxTypeId).append(", vatAmount=").append(vatAmount)
				.append(", grossAmount=").append(grossAmount).append("]");
		return builder.toString();
	}
}
