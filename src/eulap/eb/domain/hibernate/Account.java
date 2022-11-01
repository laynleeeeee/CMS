package eulap.eb.domain.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * A class that represents the ACCOUNT table.

 *
 */
@Entity
@Table(name = "ACCOUNT")
public class Account extends BaseDomain{
	private Integer accountTypeId;
	private String number;
	private String accountName;
	private String description;
	private Integer relatedAccountId;
	private Integer parentAccountId;
	private boolean active;
	private AccountType accountType;
	private Account relatedAccount;
	private Account parentAccount;
	private int serviceLeaseKeyId;
	private String accountTypeName;
	private Integer ebObjectId;
	private EBObject ebObject;
	private String parentAccountName;

	/**
	 * Create an instance of account.
	 */
	public static Account getInstanceOf(Integer id, String number, String accountName, String description, Integer accountTypeId,
			Integer relatedAccountId, Integer parentAccountId, Integer ebObjectId, boolean active, Integer createdBy, Date createdDate, 
			Integer updatedBy, Date updatedDate, Integer serviceLeaseKeyId) {
		Account account = new Account();
		account.setId(id);
		account.setNumber(number);
		account.setAccountName(accountName);
		account.setDescription(description);
		account.setAccountTypeId(accountTypeId);
		account.setRelatedAccountId(relatedAccountId);
		account.setParentAccountId(parentAccountId);
		account.setEbObjectId(ebObjectId);
		account.setActive(active);
		account.setCreatedBy(createdBy);
		account.setCreatedDate(createdDate);
		account.setUpdatedBy(updatedBy);
		account.setUpdatedDate(updatedDate);
		account.setServiceLeaseKeyId(serviceLeaseKeyId);
		return account;
	}

	public static final int OBJECT_TYPE_ID = 100;
	public static final int MAX_LEVEL = 4;

	public enum FIELD {id, accountTypeId, number, accountName, description, relatedAccountId, parentAccountId, active,
		accountType, serviceLeaseKeyId}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "ACCOUNT_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
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
	 * Get the account type id of the account.
	 * @return accountTypeId The account type id of the account.
	 */
	@Column (name = "ACCOUNT_TYPE_ID")
	public Integer getAccountTypeId() {
		return accountTypeId;
	}

	/**
	 * Set the account type id of the account.
	 * @param accountTypeId The account type id of the account.
	 */
	public void setAccountTypeId(Integer accountTypeId) {
		this.accountTypeId = accountTypeId;
	}

	/**
	 * Get the number of the account.
	 * @return number The account number.
	 */
	@Column (name = "NUMBER")
	public String getNumber() {
		return number;
	}

	/**
	 * Set the number of the account.
	 * @param number The account number.
	 */
	public void setNumber(String number) {
		this.number = number;
	}

	/**
	 * Get the name of the account.
	 * @return accountName The account name.
	 */
	@Column (name = "ACCOUNT_NAME")
	public String getAccountName() {
		return accountName;
	}

	/**
	 * Set the name of the account.
	 * @param accountName The account name.
	 */
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	/**
	 * Get the description of the account.
	 * @return description The description.
	 */
	@Column (name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}

	/**
	 * Set the description of the account.
	 * @param description The description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Get the id of the related account.
	 * @return relatedAccountId The related account id.
	 */
	@Column (name = "RELATED_ACCOUNT_ID")
	public Integer getRelatedAccountId() {
		return relatedAccountId;
	}

	/**
	 * Set the id of the related account.
	 * @param relatedAccountId The related account id.
	 */
	public void setRelatedAccountId(Integer relatedAccountId) {
		this.relatedAccountId = relatedAccountId;
	}

	/**
	 * Get the id of the parent account.
	 * @return parentAccountId The parent account id.
	 */
	@Column (name = "PARENT_ACCOUNT_ID")
	public Integer getParentAccountId() {
		return parentAccountId;
	}

	/**
	 * Set the id of the parent account.
	 * @param parentAccountId The parent account id.
	 */
	public void setParentAccountId(Integer parentAccountId) {
		this.parentAccountId = parentAccountId;
	}

	/**
	 * Get the related account object.
	 * @return The related account object.
	 */
	@ManyToOne (fetch = FetchType.EAGER)
	@JoinColumn (name = "RELATED_ACCOUNT_ID", insertable=false, updatable=false, nullable=true)
	public Account getRelatedAccount() {
		return relatedAccount;
	}

	/**
	 * Set the related account object.
	 * @param relatedAccount The related account object.
	 */
	public void setRelatedAccount(Account relatedAccount) {
		this.relatedAccount = relatedAccount;
	}

	/**
	 * Get the parent account object.
	 * @return The parent account object.
	 */
	@ManyToOne (fetch = FetchType.EAGER)
	@JoinColumn (name = "PARENT_ACCOUNT_ID", insertable=false, updatable=false, nullable=true)
	public Account getParentAccount() {
		return parentAccount;
	}

	/**
	 * Set the parent account object.
	 * @param parentAccount The parent account object.
	 */
	public void setParentAccount(Account parentAccount) {
		this.parentAccount = parentAccount;
	}

	/**
	 * Check if the account is active.
	 * @return True if the account is active, otherwise false.
	 */
	@Column (name = "ACTIVE")
	public boolean isActive() {
		return active;
	}

	/**
	 * Set the status of the account.
	 * @param active True or false.
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Get the account type object associated with the account.
	 * @return accountType The account type object.
	 */
	@ManyToOne
	@JoinColumn (name = "ACCOUNT_TYPE_ID", insertable=false, updatable=false)
	public AccountType getAccountType() {
		return accountType;
	}

	/**
	 * Set the account type object.
	 * @param accountType he account type object.
	 */
	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	/**
	 * Get the service lease key id.
	 * @return The service lease key id.
	 */
	@Column(name = "EB_SL_KEY_ID", columnDefinition="INT(10)")
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

	@Transient
	public String getAccountTypeName() {
		return accountTypeName;
	}

	public void setAccountTypeName(String accountTypeName) {
		this.accountTypeName = accountTypeName;
	}

	@Transient
	public String getNumberAndName () {
		if (number.isEmpty()) {
			return accountName;
		}
		return number + " - " + accountName;
	}

	@Override
	public String toString() {
		return "Account [accountTypeId=" + accountTypeId + ", number=" + number
				+ ", accountName=" + accountName + ", description="
				+ description + ", relatedAccountId=" + relatedAccountId
				+ ", active=" + active + ", accountType=" + accountType
				+ ", relatedAccount=" + relatedAccount + ", serviceLeaseKeyId="
				+ serviceLeaseKeyId + ", getId()=" + getId()
				+ ", accountTypeName= " + accountTypeName
				+ ", getCreatedBy()=" + getCreatedBy() + ", getCreatedDate()="
				+ getCreatedDate() + ", getUpdatedBy()=" + getUpdatedBy()
				+ ", getUpdatedDate()=" + getUpdatedDate() + "]";
	}

	@OneToOne
	@JoinColumn (name="EB_OBJECT_ID", nullable=true, insertable=false, updatable=false)
	public EBObject getEbObject() {
		return ebObject;
	}

	public void setEbObject(EBObject ebObject) {
		this.ebObject = ebObject;
	}

	@Column(name="EB_OBJECT_ID", columnDefinition="int(10)")
	public Integer getEbObjectId() {
		return ebObjectId;
	}

	public void setEbObjectId(Integer eBObjecctId) {
		this.ebObjectId = eBObjecctId;
	}

	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE_ID;
	}

	@Transient
	public String getParentAccountName() {
		return parentAccountName;
	}

	public void setParentAccountName(String parentAccountName) {
		this.parentAccountName = parentAccountName;
	}
}