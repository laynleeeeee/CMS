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
 * Object representation class for SALES_QUOTATION table

 */

@Entity
@Table(name="SALES_QUOTATION")
public class SalesQuotation extends BaseFormWorkflow {
	private Integer companyId;
	private Date date;
	private Integer arCustomerId;
	private Integer arCustomerAcctId;
	private String shipTo;
	private String subject;
	private String generalConditions;
	private Integer customerTypeId;
	private Double amount;
	private Integer wtAcctSettingId;
	private Double wtAmount;
	private List<SalesQuotationItem> sqItems;
	private String sqItemJson;
	private List<SalesQuotationLine> sqLines;
	private String sqLineJson;
	private List<SalesQuotationTruckingLine> sqtLines;
	private String sqtLineJson;
	private List<SalesQuotationEquipmentLine> sqeLines;
	private String sqeLineJson;
	private Integer sequenceNumber;
	private ArCustomer arCustomer;
	private Company company;
	private ArCustomerAccount arCustomerAccount;
	private CustomerType customerType;
	private String commonErrorMsg;

	public static final int OBJECT_TYPE = 12000;

	public enum FIELD {
		id, companyId, date, arCustomerId, arCustomerAcctId, formWorkflowId, ebObjectId, customerTypeId, sequenceNumber,
		createdDate
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "SALES_QUOTATION_ID", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name = "COMPANY_ID")
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@Column(name = "DATE")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Column(name = "AR_CUSTOMER_ID")
	public Integer getArCustomerId() {
		return arCustomerId;
	}

	public void setArCustomerId(Integer arCustomerId) {
		this.arCustomerId = arCustomerId;
	}

	@Column(name = "AR_CUSTOMER_ACCOUNT_ID")
	public Integer getArCustomerAcctId() {
		return arCustomerAcctId;
	}

	public void setArCustomerAcctId(Integer arCustomerAcctId) {
		this.arCustomerAcctId = arCustomerAcctId;
	}

	@Column(name = "SHIP_TO")
	public String getShipTo() {
		return shipTo;
	}

	public void setShipTo(String shipTo) {
		this.shipTo = shipTo;
	}

	@Column(name = "SUBJECT")
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Column(name = "GENERAL_CONDITIONS")
	public String getGeneralConditions() {
		return generalConditions;
	}

	public void setGeneralConditions(String generalConditions) {
		this.generalConditions = generalConditions;
	}

	@Override
	@Transient
	public List<OOChild> getChildren() {
		List<OOChild> children = new ArrayList<OOChild>();
		if (sqItems != null) {
			children.addAll(sqItems);
		}
		if (sqLines != null) {
			children.addAll(sqLines);
		}
		if(sqtLines != null) {
			children.addAll(sqtLines);
		}
		if(sqeLines != null) {
			children.addAll(sqeLines);
		}
		return children;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE;
	}

	@Column(name = "CUSTOMER_TYPE_ID")
	public Integer getCustomerTypeId() {
		return customerTypeId;
	}

	public void setCustomerTypeId(Integer customerTypeId) {
		this.customerTypeId = customerTypeId;
	}

	@Column(name = "AMOUNT")
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
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

	@Transient
	public List<SalesQuotationItem> getSqItems() {
		return sqItems;
	}

	public void setSqItems(List<SalesQuotationItem> sqItems) {
		this.sqItems = sqItems;
	}

	@Transient
	public String getSqItemJson() {
		return sqItemJson;
	}

	public void setSqItemJson(String sqItemJson) {
		this.sqItemJson = sqItemJson;
	}

	@Transient
	public void serializeSQItems() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		sqItemJson = gson.toJson(sqItems);
	}

	@Transient
	public void deserializeSQItems() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<SalesQuotationItem>>(){}.getType();
		sqItems = gson.fromJson(sqItemJson, type);
	}

	@Transient
	public List<SalesQuotationLine> getSqLines() {
		return sqLines;
	}

	public void setSqLines(List<SalesQuotationLine> sqLines) {
		this.sqLines = sqLines;
	}

	@Transient
	public String getSqLineJson() {
		return sqLineJson;
	}

	public void setSqLineJson(String sqLineJson) {
		this.sqLineJson = sqLineJson;
	}

	@Transient
	public void serializeSQLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		sqLineJson = gson.toJson(sqLines);
	}

	@Transient
	public void deserializeSQLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<SalesQuotationLine>>(){}.getType();
		sqLines = gson.fromJson(sqLineJson, type);
	}

	@Column(name = "SEQUENCE_NO")
	public Integer getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	@OneToOne
	@JoinColumn(name = "AR_CUSTOMER_ID", insertable=false, updatable=false)
	public ArCustomer getArCustomer() {
		return arCustomer;
	}

	public void setArCustomer(ArCustomer arCustomer) {
		this.arCustomer = arCustomer;
	}

	@OneToOne
	@JoinColumn(name = "COMPANY_ID", insertable=false, updatable=false)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@OneToOne
	@JoinColumn(name = "AR_CUSTOMER_ACCOUNT_ID", insertable=false, updatable=false)
	public ArCustomerAccount getArCustomerAccount() {
		return arCustomerAccount;
	}

	public void setArCustomerAccount(ArCustomerAccount arCustomerAccount) {
		this.arCustomerAccount = arCustomerAccount;
	}

	@OneToOne
	@JoinColumn(name = "CUSTOMER_TYPE_ID", insertable=false, updatable=false)
	public CustomerType getCustomerType() {
		return customerType;
	}

	public void setCustomerType(CustomerType customerType) {
		this.customerType = customerType;
	}

	@Transient
	public String getCommonErrorMsg() {
		return commonErrorMsg;
	}

	public void setCommonErrorMsg(String commonErrorMsg) {
		this.commonErrorMsg = commonErrorMsg;
	}

	@Transient
	public List<SalesQuotationTruckingLine> getSqtLines() {
		return sqtLines;
	}

	public void setSqtLines(List<SalesQuotationTruckingLine> sqtLines) {
		this.sqtLines = sqtLines;
	}

	@Transient
	public String getSqtLineJson() {
		return sqtLineJson;
	}

	public void setSqtLineJson(String sqtLineJson) {
		this.sqtLineJson = sqtLineJson;
	}

	@Transient
	public void serializeSQTLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		sqtLineJson = gson.toJson(sqtLines);
	}

	@Transient
	public void deserializeSQTLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<SalesQuotationTruckingLine>>(){}.getType();
		sqtLines = gson.fromJson(sqtLineJson, type);
	}

	@Transient
	public List<SalesQuotationEquipmentLine> getSqeLines() {
		return sqeLines;
	}

	public void setSqeLines(List<SalesQuotationEquipmentLine> sqeLines) {
		this.sqeLines = sqeLines;
	}

	@Transient
	public String getSqeLineJson() {
		return sqeLineJson;
	}

	public void setSqeLineJson(String sqeLineJson) {
		this.sqeLineJson = sqeLineJson;
	}

	@Transient
	public void serializeSQELines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		sqeLineJson = gson.toJson(sqeLines);
	}

	@Transient
	public void deserializeSQELines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<SalesQuotationEquipmentLine>>(){}.getType();
		sqeLines = gson.fromJson(sqeLineJson, type);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SalesQuotation [companyId=").append(companyId).append(", date=").append(date)
				.append(", arCustomerId=").append(arCustomerId).append(", arCustomerAcctId=").append(arCustomerAcctId)
				.append(", shipTo=").append(shipTo).append(", subject=").append(subject).append(", generalConditions=")
				.append(generalConditions).append(", customerTypeId=").append(customerTypeId).append(", amount=")
				.append(amount).append(", wtAcctSettingId=").append(wtAcctSettingId).append(", wtAmount=")
				.append(wtAmount).append("]");
		return builder.toString();
	}
}
