package eulap.eb.domain.hibernate;

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

import com.google.gson.annotations.Expose;

import eulap.common.domain.BaseDomain;

/**
 * A class that represents the AR_RECEIPT_TRANSACTION table in the CBS database.

 *
 */
@Entity
@Table(name="AR_RECEIPT_TRANSACTION")
public class ArReceiptTransaction extends BaseDomain{
	@Expose
	private Integer arReceiptId;
	@Expose
	private Integer arTransactionId;
	@Expose
	private String transactionNumber;
	@Expose
	private Double amount;
	private ArTransaction arTransaction;

	public enum FIELD {
		id, arReceiptId, arTransactionId, amount
	}

	public static ArReceiptTransaction getInstanceOf (Integer id, Integer arReceiptId, Integer arTransactionId, Double amount) {
		ArReceiptTransaction art = new ArReceiptTransaction();
		art.setId(id);
		art.arReceiptId = arReceiptId;
		art.arTransactionId = arTransactionId;
		art.amount = amount;
		return art;
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "AR_RECEIPT_TRANSACTION_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	/**
	 * Get the unique id of AR receipt.
	 * @return The unique id of AR receipt.
	 */
	@Column (name = "AR_RECEIPT_ID")
	public Integer getArReceiptId() {
		return arReceiptId;
	}

	public void setArReceiptId(Integer arReceiptId) {
		this.arReceiptId = arReceiptId;
	}

	/**
	 * Get the unique id of AR transaction.
	 * @return  The unique id of AR transaction.
	 */
	@Column (name = "AR_TRANSACTION_ID")
	public Integer getArTransactionId() {
		return arTransactionId;
	}

	public void setArTransactionId(Integer arTransactionId) {
		this.arTransactionId = arTransactionId;
	}

	@Transient
	public String getTransactionNumber() {
		return transactionNumber;
	}

	public void setTransactionNumber(String transactionNumber) {
		this.transactionNumber = transactionNumber;
	}

	/**
	 * Get the amount.
	 * @return The amount.
	 */
	@Column (name = "AMOUNT")
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	/**
	 * Get the associated ar transaction object.
	 * @return
	 */
	@ManyToOne (fetch = FetchType.EAGER)
	@JoinColumn (name = "AR_TRANSACTION_ID", insertable=false, updatable=false, nullable=true)
	public ArTransaction getArTransaction() {
		return arTransaction;
	}

	public void setArTransaction(ArTransaction arTransaction) {
		this.arTransaction = arTransaction;
	}

	/**
	 * Get the AR transaction number.
	 * @return The AR transaction number.
	 */
	@Transient
	public String getArTransactionNo () {
		if (arTransaction != null) {
			return arTransaction.getTransactionNumber();
		}
		return null;
	}

	@Override
	public String toString() {
		return "ArReceiptTransaction [arReceiptId=" + arReceiptId + ", arTransactionId=" + arTransactionId
				+ ", transactionNumber=" + transactionNumber + ", amount=" + amount + ", arTransaction=" + arTransaction + "]";
	}
}
