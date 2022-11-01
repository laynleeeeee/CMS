package eulap.eb.domain.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * A class that represents the ACCOUNT_CLASS table.

 *
 */
@Entity
@Table(name = "ACCOUNT_CLASS")
public class AccountClass extends BaseDomain{
	/**
	 * Prefixes:
	 * N = NOMINAL
	 * R = REAL
	 * SC = SPECIAL CLASS
	 */
	public final static int N_MAIN_REVENUE = 1;
	public final static int N_COS = 2;
	public final static int N_EXPENSES = 3;
	public final static int N_OTHERS = 4;
	public final static int R_CURRENT_ASSETS = 5;
	public final static int R_NON_CURRENT_ASSETS = 6;
	public final static int R_CURRENT_LIABILITIES = 7;
	public final static int R_NON_CURRENT_LIABILITIES = 8;
	public final static int R_EQUITY = 9;
	public final static int SC_BEGINNING_INVENTORY = 10;
	public final static int SC_ENDING_INVENTORY = 11;
	public final static int TEMPORARY_ACCOUNT = 12;

	private String name;
	private int serviceLeaseKeyId;
	private boolean active;

	public enum FIELD {id, name, serviceLeaseKeyId, active};

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "ACCOUNT_CLASS_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Override
	@Column(name = "CREATED_BY")
	public int getCreatedBy() {
		return super.getCreatedBy();
	}

	@Override
	@Column(name = "CREATED_DATE")
	public Date getCreatedDate() {
		return super.getCreatedDate();
	}

	@Override
	@Column(name = "UPDATED_BY")
	public int getUpdatedBy() {
		return super.getUpdatedBy();
	}

	@Override
	@Column(name = "UPDATED_DATE")
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
	}

	/**
	 * Get the account class name.
	 * @return The name.
	 */
	@Column(name = "NAME")
	public String getName() {
		return name;
	}

	/**
	 * Set the account class name.
	 * @param name The name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the service lease key id.
	 * @return The service lease key id.
	 */
	@Column(name = "EB_SL_KEY_ID")
	public int getServiceLeaseKeyId() {
		return serviceLeaseKeyId;
	}

	/**
	 * Set the service lease key id.
	 * @param serviceLeaseKeyId The service lease key id.
	 */
	public void setServiceLeaseKeyId(int serviceLeaseKeyId) {
		this.serviceLeaseKeyId = serviceLeaseKeyId;
	}

	/**
	 * Check if the account class is active.
	 * @return True, otherwise false.
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Set the status of account class.
	 * @param active True, otherwise false.
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return "AccountClass [name=" + name + ", serviceLeaseKeyId="
				+ serviceLeaseKeyId + ", active=" + active + ", getId()="
				+ getId() + ", getCreatedBy()=" + getCreatedBy()
				+ ", getCreatedDate()=" + getCreatedDate()
				+ ", getUpdatedBy()=" + getUpdatedBy() + ", getUpdatedDate()="
				+ getUpdatedDate() + "]";
	}
}
