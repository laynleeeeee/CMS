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
/**
 * An Object representation of WAREHOUSE in CBS database. 


 */
@Entity
@Table (name="WAREHOUSE")
public class Warehouse extends BaseDomain{
	private Integer companyId;
	private String name;
	private String address;
	private boolean active;
	private Company company;
	private Double existingStocks;
	private Integer ebObjectId;
	private EBObject ebObject;
	private Integer parentWarehouseId;
	private String parentWarehouseName;
	private Integer divisionId;
	private Division division;

	public static final int MAX_NAME = 30;
	public static final int MAX_ADDRESS = 50;
	public static final int OBJECT_TYPE_ID = 98;

	public enum FIELD {
		id, name, companyId, active, address, parentWarehouseId, divisionId
	};

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "WAREHOUSE_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
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

	/**
	 * Get the company id.
	 * @return The company id.
	 */
	@Column(name = "COMPANY_ID", columnDefinition = "INT(10)")
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	/**
	 * Get the name of the warehouse.
	 * @return The name of the warehouse.
	 */
	@Column(name = "NAME", columnDefinition = "VARCHAR(30)")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the address of the warehouse
	 * @return The address of the warehouse.
	 */
	@Column(name = "ADDRESS", columnDefinition = "VARCHAR(50)")
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@OneToOne
	@JoinColumn(name = "COMPANY_ID", insertable = false, updatable = false)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Column(name = "ACTIVE", columnDefinition = "TINYINT(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Get the existing stocks of the item under this warehouse.
	 */
	@Transient
	public Double getExistingStocks() {
		return existingStocks;
	}

	public void setExistingStocks(Double existingStocks) {
		this.existingStocks = existingStocks;
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

	@Transient
	public Integer getObjectTypeId() {
		return OBJECT_TYPE_ID;
	}

	@Column(name="PARENT_WAREHOUSE_ID", columnDefinition="int(10)")
	public Integer getParentWarehouseId() {
		return parentWarehouseId;
	}

	public void setParentWarehouseId(Integer parentWarehouseId) {
		this.parentWarehouseId = parentWarehouseId;
	}

	@Transient
	public String getParentWarehouseName() {
		return parentWarehouseName;
	}

	public void setParentWarehouseName(String parentWarehouseName) {
		this.parentWarehouseName = parentWarehouseName;
	}

	@Column(name="DIVISION_ID", columnDefinition="int(10)")
	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@OneToOne
	@JoinColumn (name="DIVISION_ID", nullable=true, insertable=false, updatable=false)
	public Division getDivision() {
		return division;
	}

	public void setDivision(Division division) {
		this.division = division;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Warehouse [companyId=").append(companyId).append(", name=").append(name).append(", address=")
				.append(address).append(", active=").append(active).append(", existingStocks=").append(existingStocks)
				.append(", ebObjectId=").append(ebObjectId).append(", parentWarehouseId=").append(parentWarehouseId)
				.append(", parentWarehouseName=").append(parentWarehouseName).append(", divisionId=").append(divisionId)
				.append("]");
		return builder.toString();
	}
}
