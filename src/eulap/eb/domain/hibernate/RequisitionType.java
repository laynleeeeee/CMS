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
 * Object representation for REQUISITION_TYPE table.

 */
@Entity
@Table(name="REQUISITION_TYPE")
public class RequisitionType extends BaseDomain {
	private String name;
	private boolean active;

	public enum FIELD {
		id, name, active, createdBy, updatedBy
	}

	public static final int RT_TIRE = 1;
	public static final int RT_FUEL = 2;
	public static final int RT_PMS = 3;
	public static final int RT_ELECTRICAL = 4;
	public static final int RT_CONSTRUCTION_MATERIAL = 5;
	public static final int RT_ADMIN = 6;
	public static final int RT_MOTORPOOL = 7;
	public static final int RT_OIL = 8;
	public static final int RT_SUBCON = 9;
	public static final int RT_PAKYAWAN = 10;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "REQUISITION_TYPE_ID", columnDefinition="int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Override
	@Column(name = "CREATED_BY", columnDefinition="INT(10)")
	public int getCreatedBy() {
		return super.getCreatedBy();
	}

	@Override
	@Column(name = "CREATED_DATE", columnDefinition="TIMESTAMP")
	public Date getCreatedDate() {
		return super.getCreatedDate();
	}

	@Override
	@Column(name = "UPDATED_BY", columnDefinition="INT(10)")
	public int getUpdatedBy() {
		return super.getUpdatedBy();
	}

	@Override
	@Column(name = "UPDATED_DATE", columnDefinition="TIMESTAMP")
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
	}

	@Column(name = "NAME", columnDefinition = "int(10)")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "ACTIVE", columnDefinition = "tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RequisitionType [name=").append(name).append(", active=").append(active).append("]");
		return builder.toString();
	}
}
