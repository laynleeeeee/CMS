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
 * Object representation for AP_INVOICE_ITEM table

 */

@Entity
@Table(name="AP_INVOICE_ITEM")
public class ApInvoiceItem extends BaseFormLine {
	@Expose
	private Integer apInvoiceId;
	@Expose
	private Double amount;
	@Expose
	private String invoiceNumber;
	private boolean active;

	/**
	 * AP_INVOICE_ITEM_LINE_OBJECT_TYPE_ID object type id
	 */
	public static final int OBJECT_TYPE_ID = 12027;

	public enum FIELD {
		id, apInvoiceId, ebObjectId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "AP_INVOICE_ITEM_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name="AP_INVOICE_ID", columnDefinition="int(10)")
	public Integer getApInvoiceId() {
		return apInvoiceId;
	}

	public void setApInvoiceId(Integer apInvoiceId) {
		this.apInvoiceId = apInvoiceId;
	}

	@Column(name="AMOUNT", columnDefinition="double")
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Column(name="ACTIVE", columnDefinition="tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Transient
	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	@Transient
	@Override
	public Integer getObjectTypeId() {
		return OBJECT_TYPE_ID;
	}

	@Transient
	@Override
	public Integer getRefenceObjectId() {
		return null;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ApInvoiceItem [apInvoiceId=").append(apInvoiceId)
		.append(", amount=").append(amount).append(", ebObjectId=")
		.append(getEbObjectId()).append("]");
		return builder.toString();
	}
}
