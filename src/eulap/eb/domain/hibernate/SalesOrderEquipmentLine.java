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
 * Object repesentation for SALES_ORDER_EQUIPMENT_LINE table.

 *
 */
@Entity
@Table(name="SALES_ORDER_EQUIPMENT_LINE")
public class SalesOrderEquipmentLine extends AROtherCharge {
	private Integer salesOrderId;
	@Expose
	private Double discountValue;
	@Expose
	private Double discount;
	@Expose
	private Integer refenceObjectId;

	public enum FIELD {
		id, salesOrderId, discount, amount, ebObjectId
	}

	/**
	 * Sales Order Item object type id: 12021
	 */
	public static final int OBJECT_TYPE = 12021;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "SALES_ORDER_EQUIPMENT_LINE_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "SALES_ORDER_ID")
	public Integer getSalesOrderId() {
		return salesOrderId;
	}

	public void setSalesOrderId(Integer salesOrderId) {
		this.salesOrderId = salesOrderId;
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

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE;
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
		builder.append("SalesOrderLine [salesOrderId=").append(salesOrderId)
				.append(", discountValue=").append(discountValue).append(", discount=")
				.append(discount).append(", refenceObjectId=").append(refenceObjectId).append("]");
		return builder.toString();
	}
}
