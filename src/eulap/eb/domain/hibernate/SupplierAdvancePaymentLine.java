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
 * Domain object representation class for SUPPLIER_ADVANCE_PAYMENT_LINE

 */

@Entity
@Table(name="SUPPLIER_ADVANCE_PAYMENT_LINE")
public class SupplierAdvancePaymentLine extends BaseFormLine {
	private Integer supplierAdvPaymentId;
	@Expose
	private Integer refenceObjectId;
	@Expose
	private String referenceNo;
	@Expose
	private Double amount;

	public static final int OBJECT_TYPE_ID = 24001;

	public enum FIELD {
		id, supplierAdvPaymentId, ebObjectId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "SUPPLIER_ADVANCE_PAYMENT_LINE_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "SUPPLIER_ADVANCE_PAYMENT_ID")
	public Integer getSupplierAdvPaymentId() {
		return supplierAdvPaymentId;
	}

	public void setSupplierAdvPaymentId(Integer supplierAdvPaymentId) {
		this.supplierAdvPaymentId = supplierAdvPaymentId;
	}

	@Column(name = "AMOUNT")
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Transient
	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
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
		return OBJECT_TYPE_ID;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SupplierAdvancePaymentLine [supplierAdvPaymentId=").append(supplierAdvPaymentId)
				.append(", amount=").append(amount).append("]");
		return builder.toString();
	}
}
