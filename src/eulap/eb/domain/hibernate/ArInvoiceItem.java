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

/**
 * Object Representation class of AR_INVOICE_ITEM

 */

@Entity
@Table(name="AR_INVOICE_ITEM")
public class ArInvoiceItem extends SaleItem {
	@Expose
	private Integer arInvoiceId;
	@Expose
	private Integer refenceObjectId;
	@Expose
	private Integer itemDiscountTypeId;
	@Expose
	private Double discountValue;
	private ItemDiscountType itemDiscountType;

	public static final int OBJECT_TYPE_ID = 12010;

	public enum FIELD {
		id, ebObjectId, itemId, deliveryReceiptId, arInvoiceId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "AR_INVOICE_ITEM_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name="AR_INVOICE_ID")
	public Integer getArInvoiceId() {
		return arInvoiceId;
	}

	public void setArInvoiceId(Integer arInvoiceId) {
		this.arInvoiceId = arInvoiceId;
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
		builder.append("ArInvoiceItem [arInvoiceId=").append(arInvoiceId).append(", refenceObjectId=")
				.append(refenceObjectId).append(", itemDiscountTypeId=").append(itemDiscountTypeId)
				.append(", discountValue=").append(discountValue).append("]");
		return builder.toString();
	}
}
