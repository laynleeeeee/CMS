package eulap.eb.domain.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

/**
 * Object representation of AP_PAYMENT_LINE table.

 */
@Entity
@Table(name = "AP_PAYMENT_LINE")
public class ApPaymentLine extends BaseFormLine{
	private Integer apPaymentId;
	private Integer apPaymentLineTypeId;
	private Double paidAmount;
	private Integer refenceObjectId;
	private ApPayment apPayment;
	private Double currencyRateValue;

	public enum FIELD {
		apPaymentId, apPaymentLineTypeId, paidAmount, ebObjectId
	}

	public static final int OBJECT_TYPE =24004;

	public static ApPaymentLine getInstanceOf (int id, int apPaymentId, int supplierAdvancePaymentId, Double amount) {
		ApPaymentLine api = new ApPaymentLine();
		api.setId(id);
		api.apPaymentId = apPaymentId;
		return api;
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "AP_PAYMENT_LINE_ID", columnDefinition = "int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Override
	@Column(name = "CREATED_BY", columnDefinition = "int(10)")
	public int getCreatedBy() {
		return super.getCreatedBy();
	}

	@Override
	@Column(name = "CREATED_DATE", columnDefinition = "timestamp")
	public Date getCreatedDate() {
		return super.getCreatedDate();
	}

	@Override
	@Column(name = "UPDATED_BY", columnDefinition = "int(10)")
	public int getUpdatedBy() {
		return super.getUpdatedBy();
	}

	@Override
	@Column(name = "UPDATED_DATE", columnDefinition = "timestamp")
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
	}

	@Column(name = "AP_PAYMENT_ID", columnDefinition = "int(10)")
	public Integer getApPaymentId() {
		return apPaymentId;
	}

	public void setApPaymentId(Integer apPaymentId) {
		this.apPaymentId = apPaymentId;
	}

	@Column(name = "PAID_AMOUNT", columnDefinition = "DOUBLE")
	public Double getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(Double paidAmount) {
		this.paidAmount = paidAmount;
	}

	@Column(name = "AP_PAYMENT_LINE_TYPE_ID", columnDefinition = "int(10)")
	public Integer getApPaymentLineTypeId() {
		return apPaymentLineTypeId;
	}

	public void setApPaymentLineTypeId(Integer apPaymentLineTypeId) {
		this.apPaymentLineTypeId = apPaymentLineTypeId;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "AP_PAYMENT_ID", insertable=false, updatable=false, nullable=true)
	public ApPayment getApPayment() {
		return apPayment;
	}

	public void setApPayment(ApPayment apPayment) {
		this.apPayment = apPayment;
	}

	@Column(name = "CURRENCY_RATE_VALUE", columnDefinition = "double")
	public Double getCurrencyRateValue() {
		return currencyRateValue;
	}

	public void setCurrencyRateValue(Double currencyRateValue) {
		this.currencyRateValue = currencyRateValue;
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
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ApPaymentLine [apPaymentId=").append(apPaymentId).append(", apPaymentLineTypeId=")
				.append(apPaymentLineTypeId).append(", paidAmount=").append(paidAmount).append("]");
		return builder.toString();
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE;
	}
}
