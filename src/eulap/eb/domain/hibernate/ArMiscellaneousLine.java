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
 * Class that represents AR_MISCELLANEOUS_LINE table of the database

 */
@Entity
@Table(name = "AR_MISCELLANEOUS_LINE")
public class ArMiscellaneousLine extends ServiceLine{
	private Integer arMiscellaneousId;
	private ArMiscellaneous arMiscellaneous;
	@Expose
	private String arLineSetupNumber;

	/**
	 * Object Type ID of {@link ArMiscellaneousLine} = 141.
	 */
	public static final int OBJECT_TYPE_ID = 141;

	public enum FIELD {arMiscellaneousId, arLineSetupId, unitOfMeasurementId, amount, upAmount, quantity}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "AR_MISCELLANEOUS_LINE_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "AR_MISCELLANEOUS_ID")
	public Integer getArMiscellaneousId() {
		return arMiscellaneousId;
	}

	public void setArMiscellaneousId(Integer arMiscellaneousId) {
		this.arMiscellaneousId = arMiscellaneousId;
	}

	@ManyToOne
	@JoinColumn (name = "AR_MISCELLANEOUS_ID", insertable=false, updatable=false)
	public ArMiscellaneous getArMiscellaneous() {
		return arMiscellaneous;
	}

	public void setArMiscellaneous(ArMiscellaneous arMiscellaneous) {
		this.arMiscellaneous = arMiscellaneous;
	}

	@Transient
	public String getArLineSetupNumber() {
		return arLineSetupNumber;
	}

	public void setArLineSetupNumber(String arLineSetupNumber) {
		this.arLineSetupNumber = arLineSetupNumber;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE_ID;
	}

	@Override
	@Transient
	public Integer getRefenceObjectId() {
		return null;
	}

	@Override
	public String toString() {
		return "ArMiscellaneousLine [arMiscellaneousId=" + arMiscellaneousId
				+ ", arMiscellaneous=" + arMiscellaneous
				+ ", arLineSetupNumber=" + arLineSetupNumber + "]";
	}

}
