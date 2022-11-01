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
 * A class that represents the FLEET_PMS table.

 *
 */
@Entity
@Table(name = "PREVENTIVE_MAINTENANCE_SCHEDULE")
public class FleetPms extends BaseDomain {
	@Expose
	private Integer ebObjectId;
	private EBObject ebObject;
	@Expose
	private String changeOilEngine;
	@Expose
	private String overhaulEngineMain;
	@Expose
	private String overhaulEngineAuxiliary;
	@Expose
	private String changeOilTransmission;
	@Expose
	private String filterFuel;
	@Expose
	private String filterOil;
	@Expose
	private String filterAir;
	@Expose
	private String filterFanbelt;
	@Expose
	private String net;
	@Expose
	private boolean active;
	@Expose
	private boolean isSchedule;

	/**
	 * Object Type Id for FleetPms = 95.
	 */
	public static final int OBJECT_TYPE_ID = 95;

	/**
	 * Or Type Id for FleetPms Reference Documents = 42.
	 */
	public static final int OR_TYPE_REF_DOC_ID = 42;

	/**
	 * Maximum allowable character for Fleet PMS Fields = 50.
	 */
	public static final int MAX_CHAR = 50;

	public enum FIELD {
		id, ebObjectId, active, isSchedule
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator (name = "increment", strategy = "increment")
	@Column (name = "PREVENTIVE_MAINTENANCE_SCHEDULE_ID", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name = "CHANGE_OIL_ENGINE", columnDefinition = "varchar(50)")
	public String getChangeOilEngine() {
		return changeOilEngine;
	}

	public void setChangeOilEngine(String changeOilEngine) {
		this.changeOilEngine = changeOilEngine;
	}

	@Column(name = "OVERHAUL_ENGINE_MAIN", columnDefinition = "varchar(50)")
	public String getOverhaulEngineMain() {
		return overhaulEngineMain;
	}

	public void setOverhaulEngineMain(String overhaulEngineMain) {
		this.overhaulEngineMain = overhaulEngineMain;
	}

	@Column(name = "OVERHAUL_ENGINE_AUXILIARY", columnDefinition = "varchar(50)")
	public String getOverhaulEngineAuxiliary() {
		return overhaulEngineAuxiliary;
	}

	public void setOverhaulEngineAuxiliary(String overhaulEngineAuxiliary) {
		this.overhaulEngineAuxiliary = overhaulEngineAuxiliary;
	}

	@Column(name = "CHANGE_OIL_TRANSMISSION", columnDefinition = "varchar(50)")
	public String getChangeOilTransmission() {
		return changeOilTransmission;
	}

	public void setChangeOilTransmission(String changeOilTransmission) {
		this.changeOilTransmission = changeOilTransmission;
	}

	@Column(name = "FILTER_FUEL", columnDefinition = "varchar(50)")
	public String getFilterFuel() {
		return filterFuel;
	}

	public void setFilterFuel(String filterFuel) {
		this.filterFuel = filterFuel;
	}

	@Column(name = "FILTER_OIL", columnDefinition = "varchar(50)")
	public String getFilterOil() {
		return filterOil;
	}

	public void setFilterOil(String filterOil) {
		this.filterOil = filterOil;
	}

	@Column(name = "FILTER_AIR", columnDefinition = "varchar(50)")
	public String getFilterAir() {
		return filterAir;
	}

	public void setFilterAir(String filterAir) {
		this.filterAir = filterAir;
	}

	@Column(name = "FILTER_FANBELT", columnDefinition = "varchar(50)")
	public String getFilterFanbelt() {
		return filterFanbelt;
	}

	public void setFilterFanbelt(String filterFanbelt) {
		this.filterFanbelt = filterFanbelt;
	}

	@Column(name = "NET", columnDefinition = "varchar(50)")
	public String getNet() {
		return net;
	}

	public void setNet(String net) {
		this.net = net;
	}

	@Column(name = "ACTIVE", columnDefinition = "tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Column(name = "IS_SCHEDULE", columnDefinition = "tinyint(1)")
	public boolean isSchedule() {
		return isSchedule;
	}

	public void setSchedule(boolean isSchedule) {
		this.isSchedule = isSchedule;
	}

	@Column(name = "EB_OBJECT_ID", columnDefinition = "INT(10)")
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FleetPms [ebObjectId=").append(ebObjectId).append(", changeOilEngine=").append(changeOilEngine)
				.append(", overhaul_engine_main=").append(overhaulEngineMain).append(", overhaul_engine_auxiliary=")
				.append(overhaulEngineAuxiliary).append(", changeOilTransmission=").append(changeOilTransmission)
				.append(", filterFuel=").append(filterFuel).append(", filterOil=").append(filterOil)
				.append(", filterAir=").append(filterAir).append(", filterFanbelt=").append(filterFanbelt)
				.append(", net=").append(net).append(", active=").append(active).append(", isSchedule=")
				.append(isSchedule).append("]");
		return builder.toString();
	}

}
