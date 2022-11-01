package eulap.eb.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.EmployeeShiftAdditionalPayDao;
import eulap.eb.dao.EmployeeShiftDao;
import eulap.eb.dao.EmployeeShiftDayOffDao;
import eulap.eb.domain.hibernate.EmployeeShift;
import eulap.eb.domain.hibernate.EmployeeShiftAdditionalPay;
import eulap.eb.domain.hibernate.EmployeeShiftDayOff;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.DayOffDto;

/**
 * Class that handles the business logic / service layer of {@link EmployeeShift}

 *
 */
@Service
public class EmployeeShiftService {
	private static final Logger logger = Logger.getLogger(EmployeeShiftService.class);

	@Autowired
	private EmployeeShiftDao employeeShiftDao;
	@Autowired
	private EmployeeShiftAdditionalPayDao employeeShiftAdditionalPayDao;
	@Autowired
	private EmployeeShiftDayOffDao employeeShiftDayOffDao;

	/**
	 * Get the employee shift object by id.
	 * @param employeeShiftId The unique id of  {@link EmployeeShift} object.
	 * @return The employee shift object.
	 */
	public EmployeeShift getEmployeeShift(int employeeShiftId){
		EmployeeShift employeeShift = employeeShiftDao.get(employeeShiftId);
		if (employeeShift != null) {
			employeeShift.setEmployeeShiftAdditionalPay(
					employeeShiftAdditionalPayDao.getByShift(employeeShift.getId()));
		}
		return employeeShift;
	}

	/**
	 * Get the Day off dto by employee shift id.
	 * @param employeeShiftId The employee shift id.
	 * @return The day off dto.
	 */
	public DayOffDto getDayOffsByEmplyeeShiftId(int employeeShiftId){
		List<EmployeeShiftDayOff> employeeShiftDayOffs =
				employeeShiftDayOffDao.getAllByRefId(EmployeeShiftDayOff.FIELD.employeeShiftId.name(), employeeShiftId);
		DayOffDto dayOffDto = new DayOffDto();
		if(employeeShiftDayOffs != null){
			for (EmployeeShiftDayOff employeeShiftDayOff : employeeShiftDayOffs) {
				setDayOffDTO(dayOffDto, employeeShiftDayOff.getDayOfTheWeek());
			}
		}
		return dayOffDto;
	}

	/**
	 * Set day off dto.
	 * @param dayOffDto The day off dto.
	 * @param dayOfTheWeek The day of the week in integer value.
	 */
	private void setDayOffDTO(DayOffDto dayOffDto, Integer dayOfTheWeek){
		switch (dayOfTheWeek) {
		case Calendar.SUNDAY:
			dayOffDto.setSunday(true);
			break;
		case Calendar.MONDAY:
			dayOffDto.setMonday(true);
			break;
		case Calendar.TUESDAY:
			dayOffDto.setTuesday(true);
			break;
		case Calendar.WEDNESDAY:
			dayOffDto.setWednesday(true);
			break;
		case Calendar.THURSDAY:
			dayOffDto.setThursday(true);
			break;
		case Calendar.FRIDAY:
			dayOffDto.setFriday(true);
			break;
		case Calendar.SATURDAY:
			dayOffDto.setSaturday(true);
			break;
		default:
			throw new RuntimeException("No days of the week like '" + dayOfTheWeek + "' id.");
		}
	}

	/**
	 * Get the paged list of CSC's employee shift objects.
	 * @param companyId The company filter.
	 * @param name The name of the shift.
	 * @param firstHalfShiftStart The first half start of the shift.
	 * @param firstHalfShiftEnd The first half end of the shift.
	 * @param secondHalfShiftStart The second half start of the shift.
	 * @param secondHalfShiftEnd The second half end of the shift.
	 * @param dailyWorkingHours The daily working hours.
	 * @param status ALL, ACTIVE, or INACTIVE.
	 * @param pageNumber The page number.
	 * @param user The user currently log.
	 * @param pageSetting he page setting.
	 * @return The list of employee shifts of CSC.
	 */
	public Page<EmployeeShift> getCscEmployeeShifts (Integer companyId, String name, String firstHalfShiftStart, String firstHalfShiftEnd,
			String secondHalfShiftStart, String secondHalfShiftEnd,
			Double dailyWorkingHours, String status, int pageNumber, User user) {
		SearchStatus searchStatus = SearchStatus.getInstanceOf(status);
		Page<EmployeeShift> result = employeeShiftDao.getCscEmployeeShifts(companyId, name, firstHalfShiftStart, firstHalfShiftEnd, secondHalfShiftStart, 
				secondHalfShiftEnd, dailyWorkingHours, searchStatus, user, new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
		if (result.getDataSize() > 0) {
			for (EmployeeShift es : result.getData()) {
				es.setEmployeeShiftAdditionalPay(employeeShiftAdditionalPayDao.getByShift(es.getId()));
			}
		}
		return result;
	}

	public void saveEmployeeShift(User user, EmployeeShift employeeShift){
		logger.debug("Saving the CSC Employee Shift");
		boolean isNewRecord = employeeShift.getId() == 0;
		AuditUtil.addAudit(employeeShift, new Audit(user.getId(), isNewRecord, new Date()));
		employeeShift.setName(StringFormatUtil.removeExtraWhiteSpaces(
				employeeShift.getName().trim(), null).trim());
		employeeShift.setFirstHalfShiftStart(employeeShift.getFirstHalfShiftStart().trim());
		employeeShift.setSecondHalfShiftEnd(employeeShift.getSecondHalfShiftEnd().trim());
		employeeShift.setAllowableBreak(employeeShift.getAllowableBreak());
		employeeShift.setLateMultiplier(employeeShift.getLateMultiplier());
		employeeShiftDao.saveOrUpdate(employeeShift);
		EmployeeShiftAdditionalPay additionalPay = employeeShift.getEmployeeShiftAdditionalPay();
		additionalPay.setEmployeeShiftId(employeeShift.getId());
		employeeShiftDao.saveOrUpdate(additionalPay);
		saveEmployeeShiftDayOff(employeeShift, user);
		logger.debug("CSC Employee Shift successfully saved.");
	}

	/**
	 * Save employee shift day off.
	 * @param employeeShift The employee shift.
	 * @param user The user current logged.
	 */
	private void saveEmployeeShiftDayOff(EmployeeShift employeeShift, User user){
		DayOffDto dayOffDto = employeeShift.getDayOffDto();
		int employeeShiftId = employeeShift.getId();
		List<EmployeeShiftDayOff> employeeShiftDayOffs =
				employeeShiftDayOffDao.getAllByRefId(EmployeeShiftDayOff.FIELD.employeeShiftId.name(), employeeShiftId);
		if(employeeShiftDayOffs != null && !employeeShiftDayOffs.isEmpty()){
			for (EmployeeShiftDayOff employeeShiftDayOff : employeeShiftDayOffs) {
				employeeShiftDayOffDao.delete(employeeShiftDayOff);
			}
		}
		EmployeeShiftDayOff employeeShiftDayOff = null;
		if(dayOffDto.isSunday()){
			employeeShiftDayOff = new EmployeeShiftDayOff();
			saveDayOff(employeeShiftDayOff, user, employeeShiftId, Calendar.SUNDAY);
		}
		if(dayOffDto.isMonday()){
			employeeShiftDayOff = new EmployeeShiftDayOff();
			saveDayOff(employeeShiftDayOff, user, employeeShiftId, Calendar.MONDAY);
		}
		if(dayOffDto.isTuesday()){
			employeeShiftDayOff = new EmployeeShiftDayOff();
			saveDayOff(employeeShiftDayOff, user, employeeShiftId, Calendar.TUESDAY);
		}
		if(dayOffDto.isWednesday()){
			employeeShiftDayOff = new EmployeeShiftDayOff();
			saveDayOff(employeeShiftDayOff, user, employeeShiftId, Calendar.WEDNESDAY);
		}
		if(dayOffDto.isThursday()){
			employeeShiftDayOff = new EmployeeShiftDayOff();
			saveDayOff(employeeShiftDayOff, user, employeeShiftId, Calendar.THURSDAY);
		}
		if(dayOffDto.isFriday()){
			employeeShiftDayOff = new EmployeeShiftDayOff();
			saveDayOff(employeeShiftDayOff, user, employeeShiftId, Calendar.FRIDAY);
		}
		if(dayOffDto.isSaturday()){
			employeeShiftDayOff = new EmployeeShiftDayOff();
			saveDayOff(employeeShiftDayOff, user, employeeShiftId, Calendar.SATURDAY);
		}
	}

	/**
	 * Save day off.
	 * @param employeeShiftDayOff The employee shift day off.
	 * @param user The user current logged.
	 * @param employeeShiftId The employee shift.
	 * @param dayOftheWeek The day of the week integer value.
	 */
	private void saveDayOff(EmployeeShiftDayOff employeeShiftDayOff, User user, int employeeShiftId, int dayOftheWeek){
		employeeShiftDayOff.setEmployeeShiftId(employeeShiftId);
		employeeShiftDayOff.setDayOfTheWeek(dayOftheWeek);
		AuditUtil.addAudit(employeeShiftDayOff, new Audit(user.getId(), true, new Date()));
		employeeShiftDayOffDao.save(employeeShiftDayOff);
	}

	/**
	 * Get the list of employee shift and the selected employee shift if inactive.
	 * @param employeeShiftId The employee shift Id.
	 * @return The list of employee shift.
	 */
	public List<EmployeeShift> getEmployeeShifts(Integer companyId, Integer employeeShiftId) {
		List<EmployeeShift> employeeShifts = employeeShiftDao.getEmployeeShiftByCompanyId(companyId);
		if(employeeShiftId != null) {
			Collection<Integer> employeeShiftIds = new ArrayList<Integer>();
			for (EmployeeShift employeeShift : employeeShifts) {
				employeeShiftIds.add(employeeShift.getId());
			}
			if(!employeeShiftIds.contains(employeeShiftId)) {
				employeeShifts.add(employeeShiftDao.get(employeeShiftId));
			}
		}
		return employeeShifts;
	}

	/**
	 * Checks if the name is unique.
	 * @return True if name is unique, otherwise false.
	 */
	public boolean isUniqueName(EmployeeShift employeeShift) {
		return employeeShiftDao.isUniqueName(employeeShift);
	}

	/**
	 * Checks if there is already an active setting for a company.
	 * @param setting The PosMiddlewareSetting object.
	 * @return True if there is already existing, otherwise false.
	 */
	public boolean hasActive (EmployeeShift employeeShift) {
		boolean hasActive = employeeShiftDao.hasActive(employeeShift);
		if (hasActive && !employeeShift.isActive()) {
			return false;
		}
		return hasActive;
	}
}
