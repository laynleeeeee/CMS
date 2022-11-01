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
 * Object representation class of FLEET_CAPTAIN_MDM Table.

 *
 */
@Entity
@Table(name="FLEET_CAPTAIN_MDM")
public class FleetCaptainMdm extends BaseDomain{

	@Expose
	private Integer refObjectId;
	@Expose
	private Integer ebObjectId;
	private EBObject ebObject;
	@Expose
	private Date date;
	@Expose
	private String name;
	@Expose
	private String position;
	@Expose
	private String licenseNo;
	@Expose
	private Date licenseNoExpirationDate;
	@Expose
	private String seamansBook;
	@Expose
	private Date seamansBookExpirationDate;
	@Expose
	private String fisheries;
	@Expose
	private Date fishesriesExpirationDate;
	@Expose
	private String passport;
	@Expose
	private Date passportExpirationDate;
	@Expose
	private String remarks;
	@Expose
	private Boolean active;
	@Expose
	private Integer orTypeId;

	/**
	 * Object type id of Fleet Captain MDM = 93.
	 */
	public static final int OBJECT_TYPE_ID = 93;

	/**
	 * OR Type Id of Captain = 37.
	 */
	public static final int CAPTAIN_OR_TYPE_ID = 37;

	/**
	 * OR Type Id of Captain = 38.
	 */
	public static final int MDM_OR_TYPE_ID = 38;

	/**
	 * Or Type Id of Fleet Incident = 33.
	 */
	public static final int OR_TYPE_REF_DOC = 33;

	/**
	 * Max character for Fleet Incident fields = 100.
	 */
	public static final int MAX_CHAR = 100;

	public enum FIELD {
			id, refObjectId, ebObjectId, date, active, licenseNo, seamansBook, fisheries, passport
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "FLEET_CAPTAIN_MDM_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
	public int getId() {
		return super.getId();
	}

	@Transient
	public Integer getRefObjectId() {
		return refObjectId;
	}

	public void setRefObjectId(Integer refObjectId) {
		this.refObjectId = refObjectId;
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

	@Column(name="DATE", columnDefinition="DATE")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Column(name="NAME", columnDefinition="VARCHAR(100)")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name="POSITION", columnDefinition="VARCHAR(100)")
	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	@Column(name="LICENSE_NO", columnDefinition="VARCHAR(100)")
	public String getLicenseNo() {
		return licenseNo;
	}

	public void setLicenseNo(String licenseNo) {
		this.licenseNo = licenseNo;
	}

	@Column(name="LN_EXPIRATION_DATE", columnDefinition="DATE")
	public Date getLicenseNoExpirationDate() {
		return licenseNoExpirationDate;
	}

	public void setLicenseNoExpirationDate(Date licenseNoExpirationDate) {
		this.licenseNoExpirationDate = licenseNoExpirationDate;
	}

	@Column(name="SEAMANS_BOOK", columnDefinition="VARCHAR(100)")
	public String getSeamansBook() {
		return seamansBook;
	}

	public void setSeamansBook(String seamansBook) {
		this.seamansBook = seamansBook;
	}

	@Column(name="SB_EXPIRATION_DATE", columnDefinition="DATE")
	public Date getSeamansBookExpirationDate() {
		return seamansBookExpirationDate;
	}

	public void setSeamansBookExpirationDate(Date seamansBookExpirationDate) {
		this.seamansBookExpirationDate = seamansBookExpirationDate;
	}

	@Column(name="FISHERIES", columnDefinition="VARCHAR(100)")
	public String getFisheries() {
		return fisheries;
	}

	public void setFisheries(String fisheries) {
		this.fisheries = fisheries;
	}

	@Column(name="FL_EXPIRATION_DATE", columnDefinition="DATE")
	public Date getFishesriesExpirationDate() {
		return fishesriesExpirationDate;
	}

	public void setFishesriesExpirationDate(Date fishesriesExpirationDate) {
		this.fishesriesExpirationDate = fishesriesExpirationDate;
	}

	@Column(name="PASSPORT", columnDefinition="VARCHAR(100)")
	public String getPassport() {
		return passport;
	}

	public void setPassport(String passport) {
		this.passport = passport;
	}

	@Column(name="P_EXPIRATION_DATE", columnDefinition="DATE")
	public Date getPassportExpirationDate() {
		return passportExpirationDate;
	}

	public void setPassportExpirationDate(Date passportExpirationDate) {
		this.passportExpirationDate = passportExpirationDate;
	}

	@Column(name="REMARKS", columnDefinition="TEXT")
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Column(name="ACTIVE", columnDefinition="TINYINT(1)")
	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	@Override
	@Column(name = "CREATED_BY", columnDefinition="INT(10)")
	public int getCreatedBy() {
		return super.getCreatedBy();
	}

	@Override
	@Column(name = "CREATED_DATE", columnDefinition="TIMESTAMP")
	public Date getCreatedDate(){
		return super.getCreatedDate();
	}

	@Override
	@Column(name = "UPDATED_BY", columnDefinition="INT(10)")
	public int getUpdatedBy() {
		return super.getUpdatedBy();
	}

	@Override
	@Column(name = "UPDATED_DATE", columnDefinition="TIMESTAMP")
	public Date getUpdatedDate() {
		return super.getUpdatedDate();
	}

	@Transient
	public Integer getOrTypeId() {
		return orTypeId;
	}

	public void setOrTypeId(Integer orTypeId) {
		this.orTypeId = orTypeId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FleetCaptainMdm [refObjectId=").append(refObjectId).append(", ebObjectId=").append(ebObjectId)
				.append(", ebObject=").append(ebObject).append(", date=").append(date).append(", position=")
				.append(position).append(", licenseNo=").append(licenseNo).append(", licenseNoExpirationDate=")
				.append(licenseNoExpirationDate).append(", seamansBook=").append(seamansBook)
				.append(", seamansBookExpirationDate=").append(seamansBookExpirationDate).append(", fishesries=")
				.append(fisheries).append(", fishesriesExpirationDate=").append(fishesriesExpirationDate)
				.append(", passport=").append(passport).append(", passportExpirationDate=")
				.append(passportExpirationDate).append(", remarks=").append(remarks).append(", active=").append(active)
				.append("]");
		return builder.toString();
	}
}
