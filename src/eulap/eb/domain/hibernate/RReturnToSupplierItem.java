package eulap.eb.domain.hibernate;

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

import com.google.gson.annotations.Expose;
/**
 * A class that  represent R_RETURN_TO_SUPPLIER_ITEM table in CBS.

 */
@Entity
@Table(name="R_RETURN_TO_SUPPLIER_ITEM")
public class RReturnToSupplierItem extends BaseItem {
	@Expose
	private Integer apInvoiceId;
	@Expose
	private Integer rReceivingReportItemId;
	@Expose
	private Double existingStock;
	@Expose
	private Integer refenceObjectId;
	@Expose
	private Double amount;
	@Expose
	private Integer itemDiscountTypeId;
	@Expose
	private Double discountValue;
	@Expose
	private Double discount;
	private APInvoice apInvoice;
	private ItemDiscountType itemDiscountType;

	/**
	 * Object Type Id for {@link RReturnToSupplierItem} = 58.
	 */
	public static final int OBJECT_TYPE_ID = 58;

	public enum FIELD {
		id, apInvoiceId, rReceivingReportItemId, itemId, quantity, ebObjectId, referenceObjectId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "R_RETURN_TO_SUPPLIER_ITEM_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	/**
	 * Get the unique id of the associated receiving report.
	 * @return The receiving report id.
	 */
	@Column(name = "AP_INVOICE_ID", columnDefinition="int(10)")
	public Integer getApInvoiceId() {
		return apInvoiceId;
	}

	public void setApInvoiceId(Integer apInvoiceId) {
		this.apInvoiceId = apInvoiceId;
	}

	/**
	 * Get the unique id of the associated receiving report.
	 * @return The receiving report id.
	 */
	@Column(name = "R_RECEIVING_REPORT_ITEM_ID", columnDefinition="int(10)")
	public Integer getrReceivingReportItemId() {
		return rReceivingReportItemId;
	}

	public void setrReceivingReportItemId(Integer rReceivingReportItemId) {
		this.rReceivingReportItemId = rReceivingReportItemId;
	}

	@ManyToOne
	@JoinColumn (name = "AP_INVOICE_ID", insertable=false, updatable=false)
	public APInvoice getApInvoice() {
		return apInvoice;
	}

	public void setApInvoice(APInvoice apInvoice) {
		this.apInvoice = apInvoice;
	}

	@Transient
	public Double getExistingStock() {
		return existingStock;
	}

	public void setExistingStock(Double existingStock) {
		this.existingStock = existingStock;
	}

	@Override
	public boolean isSplitWithUnitCost(BaseItem itemLine) {
		return false;
	}

	@Override
	@Transient
	public Integer getRefenceObjectId() {
		return refenceObjectId;
	}

	public void setRefenceObjectId(Integer refenceObjectId) {
		this.refenceObjectId = refenceObjectId;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE_ID;
	}

	@Column(name = "AMOUNT")
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Column(name = "ITEM_DISCOUNT_TYPE_ID")
	public Integer getItemDiscountTypeId() {
		return itemDiscountTypeId;
	}

	public void setItemDiscountTypeId(Integer itemDiscountTypeId) {
		this.itemDiscountTypeId = itemDiscountTypeId;
	}

	@Column(name = "DISCOUNT_VALUE")
	public Double getDiscountValue() {
		return discountValue;
	}

	public void setDiscountValue(Double discountValue) {
		this.discountValue = discountValue;
	}

	@Column(name = "DISCOUNT")
	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	@OneToOne
	@JoinColumn(name = "ITEM_DISCOUNT_TYPE_ID", insertable=false, updatable=false)
	public ItemDiscountType getItemDiscountType() {
		return itemDiscountType;
	}

	public void setItemDiscountType(ItemDiscountType itemDiscountType) {
		this.itemDiscountType = itemDiscountType;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RReturnToSupplierItem [apInvoiceId=").append(apInvoiceId).append(", rReceivingReportItemId=")
				.append(rReceivingReportItemId).append(", existingStock=").append(existingStock)
				.append(", refenceObjectId=").append(refenceObjectId).append("]");
		return builder.toString();
	}
}