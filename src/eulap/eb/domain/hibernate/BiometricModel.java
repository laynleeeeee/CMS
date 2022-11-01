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
 * Domain class for BIOMETRIC_MODEL

 *
 */
@Entity
@Table(name="BIOMETRIC_MODEL")
public class BiometricModel extends BaseDomain {
	private String modelName;
	private boolean active;

	public static final int AMAX = 1; // Amax ZKT-TX628
	public static final int JSR = 2; // Jstar Biometric
	public static final int FORTRESS = 3;
	public static final int GENERIC = 4;
	public static final int CSC = 5; // CSC
	public static final int YOKO = 6; // YOKO
	public static final int ZKTECOIN01A = 7; // Common ZK TECO IN01A
	public static final int REALAND = 8; // GVCH Realand
	public static final int ZKTECO628C = 9; // Common ZK TECO 0628C
	public static final int IRONVALLEY = 10;
	public static final int EULAP = 11;
	public static final int SSC = 13;
	public static final int TBC = 16; // TBC
	public static final int G5544 = 17; // G5544
	public static final int GRVFC = 18; // GRVFC

	public enum FIELD {
		id, modelName, active
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "BIOMETRIC_MODEL_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Override
	@Column(name = "CREATED_BY", columnDefinition = "INT(10)")
	public int getCreatedBy() {
		return super.getCreatedBy();
	}

	@Override
	@Column(name = "CREATED_DATE", columnDefinition = "TIMESTAMP")
	public Date getCreatedDate() {
		return super.getCreatedDate();
	}

	@Override
	@Column(name = "UPDATED_BY", columnDefinition = "INT(10)")
	public int getUpdatedBy() {
		return super.getUpdatedBy();
	}

	@Override
	@Column(name = "UPDATED_DATE", columnDefinition = "TIMESTAMP")
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
	}

	@Column(name = "MODEL_NAME", columnDefinition = "VARCHAR(50)")
	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	@Column(name = "ACTIVE", columnDefinition = "TINYINT(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BiometricModel [modelName=").append(modelName).append(", active=").append(active)
				.append(", getId()=").append(getId()).append("]");
		return builder.toString();
	}
}
