package eulap.eb.domain.hibernate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.eb.service.oo.OOChild;
import eulap.eb.web.dto.ApLineDto;

/**
 * A class that represents the LOAN_PROCEEDS table in the CBS database.

 * 
 */
@Entity
@Table (name="LOAN_PROCEEDS")
public class LoanProceeds extends BaseFormWorkflow {
	private Integer sequenceNumber;
	private Integer supplierId;
	private Integer termId;
	private Integer supplierAccountId;
	private double amount;
	private Date glDate;
	private Date date;
	private String description;
	private Supplier supplier;
	private SupplierAccount supplierAccount;
	private Term term;
	private List<ReferenceDocument> referenceDocuments;
	private String referenceDocumentsJson;
	private String referenceDocsMessage;
	private Integer wtAcctSettingId;
	private double wtAmount;
	private WithholdingTaxAcctSetting wtAcctSetting;
	private Integer divisionId;
	private Division division;
	private Integer companyId;
	private Company company;
	private Integer referenceObjectId;
	private Integer currencyId;
	private Integer currencyRateId;
	private Double currencyRateValue;
	private Currency currency;
	private String lpLinesJson;
	private List<LPLine> lPlines;
	private String lPlineMessage;
	private List<ApLineDto> apLineDtos;
	private Integer loanProceedsTypeId;
	private Integer receiptMethodId;
	private ReceiptMethod receiptMethod;
	private Integer loanAccountId;
	private Account loanAccount;
	private Double loanProceedAmt;

	/**
	 * Object type id of LOAN PROCEEDS OBJECT TYPE = 24011
	 */
	public static final int LOAN_PROCEEDS_OBJECT_TYPE = 24011;

	public enum FIELD {id, sequenceNumber, supplierId, termId, supplierAccountId, amount,
		date, glDate, description, divisionId, companyId, createdDate, loanProceedsTypeId, receiptMethodId};

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "LOAN_PROCEEDS_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name = "SEQUENCE_NO", columnDefinition = "INT(10)")
	public Integer getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	@Column(name = "SUPPLIER_ID", columnDefinition = "INT(10)")
	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	@Column(name = "TERM_ID", columnDefinition = "INT(10)")
	public Integer getTermId() {
		return termId;
	}

	public void setTermId(Integer termId) {
		this.termId = termId;
	}

	@Column(name = "SUPPLIER_ACCOUNT_ID", columnDefinition = "INT(10)")
	public Integer getSupplierAccountId() {
		return supplierAccountId;
	}

	public void setSupplierAccountId(Integer supplierAccountId) {
		this.supplierAccountId = supplierAccountId;
	}

	@Column(name = "AMOUNT", columnDefinition = "DOUBLE")
	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	/**
	 * Get the GL Date of AP Invoice/RR Date of {@link RReceivingReport}.
	 */
	@Column(name = "GL_DATE", columnDefinition = "DATE")
	public Date getGlDate() {
		return glDate;
	}

	public void setGlDate(Date glDate) {
		this.glDate = glDate;
	}

	/**
	 * Get the due date of AP Invoice.
	 */
	@Column(name = "DATE", columnDefinition = "DATE")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * Get the description of AP Invoice.
	 */
	@Column(name = "DESCRIPTION", columnDefinition = "TEXT")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	/**
	 * Get the Supplier associated with AP Invoice.
	 */
	@ManyToOne
	@JoinColumn(name="SUPPLIER_ID", insertable=false, updatable=false)
	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	/**
	 * Get the Supplier Account associated with AP Invoice.
	 */
	@ManyToOne
	@JoinColumn(name="SUPPLIER_ACCOUNT_ID", insertable=false, updatable=false)
	public SupplierAccount getSupplierAccount() {
		return supplierAccount;
	}

	public void setSupplierAccount(SupplierAccount supplierAccount) {
		this.supplierAccount = supplierAccount;
	}

	@OneToOne
	@JoinColumn(name="TERM_ID", insertable=false, updatable=false)
	public Term getTerm() {
		return term;
	}

	public void setTerm(Term term) {
		this.term = term;
	}


	@Override
	@Transient
	public List<OOChild> getChildren() {
		List<OOChild> children = new ArrayList<OOChild>();
		if (referenceDocuments != null) {
			children.addAll(referenceDocuments);
		}
		if (lPlines != null) {
			children.addAll(lPlines);
		}
		return children;
	}

	@Override
	@Transient
	public String getWorkflowName() {
		return super.getWorkflowName() + getLoanProceedsTypeId();
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return LOAN_PROCEEDS_OBJECT_TYPE;
	}

	@Override
	@Transient
	public String getShortDescription() {
		return "LP" + " " + sequenceNumber;
	}

	@Transient
	public List<ReferenceDocument> getReferenceDocuments() {
		return referenceDocuments;
	}

	public void setReferenceDocuments(List<ReferenceDocument> referenceDocuments) {
		this.referenceDocuments = referenceDocuments;
	}

	@Transient
	public String getReferenceDocumentsJson() {
		return referenceDocumentsJson;
	}

	public void setReferenceDocumentsJson(String referenceDocumentsJson) {
		this.referenceDocumentsJson = referenceDocumentsJson;
	}

	@Transient
	public void serializeReferenceDocuments (){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		referenceDocumentsJson = gson.toJson(referenceDocuments);
	}

	@Transient
	public void deserializeReferenceDocuments () {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<ReferenceDocument>>(){}.getType();
		referenceDocuments = gson.fromJson(referenceDocumentsJson, type);
	}

	@Transient
	public String getReferenceDocsMessage() {
		return referenceDocsMessage;
	}

	public void setReferenceDocsMessage(String referenceDocsMessage) {
		this.referenceDocsMessage = referenceDocsMessage;
	}

	@Override
	@Transient
	public Date getGLDate() {
		return glDate;
	}


	@Column(name = "WT_ACCOUNT_SETTING_ID")
	public Integer getWtAcctSettingId() {
		return wtAcctSettingId;
	}

	public void setWtAcctSettingId(Integer wtAcctSettingId) {
		this.wtAcctSettingId = wtAcctSettingId;
	}

	@Column(name = "WT_AMOUNT")
	public double getWtAmount() {
		return wtAmount;
	}

	public void setWtAmount(double wtAmount) {
		this.wtAmount = wtAmount;
	}

	@OneToOne
	@JoinColumn(name = "WT_ACCOUNT_SETTING_ID", insertable=false, updatable=false)
	public WithholdingTaxAcctSetting getWtAcctSetting() {
		return wtAcctSetting;
	}

	public void setWtAcctSetting(WithholdingTaxAcctSetting wtAcctSetting) {
		this.wtAcctSetting = wtAcctSetting;
	}

	@Column(name = "DIVISION_ID", columnDefinition="int(10)")
	public Integer getDivisionId() {
		return divisionId;
	}
	
	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@OneToOne
	@JoinColumn(name="DIVISION_ID", insertable=false, updatable=false)
	public Division getDivision() {
		return division;
	}

	public void setDivision(Division division) {
		this.division = division;
	}

	@Column(name = "COMPANY_ID", columnDefinition="int(10)")
	public Integer getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@OneToOne
	@JoinColumn(name="COMPANY_ID", insertable=false, updatable=false)
	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}

	@Column(name = "CURRENCY_ID", columnDefinition="int(10)")
	public Integer getCurrencyId() {
		return currencyId;
	}
	public void setCurrencyId(Integer currencyId) {
		this.currencyId = currencyId;
	}

	@OneToOne
	@JoinColumn(name="CURRENCY_ID", insertable=false, updatable=false)
	public Currency getCurrency() {
		return currency;
	}
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}


	@Transient
	public Integer getReferenceObjectId() {
		return referenceObjectId;
	}

	public void setReferenceObjectId(Integer referenceObjectId) {
		this.referenceObjectId = referenceObjectId;
	}


	@Column(name = "CURRENCY_RATE_ID")
	public Integer getCurrencyRateId() {
		return currencyRateId;
	}

	public void setCurrencyRateId(Integer currencyRateId) {
		this.currencyRateId = currencyRateId;
	}

	@Column(name = "CURRENCY_RATE_VALUE")
	public Double getCurrencyRateValue() {
		return currencyRateValue;
	}

	public void setCurrencyRateValue(Double currencyRateValue) {
		this.currencyRateValue = currencyRateValue;
	}

	@Transient
	public String getLpLinesJson() {
		return lpLinesJson;
	}

	public void setLpLinesJson(String lpLinesJson) {
		this.lpLinesJson = lpLinesJson;
	}

	@Transient
	public void serializeLpLines(){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		lpLinesJson = gson.toJson(lPlines);
	}

	@Transient
	public void deserializeLpLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<LPLine>>(){}.getType();
		lPlines = gson.fromJson(lpLinesJson, type);
	}

	@Transient
	public String getlPlineMessage() {
		return lPlineMessage;
	}

	public void setlPlineMessage(String lPlineMessage) {
		this.lPlineMessage = lPlineMessage;
	}

	@Fetch(FetchMode.SELECT)
	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn (name = "LOAN_PROCEEDS_ID", insertable=false, updatable=false)
	public List<LPLine> getlPlines() {
		return lPlines;
	}

	public void setlPlines(List<LPLine> lPlines) {
		this.lPlines = lPlines;
	}

	@Transient
	public List<ApLineDto> getApLineDtos() {
		return apLineDtos;
	}

	public void setApLineDtos(List<ApLineDto> apLineDtos) {
		this.apLineDtos = apLineDtos;
	}

	@Column(name = "LOAN_PROCEEDS_TYPE_ID")
	public Integer getLoanProceedsTypeId() {
		return loanProceedsTypeId;
	}

	public void setLoanProceedsTypeId(Integer loanProceedsTypeId) {
		this.loanProceedsTypeId = loanProceedsTypeId;
	}

	@Column(name = "RECEIPT_METHOD_ID")
	public Integer getReceiptMethodId() {
		return receiptMethodId;
	}

	public void setReceiptMethodId(Integer receiptMethodId) {
		this.receiptMethodId = receiptMethodId;
	}

	@Transient
	public ReceiptMethod getReceiptMethod() {
		return receiptMethod;
	}

	public void setReceiptMethod(ReceiptMethod receiptMethod) {
		this.receiptMethod = receiptMethod;
	}

	@Column(name = "LOAN_ACCOUNT_ID")
	public Integer getLoanAccountId() {
		return loanAccountId;
	}

	public void setLoanAccountId(Integer loanAccountId) {
		this.loanAccountId = loanAccountId;
	}

	@OneToOne
	@JoinColumn(name = "LOAN_ACCOUNT_ID", insertable=false, updatable=false)
	public Account getLoanAccount() {
		return loanAccount;
	}

	public void setLoanAccount(Account loanAccount) {
		this.loanAccount = loanAccount;
	}

	@Column(name = "LOAN_PROCEED_AMOUNT")
	public Double getLoanProceedAmt() {
		return loanProceedAmt;
	}

	public void setLoanProceedAmt(Double loanProceedAmt) {
		this.loanProceedAmt = loanProceedAmt;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LoanProceeds [sequenceNumber=").append(sequenceNumber).append(", supplierId=")
				.append(supplierId).append(", termId=").append(termId).append(", supplierAccountId=")
				.append(supplierAccountId).append(", amount=").append(amount).append(", glDate=").append(glDate)
				.append(", date=").append(date).append(", description=").append(description)
				.append(", referenceDocumentsJson=").append(referenceDocumentsJson).append(", referenceDocsMessage=")
				.append(referenceDocsMessage).append(", wtAcctSettingId=").append(wtAcctSettingId).append(", wtAmount=")
				.append(wtAmount).append(", divisionId=").append(divisionId).append(", companyId=").append(companyId)
				.append(", referenceObjectId=").append(referenceObjectId).append(", currencyId=").append(currencyId)
				.append(", currencyRateId=").append(currencyRateId).append(", currencyRateValue=")
				.append(currencyRateValue).append(", lpLinesJson=").append(lpLinesJson).append(", lPlineMessage=")
				.append(lPlineMessage).append(", apLineDtos=").append(apLineDtos).append(", loanProceedsTypeId=")
				.append(loanProceedsTypeId).append(", receiptMethodId=").append(receiptMethodId)
				.append(", loanAccountId=").append(loanAccountId).append("]");
		return builder.toString();
	}
}
