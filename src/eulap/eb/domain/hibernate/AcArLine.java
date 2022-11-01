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
 * Class that represents AC_AR_LINE table of the database

 */
@Entity
@Table(name = "AC_AR_LINE")
public class AcArLine extends AROtherCharge {
	private Integer arReceiptId;
	private ArReceipt receipt;

	/**
	 * Object type id for {@link AcArLine} = 143
	 */
	public static final int OBJECT_TYPE_ID = 143;

	public enum FIELD {
		id, arReceiptId, arLineSetupId, unitOfMeasurementId, amount, upAmount, quantity
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "AC_AR_LINE_ID", columnDefinition = "int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "AR_RECEIPT_ID", columnDefinition="int(10)")
	public Integer getArReceiptId() {
		return arReceiptId;
	}

	public void setArReceiptId(Integer arReceiptId) {
		this.arReceiptId = arReceiptId;
	}

	@ManyToOne
	@JoinColumn (name = "AR_RECEIPT_ID", insertable=false, updatable=false)
	public ArReceipt getReceipt() {
		return receipt;
	}

	public void setReceipt(ArReceipt receipt) {
		this.receipt = receipt;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE_ID;
	}

	@Override
	@Transient
	public Integer getRefenceObjectId() {
		// No reference object id.
		return null;
	}

	@Override
	public String toString() {
		return "AcArLine [arReceiptId=" + arReceiptId + ", getId()=" + getId()
				+ ", getArLineSetupId()=" + getArLineSetupId()
				+ ", getUnitOfMeasurementId()=" + getUnitOfMeasurementId()
				+ ", getAmount()=" + getAmount() + ", getQuantity()="
				+ getQuantity() + ", getUpAmount()=" + getUpAmount()
				+ ", getUnitMeasurementName()=" + getUnitMeasurementName()
				+ ", getArLineSetupName()=" + getArLineSetupName() + "]";
	}
}
