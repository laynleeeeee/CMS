package eulap.eb.domain.hibernate;

import java.util.Date;

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

import eulap.common.domain.BaseDomain;

/**
 * Object representation class of FLEET_PMS_DRY_DOCK table.

 *
 */
@Entity
@Table(name="FLEET_DRY_DOCK")
public class FleetDryDock extends BaseDomain{

	@Expose
	private Integer ebObjectId;
	private EBObject ebObject;
	@Expose
	private Date date;
	@Expose
	private String contractor;
	@Expose
	private Boolean isActual;
	@Expose
	private Boolean active;
	@Expose
	private String strDate;

	/**
	 * Object Type Id for FleetPms = 115.
	 */
	public static final int OBJECT_TYPE_ID = 115;

	/**
	 * Or Type Id for FleetPms Reference Documents = 69.
	 */
	public static final int OR_TYPE_REF_DOC_ID = 69;

	/**
	 * Maximum allowable character for Fleet PMS Fields = 50.
	 */
	public static final int MAX_CHAR = 50;

	public enum FIELD {
		id, date, contractor, isActual, ebObjectId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "FLEET_DRY_DOCK_ID", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name = "EB_OBJECT_ID", columnDefinition = "INTEGER(10)")
	public Integer getEbObjectId() {
		return ebObjectId;
	}

	public void setEbObjectId(Integer ebObjectId) {
		this.ebObjectId = ebObjectId;
	}

	@OneToOne
	@JoinColumn (name="EB_OBJECT_ID", columnDefinition="INT(10)", insertable=false, updatable=false)
	public EBObject getEbObject() {
		return ebObject;
	}

	public void setEbObject(EBObject ebObject) {
		this.ebObject = ebObject;
	}

	@Column(name = "DATE", columnDefinition = "DATE")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Column(name = "CONTRACTOR", columnDefinition = "varchar(50)")
	public String getContractor() {
		return contractor;
	}

	public void setContractor(String contractor) {
		this.contractor = contractor;
	}

	@Column(name = "IS_ACTUAL", columnDefinition = "tinyint(1)")
	public Boolean getIsActual() {
		return isActual;
	}

	public void setIsActual(Boolean isActual) {
		this.isActual = isActual;
	}

	@Column(name = "ACTIVE", columnDefinition = "tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Transient
	public String getStrDate() {
		return strDate;
	}

	public void setStrDate(String strDate) {
		this.strDate = strDate;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FleetPmsDryDock [ebObjectId=").append(ebObjectId).append(", date=").append(date)
				.append(", contractor=").append(contractor).append(", isActual=").append(isActual).append(", active=")
				.append(active).append("]");
		return builder.toString();
	}

}
