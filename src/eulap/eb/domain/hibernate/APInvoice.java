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

import eulap.common.util.DateUtil;
import eulap.eb.service.oo.OOChild;
import eulap.eb.web.dto.ApLineDto;
import eulap.eb.web.dto.RrRawMatItemDto;

/**
 * A class that represents the AP_INVOICES table in the CBS database.
 * 

 * 
 */
@Entity
@Table (name="AP_INVOICE")
public class APInvoice extends BaseFormWorkflow {
	private Integer serviceLeaseKeyId;
	private Integer sequenceNumber;
	private Integer supplierId;
	private Integer invoiceTypeId;
	private Integer invoiceClassificationId;
	private Integer termId;
	private Integer supplierAccountId;
	private String invoiceNumber;
	private double amount;
	private Date invoiceDate;
	private Date glDate;
	private Date dueDate;
	private String description;
	private List<APLine> aPlines;
	private String aPlineMessage;
	private boolean isPosting;
	private Double balance;
	private int paymentStatus;
	private Supplier supplier;
	private SupplierAccount supplierAccount;
	private Term term;
	private InvoiceType invoiceType;
	private List<ApLineDto> apLineDtos;
	private List<RReceivingReportItem> rrItems;
	private RReceivingReport receivingReport;
	private String rrItemsJson;
	private List<RReturnToSupplierItem> rtsItems;
	private RReturnToSupplier returnToSupplier;
	private String rtsItemsJson;
	private String referenceNo;
	private String accountCode;
	private String accountTitle;
	private List<ApInvoiceLine> apInvoiceLines;
	private String apInvoiceLinesJson;
	private List<ReferenceDocument> referenceDocuments;
	private String referenceDocumentsJson;
	private String referenceDocsMessage;
	private RPurchaseOrder purchaseOrder;
	private Integer purchaseOderId;
	private String poNumber;
	private List<RriBagQuantity> rriBagQuantities;
	private String rriBagQuantitiesJson;
	private String rriBagQtyErrMsg;
	private RrRawMatItemDto rrRawMatItemDto;
	private List<RriBagDiscount> rriBagDiscounts;
	private String rriBagDiscountsJson;
	private String rriBagDiscountErrMsg;
	private String errorMsgStockCode;
	private String errorMsgBuyingPrice;
	private List<ApInvoiceItem> apInvoiceItems;
	private String apLinesJson;
	private Integer wtAcctSettingId;
	private double wtAmount;
	private WithholdingTaxAcctSetting wtAcctSetting;
	private String bmsNumber;
	private Integer divisionId;
	private Division division;
	private Integer companyId;
	private Company company;
	private Integer currencyId;
	private Integer currencyRateId;
	private Double currencyRateValue;
	private Currency currency;
	private InvoiceClassification invoiceClassification;
	private Integer rrNumber;
	private Integer referenceObjectId;
	private List<ApInvoiceGoods> apInvoiceGoods;
	private String apInvoiceGoodsJson;
	private List<SerialItem> serialItems;
	private String serialItemsJson;
	private Integer warehouseId;
	private String strInvoiceDate;
	private String strGlDate;
	private String apInvoiceErrMessage;
	private InvoiceImportationDetails invoiceImportationDetails;
	private List<APLine> summarizedApLines;
	private String summarizedApLinesJson;
	/**
	 * For ap loan.
	 */
	private Double principalPayment;
	private Double principalLoan;
	private Integer lpNumber;
	private Integer loanAccountId;
	private Account loanAccount;
	private Integer userCustodianId;
	private UserCustodian userCustodian;
	private List<PettyCashReplenishmentLine> pcrls;
	private String pcrlsJson;
	private String pcrlErrorMessage;

	public static final int MAX_CHAR_INVOICE_NO = 100;
	public static final int MAX_CHAR_BMS = 50;

	public static final int AP_INVOICE_OBJECT_TYPE_ID = 1;
	public static final int RECEIVING_REPORT_OBJECT_TYPE_ID = 160;
	public static final int RTS_OBJECT_TYPE_ID = 161;
	public static final int RR_RAW_MATERIAL_OBJECT_TYPE_ID = 162;
	public static final int RR_NET_WEIGHT_OBJECT_TYPE_ID = 163;
	public static final int INVOICE_ITEM_OBJECT_TYPE_ID = 12025;
	public static final int INVOICE_SERVICE_OBJECT_TYPE_ID = 12026;
	public static final int INVOICE_GS_OBJECT_TYPE_ID = 24002;
	public static final int AP_LOAN_OBJECT_TYPE_ID = 24017;
	public static final int PCR_OBJECT_TYPE_ID = 24016;

	public static final int GS_SERIAL_ITEM_OR_TYPE_ID = 24001;
	public static final int RR_INVOICE_GS_OR_TYPE_ID = 24002;
	public static final int GS_INVOICE_CHILD_TO_CHILD_OR_TYPE_ID = 24003;
	public static final int LP_AP_LOAN_OR_TYPE_ID = 24009;

	public static final int STATUS_ALL = 1;
	public static final int STATUS_UNUSED = 2;
	public static final int STATUS_USED = 3;

	public enum FIELD {id, formWorkflowId, serviceLeaseKeyId, companyId, divisionId, currencyId, sequenceNumber, supplierId,
		invoiceTypeId, termId, supplierAccountId, invoiceNumber, amount, invoiceDate, referenceNo, 
		glDate, dueDate, description, paymentStatus, supplier, supplierAccount, createdDate,
		ebObjectId, invoiceClassificationId, userCustodianId};

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "AP_INVOICE_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name = "EB_SL_KEY_ID", columnDefinition = "INT(10)")
	public int getServiceLeaseKeyId() {
		return serviceLeaseKeyId;
	}

	public void setServiceLeaseKeyId(Integer serviceLeaseKeyId) {
		this.serviceLeaseKeyId = serviceLeaseKeyId;
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

	@Column(name = "INVOICE_TYPE_ID", columnDefinition = "INT(10)")
	public Integer getInvoiceTypeId() {
		return invoiceTypeId;
	}

	public void setInvoiceTypeId(Integer invoiceTypeId) {
		this.invoiceTypeId = invoiceTypeId;
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

	@Column(name = "INVOICE_NUMBER", columnDefinition = "VARCHAR(100)")
	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	@Column(name = "AMOUNT", columnDefinition = "DOUBLE")
	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	/**
	 * Get the Invoice Date of AP Invoice/Invoice date of {@link RReceivingReport}
	 */
	@Column(name = "INVOICE_DATE", columnDefinition = "DATE")
	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
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
	@Column(name = "DUE_DATE", columnDefinition = "DATE")
	public Date getDueDate() {
		return dueDate;
	}
	
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
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

	@Fetch(FetchMode.SELECT)
	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn (name = "AP_INVOICE_ID", insertable=false, updatable=false)
	public List<APLine> getaPlines() {
		return aPlines;
	}

	public void setaPlines(List<APLine> aPlines) {
		this.aPlines = aPlines;
	}

	@Transient
	public String getAPlineMessage() {
		return aPlineMessage;
	}

	public void setAPlineMessage(String aPlineMessage) {
		this.aPlineMessage = aPlineMessage;
	}

	@Transient
	public boolean isPosting() {
		return isPosting;
	}

	public void setPosting(boolean isPosting) {
		this.isPosting = isPosting;
	}

	@Transient
	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	@Transient
	public int getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(int paymentStatus) {
		this.paymentStatus = paymentStatus;
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
	
	@Transient
	public List<ApLineDto> getApLineDtos() {
		return apLineDtos;
	}
	
	public void setApLineDtos(List<ApLineDto> apLineDtos) {
		this.apLineDtos = apLineDtos;
	}

	@OneToOne
	@JoinColumn(name="TERM_ID", insertable=false, updatable=false)
	public Term getTerm() {
		return term;
	}

	public void setTerm(Term term) {
		this.term = term;
	}

	@OneToOne
	@JoinColumn(name="INVOICE_TYPE_ID", insertable=false, updatable=false)
	public InvoiceType getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(InvoiceType invoiceType) {
		this.invoiceType = invoiceType;
	}

	/**
	 * Get the list of associated receiving report items.
	 * @return The list of associated receiving report items.
	 */
	@Fetch(FetchMode.SELECT)
	@OneToMany(fetch=FetchType.LAZY)
	@JoinColumn(name="AP_INVOICE_ID", insertable=false, updatable=false)
	public List<RReceivingReportItem> getRrItems() {
		return rrItems;
	}

	public void setRrItems(List<RReceivingReportItem> rrItems) {
		this.rrItems = rrItems;
	}

	@Fetch(FetchMode.SELECT)
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="AP_INVOICE_ID", insertable=false, updatable=false)
	public RReceivingReport getReceivingReport() {
		return receivingReport;
	}

	public void setReceivingReport(RReceivingReport receivingReport) {
		this.receivingReport = receivingReport;
	}

	@Transient
	public String getRrItemsJson() {
		return rrItemsJson;
	}

	public void setRrItemsJson(String rrItemsJson) {
		this.rrItemsJson = rrItemsJson;
	}

	@Transient
	public void serializeRrItems (){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		rrItemsJson = gson.toJson(rrItems);
	}

	@Transient
	public void deserializeRrItems () {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<RReceivingReportItem>>(){}.getType();
		rrItems = gson.fromJson(rrItemsJson, type);
	}

	/**
	 * Get the list of associated receiving report items.
	 * @return The list of associated receiving report items.
	 */
	@Fetch(FetchMode.SELECT)
	@OneToMany(fetch=FetchType.LAZY)
	@JoinColumn(name="AP_INVOICE_ID", insertable=false, updatable=false)
	public List<RReturnToSupplierItem> getRtsItems() {
		return rtsItems;
	}

	public void setRtsItems(List<RReturnToSupplierItem> rtsItems) {
		this.rtsItems = rtsItems;
	}

	@Transient
	public String getRtsItemsJson() {
		return rtsItemsJson;
	}

	public void setRtsItemsJson(String rtsItemsJson) {
		this.rtsItemsJson = rtsItemsJson;
	}

	/**
	 * Get the associated domain object receivingReport. 
	 * @return The associated domain object receivingReport.
	 */
	@OneToOne
	@JoinColumn (name = "AP_INVOICE_ID", insertable=false, updatable=false)
	public RReturnToSupplier getReturnToSupplier() {
		return returnToSupplier;
	}

	public void setReturnToSupplier(RReturnToSupplier returnToSupplier) {
		this.returnToSupplier = returnToSupplier;
	}

	@Transient
	public void serializeRtsItems (){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		rtsItemsJson = gson.toJson(rtsItems);
	}

	@Transient
	public void deserializeRtsItems () {
		Gson gson = new GsonBuilder()
			.setDateFormat(DateUtil.DATE_FORMAT)
			.excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<RReturnToSupplierItem>>(){}.getType();
		rtsItems = gson.fromJson(rtsItemsJson, type);
	}

	@Transient
	public String getReferenceNo() {
		return referenceNo;
	}
	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	@Transient
	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}

	@Transient
	public String getAccountTitle() {
		return accountTitle;
	}

	public void setAccountTitle(String accountTitle) {
		this.accountTitle = accountTitle;
	}

	@Fetch(FetchMode.SELECT)
	@OneToMany(fetch=FetchType.LAZY)
	@JoinColumn(name="AP_INVOICE_ID", insertable=false, updatable=false)
	public List<ApInvoiceLine> getApInvoiceLines() {
		return apInvoiceLines;
	}

	public void setApInvoiceLines(List<ApInvoiceLine> apInvoiceLines) {
		this.apInvoiceLines = apInvoiceLines;
	}

	@Transient
	public String getApInvoiceLinesJson() {
		return apInvoiceLinesJson;
	}

	public void setApInvoiceLinesJson(String apInvoiceLinesJson) {
		this.apInvoiceLinesJson = apInvoiceLinesJson;
	}

	@Transient
	public void serializeInvoiceLines (){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		apInvoiceLinesJson = gson.toJson(apInvoiceLines);
	}

	@Transient
	public void deserializeInvoiceLines () {
		Gson gson = new GsonBuilder()
			.setDateFormat(DateUtil.DATE_FORMAT)
			.excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<ApInvoiceLine>>(){}.getType();
		apInvoiceLines = gson.fromJson(apInvoiceLinesJson, type);
	}

	@Override
	@Transient
	public List<OOChild> getChildren() {
		List<OOChild> children = new ArrayList<OOChild>();
		if (aPlines != null) {
			children.addAll(aPlines);
		}
		if (rrItems != null) {
			// Add RR items
			for (RReceivingReportItem item : rrItems) {
				if (item.getRmItem() == null) {
					children.add(item);
				}
			}
			// Add RR raw material items (individual selection)
			for (RReceivingReportItem item : rrItems) {
				if (item.getRmItem() != null) {
					children.add(item.getRmItem());
				}
			}
		}
		if (rtsItems != null) {
			children.addAll(rtsItems);
		}
		if (rriBagQuantities != null) {
			children.addAll(rriBagQuantities);
		}
		if (rriBagDiscounts != null) {
			children.addAll(rriBagDiscounts);
		}
		if (apInvoiceItems != null) {
			children.addAll(apInvoiceItems);
		}
		if (apInvoiceLines != null) {
			children.addAll(apInvoiceLines);
		}
		if (referenceDocuments != null) {
			children.addAll(referenceDocuments);
		}
		if (serialItems != null) {
			children.addAll(serialItems);
		}
		if (apInvoiceGoods != null) {
			children.addAll(apInvoiceGoods);
		}
		if (pcrls != null) {
			children.addAll(pcrls);
		}
		return children;
	}

	@Override
	@Transient
	public String getWorkflowName() {
		if (invoiceTypeId != null) {
			String workflowName = super.getWorkflowName();
			if (invoiceTypeId <= InvoiceType.CREDIT_MEMO_TYPE_ID) {
				return workflowName + InvoiceType.REGULAR_TYPE_ID;
			}
			return workflowName + getInvoiceTypeId();
		}
		return null;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		if (invoiceTypeId != null) {
			switch(invoiceTypeId) {
				case InvoiceType.CREDIT_MEMO_TYPE_ID:
				case InvoiceType.DEBIT_MEMO_TYPE_ID:
				case InvoiceType.PREPAID_TYPE_ID:
				case InvoiceType.REGULAR_TYPE_ID:
				case InvoiceType.API_NON_PO_CENTRAL:
				case InvoiceType.API_NON_PO_NSB3:
				case InvoiceType.API_NON_PO_NSB4:
				case InvoiceType.API_NON_PO_NSB5:
				case InvoiceType.API_NON_PO_NSB8:
				case InvoiceType.API_NON_PO_NSB8A:
				case InvoiceType.API_CONF_CENTRAL:
				case InvoiceType.API_CONF_NSB3:
				case InvoiceType.API_CONF_NSB4:
				case InvoiceType.API_CONF_NSB5:
				case InvoiceType.API_CONF_NSB8:
				case InvoiceType.API_CONF_NSB8A:
				case InvoiceType.API_IMPORT_CENTRAL:
				case InvoiceType.API_IMPORT_NSB3:
				case InvoiceType.API_IMPORT_NSB4:
				case InvoiceType.API_IMPORT_NSB5:
				case InvoiceType.API_IMPORT_NSB8:
				case InvoiceType.API_IMPORT_NSB8A:
					return AP_INVOICE_OBJECT_TYPE_ID;
				case InvoiceType.RR_CENTRAL_TYPE_ID:
				case InvoiceType.RR_NSB3_TYPE_ID:
				case InvoiceType.RR_NSB4_TYPE_ID:
				case InvoiceType.RR_NSB5_TYPE_ID:
				case InvoiceType.RR_NSB8_TYPE_ID:
				case InvoiceType.RR_NSB8A_TYPE_ID:
				case InvoiceType.RR_TYPE_ID:
					return RECEIVING_REPORT_OBJECT_TYPE_ID;
				case InvoiceType.RTS_TYPE_ID:
				case InvoiceType.RTS_CENTRAL_TYPE_ID:
				case InvoiceType.RTS_NSB3_TYPE_ID:
				case InvoiceType.RTS_NSB4_TYPE_ID:
				case InvoiceType.RTS_NSB5_TYPE_ID:
				case InvoiceType.RTS_NSB8_TYPE_ID:
				case InvoiceType.RTS_NSB8A_TYPE_ID:
					return RTS_OBJECT_TYPE_ID;
				case InvoiceType.RR_RAW_MAT_TYPE_ID:
					return RR_RAW_MATERIAL_OBJECT_TYPE_ID;
				case InvoiceType.RR_RM_NET_WEIGHT_TYPE_ID:
					return RR_NET_WEIGHT_OBJECT_TYPE_ID;
				case InvoiceType.INVOICE_ITEM_TYPE_ID:
					return INVOICE_ITEM_OBJECT_TYPE_ID;
				case InvoiceType.INVOICE_SERVICE_TYPE_ID:
					return INVOICE_SERVICE_OBJECT_TYPE_ID;
				case InvoiceType.API_GS_CENTRAL:
				case InvoiceType.API_GS_NSB3:
				case InvoiceType.API_GS_NSB4:
				case InvoiceType.API_GS_NSB5:
				case InvoiceType.API_GS_NSB8:
				case InvoiceType.API_GS_NSB8A:
					return INVOICE_GS_OBJECT_TYPE_ID;
				case InvoiceType.AP_LOAN_CENTRAL:
				case InvoiceType.AP_LOAN_NSB3:
				case InvoiceType.AP_LOAN_NSB4:
				case InvoiceType.AP_LOAN_NSB5:
				case InvoiceType.AP_LOAN_NSB8:
				case InvoiceType.AP_LOAN_NSB8A:
					return AP_LOAN_OBJECT_TYPE_ID;
				case InvoiceType.PCR_CENTRAL:
				case InvoiceType.PCR_NSB3:
				case InvoiceType.PCR_NSB4:
				case InvoiceType.PCR_NSB5:
				case InvoiceType.PCR_NSB8:
				case InvoiceType.PCR_NSB8A:
					return PCR_OBJECT_TYPE_ID;
			}
		}
		return null;
	}

	@Override
	@Transient
	public String getShortDescription() {
		return "RR" + " " + sequenceNumber;
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

	@Transient
	public RPurchaseOrder getPurchaseOrder() {
		return purchaseOrder;
	}

	public void setPurchaseOrder(RPurchaseOrder purchaseOrder) {
		this.purchaseOrder = purchaseOrder;
	}

	@Transient
	public Integer getPurchaseOderId() {
		return purchaseOderId;
	}

	public void setPurchaseOderId(Integer purchaseOderId) {
		this.purchaseOderId = purchaseOderId;
	}

	@Override
	@Transient
	public Date getGLDate() {
		return glDate;
	}

	@Transient
	public List<RriBagQuantity> getRriBagQuantities() {
		return rriBagQuantities;
	}

	public void setRriBagQuantities(List<RriBagQuantity> rriBagQuantities) {
		this.rriBagQuantities = rriBagQuantities;
	}

	@Transient
	public String getRriBagQuantitiesJson() {
		return rriBagQuantitiesJson;
	}

	public void setRriBagQuantitiesJson(String rriBagQuantitiesJson) {
		this.rriBagQuantitiesJson = rriBagQuantitiesJson;
	}

	@Transient
	public String getRriBagQtyErrMsg() {
		return rriBagQtyErrMsg;
	}

	public void setRriBagQtyErrMsg(String rriBagQtyErrMsg) {
		this.rriBagQtyErrMsg = rriBagQtyErrMsg;
	}

	@Transient
	public void serializeRriBagQuantities (){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		rriBagQuantitiesJson = gson.toJson(rriBagQuantities);
	}

	@Transient
	public void deserializeRriBagQuantities () {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<RriBagQuantity>>(){}.getType();
		rriBagQuantities = gson.fromJson(rriBagQuantitiesJson, type);
	}

	@Transient
	public RrRawMatItemDto getRrRawMatItemDto() {
		return rrRawMatItemDto;
	}

	public void setRrRawMatItemDto(RrRawMatItemDto rrRawMatItemDto) {
		this.rrRawMatItemDto = rrRawMatItemDto;
	}

	@Transient
	public List<RriBagDiscount> getRriBagDiscounts() {
		return rriBagDiscounts;
	}

	public void setRriBagDiscounts(List<RriBagDiscount> rriBagDiscounts) {
		this.rriBagDiscounts = rriBagDiscounts;
	}

	@Transient
	public String getRriBagDiscountsJson() {
		return rriBagDiscountsJson;
	}

	public void setRriBagDiscountsJson(String rriBagDiscountsJson) {
		this.rriBagDiscountsJson = rriBagDiscountsJson;
	}

	@Transient
	public String getRriBagDiscountErrMsg() {
		return rriBagDiscountErrMsg;
	}

	public void setRriBagDiscountErrMsg(String rriBagDiscountErrMsg) {
		this.rriBagDiscountErrMsg = rriBagDiscountErrMsg;
	}

	@Transient
	public void serializeRriBagDiscounts (){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		rriBagDiscountsJson = gson.toJson(rriBagDiscounts);
	}

	@Transient
	public void deserializeRriBagDiscounts () {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<RriBagDiscount>>(){}.getType();
		rriBagDiscounts = gson.fromJson(rriBagDiscountsJson, type);
	}

	@Transient
	public String getErrorMsgStockCode() {
		return errorMsgStockCode;
	}

	public void setErrorMsgStockCode(String errorMsgStockCode) {
		this.errorMsgStockCode = errorMsgStockCode;
	}

	@Transient
	public String getErrorMsgBuyingPrice() {
		return errorMsgBuyingPrice;
	}

	public void setErrorMsgBuyingPrice(String errorMsgBuyingPrice) {
		this.errorMsgBuyingPrice = errorMsgBuyingPrice;
	}

	@Transient
	public List<ApInvoiceItem> getApInvoiceItems() {
		return apInvoiceItems;
	}

	public void setApInvoiceItems(List<ApInvoiceItem> apInvoiceItems) {
		this.apInvoiceItems = apInvoiceItems;
	}

	@Transient
	public String getApLinesJson() {
		return apLinesJson;
	}

	public void setApLinesJson(String apLinesJson) {
		this.apLinesJson = apLinesJson;
	}

	@Transient
	public void serializeApLines(){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		apLinesJson = gson.toJson(aPlines);
	}

	@Transient
	public void deserializeApLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<APLine>>(){}.getType();
		aPlines = gson.fromJson(apLinesJson, type);
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

	@Column(name = "BMS_NUMBER", columnDefinition="varchar(50)")
	public String getBmsNumber() {
		return bmsNumber;
	}

	public void setBmsNumber(String bmsNumber) {
		this.bmsNumber = bmsNumber;
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

	@Column(name = "INVOICE_CLASSIFICATION_ID", columnDefinition = "INT(10)")
	public Integer getInvoiceClassificationId() {
		return invoiceClassificationId;
	}
	public void setInvoiceClassificationId(Integer invoiceClassificationId) {
		this.invoiceClassificationId = invoiceClassificationId;
	}

	@OneToOne
	@JoinColumn(name="INVOICE_CLASSIFICATION_ID", insertable=false, updatable=false)
	public InvoiceClassification getInvoiceClassification() {
		return invoiceClassification;
	}
	public void setInvoiceClassification(InvoiceClassification invoiceClassification) {
		this.invoiceClassification = invoiceClassification;
	}

	@OneToOne
	@JoinColumn(name = "AP_INVOICE_ID", insertable = false, updatable = false)
	public InvoiceImportationDetails getInvoiceImportationDetails() {
		return invoiceImportationDetails;
	}
	public void setInvoiceImportationDetails(InvoiceImportationDetails invoiceImportationDetails) {
		this.invoiceImportationDetails = invoiceImportationDetails;
	}

	@Transient
	public List<ApInvoiceGoods> getApInvoiceGoods() {
		return apInvoiceGoods;
	}

	public void setApInvoiceGoods(List<ApInvoiceGoods> apInvoiceGoods) {
		this.apInvoiceGoods = apInvoiceGoods;
	}

	@Transient
	public String getApInvoiceGoodsJson() {
		return apInvoiceGoodsJson;
	}

	public void setApInvoiceGoodsJson(String apInvoiceGoodsJson) {
		this.apInvoiceGoodsJson = apInvoiceGoodsJson;
	}

	@Transient
	public void serializeApInvoiceGoods(){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		apInvoiceGoodsJson = gson.toJson(apInvoiceGoods);
	}

	@Transient
	public void deserializeApInvoiceGoods() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<ApInvoiceGoods>>(){}.getType();
		apInvoiceGoods = gson.fromJson(apInvoiceGoodsJson, type);
	}

	@Transient
	public Integer getRrNumber() {
		return rrNumber;
	}

	public void setRrNumber(Integer rrNumber) {
		this.rrNumber = rrNumber;
	}

	@Transient
	public Integer getReferenceObjectId() {
		return referenceObjectId;
	}

	public void setReferenceObjectId(Integer referenceObjectId) {
		this.referenceObjectId = referenceObjectId;
	}

	@Transient
	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	@Transient
	public List<SerialItem> getSerialItems() {
		return serialItems;
	}

	public void setSerialItems(List<SerialItem> serialItems) {
		this.serialItems = serialItems;
	}

	@Transient
	public String getSerialItemsJson() {
		return serialItemsJson;
	}

	public void setSerialItemsJson(String serialItemsJson) {
		this.serialItemsJson = serialItemsJson;
	}

	@Transient
	public void serializeSerialItems() {
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		serialItemsJson = gson.toJson(serialItems);
	}

	@Transient
	public void deserializeSerialItems() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<SerialItem>>(){}.getType();
		serialItems = gson.fromJson(serialItemsJson, type);
	}

	@Transient
	public List<PettyCashReplenishmentLine> getPcrls() {
		return pcrls;
	}
	public void setPcrls(List<PettyCashReplenishmentLine> pcrls) {
		this.pcrls = pcrls;
	}

	@Transient
	public String getPcrlsJson() {
		return pcrlsJson;
	}

	public void setPcrlsJson(String pcrlsJson) {
		this.pcrlsJson = pcrlsJson;
	}

	@Transient
	public void serializePcrls() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		pcrlsJson = gson.toJson(pcrls);
	}

	public void deserializePcrls() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat(DateUtil.DATE_FORMAT).create();
		Type type = new TypeToken <List<PettyCashReplenishmentLine>>(){}.getType();
		pcrls = gson.fromJson(pcrlsJson, type);
	}

	@Column(name = "USER_CUSTODIAN_ID")
	public Integer getUserCustodianId() {
		return userCustodianId;
	}

	public void setUserCustodianId(Integer userCustodianId) {
		this.userCustodianId = userCustodianId;
	}

	@OneToOne
	@JoinColumn(name="USER_CUSTODIAN_ID", insertable=false, updatable=false)
	public UserCustodian getUserCustodian() {
		return userCustodian;
	}

	public void setUserCustodian(UserCustodian userCustodian) {
		this.userCustodian = userCustodian;
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
	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	@Transient
	public String getStrInvoiceDate() {
		return strInvoiceDate;
	}

	public void setStrInvoiceDate(String strInvoiceDate) {
		this.strInvoiceDate = strInvoiceDate;
	}

	@Transient
	public String getStrGlDate() {
		return strGlDate;
	}

	public void setStrGlDate(String strGlDate) {
		this.strGlDate = strGlDate;
	}

	@Transient
	public String getApInvoiceErrMessage() {
		return apInvoiceErrMessage;
	}

	public void setApInvoiceErrMessage(String apInvoiceErrMessage) {
		this.apInvoiceErrMessage = apInvoiceErrMessage;
	}

	@Column(name = "PRINCIPAL_PAYMENT")
	public Double getPrincipalPayment() {
		return principalPayment;
	}

	public void setPrincipalPayment(Double principalPayment) {
		this.principalPayment = principalPayment;
	}

	@Column(name = "PRINCIPAL_LOAN")
	public Double getPrincipalLoan() {
		return principalLoan;
	}

	public void setPrincipalLoan(Double principalLoan) {
		this.principalLoan = principalLoan;
	}

	@Transient
	public Integer getLpNumber() {
		return lpNumber;
	}

	public void setLpNumber(Integer lpNumber) {
		this.lpNumber = lpNumber;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("APInvoice [serviceLeaseKeyId=").append(serviceLeaseKeyId).append(", sequenceNumber=")
				.append(sequenceNumber).append(", supplierId=").append(supplierId).append(", invoiceTypeId=")
				.append(invoiceTypeId).append(", termId=").append(termId).append(", supplierAccountId=")
				.append(supplierAccountId).append(", invoiceNumber=").append(invoiceNumber).append(", amount=")
				.append(amount).append(", invoiceDate=").append(invoiceDate).append(", glDate=").append(glDate)
				.append(", dueDate=").append(dueDate).append(", description=").append(description)
				.append(", wtAcctSettingId=").append(wtAcctSettingId).append(", wtAmount=").append(wtAmount)
				.append(", bmsNumber=").append(bmsNumber).append(", divisionId=").append(divisionId)
				.append(", companyId=").append(companyId).append(", currencyId=").append(currencyId)
				.append(", referenceNo=").append(referenceNo).append(", invoiceClassificationId=")
				.append(invoiceClassificationId).append(", currencyRateId=").append(currencyRateId)
				.append(", currencyRateValue=").append(currencyRateValue).append(", userCustodianId=")
				.append(userCustodianId).append("]");
		return builder.toString();
	}

	@Transient
	public String getPcrlErrorMessage() {
		return pcrlErrorMessage;
	}

	public void setPcrlErrorMessage(String pcrlErrorMessage) {
		this.pcrlErrorMessage = pcrlErrorMessage;
	}

	@Transient
	public List<APLine> getSummarizedApLines() {
		return summarizedApLines;
	}

	public void setSummarizedApLines(List<APLine> summarizedApLines) {
		this.summarizedApLines = summarizedApLines;
	}

	@Transient
	public String getSummarizedApLinesJson() {
		return summarizedApLinesJson;
	}

	public void setSummarizedApLinesJson(String summarizedApLinesJson) {
		this.summarizedApLinesJson = summarizedApLinesJson;
	}

	@Transient
	public void serializeSummarizedApLines (){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		summarizedApLinesJson = gson.toJson(summarizedApLines);
	}

	@Transient
	public void deserializeSummarizedApLines () {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<APLine>>(){}.getType();
		summarizedApLines = gson.fromJson(summarizedApLinesJson, type);
	}
}
