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

import eulap.common.domain.BaseDomain;

/**
 * Object class that represents HOLIDAY_SETTING table from the database

 *
 */

@Entity
@Table(name="HOLIDAY_SETTING")
public class HolidaySetting extends BaseDomain{

	private Integer companyId;
	private String name;
	private Date date;
	private boolean active;
	private Integer holidayTypeId;
	private HolidayType holidayType;
	private Company company;

	public static final int MAX_NAME = 50;

	public enum FIELD{
		id,companyId, holidayTypeId, name, date, active
	}

	@Id
	@Override
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "HOLIDAY_SETTING_ID", unique = true, nullable = false, insertable = false, updatable = false)
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

	@OneToOne
	@JoinColumn(name = "COMPANY_ID", insertable = false, updatable = false)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Column(name = "COMPANY_ID", columnDefinition="int(10)")
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@Column(name = "NAME", columnDefinition="varchar(50)")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "DATE", columnDefinition="date")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	@Column(name = "ACTIVE", columnDefinition="tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Column(name = "HOLIDAY_TYPE_ID", columnDefinition="int(10)")
	public Integer getHolidayTypeId() {
		return holidayTypeId;
	}

	public void setHolidayTypeId(Integer holidayTypeId) {
		this.holidayTypeId = holidayTypeId;
	}

	@OneToOne
	@JoinColumn(name = "HOLIDAY_TYPE_ID", insertable = false, updatable = false)
	public HolidayType getHolidayType() {
		return holidayType;
	}

	public void setHolidayType(HolidayType holidayType) {
		this.holidayType = holidayType;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HolidaySetting [companyId=").append(companyId)
				.append(", name=").append(name).append(", date=").append(date)
				.append(", active=").append(active).append(", company=")
				.append(company).append(", holidayTypeId=")
				.append(holidayTypeId).append(", getId()=").append(getId())
				.append("]");
		return builder.toString();
	}

}
