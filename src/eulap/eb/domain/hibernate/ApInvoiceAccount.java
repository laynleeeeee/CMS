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
 * Object representation class for AP_INVOICE_ACCOUNT
 * If AP invoice item will be moved to CMS, this will be merged to inventory account

 */

@Entity
@Table(name="AP_INVOICE_ACCOUNT")
public class ApInvoiceAccount extends BaseDomain {
	private Integer companyId;
	private Integer divisionId;
	private Integer accountCombinationId;
	private Integer discountAcId;
	private boolean active;

	public enum FIELD {
		id, companyId, divisionId, accountCombinationId, active
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "AP_INVOICE_ACCOUNT_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Override
	@Column(name = "CREATED_BY", columnDefinition="INT(10)")
	public int getCreatedBy() {
		return super.getCreatedBy();
	}

	@Override
	@Column(name = "CREATED_DATE", columnDefinition="TIMESTAMP")
	public Date getCreatedDate() {
		return super.getCreatedDate();
	}

	@Override
	@Column(name = "UPDATED_BY", columnDefinition="INT(10)")
	public int getUpdatedBy() {
		return super.getUpdatedBy();
	}

	@Override
	@Column(name = "UPDATED_DATE", columnDefinition="TIMESTAMP")
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
	}

	@Column(name="COMPANY_ID", columnDefinition="int(10)")
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@Column(name="ACCOUNT_COMBINATION_ID", columnDefinition="int(10)")
	public Integer getAccountCombinationId() {
		return accountCombinationId;
	}

	public void setAccountCombinationId(Integer accountCombinationId) {
		this.accountCombinationId = accountCombinationId;
	}

	@Column(name="ACTIVE", columnDefinition="tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Column(name="DIVISION_ID")
	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@Column(name="DISCOUNT_AC_ID")
	public Integer getDiscountAcId() {
		return discountAcId;
	}

	public void setDiscountAcId(Integer discountAcId) {
		this.discountAcId = discountAcId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ApInvoiceAccount [companyId=").append(companyId).append(", divisionId=").append(divisionId)
				.append(", accountCombinationId=").append(accountCombinationId).append(", discountAcId=")
				.append(discountAcId).append(", active=").append(active).append("]");
		return builder.toString();
	}
}
