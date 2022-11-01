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

import com.google.gson.annotations.Expose;

/**
 * Class that represents AR_LINE table of the database

 */
@Entity
@Table(name = "AR_LINE")
public class ArLine extends AROtherCharge{
	@Expose
	private Integer aRTransactionId;
	private ArTransaction arTransaction;
	@Expose
	private String arLineSetupNumber;
	private Integer referenceObjectId;

	/**
	 * Object Type ID for {@link ArLine} = 18.
	 */
	public static final int OBJECT_TYPE_ID = 18;

	public enum FIELD {
		id, aRTransactionId, arLineSetupId, unitOfMeasurementId, amount, upAmount, quantity
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "AR_LINE_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "AR_TRANSACTION_ID")
	public Integer getaRTransactionId() {
		return aRTransactionId;
	}

	public void setaRTransactionId(Integer aRTransactionId) {
		this.aRTransactionId = aRTransactionId;
	}

	@ManyToOne
	@JoinColumn (name = "AR_TRANSACTION_ID", insertable=false, updatable=false)
	public ArTransaction getArTransaction() {
		return arTransaction;
	}

	public void setArTransaction(ArTransaction arTransaction) {
		this.arTransaction = arTransaction;
	}

	@Transient
	public String getArLineSetupNumber() {
		return arLineSetupNumber;
	}

	public void setArLineSetupNumber(String arLineSetupNumber) {
		this.arLineSetupNumber = arLineSetupNumber;
	}

	@Override
	public String toString() {
		return "ArLine [aRTransactionId=" + aRTransactionId
				+ ", arLineSetupId=" + getArLineSetupId() + ", unitOfMeasurementId="
				+ getUnitOfMeasurementId() + ", amount=" + getAmount() + ", quantity="
				+ getQuantity() + ", upAmount=" + getUpAmount()
				+ ", arLineSetupNumber=" + arLineSetupNumber
				+ ", unitMeasurementName=" + getUnitMeasurementName()
				+ ", arLineSetupName=" + getArLineSetupName() + ", getId()="
				+ getId() + ", getCreatedBy()=" + getCreatedBy()
				+ ", getCreatedDate()=" + getCreatedDate()
				+ ", getUpdatedBy()=" + getUpdatedBy() + ", getUpdatedDate()="
				+ getUpdatedDate() + "]";
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE_ID;
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
