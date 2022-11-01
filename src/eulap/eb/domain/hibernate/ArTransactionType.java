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
 * Class that represents the TRANSACTION_TYPE table in database

 */
@Entity
@Table(name = "AR_TRANSACTION_TYPE")
public class ArTransactionType extends BaseDomain{
	private Integer serviceLeaseKeyId;
	private String name;
	private boolean active;

	public static final int TYPE_REGULAR_TRANSACTION = 1;
	public static final int TYPE_DEBIT_MEMO = 2;
	public static final int TYPE_CREDIT_MEMO = 3;
	public static final int TYPE_ACCOUNT_SALE = 4;
	public static final int TYPE_SALE_RETURN = 5;
	public static final int TYPE_SALE_RETURN_EMPTY_BOTTLE = 7;
	public static final int TYPE_ACCOUNT_SALES_IS = 10;
	public static final int TYPE_ACCOUNT_SALES_RETURN_IS = 11;
	public static final int TYPE_SALES_ORDER = 15;
	public static final int TYPE_AR_INVOICE = 16;
	public static final int TYPE_AR_TRANSACTION_CENTRAL = 17;
	public static final int TYPE_AR_TRANSACTION_NSB3 = 18;
	public static final int TYPE_AR_TRANSACTION_NSB4 = 19;
	public static final int TYPE_AR_TRANSACTION_NSB5 = 20;
	public static final int TYPE_AR_TRANSACTION_NSB8 = 21;
	public static final int TYPE_AR_TRANSACTION_NSB8A = 22;

	public enum FIELD {id, serviceLeaseKeyId, name, active}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "AR_TRANSACTION_TYPE_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "EB_SL_KEY_ID", columnDefinition = "INT(10)")
	public int getServiceLeaseKeyId() {
		return serviceLeaseKeyId;
	}

	public void setServiceLeaseKeyId(Integer serviceLeaseKeyId) {
		this.serviceLeaseKeyId = serviceLeaseKeyId;
	}

	@Column(name = "NAME", columnDefinition = "VARCHAR(50)")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name ="ACTIVE", columnDefinition = "TINYINT(1)")
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
	 * Get the formatted number and name of the customer
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
		return "ArTransactionType [id="+getId()+", serviceLeaseKeyId=" + serviceLeaseKeyId
				+ ", name=" + name + ", active=" + active + "]";
	}
}
