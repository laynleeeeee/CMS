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
 * Object representation of RETAIL - TRANSFER_RECEIPT table.

 *
 */
@Entity
@Table(name="R_TRANSFER_RECEIPT")
public class RTransferReceipt extends BaseFormWorkflow {
	private Integer companyId;
	private Integer warehouseFromId;
	private Integer warehouseToId;
	private Integer transferReceiptTypeId;
	private Date trDate;
	private String drNumber;
	private Integer trNumber;
	private List<RTransferReceiptItem> rTrItems;
	private Company company;
	private Warehouse warehouseFrom;
	private Warehouse warehouseTo;
	private TransferReceiptType transferReceiptType;
	private String trMessage;
	private String receiptItemsJson;

	public static final int TR_RETAIL = 1;
	public static final int TR_IS = 2;

	/**
	 * Object Type for Transfer Receipt = 25.
	 */
	public static final int TR_OBJECT_TYPE_ID = 25;

	/**
	 * Object Type for Transfer Receipt IS = 148.
	 */
	public static final int TR_IS_OBJECT_TYPE_ID = 148;

	public enum FIELD {
		id, companyId, warehouseFromId, warehouseToId, trDate,
		drNumber, trNumber, formWorkflowId, transferReceiptTypeId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "R_TRANSFER_RECEIPT_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
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

	/**
	 * Get the unique id of the company.
	 * @return The company id.
	 */
	@Column(name = "COMPANY_ID", columnDefinition="int(10)")
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	/**
	 * Get the Id of the warehouse from of the Retail - Transfer Receipt.
	 * @return The Id of the warehouse.
	 */
	@Column(name="WAREHOUSE_FROM_ID", columnDefinition="int(10)")
	public Integer getWarehouseFromId() {
		return warehouseFromId;
	}

	/**
	 * Set the Id of the warehouse from of the Retail - Transfer Receipt.
	 * @param fromId The Id of the location.
	 */
	public void setWarehouseFromId(Integer warehouseFromId) {
		this.warehouseFromId = warehouseFromId;
	}

	/**
	 * Get the Id of the warehouse to of the Retail - Transfer Receipt.
	 * @return The Id of the warehouse.
	 */
	@Column(name="WAREHOUSE_TO_ID", columnDefinition="int(10)")
	public Integer getWarehouseToId() {
		return warehouseToId;
	}

	/**
	 * Set the Id of the warehouse to of the Retail - Transfer Receipt.
	 * @param fromId The Id of the warehouse.
	 */
	public void setWarehouseToId(Integer warehouseToId) {
		this.warehouseToId = warehouseToId;
	}

	/**
	 * Get the Date of the Retail - Transfer Receipt.
	 * @return The TR date.
	 */
	@Column(name="TR_DATE", columnDefinition="date")
	public Date getTrDate() {
		return trDate;
	}

	/**
	 * Set the Date of the Retail - Transfer Receipt.
	 * @param trDate The TR date.
	 */
	public void setTrDate(Date trDate) {
		this.trDate = trDate;
	}

	/**
	 * Get the DR Number of the Retail - Transfer Receipt.
	 * @return The DR number.
	 */
	@Column(name="DR_NUMBER", columnDefinition="varchar(20)")
	public String getDrNumber() {
		return drNumber;
	}

	/**
	 * Set the DR Number of the Transfer Receipt.
	 * @param drNumber The DR number.
	 */
	public void setDrNumber(String drNumber) {
		this.drNumber = drNumber;
	}

	/**
	 * Get the TR number of the Retail - Transfer Receipt.
	 * @return The TR number.
	 */
	@Column(name="TR_NUMBER", columnDefinition="int(20)")
	public Integer getTrNumber() {
		return trNumber;
	}

	/**
	 * Set the TR number of the Retail - Transfer Receipt.
	 * @param trNumber The TR number.
	 */
	public void setTrNumber(Integer trNumber) {
		this.trNumber = trNumber;
	}

	/**
	 * Get the Items associated with the Retail - Transfer Receipt.
	 * @return The RTransferReceiptItems items.
	 */
	@Fetch(FetchMode.SELECT)
	@OneToMany(fetch=FetchType.EAGER)
	@JoinColumn(name="R_TRANSFER_RECEIPT_ID", insertable=false, updatable=false)
	public List<RTransferReceiptItem> getrTrItems() {
		return rTrItems;
	}

	public void setrTrItems(List<RTransferReceiptItem> rTrItems) {
		this.rTrItems = rTrItems;
	}

	@ManyToOne
	@JoinColumn (name = "COMPANY_ID", insertable=false, updatable=false)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@ManyToOne
	@JoinColumn (name = "WAREHOUSE_FROM_ID", insertable=false, updatable=false)
	public Warehouse getWarehouseFrom() {
		return warehouseFrom;
	}

	public void setWarehouseFrom(Warehouse warehouseFrom) {
		this.warehouseFrom = warehouseFrom;
	}

	@ManyToOne
	@JoinColumn (name = "WAREHOUSE_TO_ID", insertable=false, updatable=false)
	public Warehouse getWarehouseTo() {
		return warehouseTo;
	}

	public void setWarehouseTo(Warehouse warehouseTo) {
		this.warehouseTo = warehouseTo;
	}

	@ManyToOne
	@JoinColumn (name = "TRANSFER_RECEIPT_TYPE_ID", insertable=false, updatable=false)
	public TransferReceiptType getTransferReceiptType() {
		return transferReceiptType;
	}

	public void setTransferReceiptType(TransferReceiptType transferReceiptType) {
		this.transferReceiptType = transferReceiptType;
	}

	@Column(name="TRANSFER_RECEIPT_TYPE_ID", columnDefinition="int(10)")
	public Integer getTransferReceiptTypeId() {
		return transferReceiptTypeId;
	}

	public void setTransferReceiptTypeId(Integer transferReceiptTypeId) {
		this.transferReceiptTypeId = transferReceiptTypeId;
	}

	@Override
	public String toString() {
		return "RTransferReceipt [companyId=" + companyId
				+ ", warehouseFromId=" + warehouseFromId + ", warehouseToId="
				+ warehouseToId + ", trDate=" + trDate + ", drNumber="
				+ drNumber + ", trNumber=" + trNumber + ", ID=" + getId() + "]";
	}

	@Transient
	public String getTrMessage() {
		return trMessage;
	}

	public void setTrMessage(String trMessage) {
		this.trMessage = trMessage;
	}

	/**
	 * Formats the TR Number to: A 1
	 * @return The formatted TR Number.
	 */
	@Transient
	public String getFormattedTRNumber() {
		if (company != null){
			if (company.getCompanyCode() != null){
				String companyCode = company.getCompanyCode();
				return companyCode + " " + trNumber;
			}else {
				char firstLetter = company.getName().charAt(0);
				return firstLetter + " " + trNumber;
			}
		}
		return null;
	}

	@Transient
	public String getReceiptItemsJson() {
		return receiptItemsJson;
	}

	public void setReceiptItemsJson(String receiptItemsJson) {
		this.receiptItemsJson = receiptItemsJson;
	}

	@Transient
	public void serializeItems() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		receiptItemsJson = gson.toJson(rTrItems);
	}

	public void deserializeItems() {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		Type type = new TypeToken <List<RTransferReceiptItem>>(){}.getType();
		rTrItems = gson.fromJson(receiptItemsJson, type);
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		if(transferReceiptTypeId == TR_IS) {
			return TR_IS_OBJECT_TYPE_ID;
		} else {
			return TR_OBJECT_TYPE_ID;
		}
	}

	@Override
	@Transient
	public List<OOChild> getChildren() {
		List<OOChild> children = new ArrayList<>();
		children.addAll(rTrItems);
		return children;
	}

	@Override
	@Transient
	public String getWorkflowName() {
		return super.getWorkflowName() + getTransferReceiptTypeId();
	}

	@Override
	@Transient
	public String getShortDescription() {
		return "TR" + " " + trNumber;
	}

	@Override
	@Transient
	public Date getGLDate() {
		return trDate;
	}
}