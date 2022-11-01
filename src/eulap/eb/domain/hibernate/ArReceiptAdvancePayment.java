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
 * Object class representation for AR_RECEIPT_ADVANCE_PAYMENT table

 */

@Entity
@Table(name="AR_RECEIPT_ADVANCE_PAYMENT")
public class ArReceiptAdvancePayment extends BaseFormLine {
	@Expose
	private Integer arReceiptId;
	@Expose
	private Integer customerAdvancePaymentId;
	@Expose
	private String capNumber;
	@Expose
	private Double amount;
	@Expose
	private Integer refenceObjectId;

	public static final int OBJECT_TYPE_ID = 12012;

	public enum FIELD {
		id, arReceiptId, customerAdvancePaymentId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "AR_RECEIPT_ADVANCE_PAYMENT_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "AR_RECEIPT_ID")
	public Integer getArReceiptId() {
		return arReceiptId;
	}

	public void setArReceiptId(Integer arReceiptId) {
		this.arReceiptId = arReceiptId;
	}

	@Column(name = "CUSTOMER_ADVANCE_PAYMENT_ID")
	public Integer getCustomerAdvancePaymentId() {
		return customerAdvancePaymentId;
	}

	public void setCustomerAdvancePaymentId(Integer customerAdvancePaymentId) {
		this.customerAdvancePaymentId = customerAdvancePaymentId;
	}

	@Transient
	public String getCapNumber() {
		return capNumber;
	}

	public void setCapNumber(String capNumber) {
		this.capNumber = capNumber;
	}

	@Transient
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ArReceiptAdvancePayment [arReceiptId=").append(arReceiptId)
				.append(", customerAdvancePaymentId=").append(customerAdvancePaymentId).append(", capNumber=")
				.append(capNumber).append(", amount=").append(amount).append(", refenceObjectId=")
				.append(refenceObjectId).append("]");
		return builder.toString();
	}
}
