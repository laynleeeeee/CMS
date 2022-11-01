package eulap.eb.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.eb.dao.EmployeeDtrDao;
import eulap.eb.dao.EmployeeLeaveCreditLineDao;
import eulap.eb.dao.EmployeeRequestDao;
import eulap.eb.dao.EmployeeShiftDao;
import eulap.eb.dao.LeaveDetailDao;
import eulap.eb.dao.PersonnelActionNoticeDao;
import eulap.eb.domain.hibernate.EmployeeDtr;
import eulap.eb.domain.hibernate.EmployeeRequest;
import eulap.eb.domain.hibernate.EmployeeShift;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.LeaveDetail;
import eulap.eb.domain.hibernate.PersonnelActionNotice;
import eulap.eb.web.dto.ActionNoticeDto;
import eulap.eb.web.dto.AvailableLeavesDto;
import eulap.eb.web.dto.DTRDailyShiftScheduleDto;
import eulap.eb.web.dto.DTRMonthlyScheduleDto;
import eulap.eb.web.dto.EmployeeLeaveDto;
import eulap.eb.web.dto.EmployeeShiftDto;

/**
 * Handles business logic of {@link EmployeeDtr}

 *
 */
@Service
public class EmployeeDtrService {
	private static Logger logger = Logger.getLogger(EmployeeDtrService.class);
	@Autowired
	private EmployeeDtrDao dtrDao;
	@Autowired
	private EmployeeLeaveCreditLineDao creditLineDao;
	@Autowired
	private PersonnelActionNoticeDao personnelActionNoticeDao;
	@Autowired
	private EmployeeShiftDao employeeShiftDao;
	@Autowired
	private LeaveDetailDao leaveDetailDao;
	@Autowired
	private EmployeeRequestDao employeeRequestDao;
	@Autowired
	private EmployeeRequestService employeeRequestService;

	/**
	 * Save employee dtr.
	 * @param employeeId The employe id.
	 * @param logTime The employee log time.
	 * @param locationId The location id.
	 * @throws ParseException
	 */
	public void save(Integer employeeId, String logTime, Integer locationId) throws ParseException {
		logger.info("Saving employee dtr: " + "Employee ID: " + employeeId + " LOGTIME: " + logTime);
		logTime = logTime.replaceAll(";", " ");
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		EmployeeDtr employeeDtr = new EmployeeDtr();
		employeeDtr.setEmployeeId(employeeId);
		employeeDtr.setIsSynchronize(true);
		employeeDtr.setLocationId(locationId);
		employeeDtr.setLogTime(format.parse(logTime));
		employeeDtr.setActive(true);
		dtrDao.save(employeeDtr);
		logger.info("Sucessfully saved employee dtr: " + "Employee ID: " + employeeId + " LOGTIME: " + logTime);
	}

	/**
	 * Get the list of latest employee available leaves.
	 * @param erUpdatedDate The updated date of employee request.
	 * @param elsUpdatedDate The updated date of leave credits.
	 * @return The list of available leaves.
	 */
	public List<AvailableLeavesDto> getLatestAvailableLeaves(Date erUpdatedDate, Date elsUpdatedDate) {
		return creditLineDao.getEmpAvailableLeaves(erUpdatedDate, elsUpdatedDate);
	}

	/**
	 * Get the list of the latest employees daily shift.
	 * @param updatedDate The last updated date.
	 * @return The list of daily shift schedule.
	 */
	public List<DTRDailyShiftScheduleDto> getLatestDailyShifts(Date updatedDate) {
		return dtrDao.getLatestDailyShifts(updatedDate);
	}

	/**
	 * Get the list of the latest action notice.
	 * @param updatedDate The updated date.
	 * @return The list action notice.
	 */
	public List<ActionNoticeDto> getActionNotices(Date updatedDate) {
		List<ActionNoticeDto> actionNoticeDtos = new ArrayList<>();
		List<PersonnelActionNotice> personnelActionNotices = personnelActionNoticeDao.getLatestActionNotices(updatedDate);
		ActionNoticeDto actionNoticeDto = null;
		for (PersonnelActionNotice personnelActionNotice : personnelActionNotices) {
			actionNoticeDto = new ActionNoticeDto();
			actionNoticeDto.setActionNoticeId(personnelActionNotice.getId());
			actionNoticeDto.setActionNoticeTypeId(personnelActionNotice.getActionNoticeId());
			actionNoticeDto.setEmployeeId(personnelActionNotice.getEmployeeId());
			actionNoticeDto.setActionNoticeDate(personnelActionNotice.getDate());
			List<FormWorkflowLog> formWorkflowLogs = personnelActionNotice.getFormWorkflow().getFormWorkflowLogs();
			int statusId = 0;
			for (FormWorkflowLog formWorkflowLog : formWorkflowLogs) {
				statusId = personnelActionNotice.getFormWorkflow().getCurrentStatusId();
				if(formWorkflowLog.getFormStatusId() == statusId){
					actionNoticeDto.setUpdatedDate(formWorkflowLog.getCreatedDate());
					actionNoticeDto.setStatusId(statusId);
				}
			}
			actionNoticeDtos.add(actionNoticeDto);
		}
		return actionNoticeDtos;
	}

	/**
	 * Get the list of the latest employee shift base on updated date.
	 * @param updatedDate The updated date.
	 * @return The list of employee shift.
	 */
	public List<EmployeeShiftDto> getLatestShifts(Date updatedDate) {
		List<EmployeeShift> employeeShifts = employeeShiftDao.getLatestEmployeeShift(updatedDate);
		List<EmployeeShiftDto> employeeShiftDtos = new ArrayList<>();
		EmployeeShiftDto employeeShiftDto = null;
		for (EmployeeShift employeeShift : employeeShifts) {
			employeeShiftDto = new EmployeeShiftDto();
			employeeShiftDto.setEmployeeShiftId(employeeShift.getId());
			employeeShiftDto.setActive(employeeShift.isActive());
			employeeShiftDto.setCompanyId(employeeShift.getCompanyId());
			employeeShiftDto.setUpdatedDate(employeeShift.getUpdatedDate());
			employeeShiftDto.setCreatedDate(employeeShift.getCreatedDate());
			employeeShiftDto.setNightShift(employeeShift.isNightShift());
			employeeShiftDto.setDailyWorkingHours(employeeShift.getDailyWorkingHours());
			employeeShiftDto.setUserByCreatedBy(employeeShift.getCreatedBy());
			employeeShiftDto.setUserByUpdatedBy(employeeShift.getUpdatedBy());
			employeeShiftDto.setStartShift(employeeShift.getFirstHalfShiftStart());
			employeeShiftDto.setEndShift(employeeShift.getSecondHalfShiftEnd());
			employeeShiftDto.setDayOff(employeeShift.getDayOff());
			employeeShiftDtos.add(employeeShiftDto);
		}
		return employeeShiftDtos;
	}

	/**
	 * Get the list of the latest employee leaves base on updated date.
	 * @param updatedDate The updated date.
	 * @return The list of employee leaves.
	 */
	public List<EmployeeLeaveDto> getEmployeeLeaves(Date updatedDate) {
		List<EmployeeLeaveDto> employeeLeaveDtos = new ArrayList<>();
		List<LeaveDetail> leaveDetails = leaveDetailDao.getEmployeeLeavesByFormDate(updatedDate);
		EmployeeLeaveDto employeeLeaveDto = null;
		EmployeeRequest employeeRequest = new EmployeeRequest();
		for (LeaveDetail leaveDetail : leaveDetails) {
			int requestId = leaveDetail.getEmployeeRequestId();
			employeeLeaveDto = new EmployeeLeaveDto();
			employeeRequest = employeeRequestDao.get(requestId);
			employeeLeaveDto.setEmployeeLeaveId(requestId);
			employeeLeaveDto.setDateFrom(leaveDetail.getDateFrom());
			employeeLeaveDto.setDateTo(leaveDetail.getDateTo());
			employeeLeaveDto.setEmployeeId(employeeRequest.getEmployeeId());
			List<FormWorkflowLog> formWorkflowLogs = employeeRequestService.getFormWorkflow(requestId).getFormWorkflowLogs();
			int statusId = 0;
			for (FormWorkflowLog formWorkflowLog : formWorkflowLogs) {
				statusId = employeeRequest.getFormWorkflow().getCurrentStatusId();
				if(formWorkflowLog.getFormStatusId() == statusId){
					employeeLeaveDto.setUpdatedDate(formWorkflowLog.getCreatedDate());
					employeeLeaveDto.setWorkflowStatus(statusId);
				}
			}
			employeeLeaveDtos.add(employeeLeaveDto);
		}
		return employeeLeaveDtos;
	}

	/**
	 * Get the list of latest monthly shift schedule.
	 * @param updateddate The updated date.
	 * @param updatedTimePeriodDate The payroll time period updated date.
	 * @return List {@link DTRMonthlyScheduleDto}
	 */
	public List<DTRMonthlyScheduleDto> getLatestMonthlySheds(Date updateddate, Date updatedTimePeriodDate) {
		return dtrDao.getLatestMonthlySheds(updateddate, updatedTimePeriodDate);
	}
}
