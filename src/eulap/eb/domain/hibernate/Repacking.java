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

import eulap.eb.service.oo.OOChild;

/**
 * Object representation of REPACKING table.

 */

@Entity
@Table(name = "REPACKING")
public class Repacking extends BaseFormWorkflow {
	private Integer companyId;
	private Integer divisionId;
	private Integer warehouseId;
	private Integer rNumber;
	private Date rDate;
	private String remarks;
	private List<RepackingItem> rItems;
	private String repackingItemsJson;
	private String repackingMessage;
	private Company company;
	private Division division;
	private Warehouse warehouse;
	private Integer repackingTypeId;
	private List<RepackingRawMaterial> repackingRawMaterials;
	private String rpRawMaterialJson;
	private List<RepackingFinishedGood> repackingFinishedGoods;
	private String rpFinishedGoodJson;
	private List<ReferenceDocument> referenceDocuments;
	private String referenceDocumentsJson;
	private String referenceDocsMessage;

	/**
	 * Object Type Id for Repacking = 59.
	 */
	public static final int REPACKING_OBJECT_TYPE_ID = 59;

	public enum FIELD {
		id, companyId, divisionId, warehouseId, rNumber, rDate, remarks, formWorkflowId, ebObjectId, repackingTypeId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "REPACKING_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name = "COMPANY_ID", columnDefinition="int(10)")
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@Column(name = "DIVISION_ID", columnDefinition="int(10)")
	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@Column(name = "WAREHOUSE_ID", columnDefinition="int(10)")
	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	@Column(name = "R_NUMBER", columnDefinition="int(20)")
	public Integer getrNumber() {
		return rNumber;
	}

	public void setrNumber(Integer rNumber) {
		this.rNumber = rNumber;
	}

	@Column(name = "R_DATE", columnDefinition="date")
	public Date getrDate() {
		return rDate;
	}

	public void setrDate(Date rDate) {
		this.rDate = rDate;
	}

	@Column(name = "REMARKS", columnDefinition="varchar(100)")
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Fetch(FetchMode.SELECT)
	@OneToMany(fetch=FetchType.EAGER)
	@JoinColumn(name="REPACKING_ID", insertable=false, updatable=false)
	public List<RepackingItem> getrItems() {
		return rItems;
	}

	public void setrItems(List<RepackingItem> rItems) {
		this.rItems = rItems;
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
	@JoinColumn (name = "DIVISION_ID", insertable=false, updatable=false)
	public Division getDivision() {
		return division;
	}

	public void setDivision(Division division) {
		this.division = division;
	}

	@OneToOne
	@JoinColumn (name = "WAREHOUSE_ID", insertable=false, updatable=false)
	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	/**
	 * Formats the repacking sequence number to: A 1
	 * @return The formatted repacking sequence number
	 */
	@Transient
	public String getFormattedRNumber() {
		if (company != null){
			if (company.getCompanyCode() != null){
				String companyCode = company.getCompanyCode();
				return companyCode  + " " + rNumber;
			} else {
				char firstLetter = company.getName().charAt(0);
				return firstLetter + " " + rNumber;
			}
		}
		return null;
	}

	@Transient
	public String getRepackingMessage() {
		return repackingMessage;
	}

	public void setRepackingMessage(String repackingMessage) {
		this.repackingMessage = repackingMessage;
	}

	@Transient
	public String getRepackingItemsJson() {
		return repackingItemsJson;
	}

	public void setRepackingItemsJson(String repackingItemsJson) {
		this.repackingItemsJson = repackingItemsJson;
	}

	@Transient
	public void serializeItems() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		repackingItemsJson = gson.toJson(rItems);
	}

	public void deserializeItems() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<RepackingItem>>(){}.getType();
		rItems = gson.fromJson(repackingItemsJson, type);
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return REPACKING_OBJECT_TYPE_ID;
	}

	@Override
	@Transient
	public List<OOChild> getChildren() {
		List<OOChild> children = new ArrayList<OOChild>();
		if (rItems != null) {
			children.addAll(rItems);
		}
		if (repackingRawMaterials != null) {
			children.addAll(repackingRawMaterials);
		}
		if (repackingFinishedGoods != null) {
			children.addAll(repackingFinishedGoods);
		}
		if (referenceDocuments != null) {
			children.addAll(referenceDocuments);
		}
		return children;
	}

	@Override
	@Transient
	public Date getGLDate() {
		return rDate;
	}

	@Column(name = "REPACKING_TYPE_ID", columnDefinition="int(10)")
	public Integer getRepackingTypeId() {
		return repackingTypeId;
	}

	public void setRepackingTypeId(Integer repackingTypeId) {
		this.repackingTypeId = repackingTypeId;
	}

	@Transient
	public List<RepackingRawMaterial> getRepackingRawMaterials() {
		return repackingRawMaterials;
	}

	public void setRepackingRawMaterials(List<RepackingRawMaterial> repackingRawMaterials) {
		this.repackingRawMaterials = repackingRawMaterials;
	}

	@Transient
	public String getRpRawMaterialJson() {
		return rpRawMaterialJson;
	}

	public void setRpRawMaterialJson(String rpRawMaterialJson) {
		this.rpRawMaterialJson = rpRawMaterialJson;
	}

	@Transient
	public void serializeRawMaterials() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		rpRawMaterialJson = gson.toJson(repackingRawMaterials);
	}

	public void deserializeRawMaterials() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<RepackingRawMaterial>>(){}.getType();
		repackingRawMaterials = gson.fromJson(rpRawMaterialJson, type);
	}

	@Transient
	public List<RepackingFinishedGood> getRepackingFinishedGoods() {
		return repackingFinishedGoods;
	}

	public void setRepackingFinishedGoods(List<RepackingFinishedGood> repackingFinishedGoods) {
		this.repackingFinishedGoods = repackingFinishedGoods;
	}

	@Transient
	public String getRpFinishedGoodJson() {
		return rpFinishedGoodJson;
	}

	public void setRpFinishedGoodJson(String rpFinishedGoodJson) {
		this.rpFinishedGoodJson = rpFinishedGoodJson;
	}

	@Transient
	public void serializeFinishedGoods() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		rpFinishedGoodJson = gson.toJson(repackingFinishedGoods);
	}

	public void deserializeFinishedGoods() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<RepackingFinishedGood>>(){}.getType();
		repackingFinishedGoods = gson.fromJson(rpFinishedGoodJson, type);
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

	@Override
	@Transient
	public String getWorkflowName() {
		String workflowName = this.getClass().getSimpleName();
		if(divisionId != null) {
			return workflowName+getRepackingTypeId()+getDivisionId();
		}
		return workflowName + getRepackingTypeId();
	}

	@Override
	public String toString() {
		return "Repacking [companyId=" + companyId + ", divisionId=" + divisionId + ", warehouseId=" + warehouseId
				+ ", rNumber=" + rNumber + ", rDate=" + rDate + ", remarks=" + remarks + ", repackingTypeId="
				+ repackingTypeId + "]";
	}
}