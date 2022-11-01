package eulap.eb.domain.hibernate;

import java.math.BigDecimal;
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
 * The object that represents the CHECKBOOK table.

 */

@Entity
@Table(name = "CHECKBOOK")
public class Checkbook  extends BaseDomain{
	private int bankAccountId;
	private String name;
	private BigDecimal checkbookNoFrom;
	private BigDecimal checkbookNoTo;
	private Integer checkbookTemplateId;
	private boolean active;
	private BankAccount bankAccount;

	public enum FIELD {id, bankAccountId, name, checkbookNoFrom, checkbookNoTo, active}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "CHECKBOOK_ID", unique = true, nullable = false, insertable = false, updatable = false)
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
	 * Get the id of bank account.
	 * @return The bank account id.
	 */
	@Column(name = "BANK_ACCOUNT_ID")
	public int getBankAccountId() {
		return bankAccountId;
	}

	/**
	 * Set the bank account id.
	 * @param bankAccountId The bank account id.
	 */
	public void setBankAccountId(int bankAccountId) {
		this.bankAccountId = bankAccountId;
	}

	/**
	 * Get the name of the checkbook
	 * @return The name.
	 */
	@Column(name = "NAME", columnDefinition="varchar(100)")
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the checkbook.
	 * @param name The name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the number from of the checkbook.
	 * @return The number from.
	 */
	@Column(name = "CHECKBOOK_NO_FROM", columnDefinition="decimal(20,0)")
	public BigDecimal getCheckbookNoFrom() {
		return checkbookNoFrom;
	}

	/**
	 * Set the number from of the checkbook.
	 * @param checkbookNoFrom The number from.
	 */
	public void setCheckbookNoFrom(BigDecimal checkbookNoFrom) {
		this.checkbookNoFrom = checkbookNoFrom;
	}

	/**
	 * Get the number to of the checkbook.
	 * @return The number to.
	 */
	@Column(name = "CHECKBOOK_NO_TO", columnDefinition="decimal(20,0)")
	public BigDecimal getCheckbookNoTo() {
		return checkbookNoTo;
	}

	/**
	 * Set the number to of the checkbook.
	 * @param checkbookNoTo The number to.
	 */
	public void setCheckbookNoTo(BigDecimal checkbookNoTo) {
		this.checkbookNoTo = checkbookNoTo;
	}

	/**
	 * Check if checkbook is active or inactive.
	 * @return Return True, otherwise false.
	 */
	@Column(name="ACTIVE", columnDefinition="tinyint(1)")
	public boolean isActive() {
		return active;
	}

	/**
	 * Set the checkbook status to active or inactive.
	 * @param active True or false.
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	@ManyToOne
	@JoinColumn(name = "BANK_ACCOUNT_ID", insertable=false, updatable=false, nullable=true)
	public BankAccount getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(BankAccount bankAccount) {
		this.bankAccount = bankAccount;
	}

	@Column(name="CHECKBOOK_TEMPLATE_ID", columnDefinition="int(10)")
	public Integer getCheckbookTemplateId() {
		return checkbookTemplateId;
	}

	public void setCheckbookTemplateId(Integer checkbookTemplateId) {
		this.checkbookTemplateId = checkbookTemplateId;
	}

	@Override
	public String toString() {
		return "Checkbook [bankAccountId=" + bankAccountId + ", name=" + name
				+ ", checkbookNoFrom=" + checkbookNoFrom + ", checkbookNoTo="
				+ checkbookNoTo + ", active=" + active + ", getId()=" + getId()
				+ ", getCreatedBy()=" + getCreatedBy() + ", getCreatedDate()="
				+ getCreatedDate() + ", getUpdatedBy()=" + getUpdatedBy()
				+ ", getUpdatedDate()=" + getUpdatedDate() + "]";
	}
}
