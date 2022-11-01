package eulap.eb.service;

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
import eulap.eb.dao.ActionNoticeDao;
import eulap.eb.dao.EmployeeDao;
import eulap.eb.dao.PersonnelActionNoticeDao;
import eulap.eb.dao.ReferenceDocumentDao;
import eulap.eb.domain.hibernate.ActionNotice;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.Employee;
import eulap.eb.domain.hibernate.EmployeeProfile;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.PersonnelActionNotice;
import eulap.eb.domain.hibernate.ReferenceDocument;
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
 * Service class that will handle business log for {@link PersonnelActionNotice}

 *
 */
@Service
public class PersonnelActionNoticeService extends BaseWorkflowService{
	@Autowired
	private PersonnelActionNoticeDao personnelActionNoticeDao;
	@Autowired
	private ReferenceDocumentDao referenceDocDao;
	@Autowired
	private ActionNoticeDao actionNoticeDao;
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private PositionService positionService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private ReferenceDocumentService refDocService;
	@Autowired
	private EmployeeProfileService employeeProfileService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private AdminActionNoticeService actionNoticeService;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private FormStatusService formStatusService;

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return personnelActionNoticeDao.get(id).getFormWorkflow();
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return personnelActionNoticeDao.getByEbObjectId(workflowId);
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		saveForm(form, workflowName, user, "");
	}

	public void saveForm(BaseFormWorkflow form, String workflowName, User user, String fieldPrepend) {
		PersonnelActionNotice personnelActionNotice = (PersonnelActionNotice) form;
		boolean isNew = personnelActionNotice.getId() == 0;
		AuditUtil.addAudit(personnelActionNotice, new Audit(user.getId(), isNew, new Date()));
		personnelActionNotice.setJustification(StringFormatUtil.removeExtraWhiteSpaces(
				personnelActionNotice.getJustification()));
		validate(personnelActionNotice, personnelActionNotice.getResult(), fieldPrepend);
		if(!personnelActionNotice.getResult().hasErrors()) {
			if(isNew) {
				personnelActionNotice.setSequenceNo(personnelActionNoticeDao.generateSequenceNo());
			} else {
				PersonnelActionNotice savedActionNotice =
						getPersonnelActionNotice(personnelActionNotice.getId());
				DateUtil.setCreatedDate(personnelActionNotice, savedActionNotice.getCreatedDate());
			}
			personnelActionNoticeDao.saveOrUpdate(personnelActionNotice);

			List<ReferenceDocument> referenceDocuments = personnelActionNotice.getReferenceDocuments();
			if(referenceDocuments != null && !referenceDocuments.isEmpty()) {
				for (ReferenceDocument referenceDocument : referenceDocuments) {
					referenceDocument.setDescription(
							StringFormatUtil.removeExtraWhiteSpaces(referenceDocument.getDescription()));
					referenceDocDao.save(referenceDocument);
				}
			}
		}
	}

	private void validate(PersonnelActionNotice personnelActionNotice, Errors errors, String fieldPrepend) {
		if(personnelActionNotice != null) {
			Integer employeeId = personnelActionNotice.getEmployeeId();
			ValidatorUtil.validateCompany(personnelActionNotice.getCompanyId(), companyService,
					errors, "companyId");

			if(employeeId == null) {
				errors.rejectValue(fieldPrepend+"employeeId", null, null, ValidatorMessages.getString("PersonnelActionNoticeService.0"));
			}

			if(personnelActionNotice.getDate() == null) {
				errors.rejectValue(fieldPrepend+"date", null, null, ValidatorMessages.getString("PersonnelActionNoticeService.1"));
			}

			ActionNotice actionNotice = null;
			if(personnelActionNotice.getActionNoticeId() == null) {
				errors.rejectValue(fieldPrepend+"actionNoticeId", null, null, ValidatorMessages.getString("PersonnelActionNoticeService.2"));
			} else {
				actionNotice = actionNoticeService.getActionNotice(personnelActionNotice.getActionNoticeId());
			}

			if(actionNotice != null && !actionNotice.isActive()) {
				errors.rejectValue(fieldPrepend+"actionNoticeId", null, null, ValidatorMessages.getString("PersonnelActionNoticeService.3"));
			}

			//Validate Documents
			refDocService.validateReferences(personnelActionNotice.getReferenceDocuments(), errors, fieldPrepend);
			//Validate form status
			FormWorkflow workflow = personnelActionNotice.getId() != 0 ? getFormWorkflow(personnelActionNotice.getId()) : null;
			String workflowError = ValidationUtil.validateFormStatus(formStatusService, workflow);
			if (workflowError != null ) {
				errors.rejectValue(fieldPrepend+"formWorkflowId", null, null, workflowError);
			}
		}
	}

	/**
	 * Get the personnel action notice object by it's id.
	 * @param panId The personnel action notice id.
	 * @return The personnel action notice object.
	 */
	public PersonnelActionNotice getPersonnelActionNotice(Integer panId) {
		return personnelActionNoticeDao.get(panId);
	}

	/**
	 * Get the list of action notices
	 * @return
	 */
	public List<ActionNotice> getActionNotices(Integer actionNoticeId) {
		return actionNoticeDao.getActionNotices(actionNoticeId);
	}

	public List<FormSearchResult> searchForm(String searchCriteria, User user) {
		List<PersonnelActionNotice> personnelActionNotices =
				personnelActionNoticeDao.searchActionNotices(searchCriteria, user);
		List<FormSearchResult> results = new ArrayList<>();
		List<ResultProperty> properties = null;
		for(PersonnelActionNotice personnelActionNotice : personnelActionNotices){
			String title = "Personnel Action Notice No. " + personnelActionNotice.getSequenceNo().toString();
			properties = new ArrayList<>();
			properties.add(ResultProperty.getInstance("Company", personnelActionNotice.getCompany().getNumberAndName()));
			properties.add(ResultProperty.getInstance("Employee", personnelActionNotice.getEmployee().getFullName()));
			properties.add(ResultProperty.getInstance("Position", personnelActionNotice.getEmployee().getPosition().getName()));
			properties.add(ResultProperty.getInstance("Division", personnelActionNotice.getEmployee().getDivision().getName()));
			properties.add(ResultProperty.getInstance("Date", DateUtil.formatDate(personnelActionNotice.getDate())));
			properties.add(ResultProperty.getInstance("Action Notice",personnelActionNotice.getActionNotice().getName()));
			properties.add(ResultProperty.getInstance("Justification", personnelActionNotice.getJustification()));
			String status = personnelActionNotice.getFormWorkflow().getCurrentFormStatus().getDescription();
			properties.add(ResultProperty.getInstance("Status", StringFormatUtil.formatToLowerCase(status)));
			results.add(FormSearchResult.getInstanceOf(personnelActionNotice.getId(), title, properties));
		}
		return results;
	}

	public List<Employee> getEmployees(Integer companyId, String name) {
		return employeeDao.getEmployeeByCompanyAndName(companyId, name, false);
	}

	/**
	 * Get the personnel action notice object by personnel action notice id
	 * and with employee full name format.
	 * @param panId The personnel action notice id.
	 * @param isFirstNameFirst True if full name format is first name first, otherwise false.
	 * @return The personnel action notice object.
	 */
	public PersonnelActionNotice getPAN(Integer panId, boolean isFirstNameFirst) {
		PersonnelActionNotice personnelActionNotice = getPersonnelActionNotice(panId);
		Employee employee = employeeDao.get(personnelActionNotice.getEmployeeId());
		EmployeeProfile employeeProfile = employeeProfileService.getEmployeeProfile(personnelActionNotice.getEmployeeId());
		String employeeName = "";
		if(isFirstNameFirst) {
			String middleInitial = employee.getMiddleName() != null && !employee.getMiddleName().isEmpty()
					? employee.getMiddleName().charAt(0) + ". " : "";
			employeeName = employee.getFirstName() + " " + middleInitial + employee.getLastName();
		} else {
			employeeName = employee.getLastName()+", "+employee.getFirstName();
			if(employee.getMiddleName() != null && !employee.getMiddleName().isEmpty()) {
				employeeName += " "+employee.getMiddleName();
			}
		}
		personnelActionNotice.setEmployeeFullName(employeeName);
		personnelActionNotice.setEmployeePosition(positionService.getEmployeePosition(personnelActionNotice.getEmployeeId()).getName());
		personnelActionNotice.setEmployeeDivision(divisionService.getEmployeeDivision(personnelActionNotice.getEmployeeId()).getName());
		if (employeeProfile != null) {
			personnelActionNotice.setHiredDate(DateUtil.formatDate(employeeProfile.getHiredDate()));
		}
		return personnelActionNotice;
	}

	/**
	 * Get the personnel action notice object by personnel action notice id
	 * @param panId The personnel action notice id.
	 * @return The personnel action notice object.
	 */
	public PersonnelActionNotice getPAN(Integer panId) {
		return getPAN(panId, false);
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";

		PersonnelActionNotice personnelActionNotice = getPAN(personnelActionNoticeDao.getByEbObjectId(ebObjectId).getId());
		Integer pId = personnelActionNotice.getId();
		FormProperty property = workflowHandler.getProperty(personnelActionNotice.getWorkflowName(), user);
		String popupLink = "/personnelActionNotice/form?pId=" + pId;
		String printOutLink = "/" + property.getPrint() + "?pId=" + pId;

		String latestStatus = personnelActionNotice.getFormWorkflow().getCurrentFormStatus().getDescription();
		String title = "Personnel Action Notice - " + personnelActionNotice.getSequenceNo();
		shortDescription = new StringBuffer(title)
				.append(" " + personnelActionNotice.getEmployeeFullName())
				.append(" " + personnelActionNotice.getEmployeePosition())
				.append(" " + DateUtil.formatDate(personnelActionNotice.getDate()))
				.append("<b> DATE HIRED: </b>" + personnelActionNotice.getHiredDate())
				.append(" Type of Action Notice " + personnelActionNotice.getActionNotice().getName());

		return ObjectInfo.getInstance(ebObjectId, title, latestStatus,
				shortDescription.toString(), fullDescription, popupLink, printOutLink);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		int ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
		case PersonnelActionNotice.OBJECT_TYPE_ID:
			return personnelActionNoticeDao.getByEbObjectId(ebObjectId);
		case ReferenceDocument.OBJECT_TYPE_ID:
			return referenceDocDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}
}
