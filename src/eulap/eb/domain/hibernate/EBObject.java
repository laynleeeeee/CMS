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
 * A class that represent an CMS object.

 *
 */
@Entity
@Table(name = "EB_OBJECT")
public class EBObject extends BaseDomain{

	private int objectTypeId;
	
	public EBObject () {
		// For hibernate. 
	}

	public static enum FIELD {
		objectTypeId
	}
	
	private EBObject (User user, Date date, int objectTypeId) {
		setCreatedBy(user.getCreatedBy());
		setCreatedDate(date);
		setObjectTypeId(objectTypeId);
	}
	
	/**
	 * Get the instance of the Elasticbooks object. 
	 * @param user The current logged user. 
	 * @param date The current date. 
	 * @return
	 */
	public static EBObject getInstanceOf (User user, Date date, int objectTypeId) {
		return new EBObject(user, date, objectTypeId);
	}
	
	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "EB_OBJECT_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}
	
	@Column(name = "OBJECT_TYPE_ID", columnDefinition = "INT(10)")
	public int getObjectTypeId() {
		return objectTypeId;
	}
	
	public void setObjectTypeId(int objectTypeId) {
		this.objectTypeId = objectTypeId;
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
	public String toString() {
		return "EBObject [getId()=" + getId() + ", getCreatedBy()="
				+ getCreatedBy() + ", getCreatedDate()=" + getCreatedDate()
				+ "]";
	}
}
