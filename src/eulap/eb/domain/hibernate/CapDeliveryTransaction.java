package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * A class that represents CAP_DELIVERY_TRANSACTION table in database.

 *
 */
@Entity
@Table(name = "CAP_DELIVERY_TRANSACTION")
public class CapDeliveryTransaction extends BaseDomain{
	private Integer capDeliveryId;
	private Integer arTransactionId;
	private ArTransaction arTransaction;

	public enum FIELD {
		id, capDeliveryId, arTransactionId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "CAP_DELIVERY_TRANSACTION_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "CAP_DELIVERY_ID")
	public Integer getCapDeliveryId() {
		return capDeliveryId;
	}

	public void setCapDeliveryId(Integer capDeliveryId) {
		this.capDeliveryId = capDeliveryId;
	}

	@Column(name = "AR_TRANSACTION_ID")
	public Integer getArTransactionId() {
		return arTransactionId;
	}

	public void setArTransactionId(Integer arTransactionId) {
		this.arTransactionId = arTransactionId;
	}

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "AR_TRANSACTION_ID", insertable = false, updatable = false)
	public ArTransaction getArTransaction() {
		return arTransaction;
	}

	public void setArTransaction(ArTransaction arTransaction) {
		this.arTransaction = arTransaction;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CapDeliveryTransaction [capDeliveryId=")
				.append(capDeliveryId).append(", arTransactionId=")
				.append(arTransactionId).append("arTransaction")
				.append("]");
		return builder.toString();
	}
}
