package eulap.eb.domain.hibernate;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * A class that represents the TRANSACTION_CLASSIFICATION table in the database.

 *
 */
@Entity
@Table (name="TRANSACTION_CLASSIFICATION")
public class TransactionClassification extends BaseDomain {
	private Integer serviceLeaseKeyId;
	private String name;
	private boolean active;

	public static final int REG_TRAN = 1;
	public static final int DEBIT_MEMO = 2;
	public static final int CREDIT_MEMO = 3;

	public enum FIELD {id, serviceLeaseKeyId, name, active}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "TRANSACTION_CLASSIFICATION_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "EB_SL_KEY_ID", columnDefinition = "INT(10)")
	public int getServiceLeaseKeyId() {
		return serviceLeaseKeyId;
	}

	public void setServiceLeaseKeyId(int serviceLeaseKey) {
		this.serviceLeaseKeyId = serviceLeaseKey;
	}

	@Column(name = "NAME", columnDefinition = "VARCHAR(50)")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "ACTIVE", columnDefinition = "TINYINT(20)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	@Column(name = "CREATED_BY", columnDefinition = "INT(10)")
	public int getCreatedBy() {
		return super.getCreatedBy();
	}

	@Override
	@Column(name = "CREATED_DATE", columnDefinition = "TIMESTAMP")
	public Date getCreatedDate() {
		return super.getCreatedDate();
	}

	@Override
	@Column(name = "UPDATED_BY", columnDefinition = "INT(10)")
	public int getUpdatedBy() {
		return super.getUpdatedBy();
	}

	@Override
	@Column(name = "UPDATED_DATE", columnDefinition = "TIMESTAMP")
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
	}

	/**
	 * Get the formatted number and name of the invoice classification
	 * @return
	 */
	@Transient
	public String getNumberAndName () {
		NumberFormat formatter = new DecimalFormat("####");
		formatter.setMinimumIntegerDigits(10);
		return formatter.format(getId()) + " - " + name;
	}

	@Override
	public String toString() {
		return "InvoiceType [id="+getId()+", serviceLeaseKeyId=" + serviceLeaseKeyId
				+ ", name=" + name + ", active=" + active + "]";
	}
}
