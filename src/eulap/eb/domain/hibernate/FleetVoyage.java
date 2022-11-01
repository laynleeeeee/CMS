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
 * Object representation class for FLEET_PMS_VOYAGES table

 *
 */
@Entity
@Table(name="FLEET_VOYAGE")
public class FleetVoyage extends BaseDomain {
	@Expose
	private Integer ebObjectId;
	private EBObject ebObject;
	@Expose
	private Date dateOfDeparture;
	@Expose
	private Date dateOfUnloading;
	@Expose
	private String catcher;
	@Expose
	private String volume;
	@Expose
	private Boolean active;
	@Expose
	private String strDateOfDeparture;
	@Expose
	private String strDateOfUnloading;

	/**
	 * Object Type Id for FleetPms = 116.
	 */
	public static final int OBJECT_TYPE_ID = 116;

	/**
	 * Or Type Id for FleetPms Reference Documents = 70.
	 */
	public static final int OR_TYPE_REF_DOC_ID = 70;

	/**
	 * Maximum allowable character for Fleet PMS Fields = 50.
	 */
	public static final int MAX_CHAR = 50;

	public enum FIELD {
		id, ebObjectId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "FLEET_VOYAGE_ID", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name="DATE_OF_DEPARTURE", columnDefinition="DATE")
	public Date getDateOfDeparture() {
		return dateOfDeparture;
	}

	public void setDateOfDeparture(Date dateOfDeparture) {
		this.dateOfDeparture = dateOfDeparture;
	}

	@Column(name="DATE_OF_UNLOADING", columnDefinition="DATE")
	public Date getDateOfUnloading() {
		return dateOfUnloading;
	}

	public void setDateOfUnloading(Date dateOfUnloading) {
		this.dateOfUnloading = dateOfUnloading;
	}

	@Column(name="CATCHER", columnDefinition="VARCHAR(50)")
	public String getCatcher() {
		return catcher;
	}

	public void setCatcher(String catcher) {
		this.catcher = catcher;
	}

	@Column(name="VOLUME", columnDefinition="VARCHAR(50)")
	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	@Column(name="ACTIVE", columnDefinition="TINYINT(1)")
	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	@Transient
	public String getStrDateOfDeparture() {
		return strDateOfDeparture;
	}

	public void setStrDateOfDeparture(String strDateOfDerpature) {
		this.strDateOfDeparture = strDateOfDerpature;
	}

	@Transient
	public String getStrDateOfUnloading() {
		return strDateOfUnloading;
	}

	public void setStrDateOfUnloading(String strDateOfUnloading) {
		this.strDateOfUnloading = strDateOfUnloading;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FleetPmsVoyage [ebObjectId=").append(ebObjectId).append(", dateOfPalarga=")
				.append(dateOfDeparture).append(", dateOfHabwa=").append(dateOfUnloading).append(", catcher=").append(catcher)
				.append(", volume=").append(volume).append(", active=").append(active).append("]");
		return builder.toString();
	}

}
