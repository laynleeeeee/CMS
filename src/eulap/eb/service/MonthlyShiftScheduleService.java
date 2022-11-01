package eulap.eb.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.EmployeeDao;
import eulap.eb.dao.EmployeeShiftDao;
import eulap.eb.dao.MonthlyShiftScheduleDao;
import eulap.eb.dao.MonthlyShiftScheduleLineDao;
import eulap.eb.dao.PayrollTimePeriodScheduleDao;
import eulap.eb.domain.hibernate.Employee;
import eulap.eb.domain.hibernate.EmployeeShift;
import eulap.eb.domain.hibernate.MonthlyShiftSchedule;
import eulap.eb.domain.hibernate.MonthlyShiftScheduleLine;
import eulap.eb.domain.hibernate.PayrollTimePeriodSchedule;
import eulap.eb.domain.hibernate.User;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.validator.ValidatorUtil;
import eulap.eb.web.dto.DSSLineDto;
import eulap.eb.web.dto.MonthlyShiftScheduleDto;

/**
 * Handles business logic of {@link MonthlyShiftSchedule}

 *
 */
@Service
public class MonthlyShiftScheduleService {

	@Autowired
	private MonthlyShiftScheduleDao moDao;
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private EmployeeShiftDao employeeShiftDao;
	@Autowired
	private MonthlyShiftScheduleLineDao moLineDao;
	@Autowired
	private PayrollTimePeriodScheduleDao scheduleDao;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private EmployeeShiftService employeeShiftService;

	/**
	 * Get the Monthly Shift Schedule Object
	 * @param pId The Id of the Monthly Shift Schedule
	 * @return The Monthly Shift Schedule Object
	 */
	public MonthlyShiftSchedule getMonthlySchedule(Integer pId){
		List<MonthlyShiftScheduleDto> toBeRemovedLine = new ArrayList<>();
		MonthlyShiftSchedule monthlyShiftSchedule = moDao.get(pId);
		setEmployeeSchedule(monthlyShiftSchedule);
		for(MonthlyShiftScheduleDto mDto : monthlyShiftSchedule.getMonthlyShiftScheduleDtos()) {
			if(!mDto.getEmployee().isActive()) {
				toBeRemovedLine.add(mDto);
			}
		}
		monthlyShiftSchedule.getMonthlyShiftScheduleDtos().removeAll(toBeRemovedLine);
		return monthlyShiftSchedule;
	}

	/**
	 * Set the Monthly Schedule of Employee
	 * @param mSchedule The Monthly Shift Schedule Object
	 */
	private void setEmployeeSchedule(MonthlyShiftSchedule mSchedule){
		List<MonthlyShiftScheduleLine> monthlyShiftScheduleLines =  moLineDao.getAllByRefId("monthlyShiftScheduleId", mSchedule.getId());
		List<MonthlyShiftScheduleDto> monthlyShiftScheduleDtos = new ArrayList<>();
		MonthlyShiftScheduleDto monthlyShiftScheduleDto = new MonthlyShiftScheduleDto();
		Employee employee = null;
		EmployeeShift employeeShift = null;
		List<DSSLineDto> dssLineDtos = null;
		DSSLineDto dssLineDto = null;
		Map<Integer, MonthlyShiftScheduleDto> map = new HashMap<>();
		Integer employeeShiftId = null;
		Integer employeeId = null;
		boolean isEdit = mSchedule.getId() != 0;

		if(isEdit){
			List<MonthlyShiftScheduleLine> newEmployeeScheds = 
					addSchedForNewEmployees(mSchedule.getCompanyId(), mSchedule.getPayrollTimePeriodScheduleId());
			if(!newEmployeeScheds.isEmpty()){
				monthlyShiftScheduleLines.addAll(newEmployeeScheds);
			}
		}

		for(MonthlyShiftScheduleLine monthlyShiftScheduleLine : monthlyShiftScheduleLines){
			employeeShiftId = monthlyShiftScheduleLine.getEmployeeShiftId();
			employeeShift = employeeShiftDao.get(employeeShiftId);
			monthlyShiftScheduleDto = new MonthlyShiftScheduleDto();
			employeeId = monthlyShiftScheduleLine.getEmployeeId();
			employee = employeeDao.get(employeeId);
			monthlyShiftScheduleLine.setEmployee(employee);
			monthlyShiftScheduleDto.setEmployee(employee);
			monthlyShiftScheduleDto.setEmployeeId(employeeId);
			monthlyShiftScheduleDto.setEmployeeShifts(getEmployeeShifts(mSchedule.getCompanyId(), employeeShiftId));
			dssLineDto = new DSSLineDto();
			dssLineDto.setEmployeeShiftId(employeeShiftId);
			dssLineDto.setShiftName(employeeShift.getName());

			if(map.containsKey(employeeId)){
				monthlyShiftScheduleDto = map.get(monthlyShiftScheduleLine.getEmployeeId());
				dssLineDtos = monthlyShiftScheduleDto.getDssLineDtos();
				dssLineDtos.add(dssLineDto);
				map.put(employeeId, monthlyShiftScheduleDto);
			}else {
				dssLineDtos = new ArrayList<>();
				dssLineDtos.add(dssLineDto);
				monthlyShiftScheduleDto.setDssLineDtos(dssLineDtos);
				map.put(employeeId, monthlyShiftScheduleDto);
			}
		}

		if(!map.isEmpty()){
			monthlyShiftScheduleDtos = new ArrayList<>(map.values());
		}
		Collections.sort(monthlyShiftScheduleDtos, new Comparator<MonthlyShiftScheduleDto>() {

			@Override
			public int compare(MonthlyShiftScheduleDto o1, MonthlyShiftScheduleDto o2) {
				return o1.getEmployee().getFullName().compareTo(o2.getEmployee().getFullName());
			}
		});
		mSchedule.setMonthlyShiftScheduleDtos(monthlyShiftScheduleDtos);
	}

	/**
	 * Get the list of employee shifts.
	 * @param companyId
	 * @param employeeShiftId
	 * @return
	 */
	private List<EmployeeShift> getEmployeeShifts(Integer companyId, Integer employeeShiftId){
		List<EmployeeShift> employeeShifts = new ArrayList<>();
		if(employeeShiftId != null){
			employeeShifts.add(employeeShiftDao.get(employeeShiftId));
			for(EmployeeShift employeeShift : employeeShiftService.getEmployeeShifts(companyId, null)){
				if(employeeShift.getId() != employeeShiftId){
					employeeShifts.add(employeeShift);
				}
			}
		}
		return employeeShifts;
	}

	/**
	 * Add the schedule for new Employees
	 * @param companyId The Id of the company
	 * @param payrollTimePeriodScheduleId The Payroll TimePeriod Schedule Id
	 * @return The list of MonthlyShiftScheduleLines
	 */
	private List<MonthlyShiftScheduleLine> addSchedForNewEmployees(Integer companyId, 
			Integer payrollTimePeriodScheduleId){
		List<MonthlyShiftScheduleLine> mssLine = new ArrayList<>();
		List<Employee> employees = employeeDao.getNoMonthlySchedule(companyId, payrollTimePeriodScheduleId);
		PayrollTimePeriodSchedule schedule = scheduleDao.get(payrollTimePeriodScheduleId);
		if(schedule != null){
			if(employees != null && !employees.isEmpty()){
				List<EmployeeShift> shifts = employeeShiftDao.getAllActive();
				MonthlyShiftScheduleLine mssl = null;
				for(Employee e : employees){
					mssl = new MonthlyShiftScheduleLine();
					mssl.setEmployee(e);
					mssl.setEmployeeId(e.getId());
					mssl.setEmployeeShiftId(shifts.iterator().next().getId());
					mssLine.add(mssl);
				}
			}
		}
		return mssLine;
	}

	/**
	 * Evaluates whether the schedule is unique
	 * @param shiftSchedule the MonthlyShiftSchedule
	 * @return true if a duplicate has been detected, else false.
	 */
	public boolean isUniqueSchedule(MonthlyShiftSchedule shiftSchedule) {
		return moDao.isUniqueSchedule(shiftSchedule);
	}

	/**
	 * Get the MonthlyShiftScheduleLines
	 * @param companyId The company id
	 * @param month The month
	 * @param year	 The year
	 * @param user The current logged user
	 * @param pageNumber The page number
	 * @return The Paged collection of MonthlyShiftScheduleLines
	 */
	public Page<MonthlyShiftSchedule> getMonthlyShiftScheduleLines(Integer companyId, Integer month, Integer year, String status,
			User user,Integer pageNumber) {
		SearchStatus searchStatus = SearchStatus.getInstanceOf(status);
		Page<MonthlyShiftSchedule> monthlySchedules = 
				moDao.getMonthlyShiftScheduleLines(companyId, month, year, searchStatus, user, new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
		List<MonthlyShiftScheduleLine> scheduleLines = null;
		List<MonthlyShiftScheduleLine> toRemoveLines = new ArrayList<>();
		int employeeId = 0;
		int count = 0;
		for(MonthlyShiftSchedule mss : monthlySchedules.getData()){
			scheduleLines = moLineDao.getAllByRefId("monthlyShiftScheduleId", mss.getId());
			count = 0;
			employeeId = 0;
			for(MonthlyShiftScheduleLine monthlyShiftScheduleLine : scheduleLines){
				if(employeeId != monthlyShiftScheduleLine.getEmployeeId()){
					employeeId = monthlyShiftScheduleLine.getEmployeeId();
					count = 0;
				}
				if(count == 0){
					monthlyShiftScheduleLine.setEmployee(employeeDao.get(employeeId));
				}
				monthlyShiftScheduleLine.setEmployeeShift(employeeShiftDao.get(monthlyShiftScheduleLine.getEmployeeShiftId()));
				count++;
				
				if(!monthlyShiftScheduleLine.getEmployee().isActive()) {
					toRemoveLines.add(monthlyShiftScheduleLine);
				}
			}
			scheduleLines.removeAll(toRemoveLines);
			
			Collections.sort(scheduleLines, new Comparator<MonthlyShiftScheduleLine>() {

				@Override
				public int compare(MonthlyShiftScheduleLine o1, MonthlyShiftScheduleLine o2) {
					// TODO Auto-generated method stub
					return o1.getEmployee().getFullName().compareTo(o2.getEmployee().getFullName());
				}
			});
			mss.setMonthlyShiftScheduleLines(scheduleLines);
		}
		return monthlySchedules;
	}

	/**
	 * Initialize Employee Schedules
	 * @param companyId The company Id
	 * @return The list of MonthlyShiftScheduleDtos
	 */
	public List<MonthlyShiftScheduleDto> initEmployeeSchedSheet(Integer companyId){
		List<MonthlyShiftScheduleDto> monthlyShiftScheduleDtos = new ArrayList<>();
		List<Employee> employees = employeeDao.getEmployees(companyId, null, null);
		MonthlyShiftScheduleDto dto = null;
		for(Employee employee : employees){
			dto = new MonthlyShiftScheduleDto();
			dto.setEmployee(employee);
			dto.setEmployeeId(employee.getId());
			dto.setEmployeeShifts(employeeShiftDao.getEmployeeShiftByCompanyId(companyId));
			monthlyShiftScheduleDtos.add(dto);
		}
		return monthlyShiftScheduleDtos;
	}

	/**
	 * Save the MonthlyShiftSchedule
	 * @param user The current logged user
	 * @param monthlyShiftSchedule MonthlyShiftSchedule to be saved.
	 */
	public void saveMonthlyShiftSched(User user, MonthlyShiftSchedule monthlyShiftSchedule){
		boolean isNew = monthlyShiftSchedule.getId() == 0;
		AuditUtil.addAudit(monthlyShiftSchedule, new Audit(user.getId(), isNew, new Date()));
		moDao.saveOrUpdate(monthlyShiftSchedule);

		List<MonthlyShiftScheduleDto> dtos = monthlyShiftSchedule.getMonthlyShiftScheduleDtos();

		// Delete the previously saved MonthlyShiftScheduleLines
		if(!isNew){
			List<MonthlyShiftScheduleLine> moLines = moLineDao.getAllByRefId("monthlyShiftScheduleId", monthlyShiftSchedule.getId());
			for(MonthlyShiftScheduleLine monthlyShiftScheduleLine : moLines){
				moLineDao.delete(monthlyShiftScheduleLine);
			}
		}

		MonthlyShiftScheduleLine monthlyShiftScheduleLine = null;
		for(MonthlyShiftScheduleDto moDto : dtos){
			for(DSSLineDto dssLineDto : moDto.getDssLineDtos()){
				monthlyShiftScheduleLine = new MonthlyShiftScheduleLine();
				monthlyShiftScheduleLine.setEmployeeId(moDto.getEmployeeId());
				monthlyShiftScheduleLine.setEmployeeShiftId(dssLineDto.getEmployeeShiftId());
				monthlyShiftScheduleLine.setMonthlyShiftScheduleId(monthlyShiftSchedule.getId());
				moLineDao.save(monthlyShiftScheduleLine);
			}
		}
	}

	/**
	 * Handles validation of MonthlyShiftSchedule
	 * @param monthlyShiftSchedule The MonthlyShiftSchedule to be validated.
	 * @param errors The errors detected.
	 */
	public void validate(MonthlyShiftSchedule monthlyShiftSchedule, Errors errors){
		if(monthlyShiftSchedule != null){

			ValidatorUtil.validateCompany(monthlyShiftSchedule.getCompanyId(), companyService, errors, "companyId");

			if(monthlyShiftSchedule.getMonth() == null || monthlyShiftSchedule.getYear() == null){
				errors.rejectValue("payrollTimePeriodScheduleId", null, null, ValidatorMessages.getString("MonthlyShiftScheduleService.0"));
			}

			if(monthlyShiftSchedule.getPayrollTimePeriodScheduleId() == null){
				errors.rejectValue("payrollTimePeriodScheduleId", null, null, ValidatorMessages.getString("MonthlyShiftScheduleService.1"));
			}

			if(monthlyShiftSchedule.getMonthlyShiftScheduleDtos() == null || monthlyShiftSchedule.getMonthlyShiftScheduleDtos().isEmpty()){
				errors.rejectValue("monthlyShiftScheduleDtos", null, null, ValidatorMessages.getString("MonthlyShiftScheduleService.2"));
			}

			if(!isUniqueSchedule(monthlyShiftSchedule) && monthlyShiftSchedule.getPayrollTimePeriodScheduleId() != null){
				errors.rejectValue("payrollTimePeriodScheduleId", null, null, ValidatorMessages.getString("MonthlyShiftScheduleService.3"));
			}

			if(monthlyShiftSchedule.getMonthlyShiftScheduleDtos() != null) {
				boolean hasError = false;
				for(MonthlyShiftScheduleDto dto : monthlyShiftSchedule.getMonthlyShiftScheduleDtos()) {
					for(DSSLineDto dssDto : dto.getDssLineDtos()) {
						if(dssDto.getEmployeeShiftId() == null) {
							hasError = true;
						}
					}
				}
				if(hasError == true) {
					errors.rejectValue("monthlyShiftScheduleDtos", null, null, ValidatorMessages.getString("MonthlyShiftScheduleService.4"));
				}
			}
		}
	}

}
