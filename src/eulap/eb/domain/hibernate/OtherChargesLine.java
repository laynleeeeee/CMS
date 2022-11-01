package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

/**
 * Object representation for OTHER_CHARGES_LINE table.

 */

@Entity
@Table(name="OTHER_CHARGES_LINE")
public class OtherChargesLine extends OtherCharge {
	@Expose
	private Integer apLineSetupId;
	@Expose
	private String apLineSetupName;
	private ApLineSetup apLineSetup;

	public enum FIELD {
		id, apLineSetupId, ebObjectId
	}

	/**
	 * Object Type Id for {@link OtherChargesLine};
	 */
	public static final int OBJECT_TYPE_ID = 3004;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "OTHER_CHARGES_LINE_ID", columnDefinition = "int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name="AP_LINE_SETUP_ID", columnDefinition="int(10)")
	public Integer getApLineSetupId() {
		return apLineSetupId;
	}

	public void setApLineSetupId(Integer apLineSetupId) {
		this.apLineSetupId = apLineSetupId;
	}

	@OneToOne
	@JoinColumn(name = "AP_LINE_SETUP_ID", insertable=false, updatable=false)
	public ApLineSetup getApLineSetup() {
		return apLineSetup;
	}

	public void setApLineSetup(ApLineSetup apLineSetup) {
		this.apLineSetup = apLineSetup;
	}

	@Transient
	public String getApLineSetupName() {
		return apLineSetupName;
	}

	public void setApLineSetupName(String apLineSetupName) {
		this.apLineSetupName = apLineSetupName;
	}

	@Override
	@Transient
	public Integer getRefenceObjectId() {
		return null;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE_ID;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OtherChargesLine [apLineSetupId=").append(apLineSetupId)
		.append(", ebObjectId=").append(getEbObjectId())
		.append(", uomId=").append(getUnitOfMeasurementId())
		.append(", upAmount=").append(getUpAmount())
		.append(", amount=").append(getAmount()).append("]");
		return builder.toString();
	}
}