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
 * Object representation of FLEET_PROFILE table.

 * 
 */
@Entity
@Table(name = "FLEET_PROFILE")
public class FleetProfile extends BaseDomain{
	private String codeVesselName; // Required for both.
	private Date acquisitionDate;
	private String make; // Required for construction.
	private String model;
	private String chassisNo; // Required for construction.
	private String officialNo; // Required for fishing.
	private String bodyNo;
	private String plateNo;
	private String engineNo; // Required for both.
	private String description;
	private String tonnageWeight;
	private String callSign;
	private String vms;
	private String propeller;
	private String winch;
	private String driverName;
	private String captain;
	private String supplier;
	private Integer ebObjectId;
	private Integer refObjectId;
	private Integer fleetTypeId;
	private FleetType fleetType;
	private Integer companyId;
	private Integer divisionId;
	private Company company;
	private Division division;
	private Integer driverId;
	private Driver driver;

	public static final int OBJECT_TYPE_ID = 83;

	public static final int MAX_CODE_VEESSEL_NAME = 100;
	public static final int MAX_MAKE = 100;
	public static final int MAX_MODEL = 100;
	public static final int MAX_CHASSIS_NO = 100;
	public static final int MAX_PLATE_NO = 100;
	public static final int MAX_ENGINE_NO = 100;
	public static final int MAX_DESCRIPTION = 250;
	public static final int MAX_DRIVER = 100;
	public static final int MAX_SUPPLIER = 100;
	public static final int MAX_OFFICIAL_NO = 100;
	public static final int MAX_CALL_SIGN = 100;
	public static final int MAX_TONNAGE_WEIGHT = 100;
	public static final int MAX_VMS = 100;
	public static final int MAX_PROPELLER = 100;
	public static final int MAX_WINCH = 100;
	public static final int MAX_CAPTAIN = 100;

	public static final int FP_COMPANY_OR_TYPE_ID = 40;
	public static final int FP_TYPE_OR_TYPE_ID = 41;
	public static final int FP_DIVISION_OR_TYPE_ID = 51;

	public static final int PRODUCT_KEY = 20023;

	public enum FIELD {
		id, companyId, codeVesselName, acquisitionDate, make, model, chassisNo, officialNo, bodyNo, plateNo, engineNo, description, 
		tonnageWeight, callSign, vms, propeller, winch, driver, captain, supplier, ebObjectId
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "FLEET_PROFILE_ID", unique = true, nullable = false, insertable = false, updatable = false)
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


	@Column(name = "CODE_VESSEL_NAME", columnDefinition = "varchar(100)")
	public String getCodeVesselName() {
		return codeVesselName;
	}

	public void setCodeVesselName(String codeVesselName) {
		this.codeVesselName = codeVesselName;
	}

	@Column(name = "ACQUITISION_DATE", columnDefinition = "date")
	public Date getAcquisitionDate() {
		return acquisitionDate;
	}

	public void setAcquisitionDate(Date acquisitionDate) {
		this.acquisitionDate = acquisitionDate;
	}

	@Column(name = "MAKE", columnDefinition = "varchar(100)")
	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	@Column(name = "MODEL", columnDefinition = "varchar(100)")
	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	@Column(name = "CHASSIS_NO", columnDefinition = "varchar(100)")
	public String getChassisNo() {
		return chassisNo;
	}

	public void setChassisNo(String chassisNo) {
		this.chassisNo = chassisNo;
	}

	@Column(name = "OFFICIAL_NO", columnDefinition = "varchar(100)")
	public String getOfficialNo() {
		return officialNo;
	}

	public void setOfficialNo(String officialNo) {
		this.officialNo = officialNo;
	}

	@Column(name = "BODY_NO", columnDefinition = "varchar(100)")
	public String getBodyNo() {
		return bodyNo;
	}

	public void setBodyNo(String bodyNo) {
		this.bodyNo = bodyNo;
	}

	@Column(name = "PLATE_NO", columnDefinition = "varchar(100)")
	public String getPlateNo() {
		return plateNo;
	}

	public void setPlateNo(String plateNo) {
		this.plateNo = plateNo;
	}

	@Column(name = "ENGINE_NO", columnDefinition = "varchar(100)")
	public String getEngineNo() {
		return engineNo;
	}

	public void setEngineNo(String engineNo) {
		this.engineNo = engineNo;
	}

	@Column(name = "DESCRIPTION", columnDefinition = "varchar(250)")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "TONNAGE_WEIGHT", columnDefinition = "varchar(100)")
	public String getTonnageWeight() {
		return tonnageWeight;
	}

	public void setTonnageWeight(String tonnageWeight) {
		this.tonnageWeight = tonnageWeight;
	}

	@Column(name = "CALL_SIGN", columnDefinition = "varchar(100)")
	public String getCallSign() {
		return callSign;
	}

	public void setCallSign(String callSign) {
		this.callSign = callSign;
	}

	@Column(name = "VMS", columnDefinition = "varchar(100)")
	public String getVms() {
		return vms;
	}

	public void setVms(String vms) {
		this.vms = vms;
	}

	@Column(name = "PROPELLER", columnDefinition = "varchar(100)")
	public String getPropeller() {
		return propeller;
	}

	public void setPropeller(String propeller) {
		this.propeller = propeller;
	}

	@Column(name = "WINCH", columnDefinition = "varchar(100)")
	public String getWinch() {
		return winch;
	}

	public void setWinch(String winch) {
		this.winch = winch;
	}

	@Column(name = "DRIVER", columnDefinition = "varchar(100)")
	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	@Column(name = "CAPTAIN", columnDefinition = "varchar(100)")
	public String getCaptain() {
		return captain;
	}

	public void setCaptain(String captain) {
		this.captain = captain;
	}

	@Column(name = "SUPPLIER", columnDefinition = "varchar(100)")
	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	@Column(name = "EB_OBJECT_ID", columnDefinition = "int(10)")
	public Integer getEbObjectId() {
		return ebObjectId;
	}

	public void setEbObjectId(Integer ebObjectId) {
		this.ebObjectId = ebObjectId;
	}

	@Column(name = "DRIVER_ID", columnDefinition = "int(10)")
	public Integer getDriverId() {
		return driverId;
	}

	public void setDriverId(Integer driverId) {
		this.driverId = driverId;
	}

	@Transient
	public Integer getRefObjectId() {
		return refObjectId;
	}

	public void setRefObjectId(Integer refObjectId) {
		this.refObjectId = refObjectId;
	}

	@Transient
	public Integer getFleetTypeId() {
		return fleetTypeId;
	}

	public void setFleetTypeId(Integer fleetTypeId) {
		this.fleetTypeId = fleetTypeId;
	}

	@Transient
	public FleetType getFleetType() {
		return fleetType;
	}

	public void setFleetType(FleetType fleetType) {
		this.fleetType = fleetType;
	}

	@Column(name = "COMPANY_ID", columnDefinition = "int(10)")
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

	@Transient
	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@Transient
	public Division getDivision() {
		return division;
	}

	public void setDivision(Division division) {
		this.division = division;
	}

	@OneToOne
	@JoinColumn(name = "DRIVER_ID", insertable=false, updatable=false)
	public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FleetProfile [companyId=").append(companyId).append(", divisionId=").append(divisionId)
				.append(", codeVesselName=").append(codeVesselName).append(", acquisitionDate=").append(acquisitionDate)
				.append(", make=").append(make).append(", model=").append(model).append(", chassisNo=")
				.append(chassisNo).append(", officialNo=").append(officialNo).append(", bodyNo=").append(bodyNo)
				.append(", plateNo=").append(plateNo).append(", engineNo=").append(engineNo).append(", description=")
				.append(description).append(", tonnageWeight=").append(tonnageWeight).append(", callSign=")
				.append(callSign).append(", vms=").append(vms).append(", propeller=").append(propeller)
				.append(", winch=").append(winch).append(", driver=").append(driver).append(", captain=")
				.append(captain).append(", supplier=").append(supplier).append(", ebObjectId=").append(ebObjectId)
				.append(", refObjectId=").append(refObjectId).append(", fleetTypeId=").append(fleetTypeId).append("]");
		return builder.toString();
	}
}
