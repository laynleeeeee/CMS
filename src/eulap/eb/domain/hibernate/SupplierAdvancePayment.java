package eulap.eb.domain.hibernate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.eb.service.oo.OOChild;

/**
 * Domain object representation class for SUPPLIER_ADVANCE_PAYMENT

 */

@Entity
@Table(name="SUPPLIER_ADVANCE_PAYMENT")
public class SupplierAdvancePayment extends BaseFormWorkflow {
	private Integer sequenceNumber;
	private Integer companyId;
	private Integer divisionId;
	private Integer rpurchaseOrderId;
	private Date date;
	private Integer supplierId;
	private Integer supplierAcctId;
	private String bmsNumber;
	private Date invoiceDate;
	private Date glDate;
	private Date dueDate;
	private String referenceNo;
	private String requestor;
	private String remarks;
	private Integer currencyId;
	private Integer currencyRateId;
	private Double currencyRateValue;
	private Double amount;
	private List<SupplierAdvancePaymentLine> advPaymentLines;
	private String advPaymentLineJson;
	private List<ReferenceDocument> referenceDocuments;
	private String referenceDocumentJson;
	private String referenceDocsMessage;
	private String companyName;
	private String divisionName;
	private String poNumber;
	private String supplierName;
	private String supplierAcctName;
	private Division division;
	private Integer termDays;
	private Supplier supplier;
	private SupplierAccount supplierAccount;
	private Company company;
	private Currency currency;
	private Double totalLineAmount;

	public static final int OBJECT_TYPE_ID = 24000;
	public static final int REQUESTOR_MAX_CHAR = 100;

	public enum FIELD {
		id, formWorkflowId, sequenceNumber, companyId, divisionId, rpurchaseOrderId, date, ebObjectId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "SUPPLIER_ADVANCE_PAYMENT_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Override
	@Column(name = "CREATED_BY")
	public int getCreatedBy() {
		return super.getCreatedBy();
	}

	@Override
	@Column(name = "CREATED_DATE")
	public Date getCreatedDate() {
		return super.getCreatedDate();
	}

	@Override
	@Column(name = "UPDATED_BY")
	public int getUpdatedBy() {
		return super.getUpdatedBy();
	}

	@Override
	@Column(name = "UPDATED_DATE")
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
	}

	@Column(name = "SEQUENCE_NO")
	public Integer getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	@Column(name = "COMPANY_ID")
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@Column(name = "DIVISION_ID")
	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@Column(name = "R_PURCHASE_ORDER_ID")
	public Integer getRpurchaseOrderId() {
		return rpurchaseOrderId;
	}

	public void setRpurchaseOrderId(Integer rpurchaseOrderId) {
		this.rpurchaseOrderId = rpurchaseOrderId;
	}

	@Column(name = "DATE")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Column(name = "SUPPLIER_ID")
	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	@Column(name = "SUPPLIER_ACCOUNT_ID")
	public Integer getSupplierAcctId() {
		return supplierAcctId;
	}

	public void setSupplierAcctId(Integer supplierAcctId) {
		this.supplierAcctId = supplierAcctId;
	}

	@Column(name = "BMS_NUMBER")
	public String getBmsNumber() {
		return bmsNumber;
	}

	public void setBmsNumber(String bmsNumber) {
		this.bmsNumber = bmsNumber;
	}

	@Column(name = "INVOICE_DATE")
	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	@Column(name = "GL_DATE")
	public Date getGlDate() {
		return glDate;
	}

	public void setGlDate(Date glDate) {
		this.glDate = glDate;
	}

	@Column(name = "DUE_DATE")
	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	@Column(name = "REFERENCE_NO")
	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	@Column(name = "REQUESTOR")
	public String getRequestor() {
		return requestor;
	}

	public void setRequestor(String requestor) {
		this.requestor = requestor;
	}

	@Column(name = "REMARKS")
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Column(name = "CURRENCY_ID")
	public Integer getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(Integer currencyId) {
		this.currencyId = currencyId;
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

	@Column(name = "AMOUNT")
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Override
	@Transient
	public List<OOChild> getChildren() {
		List<OOChild> children = new ArrayList<OOChild>();
		if (advPaymentLines != null) {
			children.addAll(advPaymentLines);
		}
		if (referenceDocuments != null) {
			children.addAll(referenceDocuments);
		}
		return children;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE_ID;
	}

	@Transient
	public List<SupplierAdvancePaymentLine> getAdvPaymentLines() {
		return advPaymentLines;
	}

	public void setAdvPaymentLines(List<SupplierAdvancePaymentLine> advPaymentLines) {
		this.advPaymentLines = advPaymentLines;
	}

	@Transient
	public String getAdvPaymentLineJson() {
		return advPaymentLineJson;
	}

	public void setAdvPaymentLineJson(String advPaymentLineJson) {
		this.advPaymentLineJson = advPaymentLineJson;
	}

	@Transient
	public void serializeAdvPaymentLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		advPaymentLineJson = gson.toJson(advPaymentLines);
	}

	@Transient
	public void deserializeAdvPaymentLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<SupplierAdvancePaymentLine>>(){}.getType();
		advPaymentLines = gson.fromJson(advPaymentLineJson, type);
	}

	@Transient
	public String getReferenceDocumentJson() {
		return referenceDocumentJson;
	}

	public void setReferenceDocumentJson(String referenceDocumentJson) {
		this.referenceDocumentJson = referenceDocumentJson;
	}

	@Transient
	public void serializeReferenceDocuments() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		referenceDocumentJson = gson.toJson(referenceDocuments);
	}

	@Transient
	public void deserializeReferenceDocuments() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<ReferenceDocument>>(){}.getType();
		referenceDocuments = gson.fromJson(referenceDocumentJson, type);
	}

	@Transient
	public List<ReferenceDocument> getReferenceDocuments() {
		return referenceDocuments;
	}

	public void setReferenceDocuments(List<ReferenceDocument> referenceDocuments) {
		this.referenceDocuments = referenceDocuments;
	}

	@Transient
	public String getReferenceDocsMessage() {
		return referenceDocsMessage;
	}

	public void setReferenceDocsMessage(String referenceDocsMessage) {
		this.referenceDocsMessage = referenceDocsMessage;
	}

	@Transient
	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	@OneToOne
	@JoinColumn (name = "DIVISION_ID", insertable=false, updatable=false)
	public Division getDivision() {
		return division;
	}

	public void setDivision(Division division) {
		this.division = division;
	}

	@Override
	@Transient
	public Date getGLDate() {
		return glDate;
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
	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	@Transient
	public String getSupplierAcctName() {
		return supplierAcctName;
	}

	public void setSupplierAcctName(String supplierAcctName) {
		this.supplierAcctName = supplierAcctName;
	}

	@Transient
	public Integer getTermDays() {
		return termDays;
	}

	public void setTermDays(Integer termDays) {
		this.termDays = termDays;
	}

	@Override
	@Transient
	public String getWorkflowName() {
		String workflowName = super.getWorkflowName();
		if (divisionId != null) {
			return workflowName + divisionId;
		}
		return workflowName;
	}

	@OneToOne
	@JoinColumn (name = "SUPPLIER_ID", insertable=false, updatable=false)
	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	@OneToOne
	@JoinColumn (name = "SUPPLIER_ACCOUNT_ID", insertable=false, updatable=false)
	public SupplierAccount getSupplierAccount() {
		return supplierAccount;
	}

	public void setSupplierAccount(SupplierAccount supplierAccount) {
		this.supplierAccount = supplierAccount;
	}

	@OneToOne
	@JoinColumn (name = "COMPANY_ID", insertable=false, updatable=false)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@OneToOne
	@JoinColumn (name = "CURRENCY_ID", insertable=false, updatable=false)
	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	@Transient
	public Double getTotalLineAmount() {
		return totalLineAmount;
	}

	public void setTotalLineAmount(Double totalLineAmount) {
		this.totalLineAmount = totalLineAmount;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SupplierAdvancePayment [companyId=").append(companyId).append(", divisionId=")
				.append(divisionId).append(", rpurchaseOrderId=").append(rpurchaseOrderId).append(", date=")
				.append(date).append(", supplierId=").append(supplierId).append(", supplierAcctId=")
				.append(supplierAcctId).append(", bmsNumber=").append(bmsNumber).append(", invoiceDate=")
				.append(invoiceDate).append(", glDate=").append(glDate).append(", dueDate=").append(dueDate)
				.append(", referenceNo=").append(referenceNo).append(", requestor=").append(requestor)
				.append(", remarks=").append(remarks).append(", currencyId=").append(currencyId)
				.append(", currencyRateId=").append(currencyRateId).append(", currencyRateValue=")
				.append(currencyRateValue).append(", amount=").append(amount).append("]");
		return builder.toString();
	}
}
