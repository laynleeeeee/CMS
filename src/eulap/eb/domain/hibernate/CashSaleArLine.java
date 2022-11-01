package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

/**
 * Class that represents CASH_SALE_AR_LINE table of the database

 */
@Entity
@Table(name = "CASH_SALE_AR_LINE")
public class CashSaleArLine extends AROtherCharge {
	private Integer cashSaleId;
	private CashSale cashSale;
	private Integer referenceObjectId;

	public enum FIELD {
		id, cashSaleId, arLineSetupId, unitOfMeasurementId, amount, upAmount, quantity
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "CASH_SALE_AR_LINE_ID", columnDefinition = "int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "CASH_SALE_ID", columnDefinition="int(10)")
	public Integer getCashSaleId() {
		return cashSaleId;
	}

	public void setCashSaleId(Integer cashSaleId) {
		this.cashSaleId = cashSaleId;
	}

	@ManyToOne
	@JoinColumn (name = "CASH_SALE_ID", insertable=false, updatable=false)
	public CashSale getCashSale() {
		return cashSale;
	}

	public void setCashSale(CashSale cashSale) {
		this.cashSale = cashSale;
	}

	@Override
	public String toString() {
		return "CashSaleArLine [cashSaleId=" + cashSaleId + ", cashSale="
				+ cashSale + ", getId()=" + getId() + ", getArLineSetupId()="
				+ getArLineSetupId() + ", getUnitOfMeasurementId()="
				+ getUnitOfMeasurementId() + ", getAmount()=" + getAmount()
				+ ", getQuantity()=" + getQuantity() + ", getUpAmount()="
				+ getUpAmount() + "]";
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return 17;
	}

	@Override
	@Transient
	public Integer getRefenceObjectId() {
		return referenceObjectId;
	}

	public void setReferenceObjectId(Integer referenceObjectId) {
		this.referenceObjectId = referenceObjectId;
	}
}
