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
 * A class that represents object type.

 *
 */
@Entity
@Table(name = "OBJECT_TYPE")
public class ObjectType extends BaseDomain{

	private String name;
	private String referenceTable;
	private String serviceClass;
	private boolean active;
	
	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "OBJECT_TYPE_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column (name = "NAME", columnDefinition="VARCHAR(50)")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column (name = "REFERENCE_TABLE", columnDefinition="VARCHAR(50)")
	public String getReferenceTable() {
		return referenceTable;
	}

	public void setReferenceTable(String referenceTable) {
		this.referenceTable = referenceTable;
	}
	
	@Column (name = "SERVICE_CLASS", columnDefinition="VARCHAR(100)")
	public String getServiceClass() {
		return serviceClass;
	}
	
	public void setServiceClass(String serviceClass) {
		this.serviceClass = serviceClass;
	}
	
	/**
	 * Verify the status of the division if active.
	 * @return True if active, otherwise false.
	 */
	@Column(name = "ACTIVE", columnDefinition = "TINYINT(1)")
	public boolean isActive() {
		return active;
	}

	/**
	 * Set the status of the division.
	 * @param active Set to true if active, otherwise false.
	 */
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
	@Column(name = "UPDATED_BY")
	public int getUpdatedBy() {
		return super.getUpdatedBy();
	}

	@Override
	@Column(name = "UPDATED_DATE")
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
	}

	@Override
	public String toString() {
		return "ObjectType [getId()=" + getId() + ", name=" + name
				+ ", referenceTable=" + referenceTable + ", active=" + active
				+ "]";
	}
}
