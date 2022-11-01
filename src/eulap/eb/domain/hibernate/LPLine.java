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
 * A class that represents the LP_LINE table in CBS database.

 */
@Entity
@Table(name = "LP_LINE")
public class LPLine extends BaseFormLine {
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
	private Integer loadnProceedsId;
	@Expose
	private Double vatAmount;
	@Expose
	private Double grossAmount;
	private LoanProceeds loanProceeds;
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

	public enum FIELD {
		id, loadnProceedsId, accountCombinationId, amount, description, taxTypeId, vatAmount, grossAmount
	}

	/**
	 * Object type id of LOAN PROCEEDS LINE = 24012
	 */
	public static final int LP_LINE_OBJECT_TYPE = 24012;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "LP_LINE_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
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
	@JoinColumn (name = "LOAN_PROCEEDS_ID", insertable=false, updatable=false)
	public LoanProceeds getLoanProceeds() {
		return loanProceeds;
	}

	public void setLoanProceeds(LoanProceeds loanProceeds) {
		this.loanProceeds = loanProceeds;
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
		return LP_LINE_OBJECT_TYPE;
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

	@Column(name = "LOAN_PROCEEDS_ID")
	public Integer getLoadnProceedsId() {
		return loadnProceedsId;
	}

	public void setLoadnProceedsId(Integer loadnProceedsId) {
		this.loadnProceedsId = loadnProceedsId;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LPLine [accountCombinationId=")
				.append(accountCombinationId).append(", amount=").append(amount).append(", description=")
				.append(description).append(", companyNumber=").append(companyNumber).append(", divisionNumber=")
				.append(divisionNumber).append(", accountNumber=").append(accountNumber).append(", companyId=")
				.append(companyId).append(", divisionId=").append(divisionId).append(", accountId=").append(accountId)
				.append(", taxTypeId=").append(taxTypeId).append(", vatAmount=").append(vatAmount)
				.append(", grossAmount=").append(grossAmount).append("]");
		return builder.toString();
	}
}
