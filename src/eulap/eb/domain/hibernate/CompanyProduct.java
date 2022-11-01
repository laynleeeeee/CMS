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
 * Domain class for COMPANY_PRODUCT table


 */
@Entity
@Table(name = "COMPANY_PRODUCT")
public class CompanyProduct extends BaseDomain{
	private int companyId;
	private int code;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "COMPANY_PRODUCT_ID", unique = true, nullable = false, insertable = false, updatable = false)
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
	 * Get the company Id of the product.
	 * @return The company Id.
	 */
	@Column(name = "COMPANY_ID")
	public int getCompanyId() {
		return companyId;
	}

	/**
	 * Set the company Id of the product.
	 * @param companyId The company Id.
	 */
	public void setCompanyId(int companyId) {
		this.companyId = companyId;
	}

	/**
	 * Get the code of the product.
	 * @return The code.
	 */
	@Column(name = "CODE")
	public int getCode() {
		return code;
	}

	/**
	 * Set the code of the product.
	 * @param code The code.
	 */
	public void setCode(int code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "CompanyProduct [companyId=" + companyId + ", code=" + code
				+ ", getId()=" + getId() + ", getCreatedBy()=" + getCreatedBy()
				+ ", getCreatedDate()=" + getCreatedDate()
				+ ", getUpdatedBy()=" + getUpdatedBy() + ", getUpdatedDate()="
				+ getUpdatedDate() + "]";
	}
}