package eulap.eb.domain.hibernate;

import java.lang.reflect.Type;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import eulap.common.domain.BaseDomain;

/**
 * A class that represent RETAIL RECEIVING REPORT in the database.

 */
@Entity
@Table(name="R_RECEIVING_REPORT")
public class RReceivingReport extends BaseDomain {
	private Integer apInvoiceId;
	private Integer companyId;
	private Integer warehouseId;
	private String deliveryReceiptNo;
	private APInvoice apInvoice;
	private Warehouse warehouse;
	private Company company;
	private String rriErrorMsg;
	private Integer supplierId;
	private Integer supplierAccountId;
	private Integer termId;
	private Date rrDate;
	private Date invoiceDate;
	private String poNumber;
	private String supplierInvoiceNo;
	private List<SerialItem> serialItems;
	private String siMessage;
	private String serialItemsJson;
	private String bmsNumber;
	private Integer divisionId;
	private Division division;
	private String remarks;
	private String referenceDocumentJson;
	private List<ReferenceDocument> referenceDocuments;
	private String referenceDocsMessage;
	private String commonErrMsg;
	private String cancellationRemarks;

	public static final int STATUS_ALL = 1;
	public static final int STATUS_UNUSED = 2;
	public static final int STATUS_USED = 3;

	public static final int MAX_CHAR_DR_NO = 50;

	public enum FIELD {
		id, companyId, warehouseId, deliveryReceiptNo, apInvoiceId, createdDate, poNumber, divisionId, bmsNumber
	}

	/**
	 * PO to RR OR type id: 74
	 */
	public static final int RECEIVING_REPORT_TO_PURCHASE_ORDER = 74;
	/**
	 * RR to Serial Item OR type id: 74
	 */
	public static final int RECEIVING_REPORT_TO_SERIAL_ITEM = 64;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "R_RECEIVING_REPORT_ID", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name = "AP_INVOICE_ID", columnDefinition="int(10)")
	public Integer getApInvoiceId() {
		return apInvoiceId;
	}

	public void setApInvoiceId(Integer apInvoiceId) {
		this.apInvoiceId = apInvoiceId;
	}

	@OneToOne
	@JoinColumn (name = "AP_INVOICE_ID", insertable=false, updatable=false)
	public APInvoice getApInvoice() {
		return apInvoice;
	}

	public void setApInvoice(APInvoice apInvoice) {
		this.apInvoice = apInvoice;
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
	 * Get the unique id of the warehouse.
	 * @return The warehouse id.
	 */
	@Column(name = "WAREHOUSE_ID", columnDefinition="int(10)")
	public Integer getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}

	/**
	 * Get the warehouse associated with receiving report.
	 */
	@ManyToOne
	@JoinColumn(name="WAREHOUSE_ID", insertable=false, updatable=false)
	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	/**
	 * Get the delivery receipt number of the receiving report.
	 * @return The delivery receipt number.
	 */
	@Column(name = "DELIVERY_RECEIPT_NO", columnDefinition="varchar(100)")
	public String getDeliveryReceiptNo() {
		return deliveryReceiptNo;
	}

	public void setDeliveryReceiptNo(String deliveryReceiptNo) {
		this.deliveryReceiptNo = deliveryReceiptNo;
	}

	/**
	 * Get the company associated with receiving report.
	 */
	@ManyToOne
	@JoinColumn(name="COMPANY_ID", insertable=false, updatable=false)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	/**
	 * Formats the RR Number to: M 1
	 * @return The formatted RR Number.
	 */
	@Transient
	public String getFormattedRRNumber() {
		if (company != null) {
			if (company.getCompanyCode() != null) {
				String companyCode = company.getCompanyCode();
				return companyCode + " " + apInvoice.getSequenceNumber();
			} else {
				char firstLetter = company.getName().charAt(0);
				return firstLetter + " " + apInvoice.getSequenceNumber();
			}
		}
		return null;
	}

	@Transient
	public String getRrNumber() {
		return "RR " + apInvoice.getSequenceNumber();
	}

	/**
	 * Get the error message for receiving report item.
	 * @return The receiving report item error message.
	 */
	@Transient
	public String getRriErrorMsg() {
		return rriErrorMsg;
	}

	public void setRriErrorMsg(String rriErrorMsg) {
		this.rriErrorMsg = rriErrorMsg;
	}

	@Transient
	public Integer getSupplierAccountId() {
		return supplierAccountId;
	}

	public void setSupplierAccountId(Integer supplierAccountId) {
		this.supplierAccountId = supplierAccountId;
	}

	@Transient
	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	@Transient
	public Integer getTermId() {
		return termId;
	}

	public void setTermId(Integer termId) {
		this.termId = termId;
	}

	@Transient
	public Date getRrDate() {
		return rrDate;
	}

	public void setRrDate(Date rrDate) {
		this.rrDate = rrDate;
	}

	@Transient
	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	@Transient
	public String getSupplierInvoiceNo() {
		return supplierInvoiceNo;
	}

	public void setSupplierInvoiceNo(String supplierInvoiceNo) {
		this.supplierInvoiceNo = supplierInvoiceNo;
	}

	/**
	 * Get the purchase order number of the receiving report.
	 * @return The purchase order number.
	 */
	@Column(name = "PO_NUMBER", columnDefinition="varchar(100)")
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
	public String getSiMessage() {
		return siMessage;
	}

	public void setSiMessage(String siMessage) {
		this.siMessage = siMessage;
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
		Gson gson = new GsonBuilder().
				excludeFieldsWithoutExposeAnnotation().setDateFormat("MM/dd/yyyy").create();
		Type type = new TypeToken <List<SerialItem>>(){}.getType();
		serialItems = gson.fromJson(serialItemsJson, type);
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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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

	@Transient
	public String getCommonErrMsg() {
		return commonErrMsg;
	}

	public void setCommonErrMsg(String commonErrMsg) {
		this.commonErrMsg = commonErrMsg;
	}

	@Transient
	public String getCancellationRemarks() {
		return cancellationRemarks;
	}

	public void setCancellationRemarks(String cancellationRemarks) {
		this.cancellationRemarks = cancellationRemarks;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RReceivingReport [apInvoiceId=").append(apInvoiceId).append(", companyId=").append(companyId)
				.append(", warehouseId=").append(warehouseId).append(", deliveryReceiptNo=").append(deliveryReceiptNo)
				.append(", apInvoice=").append(apInvoice).append(", rriErrorMsg=").append(rriErrorMsg)
				.append(", supplierId=").append(supplierId).append(", supplierAccountId=").append(supplierAccountId)
				.append(", termId=").append(termId).append(", rrDate=").append(rrDate).append(", invoiceDate=")
				.append(invoiceDate).append(", poNumber=").append(poNumber).append(", supplierInvoiceNo=")
				.append(supplierInvoiceNo).append(", siMessage=").append(siMessage).append(", serialItemsJson=")
				.append(serialItemsJson).append(", bmsNumber=").append(bmsNumber).append(", divisionId=")
				.append(", remarks=").append(remarks).append(", divisionId=").append(divisionId).append("]");
		return builder.toString();
	}
}