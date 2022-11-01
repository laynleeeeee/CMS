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
 * Object representation class for SALES_QUOTATION_EQUIPMENT_LINE table

 */

@Entity
@Table(name="SALES_QUOTATION_EQUIPMENT_LINE")
public class SalesQuotationEquipmentLine extends AROtherCharge {
	private Integer salesQuotationId;
	@Expose
	private Double discountValue;
	@Expose
	private Double discount;
	@Expose
	private Integer refenceObjectId;

	public static final int OBJECT_TYPE = 12019;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "SALES_QUOTATION_EQUIPMENT_LINE_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "SALES_QUOTATION_ID")
	public Integer getSalesQuotationId() {
		return salesQuotationId;
	}

	public void setSalesQuotationId(Integer salesQuotationId) {
		this.salesQuotationId = salesQuotationId;
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
		builder.append("SalesQuotationLine [salesQuotationId=").append(salesQuotationId)
				.append(", discountValue=").append(discountValue).append(", discount=")
				.append(discount).append(", refenceObjectId=").append(refenceObjectId).append("]");
		return builder.toString();
	}
}
