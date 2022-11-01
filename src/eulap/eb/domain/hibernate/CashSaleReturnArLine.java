package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

/**
 * Class that represents CASH_SALE_RETURN_AR_LINE table of the database

 */
@Entity
@Table(name = "CASH_SALE_RETURN_AR_LINE")
public class CashSaleReturnArLine extends AROtherCharge {
	@Expose
	private Integer cashSaleReturnId;
	@Expose
	private Integer cashSaleALRefId;
	@Expose
	private Integer cashSaleRetALRefId;
	@Expose
	private Integer referenceObjectId;

	public static final int CSR_AR_LINE_OBJECT_TYPE_ID = 144;

	public enum FIELD {
		id, cashSaleId, arLineSetupId, unitOfMeasurementId, amount, upAmount,
		quantity, cashSaleReturnId, cashSaleALRefId, cashSaleRetALRefId, ebObjectId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "CASH_SALE_RETURN_AR_LINE_ID", columnDefinition = "int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "CASH_SALE_AR_LINE_REF_ID", columnDefinition="int(10)")
	public Integer getCashSaleALRefId() {
		return cashSaleALRefId;
	}

	public void setCashSaleALRefId(Integer cashSaleALRefId) {
		this.cashSaleALRefId = cashSaleALRefId;
	}

	@Column(name = "CASH_SALE_RETURN_AR_LINE_REF_ID", columnDefinition="int(10)")
	public Integer getCashSaleRetALRefId() {
		return cashSaleRetALRefId;
	}

	public void setCashSaleRetALRefId(Integer cashSaleRetALRefId) {
		this.cashSaleRetALRefId = cashSaleRetALRefId;
	}

	@Column(name = "CASH_SALE_RETURN_ID", columnDefinition="int(10)")
	public Integer getCashSaleReturnId() {
		return cashSaleReturnId;
	}

	public void setCashSaleReturnId(Integer cashSaleReturnId) {
		this.cashSaleReturnId = cashSaleReturnId;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return CSR_AR_LINE_OBJECT_TYPE_ID;
	}

	@Override
	@Transient
	public Integer getRefenceObjectId() {
		return referenceObjectId;
	}

	public void setReferenceObjectId(Integer referenceObjectId) {
		this.referenceObjectId = referenceObjectId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CashSaleReturnArLine [cashSaleReturnId=").append(cashSaleReturnId).append(", cashSaleALRefId=")
		.append(", ebObjectId=").append(getEbObjectId()).append(", ebObject=").append(getEbObject())
				.append(cashSaleALRefId).append(", cashSaleRetALRefId=").append(cashSaleRetALRefId)
				.append(", getId()=").append(getId()).append("]");
		return builder.toString();
	}
}
