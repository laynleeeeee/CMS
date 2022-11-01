package eulap.eb.domain.hibernate;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.eb.service.oo.OOChild;
import eulap.eb.web.dto.ApInvoiceDto;
import eulap.eb.web.dto.ApPaymentLineDto;

/**
 * Object representation of AP_PAYMENT table.

 */
@Entity
@Table(name = "AP_PAYMENT")
public class ApPayment extends BaseFormWorkflow{
	private Integer bankAccountId;
	private Integer checkbookId;
	private Integer companyId;
	private Integer supplierId;
	private Integer supplierAccountId;
	private BigDecimal checkNumber;
	private Integer voucherNumber;
	private double amount;
	private Date paymentDate;
	private Date checkDate;
	private String apInvoiceMessage;
	private List<ApInvoiceDto> apInvoices;
	private Company company;
	private Supplier supplier;
	private SupplierAccount supplierAccount;
	private BankAccount bankAccount;
	private Checkbook checkbook;
	private List<ApPaymentInvoice> apPaymentInvoices;
	private String payee;
	private boolean specifyPayee;
	private int paymentTypeId;
	private Integer currencyId;
	private Integer currencyRateId;
	private double currencyRateValue;
	private Integer divisionId;
	private Division division;
	private Currency currency;
	private String referenceDocumentJson;
	private List<ReferenceDocument> referenceDocuments;
	private String referenceDocsMessage;
	private String officialReceipt;
	private String remarks;
	private List<ApPaymentLine> apPaymentLines;
	private List<ApPaymentLineDto> apPaymentLineDtos;
	private String apPaymentLineDtosJson;
	private Date dateCleared;

	public static final int MAX_PAYEE = 50;

	/**
	 * Object Type Id for Ap Payment = 129.
	 */
	public static final int AP_PAYMENT_OBJECT_TYPE_ID = 129;

	public enum FIELD {
		id, ebObjectId, formWorkflowId, bankAccountId, checkbookId, companyId, supplierId,
		supplierAccountId, checkNumber, voucherNumber, amount, paymentDate, checkDate, currencyId, paymentTypeId, divisionId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "AP_PAYMENT_ID", columnDefinition = "int(10)", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return AP_PAYMENT_OBJECT_TYPE_ID;
	}
	@Column(name = "BANK_ACCOUNT_ID", columnDefinition = "int(10)")
	public Integer getBankAccountId() {
		return bankAccountId;
	}

	public void setBankAccountId(Integer bankAccountId) {
		this.bankAccountId = bankAccountId;
	}

	@Column(name = "CHECKBOOK_ID", columnDefinition = "int(10)")
	public Integer getCheckbookId() {
		return checkbookId;
	}

	public void setCheckbookId(Integer checkbookId) {
		this.checkbookId = checkbookId;
	}

	@Column(name = "COMPANY_ID", columnDefinition = "int(10)")
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@Column(name = "SUPPLIER_ID", columnDefinition = "int(10)")
	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	@Column(name = "SUPPLIER_ACCOUNT_ID", columnDefinition = "int(10)")
	public Integer getSupplierAccountId() {
		return supplierAccountId;
	}

	public void setSupplierAccountId(Integer supplierAccountId) {
		this.supplierAccountId = supplierAccountId;
	}

	@Column(name = "CHECK_NUMBER", columnDefinition = "BIGDECIMAL(20, 0)")
	public BigDecimal getCheckNumber() {
		return checkNumber;
	}

	public void setCheckNumber(BigDecimal checkNumber) {
		this.checkNumber = checkNumber;
	}

	@Column(name = "VOUCHER_NO", columnDefinition = "int(10)")
	public Integer getVoucherNumber() {
		return voucherNumber;
	}

	public void setVoucherNumber(Integer voucherNumber) {
		this.voucherNumber = voucherNumber;
	}

	@Column(name = "AMOUNT", columnDefinition = "DOUBLE")
	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	@Column(name = "PAYMENT_DATE", columnDefinition = "date")
	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	@Column(name = "CHECK_DATE", columnDefinition = "date")
	public Date getCheckDate() {
		return checkDate;
	}

	public void setCheckDate(Date checkDate) {
		this.checkDate = checkDate;
	}

	@Transient
	public String getApInvoiceMessage() {
		return apInvoiceMessage;
	}

	public void setApInvoiceMessage(String apInvoiceMessage) {
		this.apInvoiceMessage = apInvoiceMessage;
	}

	@Transient
	public List<ApInvoiceDto> getApInvoices() {
		return apInvoices;
	}

	public void setApInvoices(List<ApInvoiceDto> apInvoices) {
		this.apInvoices = apInvoices;
	}

	@ManyToOne
	@JoinColumn(name = "COMPANY_ID", nullable = false, updatable = false, insertable = false)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@ManyToOne
	@JoinColumn(name = "SUPPLIER_ID", nullable = false, updatable = false, insertable = false)
	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	@ManyToOne
	@JoinColumn(name = "SUPPLIER_ACCOUNT_ID", nullable = false, updatable = false, insertable = false)
	public SupplierAccount getSupplierAccount() {
		return supplierAccount;
	}

	public void setSupplierAccount(SupplierAccount supplierAccount) {
		this.supplierAccount = supplierAccount;
	}

	@Transient
	public BankAccount getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(BankAccount bankAccount) {
		this.bankAccount = bankAccount;
	}

	@Transient
	public List<ApPaymentInvoice> getApPaymentInvoices() {
		return apPaymentInvoices;
	}

	public void setApPaymentInvoices(List<ApPaymentInvoice> apPaymentInvoices) {
		this.apPaymentInvoices = apPaymentInvoices;
	}

	@Transient
	public Checkbook getCheckbook() {
		return checkbook;
	}

	public void setCheckbook(Checkbook checkbook) {
		this.checkbook = checkbook;
	}

	@Column(name = "PAYEE", columnDefinition = "VARCHAR(50)")
	public String getPayee() {
		return payee;
	}

	public void setPayee(String payee) {
		this.payee = payee;
	}

	@Column(name = "SPECIFY_PAYEE", columnDefinition = "tinyint(1)")
	public boolean isSpecifyPayee() {
		return specifyPayee;
	}

	public void setSpecifyPayee(boolean specifyPayee) {
		this.specifyPayee = specifyPayee;
	}

	@Column(name = "PAYMENT_TYPE_ID", columnDefinition = "INT(10)")
	public int getPaymentTypeId() {
		return paymentTypeId;
	}

	public void setPaymentTypeId(int paymentTypeId) {
		this.paymentTypeId = paymentTypeId;
	}

	@Column (name = "CURRENCY_ID")
	public Integer getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(Integer currencyId) {
		this.currencyId = currencyId;
	}

	@Column (name = "CURRENCY_RATE_ID")
	public Integer getCurrencyRateId() {
		return currencyRateId;
	}

	public void setCurrencyRateId(Integer currencyRateId) {
		this.currencyRateId = currencyRateId;
	}

	@Column (name = "CURRENCY_RATE_VALUE")
	public double getCurrencyRateValue() {
		return currencyRateValue;
	}

	public void setCurrencyRateValue(double currencyRateValue) {
		this.currencyRateValue = currencyRateValue;
	}

	@Transient
	public String getReferenceDocumentJson() {
		return referenceDocumentJson;
	}

	public void setReferenceDocumentJson(String referenceDocumentJson) {
		this.referenceDocumentJson = referenceDocumentJson;
	}

	@Transient
	public void serializeReferenceDocuments(){
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		referenceDocumentJson = gson.toJson(referenceDocuments);
	}

	@Transient
	public void deserializeReferenceDocuments(){
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

	@OneToOne
	@JoinColumn (name = "DIVISION_ID", insertable=false, updatable=false)
	public Division getDivision() {
		return division;
	}

	public void setDivision(Division division) {
		this.division = division;
	}

	@OneToOne
	@JoinColumn (name = "CURRENCY_ID", insertable=false, updatable=false)
	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	@Column (name = "OFFICIAL_RECEIPT")
	public String getOfficialReceipt() {
		return officialReceipt;
	}

	public void setOfficialReceipt(String officialReceipt) {
		this.officialReceipt = officialReceipt;
	}

	@Column (name = "REMARKS")
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Transient
	public List<ApPaymentLine> getApPaymentLines() {
		return apPaymentLines;
	}

	public void setApPaymentLines(List<ApPaymentLine> apPaymentLines) {
		this.apPaymentLines = apPaymentLines;
	}

	@Transient
	public List<ApPaymentLineDto> getApPaymentLineDtos() {
		return apPaymentLineDtos;
	}

	public void setApPaymentLineDtos(List<ApPaymentLineDto> apPaymentLineDtos) {
		this.apPaymentLineDtos = apPaymentLineDtos;
	}

	@Transient
	public String getApPaymentLineDtosJson() {
		return apPaymentLineDtosJson;
	}

	public void setApPaymentLineDtosJson(String apPaymentLineDtosJson) {
		this.apPaymentLineDtosJson = apPaymentLineDtosJson;
	}

	@Transient
	public void serializeApPaymentLineDtos() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		apPaymentLineDtosJson = gson.toJson(apPaymentLineDtos);
	}

	@Transient
	public void deserializeApPaymentLineDtos() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<ApPaymentLineDto>>(){}.getType();
		apPaymentLineDtos = gson.fromJson(apPaymentLineDtosJson, type);
	}

	@Column (name = "DIVISION_ID")
	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@Column (name = "DATE_CLEARED")
	public Date getDateCleared() {
		return dateCleared;
	}

	public void setDateCleared(Date dateCleared) {
		this.dateCleared = dateCleared;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ApPayment [bankAccountId=").append(bankAccountId).append(", checkbookId=").append(checkbookId)
				.append(", companyId=").append(companyId).append(", supplierId=").append(supplierId)
				.append(", supplierAccountId=").append(supplierAccountId).append(", checkNumber=").append(checkNumber)
				.append(", voucherNumber=").append(voucherNumber).append(", amount=").append(amount)
				.append(", paymentDate=").append(paymentDate).append(", checkDate=").append(checkDate)
				.append(", apInvoices=").append(apInvoices).append(", specifyPayee=").append(specifyPayee)
				.append(", paymentTypeId=").append(paymentTypeId).append(", currencyId=").append(currencyId)
				.append(", currencyRateId=").append(currencyRateId).append(", currencyRateValue=")
				.append(currencyRateValue).append(", referenceDocumentJson=").append(referenceDocumentJson)
				.append(", referenceDocuments=").append(referenceDocuments).append(", referenceDocsMessage=")
				.append(referenceDocsMessage).append(", officialReceipt=").append(officialReceipt).append(", remarks=")
				.append(remarks).append("]");
		return builder.toString();
	}

	@Override
	@Transient
	public List<OOChild> getChildren() {
		List<OOChild> children = new ArrayList<>();
		if(apPaymentInvoices != null) {
			children.addAll(apPaymentInvoices);
		}
		if(apPaymentLines != null) {
			children.addAll(apPaymentLines);
		}
		if(referenceDocuments != null) {
			children.addAll(referenceDocuments);
		}
		return children;
	}

	@Override
	@Transient
	public String getWorkflowName() {
		String workflowName = super.getWorkflowName();
		if (paymentTypeId != 0) {
			if (paymentTypeId == PaymentType.TYPE_DIRECT_PAYMENT) {
				return DirectPayment.class.getSimpleName();
			}
			return workflowName + divisionId;
		}
		return workflowName;
	}
}