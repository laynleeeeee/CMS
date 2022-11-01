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
 * Class that represents AR_SERVICE_LINE table of the database

 */
@Entity
@Table(name = "AR_SERVICE_LINE")
public class ArServiceLine extends ServiceLine{
	@Expose
	private Integer aRTransactionId;
	private ArTransaction arTransaction;
	@Expose
	private Integer referenceObjectId;

	/**
	 * Object Type ID for {@link ArServiceLine} = 24007.
	 */
	public static final int OBJECT_TYPE_ID = 24007;

	public enum FIELD {
		id, aRTransactionId, unitOfMeasurementId, amount, upAmount, quantity, serviceSettingId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "AR_SERVICE_LINE_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Override
	public String toString() {
		return "ArServiceLine [aRTransactionId=" + aRTransactionId + ", referenceObjectId=" + referenceObjectId + "]";
	}
}
