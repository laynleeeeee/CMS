package eulap.eb.domain.hibernate;

import java.util.Date;

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
 * Object representation of AR_LINE_SETUP table.

 *
 */
@Entity
@Table(name="AR_LINE_SETUP")
public class ArLineSetup extends BaseDomain{
	private String name;
	private Integer accountCombinationId;
	private Integer serviceLeaseKeyId;
	private boolean active;
	private Integer companyId;
	private Integer divisionId;
	private Integer accountId;
	private String arLineMessage;
	private String arLineSetupNumber;
	private AccountCombination accountCombination;
	private Integer discountACId;
	private Integer discAccId;
	private Double amount;
	private AccountCombination discAccountCombination;

	public enum FIELD {
		id, name, accountCombinationId, serviceLeaseKeyId, active, arLineSetupNumber,
		companyId, divisionId, accountId, discountACId, amount
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "AR_LINE_SETUP_ID", columnDefinition = "int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Override
	@Column(name = "CREATED_BY", columnDefinition = "int(10)")
	public int getCreatedBy() {
		return super.getCreatedBy();
	}

	@Override
	@Column(name = "CREATED_DATE", columnDefinition = "timestamp")
	public Date getCreatedDate() {
		return super.getCreatedDate();
	}

	@Override
	@Column(name = "UPDATED_BY", columnDefinition = "int(10)")
	public int getUpdatedBy() {
		return super.getUpdatedBy();
	}

	@Override
	@Column(name = "UPDATED_DATE", columnDefinition = "timestamp")
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
	}

	/**
	 * Get the name of the AR Line.
	 * @return The name of the AR Line.
	 */
	@Column(name="NAME", columnDefinition="varchar(100)")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the Id of the Account Combination of the AR Line.
	 * @return The Id of the Account Combination.
	 */
	@Column(name="ACCOUNT_COMBINATION_ID", columnDefinition="int(10)")
	public Integer getAccountCombinationId() {
		return accountCombinationId;
	}

	public void setAccountCombinationId(Integer accountCombinationId) {
		this.accountCombinationId = accountCombinationId;
	}

	/**
	 * Get the Id of the service lease key of the AR Line.
	 * @return The Id of the service lease key.
	 */
	@Column(name = "EB_SL_KEY_ID", columnDefinition = "int(10)")
	public Integer getServiceLeaseKeyId() {
		return serviceLeaseKeyId;
	}

	public void setServiceLeaseKeyId(Integer serviceLeaseKeyId) {
		this.serviceLeaseKeyId = serviceLeaseKeyId;
	}

	/**
	 * Validate if the AR Line Setup is active.
	 * @return True if active, false if inactive.
	 */
	@Column(name="ACTIVE", columnDefinition="tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Transient
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@Transient
	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@Transient
	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	@Transient
	public String getArLineMessage() {
		return arLineMessage;
	}

	public void setArLineMessage(String arLineMessage) {
		this.arLineMessage = arLineMessage;
	}

	@Transient
	public String getArLineSetupNumber() {
		return arLineSetupNumber;
	}

	public void setArLineSetupNumber(String arLineSetupNumber) {
		this.arLineSetupNumber = arLineSetupNumber;
	}

	@OneToOne
	@JoinColumn(name = "ACCOUNT_COMBINATION_ID", insertable = false, updatable = false)
	public AccountCombination getAccountCombination() {
		return accountCombination;
	}

	public void setAccountCombination(AccountCombination accountCombination) {
		this.accountCombination = accountCombination;
	}

	@Column(name="AMOUNT", columnDefinition="double")
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Column(name="DISCOUNT_AC_ID", columnDefinition="int(10)")
	public Integer getDiscountACId() {
		return discountACId;
	}

	public void setDiscountACId(Integer discountACId) {
		this.discountACId = discountACId;
	}

	@OneToOne
	@JoinColumn(name = "DISCOUNT_AC_ID", insertable = false, updatable = false)
	public AccountCombination getDiscAccountCombination() {
		return discAccountCombination;
	}

	public void setDiscAccountCombination(AccountCombination discAccountCombination) {
		this.discAccountCombination = discAccountCombination;
	}

	@Transient
	public Integer getDiscAccId() {
		return discAccId;
	}

	public void setDiscAccId(Integer discAccId) {
		this.discAccId = discAccId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ArLineSetup [name=").append(name).append(", accountCombinationId=").append(accountCombinationId)
				.append(", serviceLeaseKeyId=").append(serviceLeaseKeyId).append(", active=").append(active)
				.append(", companyId=").append(companyId).append(", divisionId=").append(divisionId)
				.append(", accountId=").append(accountId).append(", arLineMessage=").append(arLineMessage)
				.append(", arLineSetupNumber=").append(arLineSetupNumber).append(", discountACId=").append(discountACId)
				.append(", amount=").append(amount).append("]");
		return builder.toString();
	}
}
