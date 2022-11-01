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
 * Object representation of AP_PAYMENT_INVOICE table.

 */
@Entity
@Table(name = "AP_PAYMENT_INVOICE")
public class ApPaymentInvoice extends BaseFormLine{
	private Integer apPaymentId;
	private Integer invoiceId;
	private Double paidAmount;
	private ApPayment apPayment;
	private APInvoice apInvoice;

	public enum FIELD {
		apPaymentId, invoiceId, paidAmount
	}

	public static final int OBJECT_TYPE = 142;

	public static ApPaymentInvoice getInstanceOf (int id, int apPaymentId, int invoiceId, Double paidAmount) {
		ApPaymentInvoice api = new ApPaymentInvoice();
		api.setId(id);
		api.apPaymentId = apPaymentId;
		api.invoiceId = invoiceId;
		api.paidAmount = paidAmount;
		return api;
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "AP_PAYMENT_INVOICE_ID", columnDefinition = "int(10)", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name = "AP_INVOICE_ID", columnDefinition = "int(10)")
	public Integer getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(Integer invoiceId) {
		this.invoiceId = invoiceId;
	}

	@Column(name = "PAID_AMOUNT", columnDefinition = "DOUBLE")
	public Double getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(Double paidAmount) {
		this.paidAmount = paidAmount;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "AP_PAYMENT_ID", insertable=false, updatable=false, nullable=true)
	public ApPayment getApPayment() {
		return apPayment;
	}

	public void setApPayment(ApPayment apPayment) {
		this.apPayment = apPayment;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "AP_INVOICE_ID", insertable=false, updatable=false, nullable=true)
	public APInvoice getApInvoice() {
		return apInvoice;
	}

	public void setApInvoice(APInvoice apInvoice) {
		this.apInvoice = apInvoice;
	}

	@Override
	public String toString() {
		return "ApPaymentInvoice [apPaymentId=" + apPaymentId + ", invoiceId="
				+ invoiceId + ", paidAmount=" + paidAmount + "]";
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE;
	}

	@Override
	@Transient
	public Integer getRefenceObjectId() {
		return null;
	}
}
