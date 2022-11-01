package eulap.eb.domain.hibernate;

import java.util.Date;

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

import eulap.common.domain.BaseDomain;
import eulap.common.util.NumberFormatUtil;

/**
 * An Object representation of SUPPLIER_ACCOUNT in CBS database.

 *
 */
@Entity
@Table (name="SUPPLIER_ACCOUNT")
public class SupplierAccount extends BaseDomain{
	private int supplierId;
	private String name;
	private int companyId;
	private Integer defaultDebitACId;
	private Integer defaultCreditACId;
	private int termId;
	private boolean active;
	// Associatied Objects
	private Company company;
	private Supplier supplier;
	private Term term;
	private AccountCombination defaultDebitAC;
	private AccountCombination defaultCreditAC;
	//Transient
	private int debitDivisionId;
	private int creditDivisionId;
	private int debitAccountId;
	private int creditAccountId;
	private String trimmedName;

	public enum FIELD {
		id, supplierId, name, companyId, defaultDebitACId, defaultCreditACId, termId, active
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "SUPPLIER_ACCOUNT_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "SUPPLIER_ID", columnDefinition = "INT(10)")
	public int getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}

	@Column(name = "NAME", columnDefinition = "VARCHAR(100)")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "COMPANY_ID", columnDefinition = "INT(10)")
	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	@Column(name = "DEFAULT_DEBIT_AC_ID", columnDefinition = "INT(10)")
	public Integer getDefaultDebitACId() {
		return defaultDebitACId;
	}

	public void setDefaultDebitACId(Integer defaultDebitACId) {
		this.defaultDebitACId = defaultDebitACId;
	}

	@Column(name = "DEFAULT_CREDIT_AC_ID", columnDefinition = "INT(10)")
	public Integer getDefaultCreditACId() {
		return defaultCreditACId;
	}

	public void setDefaultCreditACId(Integer defaultCreditACId) {
		this.defaultCreditACId = defaultCreditACId;
	}

	@Column(name = "TERM_ID", columnDefinition = "INT(10)")
	public int getTermId() {
		return termId;
	}

	public void setTermId(int termId) {
		this.termId = termId;
	}

	@Column(name = "ACTIVE", columnDefinition = "TINYINT(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@ManyToOne
	@JoinColumn (name = "SUPPLIER_ID", insertable=false, updatable=false, nullable=true)
	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	@Transient
	public int getDebitDivisionId() {
		return debitDivisionId;
	}

	public void setDebitDivisionId(int debitDivisionId) {
		this.debitDivisionId = debitDivisionId;
	}

	@Transient
	public int getCreditDivisionId() {
		return creditDivisionId;
	}

	public void setCreditDivisionId(int creditDivisionId) {
		this.creditDivisionId = creditDivisionId;
	}

	@Transient
	public int getDebitAccountId() {
		return debitAccountId;
	}

	public void setDebitAccountId(int debitAccountId) {
		this.debitAccountId = debitAccountId;
	}

	@Transient
	public int getCreditAccountId() {
		return creditAccountId;
	}

	public void setCreditAccountId(int creditAccountId) {
		this.creditAccountId = creditAccountId;
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
	 * Get the formatted number and name of the supplier
	 */
	@Transient
	public String getNumberAndName () {
		return NumberFormatUtil.formatTo10Digit(getId()) + " - " + name;
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
	@JoinColumn(name = "DEFAULT_DEBIT_AC_ID", insertable = false, updatable = false)
	public AccountCombination getDefaultDebitAC() {
		return defaultDebitAC;
	}

	public void setDefaultDebitAC(AccountCombination defaultDebitAC) {
		this.defaultDebitAC = defaultDebitAC;
	}

	@OneToOne
	@JoinColumn(name = "DEFAULT_CREDIT_AC_ID", insertable = false, updatable = false)
	public AccountCombination getDefaultCreditAC() {
		return defaultCreditAC;
	}

	public void setDefaultCreditAC(AccountCombination defaultCreditAC) {
		this.defaultCreditAC = defaultCreditAC;
	}

	@OneToOne
	@JoinColumn(name = "TERM_ID", insertable = false, updatable = false)
	public Term getTerm() {
		return term;
	}

	public void setTerm(Term term) {
		this.term = term;
	}

	@Transient
	public String getTrimmedName() {
		return trimmedName;
	}

	public void setTrimmedName(String trimmedName) {
		this.trimmedName = trimmedName;
	}

	@Override
	public String toString() {
		return "SupplierAccount [supplierId=" + supplierId + ", name=" + name
				+ ", companyId=" + companyId + ", defaultDebitACId="
				+ defaultDebitACId + ", defaultCreditACId=" + defaultCreditACId
				+ ", termId=" + termId + ", active=" + active + ", supplier="
				+ supplier + ", debitDivisionId=" + debitDivisionId
				+ ", creditDivisionId=" + creditDivisionId
				+ ", debitAccountId=" + debitAccountId + ", creditAccountId="
				+ creditAccountId + "]";
	}
}
