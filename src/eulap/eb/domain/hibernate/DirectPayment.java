package eulap.eb.domain.hibernate;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * A class that represents the DIRECT_PAYMENT table in the database.

 *
 */
@Entity
@Table(name="DIRECT_PAYMENT")
public class DirectPayment extends BaseDomain {
	private Integer termId;
	private Integer ebObjectId;
	private Integer directPaymentTypeId;
	private Integer apPaymentId;
	private String description;
	private String invoiceNo;
	private ApPayment apPayment;
	private List<DirectPaymentLine> paymentLines;

	public static final int OBJECT_TYPE_ID = 138;
	public static final int CASH_DP_TYPE_ID = 1;
	public static final int CHECK_DP_TYPE_ID = 2;
	public static final int INVOICE_NO_MAX_CHAR = 100;

	public enum FIELD {
		id, termId, ebObjectId, directPaymentTypeId, apPaymentId, invoiceNo
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "DIRECT_PAYMENT_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "TERM_ID", columnDefinition="int(10)")
	public Integer getTermId() {
		return termId;
	}

	public void setTermId(Integer termId) {
		this.termId = termId;
	}

	@Column(name = "EB_OBJECT_ID", columnDefinition="int(10)")
	public Integer getEbObjectId() {
		return ebObjectId;
	}

	public void setEbObjectId(Integer ebObjectId) {
		this.ebObjectId = ebObjectId;
	}

	@Column(name = "DIRECT_PAYMENT_TYPE_ID", columnDefinition="int(10)")
	public Integer getDirectPaymentTypeId() {
		return directPaymentTypeId;
	}

	public void setDirectPaymentTypeId(Integer paymentTypeId) {
		this.directPaymentTypeId = paymentTypeId;
	}

	@Column(name = "DESCRIPTION", columnDefinition="text")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "INVOICE_NUMBER", columnDefinition="varchar(100)")
	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}


	@Transient
	public List<DirectPaymentLine> getPaymentLines() {
		return paymentLines;
	}

	public void setPaymentLines(List<DirectPaymentLine> paymentLines) {
		this.paymentLines = paymentLines;
	}

	@Column(name = "AP_PAYMENT_ID", columnDefinition="int(10)")
	public Integer getApPaymentId() {
		return apPaymentId;
	}

	public void setApPaymentId(Integer apPaymentId) {
		this.apPaymentId = apPaymentId;
	}

	@OneToOne
	@JoinColumn(name="AP_PAYMENT_ID", insertable = false, updatable = false)
	public ApPayment getApPayment() {
		return apPayment;
	}

	public void setApPayment(ApPayment apPayment) {
		this.apPayment = apPayment;
	}

	@Override
	public String toString() {
		return "DirectPayment [termId=" + termId + ", ebObjectId=" + ebObjectId + ", directPaymentTypeId="
				+ directPaymentTypeId + ", apPaymentId=" + apPaymentId + ", description=" + description + ", invoiceNo="
				+ invoiceNo + "]";
	}
}
