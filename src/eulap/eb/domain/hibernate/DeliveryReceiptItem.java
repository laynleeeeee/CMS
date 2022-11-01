package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

/**
 * Object Representation class of DELIVERY_RECEIPT_ITEM

 */

@Entity
@Table(name="DELIVERY_RECEIPT_ITEM")
public class DeliveryReceiptItem extends SaleItem {
	@Expose
	private Integer deliveryReceiptId;
	@Expose
	private Integer refenceObjectId;
	@Expose
	private Integer itemDiscountTypeId;
	@Expose
	private Double discountValue;

	public static final int OBJECT_TYPE_ID = 111;

	public enum FIELD {
		id, ebObjectId, itemId, deliveryReceiptId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "DELIVERY_RECEIPT_ITEM_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name="DELIVERY_RECEIPT_ID")
	public Integer getDeliveryReceiptId() {
		return deliveryReceiptId;
	}

	public void setDeliveryReceiptId(Integer deliveryReceiptId) {
		this.deliveryReceiptId = deliveryReceiptId;
	}

	@Column(name="ITEM_DISCOUNT_TYPE_ID")
	public Integer getItemDiscountTypeId() {
		return itemDiscountTypeId;
	}

	public void setItemDiscountTypeId(Integer itemDiscountTypeId) {
		this.itemDiscountTypeId = itemDiscountTypeId;
	}

	@Column(name="DISCOUNT_VALUE")
	public Double getDiscountValue() {
		return discountValue;
	}

	public void setDiscountValue(Double discountValue) {
		this.discountValue = discountValue;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE_ID;
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
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DeliveryReceiptItem [deliveryReceiptId=").append(deliveryReceiptId).append(", refenceObjectId=")
				.append(refenceObjectId).append(", itemDiscountTypeId=").append(itemDiscountTypeId)
				.append(", discountValue=").append(discountValue).append("]");
		return builder.toString();
	}
}
