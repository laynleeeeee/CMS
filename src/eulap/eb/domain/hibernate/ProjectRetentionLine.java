package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

/**
 * Domain object representation class for PROJECT_RETENTION_LINE

 */

@Entity
@Table(name="PROJECT_RETENTION_LINE")
public class ProjectRetentionLine extends BaseFormLine {
	private Integer projectRetentionId;
	@Expose
	private Integer refenceObjectId;
	@Expose
	private String referenceNo;
	@Expose
	private Double amount;
	@Expose
	private Double upAmount;
	@Expose
	private Double vatAmount;
	@Expose
	private Integer taxTypeId;
	@Expose
	private Double currencyRateValue;
	private TaxType taxType;

	public static final int OBJECT_TYPE_ID = 24010;
	public static final int OR_TYPE_ID = 24007;

	public enum FIELD {
		id, projectRetentionId, ebObjectId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "PROJECT_RETENTION_LINE_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "PROJECT_RETENTION_ID")
	public Integer getProjectRetentionId() {
		return projectRetentionId;
	}

	public void setProjectRetentionId(Integer projectRetentionId) {
		this.projectRetentionId = projectRetentionId;
	}

	@Column(name = "AMOUNT", columnDefinition="double")
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Column(name = "UP_AMOUNT", columnDefinition="double")
	public Double getUpAmount() {
		return upAmount;
	}

	public void setUpAmount(Double upAmount) {
		this.upAmount = upAmount;
	}

	@Column(name = "TAX_TYPE_ID")
	public Integer getTaxTypeId() {
		return taxTypeId;
	}

	public void setTaxTypeId(Integer taxTypeId) {
		this.taxTypeId = taxTypeId;
	}

	@Column(name = "VAT_AMOUNT")
	public Double getVatAmount() {
		return vatAmount;
	}

	public void setVatAmount(Double vatAmount) {
		this.vatAmount = vatAmount;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="TAX_TYPE_ID", insertable=false, updatable=false)
	public TaxType getTaxType() {
		return taxType;
	}

	public void setTaxType(TaxType taxType) {
		this.taxType = taxType;
	}

	@Transient
	public String getReferenceNo() {
		return referenceNo;
	}

	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	@Column(name = "CURRENCY_RATE_VALUE")
	public Double getCurrencyRateValue() {
		return currencyRateValue;
	}

	public void setCurrencyRateValue(Double currencyRateValue) {
		this.currencyRateValue = currencyRateValue;
	}

	@Override
	@Transient
	public Integer getRefenceObjectId() {
		return refenceObjectId;
	}

	public void setReferenceObjectId(Integer refenceObjectId) {
		this.refenceObjectId = refenceObjectId;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE_ID;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProjectRetentionLine [projectRetentionId=").append(projectRetentionId)
				.append(", refenceObjectId=").append(refenceObjectId).append(", referenceNo=").append(referenceNo)
				.append(", amount=").append(amount).append(", upAmount=").append(upAmount).append(", vatAmount=")
				.append(vatAmount).append(", taxTypeId=").append(taxTypeId).append("]");
		return builder.toString();
	}
}
