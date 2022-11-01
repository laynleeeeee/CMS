package eulap.eb.domain.hibernate;

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

import eulap.eb.service.oo.OOChild;

/**
 * Object representation for REQUISITION_FORM table.

 */
@Entity
@Table(name="REQUISITION_FORM")
public class RequisitionForm extends BaseFormWorkflow {
	private Integer requisitionTypeId;
	private Integer requisitionClassificationId;
	private Integer reqFormRefId;
	private Integer companyId;
	private Integer sequenceNumber;
	private Date date;
	private Integer jobOrderId;
	private Integer fleetProfileId;
	private Integer arCustomerId;
	private Double distance;
	private Integer ratioId;
	private Double liters;
	private Integer projectId;
	private String requestedBy;
	private Date requestedDate;
	private String remarks;
	private List<RequisitionFormItem> requisitionFormItems;
	private List<OtherChargesLine> otherChargesLines;
	private List<ReferenceDocument> referenceDocuments;
	private RequisitionType requisitionType;
	private RequisitionClassification requisitionClassification;
	private Company company;
	private ArCustomer arCustomer;
	private FleetProfile fleetProfile;
	private RequisitionForm reqFormRef;
	private Ratio ratio;
	private Integer warehouseId;
	private List<PurchaseRequisitionItem> purchaseRequisitionItems;
	private Integer workOrderId;
	private String woNumber;

	public static final int RF_TIRE_OBJECT_TYPE_ID = 3002;
	public static final int RF_FUEL_OBJECT_TYPE_ID = 3022;
	public static final int RF_PMS_OBJECT_TYPE_ID = 3023;
	public static final int RF_ELECTRICAL_OBJECT_TYPE_ID = 3024;
	public static final int RF_CM_OBJECT_TYPE_ID = 3025;
	public static final int RF_ADMIN_OBJECT_TYPE_ID = 3026;
	public static final int RF_MOTORPOOL_OBJECT_TYPE_ID = 3027;
	public static final int RF_OIL_OBJECT_TYPE_ID = 3028;
	public static final int RF_SUBCON_OBJECT_TYPE_ID = 3029;
	public static final int RF_PAKYAWAN_OBJECT_TYPE_ID = 3030;

	/**
	 * Referenced RF form status
	 */
	public static final int STATUS_ALL = 1;
	public static final int STATUS_UNUSED = 2;
	public static final int STATUS_USED = 3;

	public enum FIELD {
		id, requisitionTypeId, companyId, sequenceNumber, reqFormRefId, date, jobOrderId, formWorkflowId, createdDate,
		fleetProfileId, projectId, ebObjectId, requisitionClassificationId, arCustomerId, warehouseId, workOrderId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "REQUISITION_FORM_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Override
	@Column(name = "CREATED_BY", columnDefinition="INT(10)")
	public int getCreatedBy() {
		return super.getCreatedBy();
	}

	@Override
	@Column(name = "CREATED_DATE", columnDefinition="TIMESTAMP")
	public Date getCreatedDate() {
		return super.getCreatedDate();
	}

	@Override
	@Column(name = "UPDATED_BY", columnDefinition="INT(10)")
	public int getUpdatedBy() {
		return super.getUpdatedBy();
	}

	@Override
	@Column(name = "UPDATED_DATE", columnDefinition="TIMESTAMP")
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
	}

	@Column(name="REQUISITION_TYPE_ID", columnDefinition="int(10)")
	public Integer getRequisitionTypeId() {
		return requisitionTypeId;
	}

	public void setRequisitionTypeId(Integer requisitionTypeId) {
		this.requisitionTypeId = requisitionTypeId;
	}

	@Column(name="REQUISITION_CLASSIFICATION_ID", columnDefinition="int(10)")
	public Integer getRequisitionClassificationId() {
		return requisitionClassificationId;
	}

	public void setRequisitionClassificationId(Integer requisitionClassificationId) {
		this.requisitionClassificationId = requisitionClassificationId;
	}

	@Column(name="REQ_FORM_REF_ID", columnDefinition="int(10)")
	public Integer getReqFormRefId() {
		return reqFormRefId;
	}

	public void setReqFormRefId(Integer reqFormRefId) {
		this.reqFormRefId = reqFormRefId;
	}

	@Column(name="COMPANY_ID", columnDefinition="int(10)")
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@Column(name="SEQUENCE_NO", columnDefinition="int(10)")
	public Integer getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	@Column(name="DATE", columnDefinition="int(10)")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Column(name="FLEET_PROFILE_ID", columnDefinition="int(10)")
	public Integer getFleetProfileId() {
		return fleetProfileId;
	}

	public void setFleetProfileId(Integer fleetProfileId) {
		this.fleetProfileId = fleetProfileId;
	}

	@Column(name="PROJECT_ID", columnDefinition="int(10)")
	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	@Column(name="AR_CUSTOMER_ID", columnDefinition="int(10)")
	public Integer getArCustomerId() {
		return arCustomerId;
	}

	public void setArCustomerId(Integer arCustomerId) {
		this.arCustomerId = arCustomerId;
	}

	@Column(name="DISTANCE", columnDefinition="double")
	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	@Column(name="RATIO_ID", columnDefinition="int(10)")
	public Integer getRatioId() {
		return ratioId;
	}

	public void setRatioId(Integer ratioId) {
		this.ratioId = ratioId;
	}

	@Column(name="LITERS", columnDefinition="double")
	public Double getLiters() {
		return liters;
	}

	public void setLiters(Double liters) {
		this.liters = liters;
	}

	@Column(name="REQUESTED_BY", columnDefinition="varchar(100)")
	public String getRequestedBy() {
		return requestedBy;
	}

	public void setRequestedBy(String requestedBy) {
		this.requestedBy = requestedBy;
	}

	@Column(name="REQUESTED_DATE", columnDefinition="date")
	public Date getRequestedDate() {
		return requestedDate;
	}

	public void setRequestedDate(Date requestedDate) {
		this.requestedDate = requestedDate;
	}

	@Column(name="REMARKS", columnDefinition="text")
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Column(name="WORK_ORDER_ID", columnDefinition="int(10)")
	public Integer getWorkOrderId() {
		return workOrderId;
	}

	public void setWorkOrderId(Integer workOrderId) {
		this.workOrderId = workOrderId;
	}

	@Transient
	@Override
	public List<OOChild> getChildren() {
		List<OOChild> children = new ArrayList<>();
		if (requisitionFormItems != null) {
			children.addAll(requisitionFormItems);
		}
		if (referenceDocuments != null) {
			children.addAll(referenceDocuments);
		}
		if (purchaseRequisitionItems != null) {
			children.addAll(purchaseRequisitionItems);
		}
		return children;
	}

	@Transient
	public List<RequisitionFormItem> getRequisitionFormItems() {
		return requisitionFormItems;
	}

	public void setRequisitionFormItems(List<RequisitionFormItem> requisitionFormItems) {
		this.requisitionFormItems = requisitionFormItems;
	}

	@Transient
	@Override
	public Integer getObjectTypeId() {
		switch (requisitionTypeId) {
			case RequisitionType.RT_FUEL : 
				return RF_FUEL_OBJECT_TYPE_ID;
			case RequisitionType.RT_PMS : 
				return RF_PMS_OBJECT_TYPE_ID;
			case RequisitionType.RT_ELECTRICAL : 
				return RF_ELECTRICAL_OBJECT_TYPE_ID;
			case RequisitionType.RT_CONSTRUCTION_MATERIAL : 
				return RF_CM_OBJECT_TYPE_ID;
			case RequisitionType.RT_ADMIN : 
				return RF_ADMIN_OBJECT_TYPE_ID;
			case RequisitionType.RT_MOTORPOOL : 
				return RF_MOTORPOOL_OBJECT_TYPE_ID;
			case RequisitionType.RT_OIL : 
				return RF_OIL_OBJECT_TYPE_ID;
			case RequisitionType.RT_SUBCON : 
				return RF_SUBCON_OBJECT_TYPE_ID;
			case RequisitionType.RT_PAKYAWAN : 
				return RF_PAKYAWAN_OBJECT_TYPE_ID;
		}
		return RF_TIRE_OBJECT_TYPE_ID;
	}

	@Transient
	public List<OtherChargesLine> getOtherChargesLines() {
		return otherChargesLines;
	}

	public void setOtherChargesLines(List<OtherChargesLine> otherChargesLines) {
		this.otherChargesLines = otherChargesLines;
	}

	@Transient
	public List<ReferenceDocument> getReferenceDocuments() {
		return referenceDocuments;
	}

	public void setReferenceDocuments(List<ReferenceDocument> referenceDocuments) {
		this.referenceDocuments = referenceDocuments;
	}

	@ManyToOne
	@JoinColumn(name="REQUISITION_TYPE_ID", insertable=false, updatable=false)
	public RequisitionType getRequisitionType() {
		return requisitionType;
	}

	public void setRequisitionType(RequisitionType requisitionType) {
		this.requisitionType = requisitionType;
	}

	@ManyToOne
	@JoinColumn(name="REQUISITION_CLASSIFICATION_ID", insertable=false, updatable=false)
	public RequisitionClassification getRequisitionClassification() {
		return requisitionClassification;
	}

	public void setRequisitionClassification(RequisitionClassification requisitionClassification) {
		this.requisitionClassification = requisitionClassification;
	}

	@ManyToOne
	@JoinColumn(name="COMPANY_ID", insertable=false, updatable=false)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@ManyToOne
	@JoinColumn(name="AR_CUSTOMER_ID", insertable=false, updatable=false)
	public ArCustomer getArCustomer() {
		return arCustomer;
	}

	public void setArCustomer(ArCustomer arCustomer) {
		this.arCustomer = arCustomer;
	}

	@ManyToOne
	@JoinColumn(name="FLEET_PROFILE_ID", insertable=false, updatable=false)
	public FleetProfile getFleetProfile() {
		return fleetProfile;
	}

	public void setFleetProfile(FleetProfile fleetProfile) {
		this.fleetProfile = fleetProfile;
	}

	@OneToOne
	@JoinColumn(name="REQ_FORM_REF_ID", insertable=false, updatable=false)
	public RequisitionForm getReqFormRef() {
		return reqFormRef;
	}

	public void setReqFormRef(RequisitionForm reqFormRef) {
		this.reqFormRef = reqFormRef;
	}

	@ManyToOne
	@JoinColumn(name="RATIO_ID", insertable=false, updatable=false)
	public Ratio getRatio() {
		return ratio;
	}

	public void setRatio(Ratio ratio) {
		this.ratio = ratio;
	}

	/**
	 * Formats the RF Number to: RF 1
	 * @return The formatted RR Number.
	 */
	@Transient
	public String getFormattedSequenceNo() {
		if (company != null) {
			if (company.getCompanyCode() != null) {
				String companyCode = company.getCompanyCode();
				return companyCode + " " + sequenceNumber;
			} else {
				char firstLetter = company.getName().charAt(0);
				return firstLetter + " " + sequenceNumber;
			}
		}
		return null;
	}

	@Override
	@Transient
	public String getWorkflowName() {
		String workflowName = null;
		if (requisitionClassificationId != null &&
				requisitionClassificationId == RequisitionClassification.RC_PURCHASE_REQUISITION) {
			workflowName = "PurchaseRequisition";
		} else {
			workflowName = super.getWorkflowName();
		}
		workflowName += requisitionTypeId;
		return workflowName;
	}

	@Transient
	public Date getGLDate() {
		return date;
	}

	@Column(name="WAREHOUSE_ID", columnDefinition="INT(10)")
	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	@Transient
	public List<PurchaseRequisitionItem> getPurchaseRequisitionItems() {
		return purchaseRequisitionItems;
	}

	public void setPurchaseRequisitionItems(List<PurchaseRequisitionItem> purchaseRequisitionItems) {
		this.purchaseRequisitionItems = purchaseRequisitionItems;
	}

	@Transient
	public String getWoNumber() {
		return woNumber;
	}

	public void setWoNumber(String woNumber) {
		this.woNumber = woNumber;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RequisitionForm [requisitionTypeId=").append(requisitionTypeId)
				.append(", requisitionClassificationId=").append(requisitionClassificationId).append(", reqFormRefId=")
				.append(reqFormRefId).append(", companyId=").append(companyId).append(", sequenceNumber=")
				.append(sequenceNumber).append(", date=").append(date).append(", jobOrderId=").append(jobOrderId)
				.append(", fleetProfileId=").append(fleetProfileId).append(", arCustomerId=").append(arCustomerId)
				.append(", distance=").append(distance).append(", ratioId=").append(ratioId).append(", liters=")
				.append(liters).append(", projectId=").append(projectId).append(", requestedBy=").append(requestedBy)
				.append(", requestedDate=").append(requestedDate).append(", remarks=").append(remarks)
				.append(", warehouseId=").append(warehouseId).append(", workOrderId=").append(workOrderId).append("]");
		return builder.toString();
	}
}
