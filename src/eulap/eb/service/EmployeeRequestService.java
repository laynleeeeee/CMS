package eulap.eb.service;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.domain.Domain;
import eulap.common.util.DateUtil;
import eulap.common.util.StringFormatUtil;
import eulap.common.util.TimeFormatUtil;
import eulap.eb.dao.EmployeeDao;
import eulap.eb.dao.EmployeeRequestDao;
import eulap.eb.dao.EmployeeShiftDao;
import eulap.eb.dao.LeaveDetailDao;
import eulap.eb.dao.OvertimeDetailDao;
import eulap.eb.dao.ReferenceDocumentDao;
import eulap.eb.dao.TypeOfLeaveDao;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.Employee;
import eulap.eb.domain.hibernate.EmployeeRequest;
import eulap.eb.domain.hibernate.EmployeeShift;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.LeaveDetail;
import eulap.eb.domain.hibernate.OvertimeDetail;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.RequestType;
import eulap.eb.domain.hibernate.TypeOfLeave;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.fap.FormStatusService;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.validator.ValidatorUtil;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.ResultProperty;

/**
 * Service class for {@link EmployeeRequest}

 *
 */
@Service
public class EmployeeRequestService extends BaseWorkflowService {
	@Autowired
	private EmployeeRequestDao employeeRequestDao;
	@Autowired
	private ReferenceDocumentDao referenceDocumentDao;
	@Autowired
	private TypeOfLeaveDao typeOfLeaveDao;
	@Autowired
	private ReferenceDocumentService refDocService;
	@Autowired
	private LeaveDetailDao leaveDetailDao;
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private PositionService positionService;
	@Autowired
	private OvertimeDetailDao overtimeDetailDao;
	@Autowired
	private EmployeeLeaveCreditService leaveCreditService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private EmployeeShiftDao employeeShiftDao;
	@Autowired
	private FormStatusService formStatusService;

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return employeeRequestDao.get(id).getFormWorkflow();
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return employeeRequestDao.getByEbObjectId(workflowId);
	}

	/**
	 * Save the form
	 */
	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		EmployeeRequest employeeRequest = (EmployeeRequest) form;
		LeaveDetail leaveDetail = employeeRequest.getLeaveDetail();
		OvertimeDetail overtimeDetail = employeeRequest.getOvertimeDetail();
		boolean isNew = employeeRequest.getId() == 0;
		AuditUtil.addAudit(employeeRequest, new Audit(user.getId(), isNew, new Date()));
		if (isNew) {
			employeeRequest.setSequenceNo(employeeRequestDao.generateSequenceNo(employeeRequest.getRequestTypeId()));
		} else {
			EmployeeRequest savedRequestForLeave = getEmployeeRequest(employeeRequest.getId());
			DateUtil.setCreatedDate(employeeRequest, savedRequestForLeave.getCreatedDate());
		}
		employeeRequestDao.saveOrUpdate(employeeRequest);

		//Save leave details
		if (employeeRequest.getRequestTypeId().equals(RequestType.REQUEST_FOR_LEAVE)) {
			leaveDetail.setEmployeeRequestId(employeeRequest.getId());
			leaveDetailDao.saveOrUpdate(leaveDetail);
		}

		//Save overtime details
		if (employeeRequest.getRequestTypeId().equals(RequestType.REQUEST_FOR_OVERTIME)) {
			overtimeDetail.setEmployeeRequestId(employeeRequest.getId());
			overtimeDetailDao.saveOrUpdate(overtimeDetail);
		}

		//Save reference documents
		List<ReferenceDocument> referenceDocuments = employeeRequest.getReferenceDocuments();
		if (referenceDocuments != null && !referenceDocuments.isEmpty()) {
			for (ReferenceDocument referenceDocument : referenceDocuments) {
				referenceDocument.setDescription(
						StringFormatUtil.removeExtraWhiteSpaces(referenceDocument.getDescription()));
				referenceDocumentDao.save(referenceDocument);
			}
		}
	}

	/**
	 * Get the employee request object by id
	 * @param employeeRequestId The request for leave id
	 * @return The employee request object
	 */
	public EmployeeRequest getEmployeeRequest (Integer employeeRequestId) {
		return employeeRequestDao.get(employeeRequestId);
	}

	/**
	 * Validate the form
	 * @param requestForLeave The employee request - request for leave overtime
	 * @param errors The validation errors
	 */
	public void validate(EmployeeRequest employeeRequest, LeaveDetail leaveDetail,
			OvertimeDetail overtimeDetail, Errors errors) {
		validate(employeeRequest, leaveDetail, overtimeDetail, errors, "", false, false);
	}

	/**
	 * Validate form
	 * @param employeeRequest the employee request
	 * @param leaveDetail the leave detail
	 * @param overtimeDetail the overtime detail
	 * @param errors the errors
	 */
	public void validateForm(EmployeeRequest employeeRequest, LeaveDetail leaveDetail,
			OvertimeDetail overtimeDetail, Errors errors) {
		validate(employeeRequest, leaveDetail, overtimeDetail, errors, "", false, true);
	}

	/**
	 * Validate the employee request form.
	 * @param employeeRequest The employee request object
	 * @param leaveDetail The leave detail
	 * @param overtimeDetail The overtime detail
	 * @param errors The validation errors
	 * @param fieldPrepend The field prepend string
	 * @param isOpenTime True if the requested overtime is open time, otherwise false
	 */
	public void validate(EmployeeRequest employeeRequest, LeaveDetail leaveDetail,
			OvertimeDetail overtimeDetail, Errors errors, String fieldPrepend, boolean isOpenTime,
			boolean isCheckLeaveType) {
		Integer employeeId = employeeRequest.getEmployeeId();
		if (employeeRequest.getCompanyId() == null) {
			errors.rejectValue(fieldPrepend+"companyId", null, null, ValidatorMessages.getString("EmployeeRequestService.0"));
		} else {
			ValidatorUtil.checkInactiveCompany(companyService, employeeRequest.getCompanyId(), fieldPrepend+"companyId", errors, ValidatorMessages.getString("EmployeeRequestService.1"));
		}

		if (employeeRequest.getEmployeeId() == null) {
			errors.rejectValue(fieldPrepend+"employeeId", null, null, ValidatorMessages.getString("EmployeeRequestService.2"));
		}

		if (employeeRequest.getDate() == null) {
			errors.rejectValue(fieldPrepend+"date", null, null, ValidatorMessages.getString("EmployeeRequestService.3"));
		}

		if (employeeRequest.getRequestTypeId() == RequestType.REQUEST_FOR_LEAVE) {
			if (leaveDetail.getDateFrom() == null) {
				errors.rejectValue(fieldPrepend+"leaveDetail.dateFrom", null, null, ValidatorMessages.getString("EmployeeRequestService.4"));
			}

			if (leaveDetail.getDateTo() == null) {
				errors.rejectValue(fieldPrepend+"leaveDetail.dateTo", null, null, ValidatorMessages.getString("EmployeeRequestService.5"));
			}

			if (leaveDetail.getLeaveDays() == 0.0 || leaveDetail.getLeaveDays() < 0) {
				errors.rejectValue(fieldPrepend+"leaveDetail.leaveDays", null, null, ValidatorMessages.getString("EmployeeRequestService.6"));
			}

			if (leaveDetail.getTypeOfLeaveId() == null) {
				errors.rejectValue(fieldPrepend+"leaveDetail.typeOfLeaveId", null, null, ValidatorMessages.getString("EmployeeRequestService.7"));
			} else {
				TypeOfLeave typeOfLeave = typeOfLeaveDao.get(leaveDetail.getTypeOfLeaveId());
				if (!typeOfLeave.isActive()) {
					errors.rejectValue(fieldPrepend+"leaveDetail.typeOfLeaveId", null, null, ValidatorMessages.getString("EmployeeRequestService.8"));
				}
			}

			if (!isCheckLeaveType) {
				if (employeeId != null && leaveDetail.getTypeOfLeaveId()  != null) {
					double leaveBalance = leaveCreditService.getAvailableLeaves(employeeRequest.getEmployeeId(),
							leaveDetail.getTypeOfLeaveId(), false, false);
					if (leaveBalance == 0.0) {
						errors.rejectValue(fieldPrepend+"leaveDetail.typeOfLeaveId", null, null,
								ValidatorMessages.getString("EmployeeRequestService.9"));
					} else if (leaveBalance < leaveDetail.getLeaveDays()) {
						errors.rejectValue(fieldPrepend+"leaveDetail.typeOfLeaveId", null, null,
								ValidatorMessages.getString("EmployeeRequestService.10"));
					}
				}
			}
		} else {
			boolean hasInvalidDate = false;
			Date overtimeDate = overtimeDetail.getOvertimeDate();
			if (overtimeDate == null) {
				errors.rejectValue(fieldPrepend+"overtimeDetail.overtimeDate", null, null, ValidatorMessages.getString("EmployeeRequestService.11"));
				hasInvalidDate = true;
			}

			boolean hasInvalidTime = false;
			if (!isOpenTime) {
				// Start time
				if (overtimeDetail.getStartTime() == null || overtimeDetail.getStartTime() == "") {
					errors.rejectValue(fieldPrepend+"overtimeDetail.startTime", null, null, ValidatorMessages.getString("EmployeeRequestService.12"));
					hasInvalidTime = true;
				} else if (!TimeFormatUtil.isMilitaryTime(overtimeDetail.getStartTime())) {
					errors.rejectValue(fieldPrepend+"overtimeDetail.startTime", null, null, ValidatorMessages.getString("EmployeeRequestService.13"));
					hasInvalidTime = true;
				}

				// End time
				if (overtimeDetail.getEndTime() == null || overtimeDetail.getEndTime() == "") {
					errors.rejectValue(fieldPrepend+"overtimeDetail.endTime", null, null, ValidatorMessages.getString("EmployeeRequestService.14"));
					hasInvalidTime = true;
				} else if (!TimeFormatUtil.isMilitaryTime(overtimeDetail.getEndTime())) {
					errors.rejectValue(fieldPrepend+"overtimeDetail.endTime", null, null, ValidatorMessages.getString("EmployeeRequestService.15"));
					hasInvalidTime = true;
				}
	
				if (overtimeDetail.getOvertimeHours() == null) {
					errors.rejectValue(fieldPrepend+"overtimeDetail.overtimeHours", null, null, ValidatorMessages.getString("EmployeeRequestService.16"));
				} else if (overtimeDetail.getOvertimeHours() > OvertimeDetail.MAX_OT_HOURS) {
					errors.rejectValue(fieldPrepend+"overtimeDetail.overtimeHours", null, null, ValidatorMessages.getString("EmployeeRequestService.17"));
				} else if (overtimeDetail.getOvertimeHours() <= 0) {
					errors.rejectValue(fieldPrepend+"overtimeDetail.overtimeHours", null, null, ValidatorMessages.getString("EmployeeRequestService.18"));
				}
			}


			if (overtimeDetail.getPurpose() == null || overtimeDetail.getPurpose().trim().isEmpty()) {
				errors.rejectValue(fieldPrepend+"overtimeDetail.purpose", null, null, ValidatorMessages.getString("EmployeeRequestService.19"));
			}

			if (!hasInvalidTime && !hasInvalidDate) {
				if (hasOverlappingDates(overtimeDate, overtimeDetail.getStartTime(), overtimeDetail.getEndTime(),
						employeeRequest.getId(), employeeId)) {
					errors.rejectValue(fieldPrepend+"overtimeDetail.endTime", null, null,
							ValidatorMessages.getString("EmployeeRequestService.20"));
				}

				EmployeeShift employeeShift = employeeShiftDao.getBySchedule(employeeRequest.getCompanyId(), null, null,
						employeeId, overtimeDetail.getOvertimeDate());
				if (employeeShift == null) {
					errors.rejectValue(fieldPrepend+"overtimeDetail.endTime", null, null,
							ValidatorMessages.getString("EmployeeRequestService.21"));
				} else {
					Date startShift = DateUtil.appendTimeToDate(employeeShift.getFirstHalfShiftStart(), overtimeDate);
					Date endShift = DateUtil.appendTimeToDate(employeeShift.getSecondHalfShiftEnd(),
							(employeeShift.isNightShift() ? DateUtil.addDaysToDate(overtimeDate, 1) : overtimeDate));

					Date startDate = DateUtil.appendTimeToDate(overtimeDetail.getStartTime(), overtimeDate);
					Date endDate = DateUtil.appendTimeToDate(overtimeDetail.getEndTime(), overtimeDate);
					if (checkInBetweenDate(startDate, startShift, endShift)
							|| checkInBetweenDate(endDate, startShift, endShift)) {
						errors.rejectValue(fieldPrepend+"overtimeDetail.endTime", null, null,
								ValidatorMessages.getString("EmployeeRequestService.22"));
					}
				}
			}
		}

		//Validate Documents
		refDocService.validateReferences(employeeRequest.getReferenceDocuments(), errors, fieldPrepend);
		//Validate form status
		FormWorkflow workflow = employeeRequest.getId() != 0 ? getFormWorkflow(employeeRequest.getId()) : null;
		String workflowError = ValidationUtil.validateFormStatus(formStatusService, workflow);
		if (workflowError != null ) {
			errors.rejectValue(fieldPrepend+"formWorkflowId", null, null, workflowError);
		}
	}

	/**
	 * Check for request for overtime if overlaps the already created overtime for the overtime date 
	 * @param overtimeDate The overtime date
	 * @param startTime The overtime start time
	 * @param endTime The overtime end time
	 * @param employeeRequestId The employee request id
	 * @param employeeId The employee id
	 * @return True if the dates overlaps, otherwise false
	 */
	private boolean hasOverlappingDates(Date overtimeDate, String startTime, String endTime, Integer employeeRequestId, Integer employeeId) {
		if (overtimeDetailDao.hasRequestedOvertime(employeeRequestId, employeeId, overtimeDate)) {
			List<OvertimeDetail> overtimeDetails = overtimeDetailDao.getAllByEmployeeAndDate(employeeId, overtimeDate, false);
			for (OvertimeDetail od : overtimeDetails) {
				Date savedOTStart = DateUtil.appendTimeToDate(od.getStartTime(), overtimeDate);
				Date savedOTEnd = DateUtil.appendTimeToDate(od.getEndTime(), overtimeDate);

				Date startDate = DateUtil.appendTimeToDate(startTime, overtimeDate);
				Date endDate = DateUtil.appendTimeToDate(endTime, overtimeDate);
				if (checkInBetweenDate(startDate, savedOTStart, savedOTEnd)
						|| checkInBetweenDate(endDate, savedOTStart, savedOTEnd)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean checkInBetweenDate(Date date, Date startDate, Date endDate) {
		return date.after(startDate) && date.before(endDate);
	}

	/**
	 * Get the list of Type of Leaves
	 * @param leaveTypeId The type of leave id
	 * @return The list of Type of leaves.
	 */
	public List<TypeOfLeave> getTypeOfLeaves(Integer leaveTypeId) {
		return typeOfLeaveDao.getTypeOfLeaves(leaveTypeId);
	}

	/**
	 * Get the request for leave for object for editing and viewing with employee full name format.
	 * @param employeeRequestId The employee request id
	 * @param isFirstNameFirst True if full name format is first name first, otherwise false.
	 * @return The request for leave for object
	 */
	public EmployeeRequest getRequestForLeave(Integer employeeRequestId, boolean isFirstNameFirst) {
		EmployeeRequest requestForLeave = getEmployeeRequest(employeeRequestId);
		Employee employee = employeeDao.get(requestForLeave.getEmployeeId());
		String employeeName = "";
		if (isFirstNameFirst) {
			String middleInitial = employee.getMiddleName() != null && !employee.getMiddleName().isEmpty()
					? employee.getMiddleName().charAt(0) + ". " : "";
			employeeName = employee.getFirstName() + " " + middleInitial + employee.getLastName();
		} else {
			employeeName = employee.getFullName();
		}
		requestForLeave.setEmployeeFullName(employeeName);
		requestForLeave.setEmployeePosition(positionService.getEmployeePosition(requestForLeave.getEmployeeId()).getName());
		requestForLeave.setEmployeeDivision(divisionService.getEmployeeDivision(requestForLeave.getEmployeeId()).getName());
		LeaveDetail leaveDetail = leaveDetailDao.getLeaveDetailByRequestId(employeeRequestId);
		double leaveBalance = leaveCreditService.getAvailableLeaves(requestForLeave.getEmployeeId(),
				leaveDetail.getTypeOfLeaveId(), true, false);
		leaveDetail.setLeaveBalance(leaveBalance);
		requestForLeave.setLeaveDetail(leaveDetail);
		return requestForLeave;
	}

	/**
	 * Get the employee request object by employee request id and type id.
	 * @param employeeRequestId The employee request id.
	 * @param typeId The employee request type id.
	 * @return The employee request object.
	 */
	public EmployeeRequest getEmployeeRequest(Integer employeeRequestId, Integer typeId) {
		if (typeId == RequestType.REQUEST_FOR_LEAVE) {
			return getRequestForLeave(employeeRequestId, false);
		} else {
			return getRequestForOvertime(employeeRequestId, false);
		}
	}

	/**
	 * Get the request for overtime object for editing and viewing with employee full name format.
	 * @param employeeRequestId The employee request id.
	 * @param isFirstNameFirst True if full name format is first name first, otherwise false.
	 * @return The request for overtime object.
	 */
	public EmployeeRequest getRequestForOvertime(Integer employeeRequestId, boolean isFirstNameFirst) {
		EmployeeRequest requestForOvertime = getEmployeeRequest(employeeRequestId);
		Employee employee = employeeDao.get(requestForOvertime.getEmployeeId());
		String employeeName = "";
		if (isFirstNameFirst) {
			String middleInitial = employee.getMiddleName() != null && !employee.getMiddleName().isEmpty()
					? employee.getMiddleName().charAt(0) + ". " : "";
			employeeName = employee.getFirstName() + " " + middleInitial + employee.getLastName();
		} else {
			employeeName = employee.getFullName();
		}
		requestForOvertime.setEmployeeFullName(employeeName);
		requestForOvertime.setEmployeePosition(positionService.getEmployeePosition(requestForOvertime.getEmployeeId()).getName());
		requestForOvertime.setEmployeeDivision(divisionService.getEmployeeDivision(requestForOvertime.getEmployeeId()).getName());
		requestForOvertime.setOvertimeDetail(getOvertimeDetail(employeeRequestId));
		return requestForOvertime;
	}

	/**
	 * get the list of employee request based on the search criteria
	 * @param typeId The type id
	 * @param searchCriteria The search criteria
	 * @param user The user logged
	 * @return The list of employee request
	 */
	public List<FormSearchResult> search(Integer typeId, String searchCriteria, User user) {
		List<EmployeeRequest> employeeRequests =
				employeeRequestDao.searchEmployeeRequest(typeId, searchCriteria, user);
		List<FormSearchResult> results = new ArrayList<>();
		List<ResultProperty> properties = null;
		for(EmployeeRequest employeeRequest : employeeRequests) {
			String title = typeId == RequestType.REQUEST_FOR_LEAVE ? "Request for Leave No. "
							+ employeeRequest.getSequenceNo().toString() : "Request for Overtime No. "
							+ employeeRequest.getSequenceNo().toString();
			properties = new ArrayList<>();
			properties.add(ResultProperty.getInstance("Branch", employeeRequest.getCompany().getNumberAndName()));
			properties.add(ResultProperty.getInstance("Date", DateUtil.formatDate(employeeRequest.getDate())));
			properties.add(ResultProperty.getInstance("Employee", employeeRequest.getEmployee().getFullName()));
			if (employeeRequest.getLeaveDetail() != null) {
				properties.add(ResultProperty.getInstance("Leave Type", employeeRequest.getLeaveDetail().getTypeOfLeave().getName()));
				if (employeeRequest.getLeaveDetail().getRemarks() != null) {
					properties.add(ResultProperty.getInstance("Remarks", employeeRequest.getLeaveDetail().getRemarks()));
				}
			} else if (employeeRequest.getOvertimeDetail() != null) {
				if (employeeRequest.getOvertimeDetail().getPurpose() != null) {
					properties.add(ResultProperty.getInstance("Purpose", employeeRequest.getOvertimeDetail().getPurpose()));
				}
			}
			String status = employeeRequest.getFormWorkflow().getCurrentFormStatus().getDescription();
			properties.add(ResultProperty.getInstance(ValidatorMessages.getString("EmployeeRequestService.57"), StringFormatUtil.formatToLowerCase(status)));
			results.add(FormSearchResult.getInstanceOf(employeeRequest.getId(), title, properties));
		}
		return results;
	}

	/**
	 * Get the list of Employees
	 * @param companyId The Id of the company associated with the employee.
	 * @param name The name of the employee.
	 * @return The list of Employees.
	 */
	public List<Employee> getEmployees(Integer companyId, String name) {
		return employeeDao.getEmployeeByCompanyAndName(companyId, name, false);
	}

	private OvertimeDetail getOvertimeDetail(Integer employeeRequestId) {
		OvertimeDetail overtimeDetail = overtimeDetailDao.getOvertimeDetailByRequestId(employeeRequestId);
		Double hoursDiff = null;
		String strEndTime = overtimeDetail.getEndTime();
		if (overtimeDetail.getEndTime().compareTo(overtimeDetail.getStartTime())<=0) { // Means its next day. Add 24 hours to the time.
			String timeArr[] = strEndTime.split(":");
			int endTimeHour = Integer.parseInt(timeArr[0]) + 24;
			strEndTime = endTimeHour + ":" +  timeArr[1];
		}
		try {
			hoursDiff = DateUtil.getHoursDiff(overtimeDetail.getStartTime(), strEndTime, null);
			DecimalFormat df = new DecimalFormat("#.##");
			hoursDiff = Double.valueOf(df.format(hoursDiff));
		} catch (ParseException e) {
			return null;
		}
		overtimeDetail.setAllowableBreak(hoursDiff - overtimeDetail.getOvertimeHours());
		return overtimeDetail;
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";

		int typeId = employeeRequestDao.getByEbObjectId(ebObjectId).getRequestTypeId();
		EmployeeRequest employeeRequest = typeId == RequestType.REQUEST_FOR_LEAVE ?
				getRequestForLeave(employeeRequestDao.getByEbObjectId(ebObjectId).getId(), false) :
				getRequestForOvertime(employeeRequestDao.getByEbObjectId(ebObjectId).getId(), false);
		Integer pId = employeeRequest.getId();
		FormProperty property = workflowHandler.getProperty(employeeRequest.getWorkflowName(), user);
		String popupLink = "/" + property.getEdit() + "?pId=" + pId;
		String printOutLink = "/" + property.getPrint() + "?pId=" + pId;

		String formName = typeId == RequestType.REQUEST_FOR_LEAVE ? "Request For Leave" : "Request For Overtime";
		String latestStatus = employeeRequest.getFormWorkflow().getCurrentFormStatus().getDescription();
		String title = formName+ " - " + employeeRequest.getSequenceNo();
		shortDescription = new StringBuffer(title)
				.append(" " + employeeRequest.getEmployeeFullName())
				.append(" " + employeeRequest.getEmployeePosition());
		if (typeId == RequestType.REQUEST_FOR_LEAVE) {
			shortDescription.append("<b> DATE : </b> " + DateUtil.formatDate(employeeRequest.getDate()))
					.append("<b> DATE FROM : </b>" + DateUtil.formatDate(employeeRequest.getLeaveDetail().getDateFrom()))
					.append("<b> DATE TO : </b>" + DateUtil.formatDate(employeeRequest.getLeaveDetail().getDateTo()))
					.append("<b> Days : </b>" + employeeRequest.getLeaveDetail().getLeaveDays())
					.append("<b> LEAVE TYPE: </b>" + employeeRequest.getLeaveDetail().getTypeOfLeave());
		} else {
			shortDescription.append("<b> DATE : </b> " + DateUtil.formatDate(employeeRequest.getDate()))
					.append("<b> OVERTIME DATE </b> " + DateUtil.formatDate(employeeRequest.getOvertimeDetail().getOvertimeDate()))
					.append("<b> START TIME </b> " + employeeRequest.getOvertimeDetail().getStartTime())
					.append("<b> END TIME </b> " + employeeRequest.getOvertimeDetail().getEndTime())
					.append("<b> OVERTIME HOURS </b> " + employeeRequest.getOvertimeDetail().getOvertimeHours());
		}

		return ObjectInfo.getInstance(ebObjectId, title, latestStatus,
				shortDescription.toString(), fullDescription, popupLink, printOutLink);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		Integer ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
		case EmployeeRequest.RFL_OBJECT_TYPE_ID:
		case EmployeeRequest.RFO_OBJECT_TYPE_ID:
			return employeeRequestDao.getByEbObjectId(ebObjectId);
		case ReferenceDocument.OBJECT_TYPE_ID:
			return referenceDocumentDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}
}
