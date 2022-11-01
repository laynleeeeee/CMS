package eulap.eb.domain.hibernate;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.google.gson.annotations.Expose;

import eulap.common.domain.BaseDomain;

/**
 * A class that represents the FLEET_INSURANCE_PERMIT_RENEWAL table.

 *
 */
@Entity
@Table(name = "FLEET_INSURANCE_PERMIT_RENEWAL")
public class FleetInsurancePermitRenewal extends BaseDomain {
	@Expose
	private String refNo;
	@Expose
	private String description;
	@Expose
	private Date issuanceDate;
	@Expose
	private Date expirationDate;
	@Expose
	private String remarks;
	@Expose
	private Integer ebObjectId;
	private EBObject ebObject;
	private boolean active;

	public static final int OBJECT_TYPE_ID = 91;
	public static final int REFERENCE_DOCUMENT_OR_TYPE_ID = 26;
	public static final int MAX_REF_NO = 20;
	public static final int MAX_DESCRIPTION = 100;

	public enum FIELD {
		id, refNo, description, issuanceDate, expirationDate, remarks, ebObjectId, active
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "FLEET_INSURANCE_PERMIT_RENEWAL_ID", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column (name = "REF_NO", columnDefinition="varchar(20)")
	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	@Column (name = "DESCRIPTION", columnDefinition="varchar(100)")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column (name = "ISSUANCE_DATE", columnDefinition="date")
	public Date getIssuanceDate() {
		return issuanceDate;
	}

	public void setIssuanceDate(Date issuanceDate) {
		this.issuanceDate = issuanceDate;
	}

	@Column (name = "EXPIRATION_DATE", columnDefinition="date")
	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	@Column (name = "REMARKS", columnDefinition="text")
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@OneToOne
	@JoinColumn (name="EB_OBJECT_ID", nullable=true, insertable=false, updatable=false)
	public EBObject getEbObject() {
		return ebObject;
	}

	public void setEbObject(EBObject ebObject) {
		this.ebObject = ebObject;
	}

	@Column(name="EB_OBJECT_ID", columnDefinition="int(10)")
	public Integer getEbObjectId() {
		return ebObjectId;
	}

	public void setEbObjectId(Integer eBObjecctId) {
		this.ebObjectId = eBObjecctId;
	}

	@Column(name="ACTIVE", columnDefinition="tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return "FleetInsurancePermitRenewal [refNo=" + refNo + ", description=" + description + ", issuanceDate="
				+ issuanceDate + ", expirationDate=" + expirationDate + ", remarks=" + remarks + ", ebObjectId="
				+ ebObjectId + ", ebObject=" + ebObject + ", active=" + active + ", getId()=" + getId() + "]";
	}
}
