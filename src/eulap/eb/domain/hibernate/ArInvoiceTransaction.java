package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * A class that represents the AR_INVOICE_TRANSACTION table in the CBS database.

 */

@Entity
@Table(name="AR_INVOICE_TRANSACTION")
public class ArInvoiceTransaction extends BaseDomain {
	private Integer arInvoiceId;
	private Integer arTransactionId;

	public enum FIELD {
		id, arInvoiceId, arTransactionId, amount
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "AR_INVOICE_TRANSACTION_ID", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name="AR_TRANSACTION_ID")
	public Integer getArTransactionId() {
		return arTransactionId;
	}

	public void setArTransactionId(Integer arTransactionId) {
		this.arTransactionId = arTransactionId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ArInvoiceTransaction [arInvoiceId=").append(arInvoiceId).append(", arTransactionId=")
				.append(arTransactionId).append("]");
		return builder.toString();
	}
}
