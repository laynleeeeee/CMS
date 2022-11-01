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
 * Object representation of AR_CUSTOMER_ACCOUNT table.

 *
 */
@Entity
@Table(name="AR_CUSTOMER_ACCOUNT")
public class ArCustomerAccount extends BaseDomain{
	private String name;
	private int arCustomerId;
	private int companyId;
	private Integer termId;
	private int defaultDebitACId;
	private Integer defaultTransactionLineId;
	private boolean active;
	private Integer debitDivisionId;
	private Integer debitAccountId;
	private ArLineSetup defaultArLineSetup;
	private AccountCombination defaultDebitAC;
	private ArCustomer arCustomer;
	private Company company;
	private Term term;
	private Integer ebObjectId;
	private EBObject ebObject;
	private Integer defaultWithdrawalSlipACId;
	private AccountCombination defaultWithdrawalSlipAC;
	private Integer withdrawalSlipDivisionId;
	private Integer withdrawalAccountId;
	private Integer defaultCustomerAdvancesACId;
	private Integer defaultRetentionACId;

	public static final int OBJECT_TYPE_ID = 103;

	public enum FIELD {
		id, name, arCustomerId, companyId, termId, defaultTransactionLineId,
		defaultDebitACId, active, defaultWithdrawalSlipACId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "AR_CUSTOMER_ACCOUNT_ID", columnDefinition = "int(10)", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name="NAME", columnDefinition="varchar(100)")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name="AR_CUSTOMER_ID", columnDefinition="int(10)")
	public int getArCustomerId() {
		return arCustomerId;
	}

	public void setArCustomerId(int arCustomerId) {
		this.arCustomerId = arCustomerId;
	}

	@Column(name="COMPANY_ID", columnDefinition="int(10)")
	public int getCompanyId() {
		return companyId;
	}

	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	@Column(name="TERM_ID", columnDefinition="int(10)")
	public Integer getTermId() {
		return termId;
	}

	public void setTermId(Integer termId) {
		this.termId = termId;
	}

	@Column(name="DEFAULT_AR_TRANSACTION_LINE_ID", columnDefinition="int(10)")
	public Integer getDefaultTransactionLineId() {
		return defaultTransactionLineId;
	}

	public void setDefaultTransactionLineId(Integer defaultTransactionLineId) {
		this.defaultTransactionLineId = defaultTransactionLineId;
	}

	@Column(name="DEFAULT_DEBIT_AC_ID", columnDefinition="int(10)")
	public int getDefaultDebitACId() {
		return defaultDebitACId;
	}

	public void setDefaultDebitACId(int defaultDebitACId) {
		this.defaultDebitACId = defaultDebitACId;
	}

	@Column(name="ACTIVE", columnDefinition="tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Transient
	public Integer getDebitDivisionId() {
		return debitDivisionId;
	}

	public void setDebitDivisionId(Integer debitDivisionId) {
		this.debitDivisionId = debitDivisionId;
	}

	@Transient
	public Integer getDebitAccountId() {
		return debitAccountId;
	}

	public void setDebitAccountId(Integer debitAccountId) {
		this.debitAccountId = debitAccountId;
	}

	@OneToOne
	@JoinColumn(name = "DEFAULT_AR_TRANSACTION_LINE_ID", insertable = false, updatable = false)
	public ArLineSetup getDefaultArLineSetup() {
		return defaultArLineSetup;
	}

	public void setDefaultArLineSetup(ArLineSetup defaultArLineSetup) {
		this.defaultArLineSetup = defaultArLineSetup;
	}

	@OneToOne
	@JoinColumn(name = "AR_CUSTOMER_ID", insertable = false, updatable = false)
	public ArCustomer getArCustomer() {
		return arCustomer;
	}

	public void setArCustomer(ArCustomer arCustomer) {
		this.arCustomer = arCustomer;
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
	@JoinColumn(name = "TERM_ID", insertable = false, updatable = false)
	public Term getTerm() {
		return term;
	}

	public void setTerm(Term term) {
		this.term = term;
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
	@JoinColumn (name="EB_OBJECT_ID", nullable=true, insertable=false, updatable=false)
	public EBObject getEbObject() {
		return ebObject;
	}

	public void setEbObject(EBObject ebObject) {
		this.ebObject = ebObject;
	}

	@Column(name="EB_OBJECT_ID", columnDefinition="int(10)")
	public Integer getEbObjectId() {
		return ebObjectId;
	}

	public void setEbObjectId(Integer eBObjecctId) {
		this.ebObjectId = eBObjecctId;
	}

	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE_ID;
	}

	@Column(name="DEFAULT_WITHDRAWAL_SLIP_AC_ID", columnDefinition="INT(10)")
	public Integer getDefaultWithdrawalSlipACId() {
		return defaultWithdrawalSlipACId;
	}

	public void setDefaultWithdrawalSlipACId(Integer defaultWithdrawalSlipACId) {
		this.defaultWithdrawalSlipACId = defaultWithdrawalSlipACId;
	}

	@OneToOne
	@JoinColumn(name = "DEFAULT_WITHDRAWAL_SLIP_AC_ID", insertable = false, updatable = false)
	public AccountCombination getDefaultWithdrawalSlipAC() {
		return defaultWithdrawalSlipAC;
	}

	public void setDefaultWithdrawalSlipAC(AccountCombination defaultWithdrawalSlipAC) {
		this.defaultWithdrawalSlipAC = defaultWithdrawalSlipAC;
	}

	@Transient
	public Integer getWithdrawalSlipDivisionId() {
		return withdrawalSlipDivisionId;
	}

	public void setWithdrawalSlipDivisionId(Integer withdrawalSlipDivisionId) {
		this.withdrawalSlipDivisionId = withdrawalSlipDivisionId;
	}

	@Transient
	public Integer getWithdrawalAccountId() {
		return withdrawalAccountId;
	}

	public void setWithdrawalAccountId(Integer withdrawalAccountId) {
		this.withdrawalAccountId = withdrawalAccountId;
	}

	@Column(name="DEFAULT_CUSTOMER_ADVANCES_AC_ID", columnDefinition="int(10)")
	public Integer getDefaultCustomerAdvancesACId() {
		return defaultCustomerAdvancesACId;
	}

	public void setDefaultCustomerAdvancesACId(Integer defaultCustomerAdvancesACId) {
		this.defaultCustomerAdvancesACId = defaultCustomerAdvancesACId;
	}

	@Column(name="DEFAULT_RETENTION_AC_ID", columnDefinition="int(10)")
	public Integer getDefaultRetentionACId() {
		return defaultRetentionACId;
	}

	public void setDefaultRetentionACId(Integer defaultRetentionACId) {
		this.defaultRetentionACId = defaultRetentionACId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ArCustomerAccount [name=").append(name).append(", arCustomerId=").append(arCustomerId)
				.append(", companyId=").append(companyId).append(", termId=").append(termId)
				.append(", defaultDebitACId=").append(defaultDebitACId).append(", defaultTransactionLineId=")
				.append(defaultTransactionLineId).append(", active=").append(active).append(", debitDivisionId=")
				.append(debitDivisionId).append(", debitAccountId=").append(debitAccountId)
				.append(", defaultArLineSetup=").append(defaultArLineSetup).append(", defaultDebitAC=")
				.append(defaultDebitAC).append(", arCustomer=").append(arCustomer).append(", ebObjectId=")
				.append(ebObjectId).append(", defaultWithdrawalSlipACId=").append(defaultWithdrawalSlipACId)
				.append(", withdrawalSlipDivisionId=").append(withdrawalSlipDivisionId).append(", withdrawalAccountId=")
				.append(withdrawalAccountId).append("]");
		return builder.toString();
	}

}
