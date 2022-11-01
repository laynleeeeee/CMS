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
import eulap.eb.web.dto.SubWorkOrderDto;

/**
 * Object representation class for WORK_ORDER table

 */

@Entity
@Table(name="WORK_ORDER")
public class WorkOrder extends BaseFormWorkflow {
	private Integer sequenceNumber;
	private Integer salesOrderId;
	private Integer companyId;
	private Date date;
	private Integer arCustomerId;
	private Integer arCustomerAcctId;
	private Date targetEndDate;
	private String workDescription;
	private List<WorkOrderInstruction> woInstructions;
	private String woInstructionJson;
	private List<WorkOrderItem> woItems;
	private String woItemJson;
	private List<WorkOrderLine> woLines;
	private String woLineJson;
	private Company company;
	private ArCustomer arCustomer;
	private ArCustomerAccount arCustomerAccount;
	private List<ReferenceDocument> referenceDocuments;
	private String referenceDocumentsJson;
	private String soNumber;
	private String commonErrorMsg;
	private Integer refWorkOrderId;
	private String refWoNumber;
	private List<SubWorkOrderDto> subWorkOrderDtos;
	private List<WorkOrderPurchasedItem> woPurchasedItems;
	private String woPurchasedItemsJson;

	public enum FIELD {
		id, companyId, date, arCustomerId, arCustomerAcctId, formWorkflowId, ebObjectId, sequenceNumber,
		salesOrderId, targetEndDate, refWorkOrderId
	}

	public static final int OBJECT_TYPE_ID = 12013;
	public static final int WO_SERIAL_ITEM_OR_TYPE_ID = 12011;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "WORK_ORDER_ID", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name = "SALES_ORDER_ID")
	public Integer getSalesOrderId() {
		return salesOrderId;
	}

	public void setSalesOrderId(Integer salesOrderId) {
		this.salesOrderId = salesOrderId;
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

	@Column(name = "TARGET_END_DATE")
	public Date getTargetEndDate() {
		return targetEndDate;
	}

	public void setTargetEndDate(Date targetEndDate) {
		this.targetEndDate = targetEndDate;
	}

	@Column(name = "WORK_DESCRIPTION")
	public String getWorkDescription() {
		return workDescription;
	}

	public void setWorkDescription(String workDescription) {
		this.workDescription = workDescription;
	}

	@Transient
	public List<WorkOrderInstruction> getWoInstructions() {
		return woInstructions;
	}

	public void setWoInstructions(List<WorkOrderInstruction> woInstructions) {
		this.woInstructions = woInstructions;
	}

	@Transient
	public String getWoInstructionJson() {
		return woInstructionJson;
	}

	public void setWoInstructionJson(String woInstructionJson) {
		this.woInstructionJson = woInstructionJson;
	}

	@Transient
	public void serializeWoInstructions() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		woInstructionJson = gson.toJson(woInstructions);
	}

	@Transient
	public void deserializeWoInstructions() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<WorkOrderInstruction>>(){}.getType();
		woInstructions = gson.fromJson(woInstructionJson, type);
	}

	@Transient
	public List<WorkOrderItem> getWoItems() {
		return woItems;
	}

	public void setWoItems(List<WorkOrderItem> woItems) {
		this.woItems = woItems;
	}

	@Transient
	public String getWoItemJson() {
		return woItemJson;
	}

	public void setWoItemJson(String woItemJson) {
		this.woItemJson = woItemJson;
	}

	@Transient
	public void serializeWoItems() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		woItemJson = gson.toJson(woItems);
	}

	@Transient
	public void deserializeWoItems() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<WorkOrderItem>>(){}.getType();
		woItems = gson.fromJson(woItemJson, type);
	}


	@Transient
	public List<WorkOrderLine> getWoLines() {
		return woLines;
	}

	public void setWoLines(List<WorkOrderLine> woLines) {
		this.woLines = woLines;
	}

	@Transient
	public String getWoLineJson() {
		return woLineJson;
	}

	public void setWoLineJson(String woLineJson) {
		this.woLineJson = woLineJson;
	}

	@Transient
	public void serializeWoLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		woLineJson = gson.toJson(woLines);
	}

	@Transient
	public void deserializeWoLines() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<WorkOrderLine>>(){}.getType();
		woLines = gson.fromJson(woLineJson, type);
	}

	@Override
	@Transient
	public List<OOChild> getChildren() {
		List<OOChild> children = new ArrayList<OOChild>();
		if (woInstructions != null) {
			children.addAll(woInstructions);
		}
		if (woItems != null) {
			children.addAll(woItems);
		}
		if (woLines != null) {
			children.addAll(woLines);
		}
		if (referenceDocuments != null) {
			children.addAll(referenceDocuments);
		}
		if(woPurchasedItems != null) {
			children.addAll(woPurchasedItems);
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

	@OneToOne
	@JoinColumn(name = "COMPANY_ID", insertable=false, updatable=false)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
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
	@JoinColumn(name = "AR_CUSTOMER_ACCOUNT_ID", insertable=false, updatable=false)
	public ArCustomerAccount getArCustomerAccount() {
		return arCustomerAccount;
	}

	public void setArCustomerAccount(ArCustomerAccount arCustomerAccount) {
		this.arCustomerAccount = arCustomerAccount;
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
	public void serializeReferenceDocuments() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		referenceDocumentsJson = gson.toJson(referenceDocuments);
	}

	@Transient
	public void deserializeReferenceDocuments() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<ReferenceDocument>>(){}.getType();
		referenceDocuments = gson.fromJson(referenceDocumentsJson, type);
	}

	@Transient
	public String getSoNumber() {
		return soNumber;
	}

	public void setSoNumber(String soNumber) {
		this.soNumber = soNumber;
	}

	@Transient
	public String getCommonErrorMsg() {
		return commonErrorMsg;
	}

	public void setCommonErrorMsg(String commonErrorMsg) {
		this.commonErrorMsg = commonErrorMsg;
	}

	@Column(name = "REFERENCE_WORK_ORDER_ID")
	public Integer getRefWorkOrderId() {
		return refWorkOrderId;
	}

	public void setRefWorkOrderId(Integer refWorkOrderId) {
		this.refWorkOrderId = refWorkOrderId;
	}

	@Transient
	public String getRefWoNumber() {
		return refWoNumber;
	}

	public void setRefWoNumber(String refWoNumber) {
		this.refWoNumber = refWoNumber;
	}

	@Transient
	public List<SubWorkOrderDto> getSubWorkOrderDtos() {
		return subWorkOrderDtos;
	}

	public void setSubWorkOrderDtos(List<SubWorkOrderDto> subWorkOrderDtos) {
		this.subWorkOrderDtos = subWorkOrderDtos;
	}

	@Transient
	public List<WorkOrderPurchasedItem> getWoPurchasedItems() {
		return woPurchasedItems;
	}

	public void setWoPurchasedItems(List<WorkOrderPurchasedItem> woPurchasedItems) {
		this.woPurchasedItems = woPurchasedItems;
	}

	@Transient
	public String getWoPurchasedItemsJson() {
		return woPurchasedItemsJson;
	}

	public void setWoPurchasedItemsJson(String woPurchasedItemsJson) {
		this.woPurchasedItemsJson = woPurchasedItemsJson;
	}

	@Transient
	public void serializeWoPurchasedItems() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		woPurchasedItemsJson = gson.toJson(woPurchasedItems);
	}

	@Transient
	public void deserializeWoPurchasedItems() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<WorkOrderPurchasedItem>>(){}.getType();
		woPurchasedItems = gson.fromJson(woPurchasedItemsJson, type);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WorkOrder [sequenceNumber=").append(sequenceNumber).append(", salesOrderId=")
				.append(salesOrderId).append(", companyId=").append(companyId).append(", date=").append(date)
				.append(", arCustomerId=").append(arCustomerId).append(", arCustomerAcctId=").append(arCustomerAcctId)
				.append(", targetEndDate=").append(targetEndDate).append(", workDescription=").append(workDescription)
				.append("]");
		return builder.toString();
	}
}
