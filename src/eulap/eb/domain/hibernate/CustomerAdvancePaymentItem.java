package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

import eulap.eb.service.inventory.MillingItem;

/**
 * Domain class for CUSTOMER_ADVANCE_PAYMENT_ITEM

 *
 */
@Entity
@Table(name="CUSTOMER_ADVANCE_PAYMENT_ITEM")
public class CustomerAdvancePaymentItem extends SaleItem implements MillingItem{
	@Expose
	private Integer customerAdvancePaymentId;
	@Expose
	private Double existingStock;
	private Double totalQty;
	@Expose
	private Integer refenceObjectId;
	@Expose
	private Double itemBagQuantity;
	@Expose
	private Double discountValue;
	@Expose
	private Integer itemDiscountTypeId;
	private ItemDiscountType itemDiscountType;

	/**
	 * Object type Id for {@link CustomerAdvancePaymentItem} = 33.
	 */
	public static final int OBJECT_TYPE_ID = 33;
	/**
	 * Sales Order Item to Customer Advance Payment Item OR type id: 12003
	 */
	public static final int CAPI_SOI_RELATIONSHIP = 12003;

	public enum FIELD {
		customerAdvancePaymentId, warehouseId, itemId, warehouse, itemSrp,
		grossAmount, netAmount, amount, ebObjectId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "CUSTOMER_ADVANCE_PAYMENT_ITEM_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	/**
	 * Get the customer advance payment id.
	 * @return The customer advance payment id.
	 */
	@Column(name = "CUSTOMER_ADVANCE_PAYMENT_ID")
	public Integer getCustomerAdvancePaymentId() {
		return customerAdvancePaymentId;
	}

	public void setCustomerAdvancePaymentId(Integer customerAdvancePaymentId) {
		this.customerAdvancePaymentId = customerAdvancePaymentId;
	}

	@Transient
	public Double getExistingStock() {
		return existingStock;
	}

	public void setExistingStock(Double existingStock) {
		this.existingStock = existingStock;
	}

	@Transient
	public Double getTotalQty() {
		return totalQty;
	}

	public void setTotalQty(Double totalQty) {
		this.totalQty = totalQty;
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

	@Override
	@Column(name = "GROSS_AMOUNT")
	public Double getGrossAmount() {
		return super.getGrossAmount();
	}

	@Override
	public void setGrossAmount(Double grossAmount) {
		super.setGrossAmount(grossAmount);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CustomerAdvancePaymentItem [customerAdvancePaymentId=").append(customerAdvancePaymentId)
				.append(", existingStock=").append(existingStock).append(", totalQty=").append(totalQty)
				.append(", ebObjectId=").append(getEbObjectId()).append(", getId()=").append(getId())
				.append(", discountValue=").append(discountValue).append(", itemDiscountTypeId=")
				.append(itemDiscountTypeId).append("]");
		return builder.toString();
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE_ID;
	}

	@Transient
	@Override
	public Integer getRefenceObjectId() {
		return refenceObjectId;
	}

	public void setRefenceObjectId(Integer refenceObjectId) {
		this.refenceObjectId = refenceObjectId;
	}

	@Transient
	@Override
	public Double getItemBagQuantity() {
		return itemBagQuantity;
	}

	@Override
	public void setItemBagQuantity(Double itemBagQuantity) {
		this.itemBagQuantity = itemBagQuantity;
	}

	@OneToOne
	@JoinColumn(name = "ITEM_DISCOUNT_TYPE_ID", insertable=false, updatable=false)
	public ItemDiscountType getItemDiscountType() {
		return itemDiscountType;
	}

	public void setItemDiscountType(ItemDiscountType itemDiscountType) {
		this.itemDiscountType = itemDiscountType;
	}
}
