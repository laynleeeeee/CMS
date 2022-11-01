package eulap.eb.domain.hibernate;

import java.util.Date;

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

import eulap.common.domain.BaseDomain;
import eulap.common.util.DateUtil;

/**
 * Object class of R_RETURN_TO_SUPPLIER table.

 */
@Entity
@Table(name="R_RETURN_TO_SUPPLIER")
public class RReturnToSupplier extends BaseDomain{
	private Integer apInvoiceId;
	private Integer companyId;
	private Integer warehouseId;
	private Company company;
	private Warehouse warehouse;
	private String rrNumber;
	private String invoiceDate;
	private APInvoice apInvoice;
	private Integer apInvoiceRefId;

	public static final int RTS_SERIAL_ITEM_OR_TYPE_ID = 105;
	public static final int RTS_API_GS_OR_TYPE_ID = 24005;
	public static final int RTS_CHILD_TO_CHILD_OR_TYP_ID = 10;

	public enum FIELD {
		id, apInvoiceId, companyId, warehouseId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "R_RETURN_TO_SUPPLIER_ID", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name="AP_INVOICE_ID", columnDefinition="int(10)")
	public Integer getApInvoiceId() {
		return apInvoiceId;
	}

	public void setApInvoiceId(Integer apInvoiceId) {
		this.apInvoiceId = apInvoiceId;
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
	 * Get the Supplier associated with return to supplier.
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
	 * Get the Supplier associated with return to supplier.
	 */
	@ManyToOne
	@JoinColumn(name="WAREHOUSE_ID", insertable=false, updatable=false)
	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
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
	 * Formats the RTS Number to: M 1
	 * @return The formatted RTS Number.
	 */
	@Transient
	public String getFormattedRTSNumber() {
		if (company != null && apInvoice != null) {
			Integer sequenceNo = apInvoice.getSequenceNumber();
			String companyCode = company.getCompanyCode();
			if (companyCode != null) {
				return companyCode + " " + sequenceNo;
			} else {
				char firstLetter = company.getName().charAt(0);
				return firstLetter + " " + sequenceNo;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "RReturnToSupplier [ID="+getId()+"companyId=" + companyId
				+ ", warehouseId=" + warehouseId + "]";
	}

	@Transient
	public String getRrNumber() {
		return rrNumber;
	}

	public void setRrNumber(String rrNumber) {
		this.rrNumber = rrNumber;
	}

	@Transient
	public String getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	@Transient
	public Integer getApInvoiceRefId() {
		return apInvoiceRefId;
	}

	public void setApInvoiceRefId(Integer apInvoiceRefId) {
		this.apInvoiceRefId = apInvoiceRefId;
	}

	@Transient
	public String toJson () {
		Gson gson = new GsonBuilder()
			.setDateFormat(DateUtil.DATE_FORMAT)
			.excludeFieldsWithoutExposeAnnotation().create();
		return gson.toJson(this);
	}

}