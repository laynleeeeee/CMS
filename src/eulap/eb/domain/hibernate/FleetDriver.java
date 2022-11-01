package eulap.eb.domain.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

import eulap.common.domain.BaseDomain;

/**
 * 

 *
 */
@Entity
@Table(name="FLEET_DRIVER")
public class FleetDriver extends BaseDomain {
	@Expose
	private Date date;
	@Expose
	private String name;
	@Expose
	private String licenseNo;
	@Expose
	private Date expirationDate;
	@Expose
	private String remarks;
	@Expose
	private Integer ebObjectId;
	@Expose
	private boolean active;

	public static final int OBJECT_TYPE_ID = 89;
	public static final int OR_TYPE_REF_DOC = 24;

	public static final int MAX_NAME = 100;
	public static final int MAX_LICENCSE_NO = 50;

	public enum FIELD {
		id, name, licenseNo, expirationDate, remarks, ebObjectId, active
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "FLEET_DRIVER_ID", columnDefinition = "int(10)", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name = "DATE", columnDefinition = "date")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Column(name = "NAME", columnDefinition = "varchar(100)")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "LICENSE_NO", columnDefinition = "varchar(100)")
	public String getLicenseNo() {
		return licenseNo;
	}

	public void setLicenseNo(String licenseNo) {
		this.licenseNo = licenseNo;
	}

	@Column(name = "EXPIRATION_DATE", columnDefinition = "date")
	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	@Column(name = "REMARKS", columnDefinition = "varchar(100)")
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Column(name = "EB_OBJECT_ID", columnDefinition = "int(10)")
	public Integer getEbObjectId() {
		return ebObjectId;
	}

	public void setEbObjectId(Integer ebObjectId) {
		this.ebObjectId = ebObjectId;
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
		builder.append("FleetDriver [date=").append(date).append(", name=").append(name).append(", licenseNo=")
				.append(licenseNo).append(", expirationDate=").append(expirationDate).append(", remarks=")
				.append(remarks).append(", ebObjectId=").append(ebObjectId).append(", active=").append(active)
				.append("]");
		return builder.toString();
	}
}
