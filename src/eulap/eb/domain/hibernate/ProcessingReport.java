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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.eb.service.oo.OOChild;

/**
 * A class that represents the PROCESS_REPORT table in the CBS database.

 *
 */
@Entity
@Table(name="PROCESSING_REPORT")
public class ProcessingReport extends BaseFormWorkflow{
	private Integer sequenceNo;
	private Integer companyId;
	private Date date;
	private String refNumber;
	private String remarks;
	private List<PrRawMaterialsItem> prRawMaterialsItems;
	private List<PrOtherMaterialsItem> prOtherMaterialsItems;
	private List<PrOtherCharge> prOtherCharges;
	private List<PrMainProduct> prMainProducts;
	private List<PrByProduct> prByProducts;
	private String rawMaterialsJson;
	private String otherMaterialsJson;
	private String prOtherChargesJson;
	private String prMainProductJson;
	private String prByProductJson;
	private String errRMItems;
	private String errOMItems;
	private String errOtherCharges;
	private String errMainProduct;
	private String errByProduct;
	private Company company;
	private Integer processingReportTypeId;
	private ProcessingReportType processingType;

	public static final int MAX_REF_NUMBER = 20;
	public static final int MAX_REMARKS = 200;

	/**
	 * Object type for Processing Report - Milling Report = 4
	 */
	public static final int PR_MILLING_REPORT_OBJECT_TYPE_ID = 4;

	/**
	 * Object type for Processing Report - Milling Order = 27
	 */
	public static final int PR_MILLING_ORDER_OBJECT_TYPE_ID = 27;

	/**
	 * Object type for Processing Report -Pass IN = 28
	 */
	public static final int PR_PASS_IN_OBJECT_TYPE_ID = 28;

	/**
	 * Object type for Processing Report - Pass OUT = 29
	 */
	public static final int PR_PASS_OUT_OBJECT_TYPE_ID = 29;

	/**
	 * Object type for Processing Report - WIP BAKING = 56
	 */
	public static final int PR_WIP_BAKING_OBJECT_TYPE_ID = 56;

	/**
	 * Object type for Processing Report - Production = 57
	 */
	public static final int PR_PRODUCTION_OBJECT_TYPE_ID = 57;

	public enum FIELD {
		id, sequenceNo, companyId, date, refNumber, remarks,
		formWorkflowId, processingReportTypeId, createdDate
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "PROCESSING_REPORT_ID", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name = "SEQUENCE_NO", columnDefinition="int(10)")
	public Integer getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(Integer sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	@Column(name = "COMPANY_ID", columnDefinition="int(10)")
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@Column(name = "DATE", columnDefinition="date")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Column(name = "REF_NUMBER", columnDefinition="varchar(20)")
	public String getRefNumber() {
		return refNumber;
	}

	public void setRefNumber(String refNumber) {
		this.refNumber = refNumber;
	}

	@Column(name = "REMARKS", columnDefinition="varchar(200)")
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@OneToMany
	@JoinColumn (name = "PROCESSING_REPORT_ID", insertable=false, updatable=false)
	public List<PrRawMaterialsItem> getPrRawMaterialsItems() {
		return prRawMaterialsItems;
	}

	public void setPrRawMaterialsItems(List<PrRawMaterialsItem> prRawMaterialsItems) {
		this.prRawMaterialsItems = prRawMaterialsItems;
	}

	@Transient
	public void serializeRMItems(){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		rawMaterialsJson = gson.toJson(prRawMaterialsItems);
	}

	@Transient
	public void deSerializeRMItems() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<PrRawMaterialsItem>>(){}.getType();
		prRawMaterialsItems = gson.fromJson(rawMaterialsJson, type);
	}

	@OneToMany
	@JoinColumn (name = "PROCESSING_REPORT_ID", insertable=false, updatable=false)
	public List<PrOtherMaterialsItem> getPrOtherMaterialsItems() {
		return prOtherMaterialsItems;
	}

	public void setPrOtherMaterialsItems(
			List<PrOtherMaterialsItem> prOtherMaterialsItems) {
		this.prOtherMaterialsItems = prOtherMaterialsItems;
	}

	@Transient
	public void serializeOMItems(){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		otherMaterialsJson = gson.toJson(prOtherMaterialsItems);
	}

	@Transient
	public void deSerializeOMItems() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<PrOtherMaterialsItem>>(){}.getType();
		prOtherMaterialsItems = gson.fromJson(otherMaterialsJson, type);
	}

	@OneToMany
	@JoinColumn (name = "PROCESSING_REPORT_ID", insertable=false, updatable=false)
	public List<PrOtherCharge> getPrOtherCharges() {
		return prOtherCharges;
	}

	public void setPrOtherCharges(List<PrOtherCharge> prOtherCharges) {
		this.prOtherCharges = prOtherCharges;
	}

	@Transient
	public void serializePrOtherCharges(){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		prOtherChargesJson = gson.toJson(prOtherCharges);
	}

	@Transient
	public void deSerializePrOtherCharges() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<PrOtherCharge>>(){}.getType();
		prOtherCharges = gson.fromJson(prOtherChargesJson, type);
	}

	@OneToMany
	@JoinColumn (name = "PROCESSING_REPORT_ID", insertable=false, updatable=false)
	public List<PrMainProduct> getPrMainProducts() {
		return prMainProducts;
	}

	public void setPrMainProducts(List<PrMainProduct> prMainProducts) {
		this.prMainProducts = prMainProducts;
	}

	@Transient
	public void serializePrMainProducts(){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		prMainProductJson = gson.toJson(prMainProducts);
	}

	@Transient
	public void deSerializePrMainProducts() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<PrMainProduct>>(){}.getType();
		prMainProducts = gson.fromJson(prMainProductJson, type);
	}

	@OneToMany
	@JoinColumn (name = "PROCESSING_REPORT_ID", insertable=false, updatable=false)
	public List<PrByProduct> getPrByProducts() {
		return prByProducts;
	}

	public void setPrByProducts(List<PrByProduct> prByProducts) {
		this.prByProducts = prByProducts;
	}

	@Transient
	public void serializePrByProducts(){
		Gson gson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		prByProductJson = gson.toJson(prByProducts);
	}

	@Transient
	public void deSerializePrByProducts() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<PrByProduct>>(){}.getType();
		prByProducts = gson.fromJson(prByProductJson, type);
	}

	@Transient
	public String getRawMaterialsJson() {
		return rawMaterialsJson;
	}

	public void setRawMaterialsJson(String rawMaterialsJson) {
		this.rawMaterialsJson = rawMaterialsJson;
	}

	@Transient
	public String getOtherMaterialsJson() {
		return otherMaterialsJson;
	}

	public void setOtherMaterialsJson(String otherMaterialsJson) {
		this.otherMaterialsJson = otherMaterialsJson;
	}

	@Transient
	public String getPrOtherChargesJson() {
		return prOtherChargesJson;
	}

	public void setPrOtherChargesJson(String prOtherChargesJson) {
		this.prOtherChargesJson = prOtherChargesJson;
	}
	
	@Transient
	public String getErrRMItems() {
		return errRMItems;
	}

	public void setErrRMItems(String errRMItems) {
		this.errRMItems = errRMItems;
	}

	@Transient
	public String getErrOMItems() {
		return errOMItems;
	}

	public void setErrOMItems(String errOMItems) {
		this.errOMItems = errOMItems;
	}

	@Transient
	public String getErrOtherCharges() {
		return errOtherCharges;
	}

	public void setErrOtherCharges(String errOtherCharges) {
		this.errOtherCharges = errOtherCharges;
	}

	@Transient
	public String getPrMainProductJson() {
		return prMainProductJson;
	}

	public void setPrMainProductJson(String prMainProductJson) {
		this.prMainProductJson = prMainProductJson;
	}

	@Transient
	public String getPrByProductJson() {
		return prByProductJson;
	}

	public void setPrByProductJson(String prByProductJson) {
		this.prByProductJson = prByProductJson;
	}

	@Transient
	public String getErrMainProduct() {
		return errMainProduct;
	}

	public void setErrMainProduct(String errMainProduct) {
		this.errMainProduct = errMainProduct;
	}

	@Transient
	public String getErrByProduct() {
		return errByProduct;
	}


	public void setErrByProduct(String errByProduct) {
		this.errByProduct = errByProduct;
	}

	@ManyToOne
	@JoinColumn (name = "COMPANY_ID", insertable=false, updatable=false)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	/**
	 * Formats the Processing Report Number to: MN 1
	 * @return The formatted RR Number.
	 */
	@Transient
	public String getFormattedPRNumber() {
		String formattedPrNumber = processShortCut(processingReportTypeId) +" "+ sequenceNo;
		if (company != null) {
			if (company.getCompanyCode() != null) {
				String companyCode = company.getCompanyCode();
				return companyCode + " " + formattedPrNumber;
			}else {
				char firstLetter = company.getName().charAt(0);
				return firstLetter + " " + formattedPrNumber;
			}
		}
		return null;
	}

	@Transient
	public String processShortCut (int typeId) {
		switch (typeId) {
			case ProcessingReportType.WIP_BAKING:
				return "PR-B";
			case ProcessingReportType.PRODUCTION:
				return "PR";
			default:
				return "MN";
		}
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		//PROCESSING_REPORT type in OBJECT_TYPE table
		switch (processingReportTypeId) {
		case ProcessingReportType.MILLING_ORDER:
			return PR_MILLING_ORDER_OBJECT_TYPE_ID;
		case ProcessingReportType.PASS_IN:
			return PR_PASS_IN_OBJECT_TYPE_ID;
		case ProcessingReportType.PASS_OUT:
			return PR_PASS_OUT_OBJECT_TYPE_ID;
		case ProcessingReportType.WIP_BAKING:
			return PR_WIP_BAKING_OBJECT_TYPE_ID;
		case ProcessingReportType.PRODUCTION:
			return PR_PRODUCTION_OBJECT_TYPE_ID;
		default:
			return PR_MILLING_REPORT_OBJECT_TYPE_ID;
		}
	}

	@Override
	@Transient
	public List<OOChild> getChildren() {
		List<OOChild> children = new ArrayList<>();
		children.addAll(prRawMaterialsItems);
		children.addAll(prMainProducts);
		if(prOtherCharges != null) {
			children.addAll(prOtherCharges);
		}
		if(prOtherMaterialsItems != null) {
			children.addAll(prOtherMaterialsItems);
		}
		if(prByProducts != null) {
			children.addAll(prByProducts);
		}
		return children;
	}

	@Override
	@Transient
	public String getShortDescription() {
		return "PR-" + sequenceNo;
	}

	@Override
	@Transient
	public String getWorkflowName() {
		return super.getWorkflowName() + getProcessingReportTypeId();
	}

	@Column(name = "PROCESSING_REPORT_TYPE_ID", columnDefinition="int(10)")
	public Integer getProcessingReportTypeId() {
		return processingReportTypeId;
	}

	public void setProcessingReportTypeId(Integer processingReportTypeId) {
		this.processingReportTypeId = processingReportTypeId;
	}

	@ManyToOne
	@JoinColumn (name = "PROCESSING_REPORT_TYPE_ID", insertable=false, updatable=false)
	public ProcessingReportType getProcessingType() {
		return processingType;
	}

	public void setProcessingType(ProcessingReportType processingType) {
		this.processingType = processingType;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProcessingReport [sequenceNo=").append(sequenceNo).append(", companyId=").append(companyId)
				.append(", date=").append(date).append(", refNumber=").append(refNumber).append(", remarks=")
				.append(remarks).append(", company=").append(company).append(", processingReportTypeId=")
				.append(processingReportTypeId).append(", processingType=").append(processingType).append("]");
		return builder.toString();
	}
}