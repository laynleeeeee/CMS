package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.google.gson.annotations.Expose;

/**
 * Base class for Other Charges of Sales form.

 *
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class OtherCharge extends BaseFormLine {
	private UnitMeasurement unitMeasurement;
	@Expose
	private Integer unitOfMeasurementId;
	@Expose
	private Double amount;
	@Expose
	private Double quantity;
	@Expose
	private Double upAmount;
	@Expose
	private String unitMeasurementName;
	@Expose
	private Double vatAmount;
	@Expose
	private Integer taxTypeId;
	private TaxType taxType;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "UNITOFMEASUREMENT_ID", insertable=false, updatable=false)
	public UnitMeasurement getUnitMeasurement() {
		return unitMeasurement;
	}

	public void setUnitMeasurement(UnitMeasurement unitMeasurement) {
		this.unitMeasurement = unitMeasurement;
	}

	@Column(name = "UNITOFMEASUREMENT_ID", columnDefinition="int(10)")
	public Integer getUnitOfMeasurementId() {
		return unitOfMeasurementId;
	}

	public void setUnitOfMeasurementId(Integer unitOfMeasurementId) {
		this.unitOfMeasurementId = unitOfMeasurementId;
	}

	@Column(name = "AMOUNT", columnDefinition="double")
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Column(name = "QUANTITY", columnDefinition="double")
	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	@Column(name = "UP_AMOUNT", columnDefinition="double")
	public Double getUpAmount() {
		return upAmount;
	}

	public void setUpAmount(Double upAmount) {
		this.upAmount = upAmount;
	}

	@Transient
	public String getUnitMeasurementName() {
		return unitMeasurementName;
	}

	public void setUnitMeasurementName(String unitMeasurementName) {
		this.unitMeasurementName = unitMeasurementName;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OtherCharge [unitOfMeasurementId=").append(unitOfMeasurementId).append(", amount=")
				.append(amount).append(", quantity=").append(quantity).append(", upAmount=").append(upAmount)
				.append(", unitMeasurementName=").append(unitMeasurementName).append(", vatAmount=").append(vatAmount)
				.append(", taxTypeId=").append(taxTypeId).append("]");
		return builder.toString();
	}

}
