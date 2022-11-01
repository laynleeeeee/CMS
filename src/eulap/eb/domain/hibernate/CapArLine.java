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
 * Class that represents CAP_AR_LINE table of the database

 */
@Entity
@Table(name = "CAP_AR_LINE")
public class CapArLine extends AROtherCharge {
	private Integer customerAdvancePaymentId;
	private CustomerAdvancePayment customerAdvancePayment;
	@Expose
	private Double discountValue;
	@Expose
	private Double discount;

	public static final int OBJECT_TYPE_ID = 34;

	public enum FIELD {
		id, customerAdvancePaymentId, arLineSetupId, unitOfMeasurementId,
		amount, upAmount, quantity, ebObjectId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "CAP_AR_LINE_ID", columnDefinition = "int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "CUSTOMER_ADVANCE_PAYMENT_ID", columnDefinition="int(10)")
	public Integer getCustomerAdvancePaymentId() {
		return customerAdvancePaymentId;
	}

	public void setCustomerAdvancePaymentId(Integer customerAdvancePaymentId) {
		this.customerAdvancePaymentId = customerAdvancePaymentId;
	}

	@ManyToOne
	@JoinColumn (name = "CUSTOMER_ADVANCE_PAYMENT_ID", insertable=false, updatable=false)
	public CustomerAdvancePayment getCustomerAdvancePayment() {
		return customerAdvancePayment;
	}

	public void setCustomerAdvancePayment(
			CustomerAdvancePayment customerAdvancePayment) {
		this.customerAdvancePayment = customerAdvancePayment;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE_ID;
	}

	@Override
	@Transient
	public Integer getRefenceObjectId() {
		// No reference for this object.
		return null;
	}

	@Column(name = "DISCOUNT_VALUE", columnDefinition="double")
	public Double getDiscountValue() {
		return discountValue;
	}

	public void setDiscountValue(Double discountValue) {
		this.discountValue = discountValue;
	}

	@Column(name = "DISCOUNT", columnDefinition="double")
	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CapArLine [customerAdvancePaymentId=").append(customerAdvancePaymentId)
				.append(", customerAdvancePayment=").append(customerAdvancePayment).append(", ebObjectId=")
				.append(getEbObjectId()).append(", getId()=").append(getId()).append("]");
		return builder.toString();
	}
}
