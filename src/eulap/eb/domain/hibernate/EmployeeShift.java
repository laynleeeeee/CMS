package eulap.eb.domain.hibernate;

import java.util.Date;
import java.util.List;

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
import eulap.eb.web.dto.DayOffDto;

/**
 * Class representation of EMPLOYEE_SHIFT table from the database.


 */
@Entity
@Table(name="EMPLOYEE_SHIFT")
public class EmployeeShift extends BaseDomain{
	private String name;
	private String firstHalfShiftStart;
	private String firstHalfShiftEnd;
	private String secondHalfShiftStart;
	private String secondHalfShiftEnd;
	private Double allowableBreak;
	private Double lateMultiplier;
	private boolean nightShift;
	private Double dailyWorkingHours;
	private boolean active;
	private String workingDays;
	private Integer totalWorkingDays;
	private List<EmployeeShiftWorkingDay> employeeShiftWorkingDays;
	private List<EmployeeShiftDayOff> employeeShiftDayOffs;
	private EmployeeShiftAdditionalPay employeeShiftAdditionalPay;
	private Integer companyId;
	private Company company;
	private Integer dayOff;
	private DayOffDto dayOffDto;

	public static final int MAX_NAME = 50;
	public static final int MAX_HOUR = 24;
	public static final double MAX_PERCENTAGE = 99.99;

	public enum FIELD {
		id, name, firstHalfShiftStart, firstHalfShiftEnd, secondHalfShiftStart,secondHalfShiftEnd,
		allowableBreak, lateMultiplier, nightShift, dailyWorkingHours, active, companyId, updatedDate, dayOff
	}

	@Override
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	@Column(name = "EMPLOYEE_SHIFT_ID", columnDefinition = "INT(10)", unique = true, nullable = false, insertable = false, updatable = false)
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

	@Column(name = "COMPANY_ID", columnDefinition="int(10)")
	public Integer getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}

	@OneToOne
	@JoinColumn(name = "COMPANY_ID", insertable = false, updatable = false)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Column(name = "NAME", columnDefinition = "VARCHAR(50)", nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the first half start time of shift of the employee.
	 * @return The first half start shift.
	 */
	@Column(name = "FIRST_HALF_SHIFT_START", columnDefinition = "VARCHAR(5)", nullable = false)
	public String getFirstHalfShiftStart() {
		return firstHalfShiftStart;
	}

	public void setFirstHalfShiftStart(String firstHalfShiftStart) {
		this.firstHalfShiftStart = firstHalfShiftStart;
	}
	/**
	 * Get the first end time of shift of the employee.
	 * @return The first half end shift.
	 */
	@Column(name = "FIRST_HALF_SHIFT_END", columnDefinition = "VARCHAR(5)")
	public String getFirstHalfShiftEnd() {
		return firstHalfShiftEnd;
	}

	public void setFirstHalfShiftEnd(String firstHalfShiftEnd) {
		this.firstHalfShiftEnd = firstHalfShiftEnd;
	}

	/**
	 * Get the second half start time shift of the employee.
	 * @return The second half start shift.
	 */
	@Column(name = "SECOND_HALF_SHIFT_START", columnDefinition = "VARCHAR(5)")
	public String getSecondHalfShiftStart() {
		return secondHalfShiftStart;
	}

	public void setSecondHalfShiftStart(String secondHalfShiftStart) {
		this.secondHalfShiftStart = secondHalfShiftStart;
	}

	/**
	 * Get the second half end time shift of the employee.
	 * @return The second half end shift.
	 */
	@Column(name = "SECOND_HALF_SHIFT_END", columnDefinition = "VARCHAR(5)", nullable = false)
	public String getSecondHalfShiftEnd() {
		return secondHalfShiftEnd;
	}

	public void setSecondHalfShiftEnd(String secondHalfShiftEnd) {
		this.secondHalfShiftEnd = secondHalfShiftEnd;
	}

	/**
	 * Get the allowable break time of the employee.
	 * @return The allowable break.
	 */
	@Column(name = "ALLOWABLE_BREAK", columnDefinition = "DOUBLE", nullable = false)
	public Double getAllowableBreak() {
		return allowableBreak;
	}

	public void setAllowableBreak(Double allowableBreak) {
		this.allowableBreak = allowableBreak;
	}

	/**
	 * Get the multiplier to deduct the late from the salary of the employee.
	 * @return The late multiplier.
	 */
	@Column(name = "LATE_MULTIPLIER", columnDefinition = "DOUBLE", nullable = true)
	public Double getLateMultiplier() {
		return lateMultiplier;
	}

	public void setLateMultiplier(Double lateMultiplier) {
		this.lateMultiplier = lateMultiplier;
	}

	/**
	 * Set to true if the shift is a night shift.
	 * @return True if night shift, otherwise false.
	 */
	@Column(name = "NIGHT_SHIFT", columnDefinition = "tinyint(1)", nullable = false)
	public boolean isNightShift() {
		return nightShift;
	}

	public void setNightShift(boolean nightShift) {
		this.nightShift = nightShift;
	}

	@Column(name = "DAILY_WORKING_HOURS", columnDefinition = "double(4, 2)", nullable = false)
	public Double getDailyWorkingHours() {
		return dailyWorkingHours;
	}

	public void setDailyWorkingHours(Double dailyWorkingHours) {
		this.dailyWorkingHours = dailyWorkingHours;
	}

	@Column(name="ACTIVE", columnDefinition="tinyint(1)")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Transient
	public String getWorkingDays() {
		return workingDays;
	}

	public void setWorkingDays(String workingDays) {
		this.workingDays = workingDays;
	}

	@Transient
	public List<EmployeeShiftWorkingDay> getEmployeeShiftWorkingDays() {
		return employeeShiftWorkingDays;
	}

	public void setEmployeeShiftWorkingDays(List<EmployeeShiftWorkingDay> employeeShiftWorkingDays) {
		this.employeeShiftWorkingDays = employeeShiftWorkingDays;
	}

	@OneToOne
	@JoinColumn(name="EMPLOYEE_SHIFT_ID", insertable = false, updatable = false)
	public EmployeeShiftAdditionalPay getEmployeeShiftAdditionalPay() {
		return employeeShiftAdditionalPay;
	}

	public void setEmployeeShiftAdditionalPay(EmployeeShiftAdditionalPay employeeShiftAdditionalPay) {
		this.employeeShiftAdditionalPay = employeeShiftAdditionalPay;
	}

	@Transient
	public Integer getTotalWorkingDays() {
		return totalWorkingDays;
	}

	public void setTotalWorkingDays(Integer totalWorkingDays) {
		this.totalWorkingDays = totalWorkingDays;
	}

	/**
	 * @return the dayOff
	 */
	@Column(name = "DAY_OFF", columnDefinition = "tinyint(1)", nullable = true)
	public Integer getDayOff() {
		return dayOff;
	}

	/**
	 * @param dayOff the dayOff to set
	 */
	public void setDayOff(Integer dayOff) {
		this.dayOff = dayOff;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EmployeeShift [firstHalfShiftStart=")
				.append(firstHalfShiftStart).append(", firstHalfShiftEnd=")
				.append(firstHalfShiftEnd).append(", secondHalfShiftStart=")
				.append(secondHalfShiftStart).append(", secondHalfShiftEnd=")
				.append(secondHalfShiftEnd).append(", allowableBreak=")
				.append(allowableBreak).append(", lateMultiplier=")
				.append(lateMultiplier).append(", nightShift=")
				.append(nightShift).append(", dailyWorkingHours=")
				.append(dailyWorkingHours).append(", active=").append(active)
				.append(", workingDays=").append(workingDays)
				.append(", totalWorkingDays=").append(totalWorkingDays)
				.append(", employeeShiftWorkingDays=")
				.append(employeeShiftWorkingDays).append(", companyId=")
				.append(companyId).append("]");
		return builder.toString();
	}

	@Transient
	public List<EmployeeShiftDayOff> getEmployeeShiftDayOffs() {
		return employeeShiftDayOffs;
	}

	public void setEmployeeShiftDayOffs(List<EmployeeShiftDayOff> employeeShiftDayOffs) {
		this.employeeShiftDayOffs = employeeShiftDayOffs;
	}

	@Transient
	public DayOffDto getDayOffDto() {
		return dayOffDto;
	}

	public void setDayOffDto(DayOffDto dayOffDto) {
		this.dayOffDto = dayOffDto;
	}
}
