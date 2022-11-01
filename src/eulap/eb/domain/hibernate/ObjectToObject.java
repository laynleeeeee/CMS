package eulap.eb.domain.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import eulap.common.domain.BaseDomain;

/**
 * A class that represents object to object relationship.

 *
 */
@Entity
@Table(name = "OBJECT_TO_OBJECT")
public class ObjectToObject extends BaseDomain{
	public enum FIELDS {id, fromObjectId, toObjectId, orTypeId}

	public static ObjectToObject getInstanceOf (int fromObjectId, int toObjectId, int orTypeId, User user, Date date) {
		ObjectToObject oo = new ObjectToObject();
		oo.fromObjectId = fromObjectId;
		oo.toObjectId = toObjectId;
		oo.orTypeId = orTypeId;
		oo.setCreatedBy(user.getId());
		oo.setCreatedDate(new Date());
		return oo;
	}

	private int fromObjectId;
	private int toObjectId;
	private int orTypeId;
	private EBObject fromObject;
	private EBObject toObject;
	private ORType orType;

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "OBJECT_TO_OBJECT_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Column (name = "FROM_OBJECT_ID", columnDefinition="INT(10)")
	public int getFromObjectId() {
		return fromObjectId;
	}


	public void setFromObjectId(int fromObjectId) {
		this.fromObjectId = fromObjectId;
	}

	@Column (name = "TO_OBJECT_ID", columnDefinition="INT(10)")
	public int getToObjectId() {
		return toObjectId;
	}


	public void setToObjectId(int toObjectId) {
		this.toObjectId = toObjectId;
	}

	@ManyToOne
	@JoinColumn(name="FROM_OBJECT_ID", insertable=false, updatable=false)
	public EBObject getFromObject() {
		return fromObject;
	}


	public void setFromObject(EBObject fromObject) {
		this.fromObject = fromObject;
	}

	@ManyToOne
	@JoinColumn(name="TO_OBJECT_ID", insertable=false, updatable=false)
	public EBObject getToObject() {
		return toObject;
	}


	public void setToObject(EBObject toObject) {
		this.toObject = toObject;
	}

	@Column(name="OR_TYPE_ID", columnDefinition="INT(10)")
	public int getOrTypeId() {
		return orTypeId;
	}

	public void setOrTypeId(int orTypeId) {
		this.orTypeId = orTypeId;
	}

	@ManyToOne
	@JoinColumn(name="OR_TYPE_ID", insertable=false, updatable=false)
	public ORType getOrType() {
		return orType;
	}


	public void setOrType(ORType orType) {
		this.orType = orType;
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
		return "ObjectToObject [getId()=" + getId() + ", fromObjectId="
				+ fromObjectId + ", "+ ", toObjectId=" + toObjectId
				+ ", fromObject=" + fromObject + ", toObject=" + toObject
				+ ", orType=" + orType + "]";
	}
}
