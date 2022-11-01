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
 * Class that represents PROCESSING_REPORT_TYPE table in the database.

 *
 */
@Entity
@Table(name="PROCESSING_REPORT_TYPE")
public class ProcessingReportType extends BaseDomain {
	private String name;
	private boolean active;

	public static final int MILLING_REPORT = 1;
	public static final int MILLING_ORDER = 2;
	public static final int PASS_IN = 3;
	public static final int PASS_OUT = 4;
	public static final int WIP_BAKING = 5;
	public static final int PRODUCTION = 6;

	public enum FIELD {
		id, name, active
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "PROCESSING_REPORT_TYPE_ID", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Override
	@Column(name = "CREATED_BY")
	public int getCreatedBy() {
		return super.getCreatedBy();
	}

	@Override
	@Column(name = "CREATED_DATE")
	public Date getCreatedDate() {
		return super.getCreatedDate();
	}

	@Override
	@Column(name = "UPDATED_BY")
	public int getUpdatedBy() {
		return super.getUpdatedBy();
	}

	@Override
	@Column(name = "UPDATED_DATE")
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
	}

	@Column(name = "NAME", columnDefinition="varchar(25)")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "ACTIVE", columnDefinition="tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProcessingReportType [name=");
		builder.append(name);
		builder.append(", active=");
		builder.append(active);
		builder.append(", getId()=");
		builder.append(getId());
		builder.append(", getCreatedBy()=");
		builder.append(getCreatedBy());
		builder.append(", getCreatedDate()=");
		builder.append(getCreatedDate());
		builder.append(", getUpdatedBy()=");
		builder.append(getUpdatedBy());
		builder.append(", getUpdatedDate()=");
		builder.append(getUpdatedDate());
		builder.append("]");
		return builder.toString();
	}
}
