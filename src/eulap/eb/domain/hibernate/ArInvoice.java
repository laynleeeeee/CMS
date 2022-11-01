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
 * The domain object for AR_INVOICE table.

 */

@Entity
@Table(name="AR_INVOICE")
public class ArInvoice extends BaseFormWorkflow {
	private Integer sequenceNo;
	private Integer companyId;
	private Integer deliveryRecieptId;
	private String drNumber;
	private Integer arCustomerId;
	private Integer arCustomerAccountId;
	private Integer termId;
	private Date date;
	private Date dueDate;
	private String remarks;
	private List<SerialItem> serialArItems;
	private List<ArInvoiceItem> nonSerialArItems;
	private String serialArItemsJson;
	private String nonSerialArItemsJson;
	private String serialErrMsg;
	private String nonSerialErrMsg;
	private Company company;
	private ArCustomer arCustomer;
	private ArCustomerAccount arCustomerAccount;
	private Term term;
	private List<ArInvoiceLine> ariLines;
	private String ariLineJson;
	private String commonErrorMsg;
	private Integer wtAcctSettingId;
	private Double wtAmount;
	private WithholdingTaxAcctSetting wtAcctSetting;
	private Double amount;
	private Double totalAdvPayment;
	private List<ArInvoiceTruckingLine> arInvoiceTruckingLines;
	private String arInvoiceTruckingLinesJson;
	private List<ArInvoiceEquipmentLine> arInvoiceEquipmentLines;
	private String arInvoiceEquipmentLinesJson;
	private Integer arInvoiceTypeId;
	private String strDrRefIds;
	private Integer divisionId;
	private Division division;
	private Integer currencyId;
	private Currency currency;
	private Integer currencyRateId;
	private double currencyRateValue;
	private String referenceDocumentJson;
	private List<ReferenceDocument> referenceDocuments;
	private String referenceDocsMessage;
	private Double recoupment;
	private Double retention;
	private Double wtVatAmount;
	private Date dateReceived;
	private String receiver;

	public static final int OBJECT_TYPE_ID = 12009;
	public static final int ARI_SERIAL_ITEM_OR_TYPE_ID = 12006;
	public static final int DRI_ARI_ITEM_OR_TYPE_ID = 12007;
	public static final int MAX_RECEIVER = 50;

	public enum FIELD {
		id, sequenceNo, companyId, arCustomerId, arCustomerAccountId, termId,
		ebObjectId, date, dueDate, formWorkflowId, deliveryRecieptId, arInvoiceTypeId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "AR_INVOICE_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name="SEQUENCE_NO")
	public Integer getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(Integer sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	@Column(name="COMPANY_ID")
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@Column(name="DELIVERY_RECEIPT_ID")
	public Integer getDeliveryRecieptId() {
		return deliveryRecieptId;
	}

	public void setDeliveryRecieptId(Integer deliveryRecieptId) {
		this.deliveryRecieptId = deliveryRecieptId;
	}

	@Transient
	public String getDrNumber() {
		return drNumber;
	}

	public void setDrNumber(String drNumber) {
		this.drNumber = drNumber;
	}

	@Column(name="AR_CUSTOMER_ID")
	public Integer getArCustomerId() {
		return arCustomerId;
	}

	public void setArCustomerId(Integer arCustomerId) {
		this.arCustomerId = arCustomerId;
	}

	@Column(name="AR_CUSTOMER_ACCOUNT_ID")
	public Integer getArCustomerAccountId() {
		return arCustomerAccountId;
	}

	public void setArCustomerAccountId(Integer arCustomerAccountId) {
		this.arCustomerAccountId = arCustomerAccountId;
	}

	@Column(name="TERM_ID")
	public Integer getTermId() {
		return termId;
	}

	public void setTermId(Integer termId) {
		this.termId = termId;
	}

	@Column(name="DATE")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Column(name="DUE_DATE")
	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	@Column(name="REMARKS")
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Transient
	public List<SerialItem> getSerialArItems() {
		return serialArItems;
	}

	public void setSerialArItems(List<SerialItem> serialArItems) {
		this.serialArItems = serialArItems;
	}

	@Transient
	public List<ArInvoiceItem> getNonSerialArItems() {
		return nonSerialArItems;
	}

	public void setNonSerialArItems(List<ArInvoiceItem> nonSerialArItems) {
		this.nonSerialArItems = nonSerialArItems;
	}

	@Transient
	public String getSerialArItemsJson() {
		return serialArItemsJson;
	}

	public void setSerialArItemsJson(String serialArItemsJson) {
		this.serialArItemsJson = serialArItemsJson;
	}

	@Transient
	public String getNonSerialArItemsJson() {
		return nonSerialArItemsJson;
	}

	public void setNonSerialArItemsJson(String nonSerialArItemsJson) {
		this.nonSerialArItemsJson = nonSerialArItemsJson;
	}

	@Transient
	public void serializeNonSerialItems(){
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		nonSerialArItemsJson = gson.toJson(nonSerialArItems);
	}

	@Transient
	public void deserializeNonSerialItems(){
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<ArInvoiceItem>>(){}.getType();
		nonSerialArItems = gson.fromJson(nonSerialArItemsJson, type);
	}

	@Transient
	public void serializeSerialItems(){
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		serialArItemsJson = gson.toJson(serialArItems);
	}

	@Transient
	public void deserializeSerialItems(){
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<SerialItem>>(){}.getType();
		serialArItems = gson.fromJson(serialArItemsJson, type);
	}

	@Transient
	public String getSerialErrMsg() {
		return serialErrMsg;
	}

	public void setSerialErrMsg(String serialErrMsg) {
		this.serialErrMsg = serialErrMsg;
	}

	@Transient
	public String getNonSerialErrMsg() {
		return nonSerialErrMsg;
	}

	public void setNonSerialErrMsg(String nonSerialErrMsg) {
		this.nonSerialErrMsg = nonSerialErrMsg;
	}

	@Transient
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Transient
	public ArCustomer getArCustomer() {
		return arCustomer;
	}

	public void setArCustomer(ArCustomer arCustomer) {
		this.arCustomer = arCustomer;
	}

	@Transient
	public ArCustomerAccount getArCustomerAccount() {
		return arCustomerAccount;
	}

	public void setArCustomerAccount(ArCustomerAccount arCustomerAccount) {
		this.arCustomerAccount = arCustomerAccount;
	}

	@Transient
	public Term getTerm() {
		return term;
	}

	public void setTerm(Term term) {
		this.term = term;
	}

	@Transient
	public List<ArInvoiceLine> getAriLines() {
		return ariLines;
	}

	public void setAriLines(List<ArInvoiceLine> ariLines) {
		this.ariLines = ariLines;
	}

	@Transient
	public String getAriLineJson() {
		return ariLineJson;
	}

	public void setAriLineJson(String ariLineJson) {
		this.ariLineJson = ariLineJson;
	}

	@Transient
	public void serializeARILines(){
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		ariLineJson = gson.toJson(ariLines);
	}

	@Transient
	public void deserializeARILines(){
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<ArInvoiceLine>>(){}.getType();
		ariLines = gson.fromJson(ariLineJson, type);
	}


	@Transient
	public String getCommonErrorMsg() {
		return commonErrorMsg;
	}

	public void setCommonErrorMsg(String commonErrorMsg) {
		this.commonErrorMsg = commonErrorMsg;
	}

	@Override
	@Transient
	public List<OOChild> getChildren() {
		List<OOChild> children = new ArrayList<OOChild>();
		if (nonSerialArItems != null) {
			children.addAll(nonSerialArItems);
		}
		if (serialArItems != null) {
			children.addAll(serialArItems);
		}
		if (ariLines != null) {
			children.addAll(ariLines);
		}
		if(arInvoiceTruckingLines != null) {
			children.addAll(arInvoiceTruckingLines);
		}
		if(referenceDocuments != null) {
			children.addAll(referenceDocuments);
		}
		return children;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE_ID;
	}

	@Override
	@Transient
	public Date getGLDate() {
		return date;
	}

	@Column(name = "WT_ACCOUNT_SETTING_ID")
	public Integer getWtAcctSettingId() {
		return wtAcctSettingId;
	}

	public void setWtAcctSettingId(Integer wtAcctSettingId) {
		this.wtAcctSettingId = wtAcctSettingId;
	}

	@Column(name = "WT_AMOUNT")
	public Double getWtAmount() {
		return wtAmount;
	}

	public void setWtAmount(Double wtAmount) {
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

	@Column(name = "AMOUNT")
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Transient
	public Double getTotalAdvPayment() {
		return totalAdvPayment;
	}

	public void setTotalAdvPayment(Double totalAdvPayment) {
		this.totalAdvPayment = totalAdvPayment;
	}

	@Transient
	public List<ArInvoiceTruckingLine> getArInvoiceTruckingLines() {
		return arInvoiceTruckingLines;
	}

	public void setArInvoiceTruckingLines(List<ArInvoiceTruckingLine> arInvoiceTruckingLines) {
		this.arInvoiceTruckingLines = arInvoiceTruckingLines;
	}

	@Transient
	public String getArInvoiceTruckingLinesJson() {
		return arInvoiceTruckingLinesJson;
	}

	public void setArInvoiceTruckingLinesJson(String arInvoiceTruckingLinesJson) {
		this.arInvoiceTruckingLinesJson = arInvoiceTruckingLinesJson;
	}

	@Transient
	public void serializeArInvoiceTruckingLines(){
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		arInvoiceTruckingLinesJson = gson.toJson(arInvoiceTruckingLines);
	}

	@Transient
	public void deserializeArInvoiceTruckingLines(){
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<ArInvoiceTruckingLine>>(){}.getType();
		arInvoiceTruckingLines = gson.fromJson(arInvoiceTruckingLinesJson, type);
	}


	@Transient
	public List<ArInvoiceEquipmentLine> getArInvoiceEquipmentLines() {
		return arInvoiceEquipmentLines;
	}

	public void setArInvoiceEquipmentLines(List<ArInvoiceEquipmentLine> arInvoiceEquipmentLines) {
		this.arInvoiceEquipmentLines = arInvoiceEquipmentLines;
	}

	@Transient
	public String getArInvoiceEquipmentLinesJson() {
		return arInvoiceEquipmentLinesJson;
	}

	public void setArInvoiceEquipmentLinesJson(String arInvoiceEquipmentLinesJson) {
		this.arInvoiceEquipmentLinesJson = arInvoiceEquipmentLinesJson;
	}

	@Transient
	public void serializeArInvoiceEquipmentLines(){
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		arInvoiceEquipmentLinesJson = gson.toJson(arInvoiceEquipmentLines);
	}

	@Transient
	public void deserializeArInvoiceEquipmentLines(){
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<ArInvoiceEquipmentLine>>(){}.getType();
		arInvoiceEquipmentLines = gson.fromJson(arInvoiceEquipmentLinesJson, type);
	}

	@Column(name = "AR_INVOICE_TYPE_ID")
	public Integer getArInvoiceTypeId() {
		return arInvoiceTypeId;
	}

	public void setArInvoiceTypeId(Integer arInvoiceTypeId) {
		this.arInvoiceTypeId = arInvoiceTypeId;
	}

	@Override
	@Transient
	public String getWorkflowName() {
		return super.getWorkflowName() + getArInvoiceTypeId();
	}

	@Column(name = "DR_REFERENCE_IDS")
	public String getStrDrRefIds() {
		return strDrRefIds;
	}

	public void setStrDrRefIds(String strDrRefIds) {
		this.strDrRefIds = strDrRefIds;
	}

	@Column(name="DIVISION_ID")
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

	@Column (name = "CURRENCY_ID")
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

	@Column (name = "RECOUPMENT")
	public Double getRecoupment() {
		return recoupment;
	}

	public void setRecoupment(Double recoupment) {
		this.recoupment = recoupment;
	}

	@Column (name = "RETENTION")
	public Double getRetention() {
		return retention;
	}

	public void setRetention(Double retention) {
		this.retention = retention;
	}

	@Column(name = "WT_VAT_AMOUNT")
	public Double getWtVatAmount() {
		return wtVatAmount;
	}

	public void setWtVatAmount(Double wtVatAmount) {
		this.wtVatAmount = wtVatAmount;
	}

	@Column(name = "DATE_RECEIVED")
	public Date getDateReceived() {
		return dateReceived;
	}

	public void setDateReceived(Date dateReceived) {
		this.dateReceived = dateReceived;
	}

	@Column(name = "RECEIVER")
	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ArInvoice [sequenceNo=").append(sequenceNo).append(", companyId=").append(companyId)
				.append(", deliveryRecieptId=").append(deliveryRecieptId).append(", drNumber=").append(drNumber)
				.append(", arCustomerId=").append(arCustomerId).append(", arCustomerAccountId=")
				.append(arCustomerAccountId).append(", termId=").append(termId).append(", date=").append(date)
				.append(", dueDate=").append(dueDate).append(", remarks=").append(remarks).append(", wtAcctSettingId=")
				.append(wtAcctSettingId).append(", wtAmount=").append(wtAmount).append(", amount=").append(amount)
				.append(", arInvoiceTypeId=").append(arInvoiceTypeId).append("]");
		return builder.toString();
	}
}
