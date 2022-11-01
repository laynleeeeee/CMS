package eulap.eb.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.domain.Domain;
import eulap.common.util.DateUtil;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.EmployeeDao;
import eulap.eb.dao.EmployeeLeaveCreditDao;
import eulap.eb.dao.EmployeeLeaveCreditLineDao;
import eulap.eb.dao.ReferenceDocumentDao;
import eulap.eb.dao.TypeOfLeaveDao;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.Employee;
import eulap.eb.domain.hibernate.EmployeeLeaveCredit;
import eulap.eb.domain.hibernate.EmployeeLeaveCreditLine;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.fap.FormStatusService;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.validator.ValidatorUtil;
import eulap.eb.web.dto.EmployeeLeaveCreditDto;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.ResultProperty;

/**
 * Handles Business logic of Employee Leave Credit Form.

 *
 */
@Service
public class EmployeeLeaveCreditService extends BaseWorkflowService{
	@Autowired
	private EmployeeLeaveCreditDao employeeLeaveCreditDao;
	@Autowired
	private EmployeeLeaveCreditLineDao leavesDao;
	@Autowired
	private ReferenceDocumentDao referenceDocDao;
	@Autowired
	private TypeOfLeaveDao typeOfLeaveDao;
	@Autowired
	private ReferenceDocumentService refDocService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private FormStatusService formStatusService;
	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return employeeLeaveCreditDao.get(id).getFormWorkflow();
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer formWorkflowId) {
		return employeeLeaveCreditDao.getByWorkflowId(formWorkflowId);
	}

	/**
	 * Get the list of Employees
	 * @param companyId The Id of the company associated with the employee.
	 * @param divisionId The Id of the division.
	 * @param name The name of the employee.
	 * @return The list of Employees.
	 */
	public List<Employee> getEmployees(Integer companyId,Integer divisionId, User user, String name){
		return employeeDao.getEmployeesByName(companyId, divisionId, user,name);
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		EmployeeLeaveCredit employeeLeaveCredit = (EmployeeLeaveCredit) form;
		boolean isNew = employeeLeaveCredit.getId() == 0;
		AuditUtil.addAudit(employeeLeaveCredit, new Audit(user.getId(), isNew, new Date()));
		employeeLeaveCredit.setRemarks(StringFormatUtil.removeExtraWhiteSpaces(employeeLeaveCredit.getRemarks()));
		validate(employeeLeaveCredit, employeeLeaveCredit.getBindingResult());
		if(!employeeLeaveCredit.getBindingResult().hasErrors()){
			if(isNew){
				employeeLeaveCredit.setSequenceNumber(employeeLeaveCreditDao.generateSequenceNo());
			} else {
				EmployeeLeaveCredit savedEmployeeCredit = getEmployeeLeaveCredit(employeeLeaveCredit.getId());
				DateUtil.setCreatedDate(employeeLeaveCredit, savedEmployeeCredit.getCreatedDate());
			}
			employeeLeaveCreditDao.saveOrUpdate(employeeLeaveCredit);
			for(EmployeeLeaveCreditLine leaves : employeeLeaveCredit.getElcLines()){
				leavesDao.save(leaves);
			}
			for(ReferenceDocument referenceDocument : employeeLeaveCredit.getReferenceDocuments()){
				referenceDocument.setDescription(StringFormatUtil.removeExtraWhiteSpaces(referenceDocument.getDescription()));
				referenceDocDao.save(referenceDocument);
			}
		}
	}

	/**
	 * Get the leaves objects by ebObject.
	 * @param ebObjectId The ebObjectId
	 * @return The list of Leaves.
	 */
	public List<EmployeeLeaveCreditLine> getEmployeeLeaveCreditLineByEbObject(
			Integer leaveTypeId, Integer ebObjectId, Integer employeeId){
		List<EmployeeLeaveCreditLine> leaves =
				leavesDao.getEmployeeLeaveCreditLineByEbObject(ebObjectId, employeeId);
		if (leaves != null && !leaves.isEmpty()) {
			for (EmployeeLeaveCreditLine l : leaves) {
				l.setAvailableLeaves(leavesDao.getAvailableLeaves(l.getEmployeeId(), leaveTypeId, false, false));
			}
		}
		return leaves;
	}

	/**
	 * Get the Available Leaves of the Employee
	 * @param employeeId The unique identifier of the Employee
	 * @param typeOfLeaveId The unique identifier of the Type of the Leave.
	 * @param isForPrintOut True if will get the total available leaves, otherwise false
	 * @return The number of the available leaves.
	 */
	public Double getAvailableLeaves(Integer employeeId, Integer typeOfLeaveId, boolean isForPrintOut, boolean isLeavEarned){
		return leavesDao.getAvailableLeaves(employeeId, typeOfLeaveId, isForPrintOut, isLeavEarned);
	}

	/**
	 * Get the employee leave credit object.
	 * @param elcId The EmployeeLeaveCredit Id.
	 * @return The EmployeeLeaveCredit Object.
	 */
	public EmployeeLeaveCredit getEmployeeLeaveCredit(Integer elcId){
		return employeeLeaveCreditDao.get(elcId);
	}

	/**
	 * Get the list of EmployeeLeaveCredit by criteria.
	 * @param searchCriteria The search Criteria.
	 * @param user The logged in user.
	 * @return The list of EmployeeLeaveCredit by criteria.
	 */
	public List<FormSearchResult> search(String searchCriteria, User user){
		List<EmployeeLeaveCredit> employeeLeaveCredits =
				employeeLeaveCreditDao.searchEmployeeLeaveCredits(searchCriteria, user);
		List<FormSearchResult> results = new ArrayList<>();
		List<ResultProperty> properties = null;
		for(EmployeeLeaveCredit employeeLeaveCredit : employeeLeaveCredits){
			String title = "Employee Leave Credit No. " +
					employeeLeaveCredit.getSequenceNumber().toString();
			properties = new ArrayList<>();
			properties.add(ResultProperty.getInstance("Branch", employeeLeaveCredit.getCompany().getNumberAndName()));
			properties.add(ResultProperty.getInstance("Leave Type", employeeLeaveCredit.getTypeOfLeave().getName()));
			properties.add(ResultProperty.getInstance("Date", DateUtil.formatDate(employeeLeaveCredit.getDate())));
			if(employeeLeaveCredit.getRemarks() != null) {
				properties.add(ResultProperty.getInstance("Remarks", employeeLeaveCredit.getRemarks()));
			}
			String status = employeeLeaveCredit.getFormWorkflow().getCurrentFormStatus().getDescription();
			properties.add(ResultProperty.getInstance("Status", StringFormatUtil.formatToLowerCase(status)));
			results.add(FormSearchResult.getInstanceOf(employeeLeaveCredit.getId(), title, properties));
		}
		return results;
	}

	/**
	 * Convert object to DTO to be used in printout with employee name format.
	 * @param employeeLeaveCredit The EmployeeLeaveCredit
	 * @param isFirstNameFirst True if the employee name format is first name first, otherwise false.
	 * @return The datasource to be used in printout.
	 */
	public List<EmployeeLeaveCreditDto> convertToDto(EmployeeLeaveCredit employeeLeaveCredit, boolean isFirstNameFirst){
		List<EmployeeLeaveCreditDto> datasource = new ArrayList<>();
		Integer ebObjectId = employeeLeaveCredit.getEbObjectId();
		employeeLeaveCredit.setElcLines(leavesDao.getEmployeeLeaveCreditLineByEbObject(ebObjectId, null));
		List<EmployeeLeaveCreditLine> employeeLeaveCreditLines = employeeLeaveCredit.getElcLines();
		for(EmployeeLeaveCreditLine employeeLeaveCreditLine : employeeLeaveCreditLines){
			Employee employee = employeeLeaveCreditLine.getEmployee();
			if(isFirstNameFirst) {
				String middleInitial = employee.getMiddleName() != null && !employee.getMiddleName().isEmpty()
						? employee.getMiddleName().charAt(0) + ". " : "";
				employeeLeaveCreditLine.setEmployeeName(employee.getFirstName() + " " + middleInitial + employee.getLastName());
			} else {
				employeeLeaveCreditLine.setEmployeeName(employee.getFullName());
			}
			employeeLeaveCreditLine.setAvailableLeaves(getAvailableLeaves(employeeLeaveCreditLine.getEmployee().getId(),
					employeeLeaveCredit.getTypeOfLeave().getId(), true, false));
		}
		datasource.add(EmployeeLeaveCreditDto.getInstanceOf(employeeLeaveCreditLines, null));
		return datasource;
	}

	public List<EmployeeLeaveCreditDto> convertToDto(EmployeeLeaveCredit employeeLeaveCredit) {
		return convertToDto(employeeLeaveCredit, false);
	}

	private void validate(EmployeeLeaveCredit employeeLeaveCredit, Errors errors){
		if(employeeLeaveCredit != null){

			// Branch
			if(employeeLeaveCredit.getCompanyId() == null){
				errors.rejectValue("companyId", null, null, ValidatorMessages.getString("EmployeeLeaveCreditService.0"));
			} else {
				ValidatorUtil.checkInactiveCompany(companyService, employeeLeaveCredit.getCompanyId(), "companyId", errors, ValidatorMessages.getString("EmployeeLeaveCreditService.1"));
			}
			if(employeeLeaveCredit.getDivisionId() == null){
				errors.rejectValue("divisionId", null, null, ValidatorMessages.getString("EmployeeLeaveCreditService.13"));
			} else {
				Division division = divisionService.getDivision(employeeLeaveCredit.getDivisionId());
				if (!division.isActive()) {
					errors.rejectValue("divisionId", null, null, ValidatorMessages.getString("EmployeeLeaveCreditService.14"));
				}
			}

			// Date
			if(employeeLeaveCredit.getDate() == null){
				errors.rejectValue("date", null, null, ValidatorMessages.getString("EmployeeLeaveCreditService.2"));
			}

			// Leave Type
			if(employeeLeaveCredit.getTypeOfLeaveId() == null){
				errors.rejectValue("typeOfLeave", null, null, ValidatorMessages.getString("EmployeeLeaveCreditService.3"));
			} else {
				employeeLeaveCredit.setTypeOfLeave(typeOfLeaveDao.get(employeeLeaveCredit.getTypeOfLeaveId()));
				if(!employeeLeaveCredit.getTypeOfLeave().isActive()){
					errors.rejectValue("typeOfLeave", null, null, ValidatorMessages.getString("EmployeeLeaveCreditService.4"));
				}
			}

			// Leaves
			if(employeeLeaveCredit.getElcLines() == null || employeeLeaveCredit.getElcLines().isEmpty()){
				errors.rejectValue("leavesMessage", null, null, ValidatorMessages.getString("EmployeeLeaveCreditService.5"));
			} else {
				boolean hasNoDeductAndAdd = false;
				boolean hasDoublehData = false;
				boolean hasNegativeCredit = false;
				boolean hasNegativeDebit = false;
				boolean hasNegativeComputedLeaves = false;
				boolean hasNoEmployee = false;
				for(EmployeeLeaveCreditLine leave : employeeLeaveCredit.getElcLines()){
					double availableLeaves = leave.getAvailableLeaves() != null ? leave.getAvailableLeaves() : 0;
					if(leave.getEmployeeId() == null) {
						hasNoEmployee = true;
					} else {
						if(leave.getDeductDebit() == null && leave.getAddCredit() == null){
							hasNoDeductAndAdd = true;
						} else if(leave.getDeductDebit() != null && leave.getAddCredit() != null){
							hasDoublehData = true;
						}
						if(leave.getAddCredit() != null && leave.getAddCredit() < 0){
							hasNegativeCredit = true;
						}
						if(leave.getDeductDebit() != null && leave.getDeductDebit() < 0){
							hasNegativeDebit = true;
						}
						if(leave.getDeductDebit() != null && ((availableLeaves - leave.getDeductDebit()) < 0)) {
							hasNegativeComputedLeaves = true;
						}
					}
				}
				if(hasDuplicateEmployee(employeeLeaveCredit.getElcLines())){
					errors.rejectValue("leavesMessage", null, null, ValidatorMessages.getString("EmployeeLeaveCreditService.6"));
				}
				if(hasDoublehData){
					errors.rejectValue("leavesMessage", null, null, ValidatorMessages.getString("EmployeeLeaveCreditService.7"));
				}
				if(hasNegativeCredit){
					errors.rejectValue("leavesMessage", null, null, ValidatorMessages.getString("EmployeeLeaveCreditService.8"));
				}
				if(hasNegativeDebit){
					errors.rejectValue("leavesMessage", null, null, ValidatorMessages.getString("EmployeeLeaveCreditService.9"));
				}
				if(hasNoDeductAndAdd){
					errors.rejectValue("leavesMessage", null, null, ValidatorMessages.getString("EmployeeLeaveCreditService.10"));
				}
				if(hasNegativeComputedLeaves) {
					errors.rejectValue("leavesMessage", null, null,
							ValidatorMessages.getString("EmployeeLeaveCreditService.11"));
				}
				if(hasNoEmployee) {
					errors.rejectValue("leavesMessage", null, null, ValidatorMessages.getString("EmployeeLeaveCreditService.12"));
				}
			}

			// Documents
			refDocService.validateReferences(employeeLeaveCredit.getReferenceDocuments(), errors);
			//Validate form status
			FormWorkflow workflow = employeeLeaveCredit.getId() != 0 ? getFormWorkflow(employeeLeaveCredit.getId()) : null;
			String workflowError = ValidationUtil.validateFormStatus(formStatusService, workflow);
			if (workflowError != null ) {
				errors.rejectValue("formWorkflowId", null, null, workflowError);
			}
		}
	}

	/**
	 * Evaluates whether the Employee has duplicate existence.
	 * @param employeeLeaveCreditLines The EmployeeLeaveCreditLines
	 * @return True if a duplicate exists, else false.
	 */
	private Boolean hasDuplicateEmployee(List<EmployeeLeaveCreditLine> employeeLeaveCreditLines){
		ArrayList<Integer> empIds = new ArrayList<>();
		boolean hasDuplicate = false;
		for(EmployeeLeaveCreditLine empLine : employeeLeaveCreditLines){
			empIds.add(empLine.getEmployeeId());
		}
		Set<Integer> setEmpId = new HashSet<>(empIds);
		for(Integer empId : setEmpId){
			if(Collections.frequency(empIds, empId) > 1){
				hasDuplicate = true;
			}
		}
		return hasDuplicate;
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";

		EmployeeLeaveCredit elc = employeeLeaveCreditDao.getByEbObjectId(ebObjectId);
		Integer pId = elc.getId();
		FormProperty property = workflowHandler.getProperty(elc.getWorkflowName(), user);
		String popupLink = "/employeeLeaveCredit/form?pId=" + pId;
		String printOutLink = "/" + property.getPrint() + "?pId=" + pId;

		String latestStatus = elc.getFormWorkflow().getCurrentFormStatus().getDescription();
		String title = "Employee Leave Credit - " + elc.getSequenceNumber();
		shortDescription = new StringBuffer(title)
				.append(" " + elc.getCompany().getName())
				.append(" " + elc.getTypeOfLeave().getName())
				.append(" " + DateUtil.formatDate(elc.getDate()));

		return ObjectInfo.getInstance(ebObjectId, title, latestStatus,
				shortDescription.toString(), fullDescription, popupLink, printOutLink);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		int ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
		case EmployeeLeaveCredit.OBJECT_TYPE_ID:
			return employeeLeaveCreditDao.getByEbObjectId(ebObjectId);
		case EmployeeLeaveCreditLine.OBJECT_TYPE_ID:
			return leavesDao.getByEbObjectId(ebObjectId);
		case ReferenceDocument.OBJECT_TYPE_ID:
			return referenceDocDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}
}