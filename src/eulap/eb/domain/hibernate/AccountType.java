package eulap.eb.domain.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * A class that represents the ACCOUNT_TYPE table.

 *
 */
@Entity
@Table(name = "ACCOUNT_TYPE")
public class AccountType extends BaseDomain{
	private int normalBalanceId;
	private String name;
	private boolean contraAccount;
	private boolean active;
	private NormalBalance normalBalance;
	private int serviceLeaseKeyId;

	public enum FIELD {id, name, normalBalanceId, contraAccount, active, normalBalance, accountClass, serviceLeaseKeyId}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "ACCOUNT_TYPE_ID", unique = true, nullable = false, insertable = false, updatable = false)
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
	 * Get the normal balance id of the account type.
	 * @return normalBalanceId The normal balance id of the account type.
	 */
	@Column (name = "NORMAL_BALANCE_ID")
	public int getNormalBalanceId() {
		return normalBalanceId;
	}

	/**
	 * Set the normal balance id of the account type.
	 * @param normalBalanceId The normal balance id of the account type.
	 */
	public void setNormalBalanceId(int normalBalanceId) {
		this.normalBalanceId = normalBalanceId;
	}

	/**
	 * Get the name of the account type.
	 * @return name The name of the account type.
	 */
	@Column (name = "NAME")
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the account type.
	 * @param name The name of the account type.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Check if the account type is a contra account.
	 * @return contraAccount True or false.
	 */
	@Column (name = "CONTRA_ACCOUNT")
	public boolean isContraAccount() {
		return contraAccount;
	}

	/**
	 * Set the contra account to either true or false.
	 * @param contraAccount True or false.
	 */
	public void setContraAccount(boolean contraAccount) {
		this.contraAccount = contraAccount;
	}

	/**
	 * Check if the account type is active.
	 * @return active True or false.
	 */
	@Column (name = "ACTIVE")
	public boolean isActive() {
		return active;
	}

	/**
	 * Set the status to either true or false.
	 * @param active True or false.
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Get the normal balance object associated with the account type.
	 * @return normalBalance The normal balance associated with the account type.
	 */
	@ManyToOne
	@JoinColumn (name = "NORMAL_BALANCE_ID", insertable=false, updatable=false)
	public NormalBalance getNormalBalance() {
		return normalBalance;
	}

	/**
	 * Set the normal balance object associated with the account type.
	 * @param normalBalance The normal balance associated with the account type.
	 */
	public void setNormalBalance(NormalBalance normalBalance) {
		this.normalBalance = normalBalance;
	}

	/**
	 * Get the service lease key id
	 * @return The service lease key id.
	 */
	@Column (name = "EB_SL_KEY_ID")
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

	@Override
	public String toString() {
		return "AccountType [normalBalanceId=" + normalBalanceId + ", name="
				+ name + ", contraAccount=" + contraAccount + ", active="
				+ active + ", normalBalance=" + normalBalance
				+ ", serviceLeaseKeyId=" + serviceLeaseKeyId
				+ ", getId()=" + getId() + ", getCreatedBy()=" + getCreatedBy()
				+ ", getCreatedDate()=" + getCreatedDate()
				+ ", getUpdatedBy()=" + getUpdatedBy() + ", getUpdatedDate()="
				+ getUpdatedDate() + "]";
	}
}
