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
 * A class that represent VAT_ACCOUNT_SETUP table.

 */

@Entity
@Table (name="VAT_ACCOUNT_SETUP")
public class VatAcctSetup extends BaseDomain {
	private Integer companyId;
	private Integer divisionId;
	private Integer taxTypeId;
	private Integer inputVatAcId;
	private Integer outputVatAcId;
	private boolean active;

	public enum FIELD {
		id, companyId, divisionId, taxTypeId, inputVatAcId, outputVatAcId, active
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "VAT_ACCOUNT_SETUP_ID", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name = "COMPANY_ID")
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@Column(name = "DIVISION_ID")
	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@Column(name = "TAX_TYPE_ID")
	public Integer getTaxTypeId() {
		return taxTypeId;
	}

	public void setTaxTypeId(Integer taxTypeId) {
		this.taxTypeId = taxTypeId;
	}

	@Column(name = "INPUT_VAT_AC_ID")
	public Integer getInputVatAcId() {
		return inputVatAcId;
	}

	public void setInputVatAcId(Integer inputVatAcId) {
		this.inputVatAcId = inputVatAcId;
	}

	@Column(name = "OUTPUT_VAT_AC_ID")
	public Integer getOutputVatAcId() {
		return outputVatAcId;
	}

	public void setOutputVatAcId(Integer outputVatAcId) {
		this.outputVatAcId = outputVatAcId;
	}

	@Column(name = "ACTIVE")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VatAcctSetup [companyId=").append(companyId).append(", inputVatAcId=").append(inputVatAcId)
				.append(", outputVatAcId=").append(outputVatAcId).append(", active=").append(active).append("]");
		return builder.toString();
	}
}
