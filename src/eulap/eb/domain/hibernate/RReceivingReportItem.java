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
 * A class that represent R_RECEIVING_REPORT_ITEM in CBS database.

 */
@Entity
@Table(name="R_RECEIVING_REPORT_ITEM")
public class RReceivingReportItem extends BaseItem{
	@Expose
	private Integer apInvoiceId;
	@Expose
	private Double srp;
	@Expose
	private Integer buyingAddOnId;
	@Expose
	private Integer buyingDiscountId;
	@Expose
	private Integer refenceObjectId;
	@Expose
	private Integer itemDiscountTypeId;
	@Expose
	private Double discount;
	@Expose
	private Double discountValue;
	private Double amount;
	private APInvoice apInvoice;
	private RReceivingReportRmItem rmItem;
	private ItemDiscountType itemDiscountType;

	public static final int RR_ITEM_OBJECT_TYPE_ID = 40;

	public enum FIELD {
		id, apInvoiceId, itemId, quantity, unitCost, ebObjectId, referenceObjectId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "R_RECEIVING_REPORT_ITEM_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	/**
	 * Get the unique id of the associated receiving report.
	 * @return The receiving report id.
	 */
	@Column(name = "AP_INVOICE_ID")
	public Integer getApInvoiceId() {
		return apInvoiceId;
	}

	public void setApInvoiceId(Integer apInvoiceId) {
		this.apInvoiceId = apInvoiceId;
	}

	@Transient
	public Double getSrp() {
		return srp;
	}

	public void setSrp(Double srp) {
		this.srp = srp;
	}

	@Override
	public boolean isSplitWithUnitCost(BaseItem itemLine) {
		return false;
	}

	@ManyToOne
	@JoinColumn(name="AP_INVOICE_ID", insertable=false, updatable=false)
	public APInvoice getApInvoice() {
		return apInvoice;
	}

	public void setApInvoice(APInvoice apInvoice) {
		this.apInvoice = apInvoice;
	}

	@Transient
	public RReceivingReportRmItem getRmItem() {
		return rmItem;
	}

	public void setRmItem(RReceivingReportRmItem rmItem) {
		this.rmItem = rmItem;
	}

	@Transient
	public Integer getBuyingAddOnId() {
		return buyingAddOnId;
	}

	public void setBuyingAddOnId(Integer buyingAddOnId) {
		this.buyingAddOnId = buyingAddOnId;
	}

	@Transient
	public Integer getBuyingDiscountId() {
		return buyingDiscountId;
	}

	public void setBuyingDiscountId(Integer buyingDiscountId) {
		this.buyingDiscountId = buyingDiscountId;
	}

	@Override
	@Transient
	public Integer getRefenceObjectId() {
		return refenceObjectId;
	}

	public void setReferenceObjectId(Integer refenceObjectId) {
		this.refenceObjectId = refenceObjectId;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return RR_ITEM_OBJECT_TYPE_ID;
	}

	@Column(name = "AMOUNT")
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Column(name = "DISCOUNT_VALUE")
	public Double getDiscountValue() {
		return discountValue;
	}

	public void setDiscountValue(Double discountValue) {
		this.discountValue = discountValue;
	}

	@Column(name = "ITEM_DISCOUNT_TYPE_ID")
	public Integer getItemDiscountTypeId() {
		return itemDiscountTypeId;
	}

	public void setItemDiscountTypeId(Integer itemDiscountTypeId) {
		this.itemDiscountTypeId = itemDiscountTypeId;
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
		builder.append("RReceivingReportItem [apInvoiceId=").append(apInvoiceId).append(", srp=").append(srp)
				.append(", buyingAddOnId=").append(buyingAddOnId).append(", buyingDiscountId=").append(buyingDiscountId)
				.append(", refenceObjectId=").append(refenceObjectId).append("]");
		return builder.toString();
	}
}
