package eulap.eb.domain.hibernate;

import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

import com.google.gson.annotations.Expose;

import eulap.common.domain.BaseDomain;
import eulap.eb.service.oo.OODomain;

/**
 * A class that defines the object to object domains

 *
 */
@MappedSuperclass
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class OOBaseDomain extends BaseDomain implements OODomain{

	@Expose
	private Integer ebObjectId;
	private EBObject ebObject;

	@Column(name = "EB_OBJECT_ID", columnDefinition = "int(10)")
	public Integer getEbObjectId() {
		return ebObjectId;
	}

	public void setEbObjectId(Integer ebObjectId) {
		this.ebObjectId = ebObjectId;
	}

	@OneToOne
	@JoinColumn (name="EB_OBJECT_ID", insertable=false, updatable=false)
	public EBObject getEbObject() {
		return ebObject;
	}

	public void setEbObject(EBObject ebObject) {
		this.ebObject = ebObject;
	}

}
