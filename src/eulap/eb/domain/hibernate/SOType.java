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
 * Object representation for SO_TYPE table

 *
 */

@Entity
@Table(name="SO_TYPE")
public class SOType extends BaseDomain{
	private String name;
	private boolean active;	

	public enum FIELD {
		id, name, description, active
	}

	public static final int PO_SO_TYPE_ID = 1;
	public static final int PCR_SO_TYPE_ID = 2;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "SO_TYPE_ID", columnDefinition = "int(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column(name = "NAME", columnDefinition = "varchar(50)")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name="ACTIVE", columnDefinition="tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
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

	@Override
	public String toString() {
		return "SOType [name=" + name + ", active=" + active + "]";
	}
}
