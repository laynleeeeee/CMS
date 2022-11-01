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
 * The domain object for DELIVERY_RECEIPT table.

 */

@Entity
@Table(name="DELIVERY_RECEIPT")
public class DeliveryReceipt extends BaseFormWorkflow{
	private Integer sequenceNo;
	private Integer companyId;
	private Integer authorityToWithdrawId;
	private Integer salesOrderId;
	private String atwNumber;
	private Integer arCustomerId;
	private String customerName;
	private Integer arCustomerAccountId;
	private String customerAcctName;
	private Integer termId;
	private Date date;
	private Date dueDate;
	private String remarks;
	private List<SerialItem> serialDrItems;
	private List<DeliveryReceiptItem> nonSerialDrItems;
	private String serialDrItemsJson;
	private String nonSerialDrItemsJson;
	private String serialErrMsg;
	private String nonSerialErrMsg;
	private Company company;
	private ArCustomer arCustomer;
	private ArCustomerAccount arCustomerAccount;
	private Term term;
	private List<DeliveryReceiptLine> drLines;
	private String drLineJson;
	private String commonErrorMsg;
	private Integer deliveryReceiptTypeId;
	private List<WaybillLine> wbLines;
	private String wbLineJson;
	private String soNumber;
	private List<EquipmentUtilizationLine> euLines;
	private String euLineJson;
	private Integer divisionId;
	private Division division;
	private Integer currencyId;
	private Currency currency;
	private Integer currencyRateId;
	private double currencyRateValue;
	private String poNumber;
	private String drRefNumber;
	private Integer salesPersonnelId;
	private String salesPersonnelName;
	private String referenceDocumentJson;
	private List<ReferenceDocument> referenceDocuments;
	private String referenceDocsMessage;
	private Integer wtAcctSettingId;
	private String wtAcctPercentage;
	private Double wtAmount;
	private Double wtVatAmount;
	private Date dateReceived;
	private String receiver;

	public static final int OBJECT_TYPE_ID = 110;

	public static final int DR_SI_OR_TYPE_ID = 12004;
	public static final int ATWI_DRI_OR_TYPE_ID = 12005;
	public static final int MAX_DR_REF_NO = 50;
	public static final int MAX_RECEIVER = 50;

	public static final String DELIVERY_RECEIPT_FORM_NAME = "DR";

	public enum FIELD {
		id, sequenceNo, companyId, arCustomerId, arCustomerAccountId, termId, salesOrderId,
		ebObjectId, date, dueDate, formWorkflowId, authorityToWithdrawId, deliveryReceiptTypeId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "DELIVERY_RECEIPT_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name="AUTHORITY_TO_WITHDRAW_ID")
	public Integer getAuthorityToWithdrawId() {
		return authorityToWithdrawId;
	}

	public void setAuthorityToWithdrawId(Integer authorityToWithdrawId) {
		this.authorityToWithdrawId = authorityToWithdrawId;
	}

	@Transient
	public String getAtwNumber() {
		return atwNumber;
	}

	public void setAtwNumber(String atwNumber) {
		this.atwNumber = atwNumber;
	}

	@Transient
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Column(name="AR_CUSTOMER_ID")
	public Integer getArCustomerId() {
		return arCustomerId;
	}

	public void setArCustomerId(Integer arCustomerId) {
		this.arCustomerId = arCustomerId;
	}

	@Transient
	public ArCustomer getArCustomer() {
		return arCustomer;
	}

	public void setArCustomer(ArCustomer arCustomer) {
		this.arCustomer = arCustomer;
	}

	@Column(name="AR_CUSTOMER_ACCOUNT_ID")
	public Integer getArCustomerAccountId() {
		return arCustomerAccountId;
	}

	public void setArCustomerAccountId(Integer arCustomerAccountId) {
		this.arCustomerAccountId = arCustomerAccountId;
	}

	@Transient
	public ArCustomerAccount getArCustomerAccount() {
		return arCustomerAccount;
	}

	public void setArCustomerAccount(ArCustomerAccount arCustomerAccount) {
		this.arCustomerAccount = arCustomerAccount;
	}

	@Column(name="TERM_ID")
	public Integer getTermId() {
		return termId;
	}

	public void setTermId(Integer termId) {
		this.termId = termId;
	}

	@Transient
	public Term getTerm() {
		return term;
	}

	public void setTerm(Term term) {
		this.term = term;
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

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE_ID;
	}

	@Transient
	public String getSerialDrItemsJson() {
		return serialDrItemsJson;
	}

	public void setSerialDrItemsJson(String serialDrItemsJson) {
		this.serialDrItemsJson = serialDrItemsJson;
	}

	@Transient
	public String getNonSerialDrItemsJson() {
		return nonSerialDrItemsJson;
	}

	public void setNonSerialDrItemsJson(String nonSerialDrItemsJson) {
		this.nonSerialDrItemsJson = nonSerialDrItemsJson;
	}

	@Transient
	public void serializeSerialDRItems(){
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		serialDrItemsJson = gson.toJson(serialDrItems);
	}

	@Transient
	public void deserializeSerialDRItems(){
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<SerialItem>>(){}.getType();
		serialDrItems = gson.fromJson(serialDrItemsJson, type);
	}

	@Transient
	public void serializeNonSerialDRItems(){
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		nonSerialDrItemsJson = gson.toJson(nonSerialDrItems);
	}

	@Transient
	public void deserializeNonSerialDRItems(){
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<DeliveryReceiptItem>>(){}.getType();
		nonSerialDrItems = gson.fromJson(nonSerialDrItemsJson, type);
	}

	@Transient
	public List<SerialItem> getSerialDrItems() {
		return serialDrItems;
	}

	public void setSerialDrItems(List<SerialItem> serialDrItems) {
		this.serialDrItems = serialDrItems;
	}

	@Transient
	public List<DeliveryReceiptItem> getNonSerialDrItems() {
		return nonSerialDrItems;
	}

	public void setNonSerialDrItems(List<DeliveryReceiptItem> nonSerialDrItems) {
		this.nonSerialDrItems = nonSerialDrItems;
	}

	@Override
	@Transient
	public Date getGLDate() {
		return date;
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
	public List<DeliveryReceiptLine> getDrLines() {
		return drLines;
	}

	public void setDrLines(List<DeliveryReceiptLine> drLines) {
		this.drLines = drLines;
	}

	@Transient
	public String getDrLineJson() {
		return drLineJson;
	}

	public void setDrLineJson(String drLineJson) {
		this.drLineJson = drLineJson;
	}

	@Transient
	public void serializeDRLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		drLineJson = gson.toJson(drLines);
	}

	@Transient
	public void deserializeDRLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<DeliveryReceiptLine>>(){}.getType();
		drLines = gson.fromJson(drLineJson, type);
	}

	@Override
	@Transient
	public List<OOChild> getChildren() {
		List<OOChild> children = new ArrayList<OOChild>();
		if (serialDrItems != null) {
			children.addAll(serialDrItems);
		}
		if (nonSerialDrItems != null) {
			children.addAll(nonSerialDrItems);
		}
		if (drLines != null) {
			children.addAll(drLines);
		}
		if(referenceDocuments != null) {
			children.addAll(referenceDocuments);
		}
		return children;
	}

	@Transient
	public String getCommonErrorMsg() {
		return commonErrorMsg;
	}

	public void setCommonErrorMsg(String commonErrorMsg) {
		this.commonErrorMsg = commonErrorMsg;
	}

	@Column(name="DELIVERY_RECEIPT_TYPE_ID")
	public Integer getDeliveryReceiptTypeId() {
		return deliveryReceiptTypeId;
	}

	public void setDeliveryReceiptTypeId(Integer deliveryReceiptTypeId) {
		this.deliveryReceiptTypeId = deliveryReceiptTypeId;
	}

	@Override
	@Transient
	public String getWorkflowName() {
		return super.getWorkflowName() + getDeliveryReceiptTypeId();
	}

	@Column(name="SALES_ORDER_ID")
	public Integer getSalesOrderId() {
		return salesOrderId;
	}

	public void setSalesOrderId(Integer salesOrderId) {
		this.salesOrderId = salesOrderId;
	}

	@Transient
	public List<WaybillLine> getWbLines() {
		return wbLines;
	}

	public void setWbLines(List<WaybillLine> wbLines) {
		this.wbLines = wbLines;
	}

	@Transient
	public String getWbLineJson() {
		return wbLineJson;
	}

	public void setWbLineJson(String wbLineJson) {
		this.wbLineJson = wbLineJson;
	}

	@Transient
	public void serializeWBLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		wbLineJson = gson.toJson(wbLines);
	}

	@Transient
	public void deserializeWBLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<WaybillLine>>(){}.getType();
		wbLines = gson.fromJson(wbLineJson, type);
	}

	@Transient
	public String getSoNumber() {
		return soNumber;
	}

	public void setSoNumber(String soNumber) {
		this.soNumber = soNumber;
	}

	@Transient
	public List<EquipmentUtilizationLine> getEuLines() {
		return euLines;
	}

	public void setEuLines(List<EquipmentUtilizationLine> euLines) {
		this.euLines = euLines;
	}

	@Transient
	public String getEuLineJson() {
		return euLineJson;
	}

	public void setEuLineJson(String euLineJson) {
		this.euLineJson = euLineJson;
	}

	@Transient
	public void serializeEULines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		euLineJson = gson.toJson(euLines);
	}

	@Transient
	public void deserializeEULines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<EquipmentUtilizationLine>>(){}.getType();
		euLines = gson.fromJson(euLineJson, type);
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

	@Column(name = "PO_NUMBER")
	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	@Column(name = "DR_REF_NUMBER")
	public String getDrRefNumber() {
		return drRefNumber;
	}

	public void setDrRefNumber(String drRefNumber) {
		this.drRefNumber = drRefNumber;
	}

	@Column(name = "SALES_PERSONNEL_ID")
	public Integer getSalesPersonnelId() {
		return salesPersonnelId;
	}

	public void setSalesPersonnelId(Integer salesPersonnelId) {
		this.salesPersonnelId = salesPersonnelId;
	}

	@Transient
	public String getSalesPersonnelName() {
		return salesPersonnelName;
	}

	public void setSalesPersonnelName(String salesPersonnelName) {
		this.salesPersonnelName = salesPersonnelName;
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

	@Transient
	public String getWtAcctPercentage() {
		return wtAcctPercentage;
	}

	public void setWtAcctPercentage(String wtAcctPercentage) {
		this.wtAcctPercentage = wtAcctPercentage;
	}

	@Transient
	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	@Transient
	public String getCustomerAcctName() {
		return customerAcctName;
	}

	public void setCustomerAcctName(String customerAcctName) {
		this.customerAcctName = customerAcctName;
	}

	@Transient
	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DeliveryReceipt [sequenceNo=").append(sequenceNo).append(", companyId=").append(companyId)
				.append(", authorityToWithdrawId=").append(authorityToWithdrawId).append(", atwNumber=")
				.append(atwNumber).append(", arCustomerId=").append(arCustomerId).append(", arCustomerAccountId=")
				.append(arCustomerAccountId).append(", termId=").append(termId).append(", date=").append(date)
				.append(", dueDate=").append(dueDate).append(", remarks=").append(remarks)
				.append(", deliveryReceiptTypeId=").append(deliveryReceiptTypeId).append("]");
		return builder.toString();
	}
}
