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
import eulap.eb.dao.DocumentTypeDao;
import eulap.eb.dao.EmployeeDao;
import eulap.eb.dao.EmployeeDocumentDao;
import eulap.eb.dao.ReferenceDocumentDao;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.DocumentType;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.Employee;
import eulap.eb.domain.hibernate.EmployeeDocument;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.fap.FormStatusService;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.ResultProperty;

/**
 * Handles business logic of {@link EmployeeDocument}

 *
 */
@Service
public class EmployeeDocumentService extends BaseWorkflowService{
	@Autowired
	private EmployeeDocumentDao employeeDocumentDao;
	@Autowired
	private DocumentTypeDao documentTypeDao;
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private ReferenceDocumentDao referenceDocumentDao;
	@Autowired
	private ReferenceDocumentService referenceDocumentService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private FormStatusService formStatusService;

	/**
	 * Get the list of active {@link DocumentType}
	 * @return List of active {@link DocumentType}
	 */
	public List<DocumentType> getActiveDocTypes () {
		return documentTypeDao.getActiveDocTypes();
	}

	/**
	 * Get the object {@link EmployeeDocument by id with full name format.}
	 * @param employeeDocumentId The id.
	 * @param isIncludeLines True - get the reference documents. False - the main object only.
	 * @param isFirstNameFirst True if full name format is first name first, otherwise false.
	 * @return The {@link EmployeeDocument}
	 */
	public EmployeeDocument getById (Integer employeeDocumentId, boolean isIncludeLines, boolean isFirstNameFirst) {
		if (!isIncludeLines) {
			return employeeDocumentDao.get(employeeDocumentId);
		}
		EmployeeDocument employeeDocument = employeeDocumentDao.get(employeeDocumentId);
		if (employeeDocument != null && employeeDocument.getEbObjectId() != null) {
			List<ReferenceDocument> referenceDocuments = 
					referenceDocumentDao.getRDsByEbObject(employeeDocument.getEbObjectId());
			employeeDocument.setReferenceDocuments(referenceDocuments);
		}
		Employee employee = employeeDao.get(employeeDocument.getEmployeeId());
		if(isFirstNameFirst) {
			String middleInitial = employee.getMiddleName() != null && !employee.getMiddleName().isEmpty()
					? employee.getMiddleName().charAt(0) + ". " : "";
			employeeDocument.setEmployeeName(employee.getFirstName() + " " + middleInitial + employee.getLastName());
		}
		return employeeDocument;
	}

	/**
	 * Get the object {@link EmployeeDocument} by id. 
	 * @param employeeDocumentId The id.
	 * @param isIncludeLines True - get the reference documents. False - the main object only.
	 * @return The {@link EmployeeDocument}
	 */
	public EmployeeDocument getById (Integer employeeDocumentId, boolean isIncludeLines) {
		return getById(employeeDocumentId, isIncludeLines, false);
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer ebObjectId) {
		return employeeDocumentDao.getByEbObjectId(ebObjectId);
	}

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return employeeDocumentDao.get(id).getFormWorkflow();
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		EmployeeDocument employeeDocument = (EmployeeDocument) form;
		boolean isNew = employeeDocument.getId() == 0;
		AuditUtil.addAudit(employeeDocument, new Audit(user.getId(), isNew, new Date()));

		validate(employeeDocument);
		if (!employeeDocument.getResult().hasErrors()) {
			if (isNew) {
				employeeDocument.setSequenceNo(employeeDocumentDao.generateSequenceNo());
			} else {
				EmployeeDocument savedED = getById(employeeDocument.getId(), false);
				DateUtil.setCreatedDate(employeeDocument, savedED.getCreatedDate());
			}
			employeeDocumentDao.saveOrUpdate(employeeDocument);
			saveRefDocs(isNew, employeeDocument);
		}
	}

	private void saveRefDocs (boolean isNew, EmployeeDocument employeeDocument) {
		if (!isNew) {
			List<ReferenceDocument> savedRDocs = 
					referenceDocumentDao.getRDsByEbObject(employeeDocument.getEbObjectId());
			if (savedRDocs != null && !savedRDocs.isEmpty()) {
				List<Integer> ids = new ArrayList<>();
				for (ReferenceDocument rd : savedRDocs) {
					ids.add(rd.getId());
				}
				referenceDocumentDao.delete(ids);
			}
		}
		List<Domain> toBeSaved = new ArrayList<>();
		List<ReferenceDocument> referenceDocuments = employeeDocument.getReferenceDocuments();
		if (referenceDocuments != null && !referenceDocuments.isEmpty()) {
			for (ReferenceDocument rd : referenceDocuments) {
				toBeSaved.add(rd);
			}
			referenceDocumentDao.batchSave(toBeSaved);
		}
	}

	private void validate(EmployeeDocument employeeDocument) {
		Errors errors = employeeDocument.getResult();
		if(employeeDocument.getCompanyId() == null) {
			errors.rejectValue("companyId", null, null, ValidatorMessages.getString("EmployeeDocumentService.0"));
		} else{
			Company company = companyService.getCompany(employeeDocument.getCompanyId());
			if(!company.isActive()) {
				errors.rejectValue("companyId", null, null, ValidatorMessages.getString("EmployeeDocumentService.1"));
			}
		}

		if (employeeDocument.getDate() == null) {
			errors.rejectValue("date", null, null, ValidatorMessages.getString("EmployeeDocumentService.2"));
		}

		if (employeeDocument.getEmployeeId() == null) {
			errors.rejectValue("employeeId", null, null, ValidatorMessages.getString("EmployeeDocumentService.3"));
		}

		if (employeeDocument.getDocumentTypeId() == null) {
			errors.rejectValue("documentTypeId", null, null, ValidatorMessages.getString("EmployeeDocumentService.4"));
		}

		if (employeeDocument.getReferenceDocuments() != null && employeeDocument.getReferenceDocuments().isEmpty()) {
			errors.rejectValue("referenceDocsMessage", null, null, ValidatorMessages.getString("EmployeeDocumentService.5"));
		}

		// Included ref doc validation.
		referenceDocumentService.validateReferences(employeeDocument.getReferenceDocuments(), errors);
		//Validate form status
		FormWorkflow workflow = employeeDocument.getId() != 0 ? getFormWorkflow(employeeDocument.getId()) : null;
		String workflowError = ValidationUtil.validateFormStatus(formStatusService, workflow);
		if (workflowError != null ) {
			errors.rejectValue("formWorkflowId", null, null, workflowError);
		}
	}

	public List<Employee> getEmployeesByName(String name) {
		return employeeDao.getEmployeesByName(null, name);
	}

	/**
	 * Get the list of EmployeeDocument by criteria.
	 * @param searchCriteria The searchCriteria.
	 * @param user The current logged user.
	 * @return
	 */
	public List<FormSearchResult> searchEmployeeDocument(String searchCriteria, User user){
		List<EmployeeDocument> employeeDocuments =
				employeeDocumentDao.searchEmployeeDocuments(searchCriteria, user);
		List<FormSearchResult> results = new ArrayList<>();
		List<ResultProperty> properties = null;
		for(EmployeeDocument employeeDocument : employeeDocuments){
			String title = "Employee Document No. " + 
					employeeDocument.getSequenceNo().toString();
			properties = new ArrayList<>();
			properties.add(ResultProperty.getInstance("Company/Branch", employeeDocument.getCompany().getName())); 
			properties.add(ResultProperty.getInstance("Document Type", employeeDocument.getDocumentType().getName())); 
			properties.add(ResultProperty.getInstance("Date", DateUtil.formatDate(employeeDocument.getDate()))); 
			if(employeeDocument.getRemarks() != null) {
				properties.add(ResultProperty.getInstance("Remarks", employeeDocument.getRemarks())); 
			}
			String status = employeeDocument.getFormWorkflow().getCurrentFormStatus().getDescription();
			properties.add(ResultProperty.getInstance("Status", StringFormatUtil.formatToLowerCase(status))); 
			results.add(FormSearchResult.getInstanceOf(employeeDocument.getId(), title, properties));
		}
		return results;
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";

		EmployeeDocument employeeDocument = employeeDocumentDao.getByEbObjectId(ebObjectId);
		Integer pId = employeeDocument.getId();
		FormProperty property = workflowHandler.getProperty(employeeDocument.getWorkflowName(), user);
		String popupLink = "/" + property.getEdit()  + "?pId=" + pId;
		String printOutLink = "/" + property.getPrint() + "?pId=" + pId;

		String latestStatus = employeeDocument.getFormWorkflow().getCurrentFormStatus().getDescription();
		String title = "Employee Document - " + employeeDocument.getSequenceNo();
		shortDescription = new StringBuffer(title)
				.append(" " + employeeDocument.getEmployee().getFullName())
				.append(" " + employeeDocument.getEmployee().getPosition().getName())
				.append(" " + DateUtil.formatDate(employeeDocument.getDate()));

		return ObjectInfo.getInstance(ebObjectId, title, latestStatus,
				shortDescription.toString(), fullDescription, popupLink, printOutLink);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		Integer ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
		case EmployeeDocument.OBJECT_TYPE_ID:
			return employeeDocumentDao.getByEbObjectId(ebObjectId);
		case ReferenceDocument.OBJECT_TYPE_ID:
			return referenceDocumentDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}

}
