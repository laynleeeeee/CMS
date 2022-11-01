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
 * The domain object for PROJECT_RETENTION table.

 */

@Entity
@Table(name="PROJECT_RETENTION")
public class ProjectRetention extends BaseFormWorkflow{
	private Integer sequenceNo;
	private Integer companyId;
	private Company company;
	private Integer divisionId;
	private Division division;
	private Integer salesOrderId;
	private Integer projectRetentionTypeId;
	private String soNumber;
	private String poNumber;
	private Integer arCustomerId;
	private Integer arCustomerAccountId;
	private ArCustomer arCustomer;
	private ArCustomerAccount arCustomerAccount;
	private Date date;
	private Date dueDate;
	private Integer currencyId;
	private Currency currency;
	private Integer currencyRateId;
	private double currencyRateValue;
	private Integer wtAcctSettingId;
	private Double wtAmount;
	private Double amount;
	private String remarks;
	private String commonErrorMsg;
	private List<ProjectRetentionLine> projectRetentionLines;
	private String projectRetentionLinesJson;
	private String referenceDocumentJson;
	private List<ReferenceDocument> referenceDocuments;
	private String referenceDocsMessage;
	private Date dateReceived;
	private String receiver;
	private WithholdingTaxAcctSetting wtAcctSetting;

	public static final int OBJECT_TYPE_ID = 24009;
	public static final int MAX_PO_NUMBER = 50;
	public static final int MAX_RECEIVER = 50;

	public static final int STATUS_ALL = 1;
	public static final int STATUS_UNUSED = 2;
	public static final int STATUS_USED = 3;

	public enum FIELD {
		id, sequenceNo, companyId, arCustomerId, arCustomerAccountId, divisionId, salesOrderId,
		ebObjectId, date, formWorkflowId, projectRetentionTypeId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "PROJECT_RETENTION_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Transient
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Column(name="DIVISION_ID" , columnDefinition = "int(10)")
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

	@Column(name="SALES_ORDER_ID" , columnDefinition = "int(10)")
	public Integer getSalesOrderId() {
		return salesOrderId;
	}

	public void setSalesOrderId(Integer salesOrderId) {
		this.salesOrderId = salesOrderId;
	}

	@Column(name="PROJECT_RETENTION_TYPE_ID" , columnDefinition = "int(10)")
	public Integer getProjectRetentionTypeId() {
		return projectRetentionTypeId;
	}

	public void setProjectRetentionTypeId(Integer projectRetentionTypeId) {
		this.projectRetentionTypeId = projectRetentionTypeId;
	}

	@Transient
	public String getSoNumber() {
		return soNumber;
	}

	public void setSoNumber(String soNumber) {
		this.soNumber = soNumber;
	}

	@Column(name="PO_NUMBER" , columnDefinition = "varchar(50)")
	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	@Column(name="AR_CUSTOMER_ID" , columnDefinition = "int(10)")
	public Integer getArCustomerId() {
		return arCustomerId;
	}

	public void setArCustomerId(Integer arCustomerId) {
		this.arCustomerId = arCustomerId;
	}

	@Column(name="AR_CUSTOMER_ACCOUNT_ID" , columnDefinition = "int(10)")
	public Integer getArCustomerAccountId() {
		return arCustomerAccountId;
	}

	public void setArCustomerAccountId(Integer arCustomerAccountId) {
		this.arCustomerAccountId = arCustomerAccountId;
	}

	@OneToOne
	@JoinColumn(name="AR_CUSTOMER_ID", insertable=false, updatable=false)
	public ArCustomer getArCustomer() {
		return arCustomer;
	}

	public void setArCustomer(ArCustomer arCustomer) {
		this.arCustomer = arCustomer;
	}

	@OneToOne
	@JoinColumn(name="AR_CUSTOMER_ACCOUNT_ID", insertable=false, updatable=false)
	public ArCustomerAccount getArCustomerAccount() {
		return arCustomerAccount;
	}

	public void setArCustomerAccount(ArCustomerAccount arCustomerAccount) {
		this.arCustomerAccount = arCustomerAccount;
	}

	@Column(name="DATE" , columnDefinition = "date")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Column(name="DUE_DATE" , columnDefinition = "date")
	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	@Column(name="CURRENCY_ID" , columnDefinition = "int(10)")
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

	@Column(name="CURRENCY_RATE_ID" , columnDefinition = "int(10)")
	public Integer getCurrencyRateId() {
		return currencyRateId;
	}

	public void setCurrencyRateId(Integer currencyRateId) {
		this.currencyRateId = currencyRateId;
	}

	@Column(name="CURRENCY_RATE_VALUE" , columnDefinition = "double")
	public double getCurrencyRateValue() {
		return currencyRateValue;
	}

	public void setCurrencyRateValue(double currencyRateValue) {
		this.currencyRateValue = currencyRateValue;
	}

	@Column(name="WT_ACCOUNT_SETTING_ID" , columnDefinition = "int(10)")
	public Integer getWtAcctSettingId() {
		return wtAcctSettingId;
	}

	public void setWtAcctSettingId(Integer wtAcctSettingId) {
		this.wtAcctSettingId = wtAcctSettingId;
	}

	@Column(name="WT_AMOUNT" , columnDefinition = "double")
	public Double getWtAmount() {
		return wtAmount;
	}

	public void setWtAmount(Double wtAmount) {
		this.wtAmount = wtAmount;
	}

	@Column(name="AMOUNT" , columnDefinition = "double")
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Column(name="REMARKS" , columnDefinition = "text")
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Transient
	public String getCommonErrorMsg() {
		return commonErrorMsg;
	}

	public void setCommonErrorMsg(String commonErrorMsg) {
		this.commonErrorMsg = commonErrorMsg;
	}

	@Transient
	public List<ProjectRetentionLine> getProjectRetentionLines() {
		return projectRetentionLines;
	}

	public void setProjectRetentionLines(List<ProjectRetentionLine> projectRetentionLines) {
		this.projectRetentionLines = projectRetentionLines;
	}

	@Transient
	public String getProjectRetentionLinesJson() {
		return projectRetentionLinesJson;
	}

	public void setProjectRetentionLinesJson(String projectRetentionLinesJson) {
		this.projectRetentionLinesJson = projectRetentionLinesJson;
	}

	@Transient
	public void serializeProjectRetentionLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		projectRetentionLinesJson = gson.toJson(projectRetentionLines);
	}

	@Transient
	public void deserializeProjectRetentionLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<ProjectRetentionLine>>(){}.getType();
		projectRetentionLines = gson.fromJson(projectRetentionLinesJson, type);
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

	@Override
	@Transient
	public List<OOChild> getChildren() {
		List<OOChild> children = new ArrayList<OOChild>();
		if (projectRetentionLines != null) {
			children.addAll(projectRetentionLines);
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

	@Override
	@Transient
	public String getWorkflowName() {
		return super.getWorkflowName() + getDivisionId();
	}

	@Column(name="DATE_RECEIVED" , columnDefinition = "DATE")
	public Date getDateReceived() {
		return dateReceived;
	}

	public void setDateReceived(Date dateReceived) {
		this.dateReceived = dateReceived;
	}

	@Column(name="RECEIVER" , columnDefinition = "varchar(50)")
	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	@OneToOne
	@JoinColumn(name = "WT_ACCOUNT_SETTING_ID", insertable=false, updatable=false)
	public WithholdingTaxAcctSetting getWtAcctSetting() {
		return wtAcctSetting;
	}

	public void setWtAcctSetting(WithholdingTaxAcctSetting wtAcctSetting) {
		this.wtAcctSetting = wtAcctSetting;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProjectRetention [sequenceNo=").append(sequenceNo).append(", companyId=").append(companyId)
				.append(", divisionId=").append(divisionId).append(", salesOrderId=").append(salesOrderId)
				.append(", soNumber=").append(soNumber).append(", poNumber=").append(poNumber).append(", arCustomerId=")
				.append(arCustomerId).append(", arCustomerAccount=").append(arCustomerAccount).append(", date=")
				.append(date).append(", currencyId=").append(currencyId).append(", currencyRateId=")
				.append(currencyRateId).append(", currencyRateValue=").append(currencyRateValue)
				.append(", wtAcctSettingId=").append(wtAcctSettingId).append(", wtAmount=").append(wtAmount)
				.append(", amount=").append(amount).append(", remarks=").append(remarks).append(", commonErrorMsg=")
				.append(commonErrorMsg).append("]");
		return builder.toString();
	}
}
