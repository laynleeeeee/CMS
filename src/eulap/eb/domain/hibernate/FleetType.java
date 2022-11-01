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

import eulap.common.domain.BaseDomain;
import eulap.eb.service.oo.OOChild;

/**
 * A class that represents the FLEET_TYPE table.

 *
 */
@Entity
@Table(name = "FLEET_TYPE")
public class FleetType extends BaseDomain implements OOChild {
	private Integer fleetCategoryId;
	private Integer companyId;
	private Company company;
	private String name;
	private boolean active;
	private Integer ebObjectId;
	private EBObject ebObject;
	private FleetCategory fleetCategory;

	public static final int MAX_FLEET_TYPE_NAME = 20;
	public static final int FLEET_TYPE_OBJECT_TYPE_ID = 82;
	public static final int FT_CONSTRUCTION = 1;
	public static final int FT_FISHING = 2;
	public static final int FLEET_TYPE_COMPANY_OR_TYPE = 39;

	public enum FIELD {
		id, name, active, ebObjectId, fleetCategoryId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "FLEET_TYPE_ID", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column (name = "FLEET_CATEGORY_ID", columnDefinition="int(10)")
	public Integer getFleetCategoryId() {
		return fleetCategoryId;
	}

	public void setFleetCategoryId(Integer fleetCategoryId) {
		this.fleetCategoryId = fleetCategoryId;
	}

	@Column (name = "NAME", columnDefinition="varchar(20)")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column (name = "ACTIVE", columnDefinition="tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
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

	@Override
	public void setEbObjectId(Integer eBObjecctId) {
		this.ebObjectId = eBObjecctId;
	}

	@Override
	@Transient
	public Integer getObjectTypeId() {
		// FLEET_TYPE type in OBJECT_TYPE table.
		return FLEET_TYPE_OBJECT_TYPE_ID;
	}

	@Override
	@Transient
	public Integer getRefenceObjectId() {
		return null;
	}

	@Transient
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@Transient
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@OneToOne
	@JoinColumn (name="FLEET_CATEGORY_ID", nullable=true, insertable=false, updatable=false)
	public FleetCategory getFleetCategory() {
		return fleetCategory;
	}

	public void setFleetCategory(FleetCategory fleetCategory) {
		this.fleetCategory = fleetCategory;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FleetType [fleetCategoryId=").append(fleetCategoryId).append(", companyId=").append(companyId)
				.append(", name=").append(name).append(", active=").append(active).append(", ebObjectId=")
				.append(ebObjectId).append("]");
		return builder.toString();
	}
}
