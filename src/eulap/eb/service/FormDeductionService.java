package eulap.eb.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.domain.Domain;
import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.DeductionTypeDao;
import eulap.eb.dao.EmployeeDao;
import eulap.eb.dao.FormDeductionDao;
import eulap.eb.dao.FormDeductionLineDao;
import eulap.eb.dao.ReferenceDocumentDao;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.DeductionType;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.Employee;
import eulap.eb.domain.hibernate.FormDeduction;
import eulap.eb.domain.hibernate.FormDeductionLine;
import eulap.eb.domain.hibernate.FormDeductionType;
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
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.ResultProperty;

/**
 * Handles business logic of {@link FormDeduction}

 *
 */
@Service
public class FormDeductionService  extends BaseWorkflowService{
	@Autowired
	private DeductionTypeDao deductionTypeDao;
	@Autowired
	private FormDeductionDao formDeductionDao;
	@Autowired
	private FormDeductionLineDao formDeductionLineDao;
	@Autowired
	private ReferenceDocumentDao referenceDocumentDao;
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private PositionService positionService;
	@Autowired
	private ReferenceDocumentService referenceDocumentService;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private FormStatusService formStatusService;

	private static final int FIRST_CUT_OFF_DAY = 15;
	private static final int SECOND_CUT_OFF_DAY = 30;

	/**
	 * Get the list of deduction types.
	 * @param deductionTypeId The deduction Type Id.
	 * @return The list of deduction types.
	 */
	public List<DeductionType> getDeductionTypes(Integer deductionTypeId){
		return deductionTypeDao.getDeductionTypes(deductionTypeId);
	}

	/**
	 * Get the list of FormDeductionLine By EbObjectId
	 * @param ebObjectId The ebObjectId
	 * @return The list of FormDeductionLines
	 */
	public List<FormDeductionLine> getFormDeductionLinesByEbObject(Integer ebObjectId){
		return formDeductionLineDao.getFormDeductionLineByEbObject(ebObjectId);
	}

	/**
	 * Process Form to deduct line.
	 * @param date The starting date.
	 * @param amountToDeduct The amount to deduct.
	 * @param noPayrollDeduction The number of lines.
	 * @param dates The list of dates in string format.
	 * @return Processed {@link FormDeductionLine}
	 */
	public List<FormDeductionLine> processDeductionLine(Date date, Double amountToDeduct, Integer noPayrollDeduction, String dates) {
		List<FormDeductionLine> deductionLines = new ArrayList<>();
		FormDeductionLine deductionLine = null;
		Date deductionDate = setFirstDate(date);
		String[] strDate= null;
		if(dates != null && !dates.trim().isEmpty()){
			strDate = dates.split(";");
			noPayrollDeduction = strDate.length;
		}
		Double amount = amountToDeduct/noPayrollDeduction;
		for (int i = 0; i < noPayrollDeduction; i++) {
			if(dates != null && !dates.trim().isEmpty()){
				deductionDate = DateUtil.parseDate(strDate[i]);
			} else if(i != 0){
				deductionDate = setFirstDate(DateUtil.addDaysToDate(deductionDate, 1));
			}
			deductionLine = new FormDeductionLine();
			deductionLine.setAmount(amount);
			deductionLine.setDate(setFirstDate(deductionDate));
			deductionLines.add(deductionLine);
		}
		return deductionLines;
	}

	private Date setFirstDate(Date date) {
		if(date != null) {
			Date firstDate = new Date();
			Calendar cal10 = new GregorianCalendar();
			cal10.setTime(date);
			cal10.set(Calendar.DATE, FIRST_CUT_OFF_DAY);
			cal10.getTime();
			Date date10 = cal10.getTime();
			Calendar calEnd = new GregorianCalendar();
			calEnd.setTime(date);
			int endDate = calEnd.getActualMaximum(Calendar.DAY_OF_MONTH);
			if(endDate < SECOND_CUT_OFF_DAY){
				calEnd.set(Calendar.DATE, endDate);
			} else {
				calEnd.set(Calendar.DATE, SECOND_CUT_OFF_DAY);
			}
			Date date25 = calEnd.getTime();
			if(date.before(date10) || date10.equals(date)){
				firstDate = date10;
			} else if(date.before(date25) || date25.equals(date)){
				firstDate = date25;
			} else {
				firstDate = DateUtil.addMonthsToDate(date10, 1);
			}
			return firstDate;
		}
		return null;
	}

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return formDeductionDao.get(id).getFormWorkflow();
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer ebObjectId) {
		return formDeductionDao.getByEbObjectId(ebObjectId);
	}

	/**
	 * Get the Form Deduction by id.
	 * @param frmDeductionId The id of the form deduction object.
	 * @return The FormDeduction Object.
	 */
	public FormDeduction getFormDeduction(Integer frmDeductionId){
		return getFormDeduction(frmDeductionId, false);
	}

	/**
	 * Get the Form Deduction by id with employee full name format..
	 * @param frmDeductionId The id of the form deduction object.
	 * @param isFirstNameFirst True if full name format is first name first, otherwise false.
	 * @return The FormDeduction Object.
	 */
	public FormDeduction getFormDeduction(Integer frmDeductionId, boolean isFirstNameFirst){
		FormDeduction formDeduction = formDeductionDao.get(frmDeductionId);
		Integer ebObjectId = formDeduction.getEbObjectId();
		Employee employee = employeeDao.get(formDeduction.getEmployeeId());
		String employeeFullName = null;
		if(isFirstNameFirst) {
			String middleInitial = employee.getMiddleName() != null && !employee.getMiddleName().isEmpty()
					? employee.getMiddleName().charAt(0) + ". " : "";
			employeeFullName = employee.getFirstName() + " " + middleInitial + employee.getLastName();
		} else {
			employeeFullName = employee.getFullName();
		}
		formDeduction.setEmployeeFullName(employeeFullName);
		formDeduction.setEmployeePosition(positionService.getEmployeePosition(formDeduction.getEmployeeId()).getName());
		List<FormDeductionLine> formDeductionLines = getFormDeductionLinesByEbObject(ebObjectId);
		formDeduction.setFormDeductionLines(formDeductionLines);
		formDeduction.setNoOfPayrollDeduction(formDeduction.getFormDeductionLines().size());
		formDeduction.setTotalDeductionAmount(computeTotalAmount(formDeductionLines));
		formDeduction.setReferenceDocuments(referenceDocumentService.getReferenceDocuments(ebObjectId));
		return formDeduction;
	}

	@Override
	public void preFormSaving(BaseFormWorkflow form, String workflowName, User user) {
		FormDeduction formDeduction = (FormDeduction) form;
		if(formDeduction.getEbObjectId() != null) {
			Integer ebObjectId = formDeduction.getEbObjectId();
			List<Integer> toBeDeletedFDLines = new ArrayList<>();
			List<FormDeductionLine> savedFDLines =
					getFormDeductionLinesByEbObject(ebObjectId);
			for(FormDeductionLine formDeductionLine : savedFDLines){
				toBeDeletedFDLines.add(formDeductionLine.getId());
			}
			formDeductionLineDao.delete(toBeDeletedFDLines);
			toBeDeletedFDLines = null;

			List<ReferenceDocument> toBeDeletedRefDocs = referenceDocumentService.getReferenceDocuments(formDeduction.getEbObjectId());
			for(ReferenceDocument reDocument : toBeDeletedRefDocs){
				referenceDocumentDao.delete(reDocument);
			}
		}
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		FormDeduction formDeduction = (FormDeduction) form;
		boolean isNew = formDeduction.getId() == 0;
		AuditUtil.addAudit(formDeduction, new Audit(user.getId(), isNew, new Date()));
		if(formDeduction.getFormDeductionTypeId() == FormDeductionType.TYPE_AUTHORITY_TO_DEDUCT){
			formDeduction.setRemarks(formDeduction.getRemarks().trim());
		}
		if(isNew){
			workflowHandler.processFormWorkflow(FormDeduction.class.getSimpleName() +
					formDeduction.getFormDeductionTypeId(), user, formDeduction);
			formDeduction.setSequenceNumber(formDeductionDao.generateSequenceNo(
					formDeduction.getFormDeductionTypeId()));
		} else {
			FormDeduction savedFormDeduction = getFormDeduction(formDeduction.getId());
			DateUtil.setCreatedDate(formDeduction, savedFormDeduction.getCreatedDate());
		}
		formDeductionDao.saveOrUpdate(formDeduction);

		for(FormDeductionLine formDeductionLine : formDeduction.getFormDeductionLines()){
			if(formDeductionLine.getDate() != null && formDeductionLine.getAmount() != null
					&& !formDeductionLine.getAmount().equals(0.00)){
				formDeductionLineDao.save(formDeductionLine);
			}
		}

		for(ReferenceDocument referenceDocument : formDeduction.getReferenceDocuments()){
			if(referenceDocument.getDescription() != null){
				referenceDocument.setDescription(StringFormatUtil.removeExtraWhiteSpaces(referenceDocument.getDescription()));
				referenceDocumentDao.save(referenceDocument);
			}
		}
	}

	public void validate(FormDeduction formDeduction, Errors errors){
		if(formDeduction != null){
			if(formDeduction.getFormDate() == null){
				errors.rejectValue("formDate", null, null, ValidatorMessages.getString("FormDeductionService.0"));
			}
			if(formDeduction.getEmployeeFullName() == null || formDeduction.getEmployeeFullName().trim().isEmpty()){
				errors.rejectValue("employeeId", null, null, ValidatorMessages.getString("FormDeductionService.1"));
			} else if(formDeduction.getEmployeeId() == null){
				errors.rejectValue("employeeId", null, null, ValidatorMessages.getString("FormDeductionService.2"));
			} else if(!employeeDao.get(formDeduction.getEmployeeId()).isActive()){
				errors.rejectValue("employeeId", null, null, ValidatorMessages.getString("FormDeductionService.3"));
			}
			if(formDeduction.getDeductionTypeId() == null){
				errors.rejectValue("deductionTypeId", null, null, ValidatorMessages.getString("FormDeductionService.4"));
			} else {
				formDeduction.setDeductionType(deductionTypeDao.get(formDeduction.getDeductionTypeId()));
				if(!formDeduction.getDeductionType().isActive()){
					errors.rejectValue("deductionTypeId", null, null, ValidatorMessages.getString("FormDeductionService.5"));
				}
			}
			if(formDeduction.getCompanyId() == null){
				errors.rejectValue("companyId", null, null, ValidatorMessages.getString("FormDeductionService.6"));
			} else {
				ValidatorUtil.checkInactiveCompany(companyService, formDeduction.getCompanyId(), "companyId", errors, ValidatorMessages.getString("FormDeductionService.7"));
			}
			if(formDeduction.getStartDate() == null){
				errors.rejectValue("startDate", null, null, ValidatorMessages.getString("FormDeductionService.8"));
			}
			if(formDeduction.getDivisionId() != null){
				ValidatorUtil.checkInactiveDivision(divisionService, formDeduction.getDivisionId(), "divisionId", errors, ValidatorMessages.getString("FormDeductionService.9"));
			} else {
				errors.rejectValue("divisionId", null, null, ValidatorMessages.getString("FormDeductionService.10"));
			}

			List<FormDeductionLine> formDeductionLines = formDeduction.getFormDeductionLines();
			if(formDeductionLines == null){
				errors.rejectValue("formDeductionMessage", null, null, ValidatorMessages.getString("FormDeductionService.11"));
			} else {
				if(hasNoAmount(formDeductionLines)) {
					errors.rejectValue("formDeductionMessage", null, null, ValidatorMessages.getString("FormDeductionService.12"));
				} else if (hasNegativeValue(formDeductionLines)) {
						errors.rejectValue("formDeductionMessage", null, null, ValidatorMessages.getString("FormDeductionService.13"));
				}

				if(hasNoDate(formDeductionLines) && !hasNoAmount(formDeductionLines)) {
					errors.rejectValue("formDeductionMessage", null, null, ValidatorMessages.getString("FormDeductionService.14"));
				}

				if(hasDuplicateDate(formDeductionLines)) {
					errors.rejectValue("formDeductionMessage", null, null, ValidatorMessages.getString("FormDeductionService.15"));
				}
			}
			referenceDocumentService.validateReferences(formDeduction.getReferenceDocuments(), errors);
			//Validate form status
			FormWorkflow workflow = formDeduction.getId() != 0 ? getFormWorkflow(formDeduction.getId()) : null;
			String workflowError = ValidationUtil.validateFormStatus(formStatusService, workflow);
			if (workflowError != null ) {
				errors.rejectValue("formWorkflowId", null, null, workflowError);
			}
		}
	}

	private Boolean hasNegativeValue(List<FormDeductionLine> formDeductionLines){
		Boolean hasNegativeAmount = false;
		for(FormDeductionLine fdl : formDeductionLines){
			if(fdl.getAmount() < 0.0){
				hasNegativeAmount = true;
			}
		}
		return hasNegativeAmount;
	}

	private Boolean hasNoAmount(List<FormDeductionLine> formDeductionLines){
		Boolean hasNoAmount = false;
		for(FormDeductionLine fdl : formDeductionLines){
			if(fdl.getDate() != null && (fdl.getAmount() == null || fdl.getAmount().equals(0.0))){
				hasNoAmount = true;
			}
		}
		return hasNoAmount;
	}

	private Boolean hasNoDate(List<FormDeductionLine> formDeductionLines){
		Boolean hasNoDate = false;
		for(FormDeductionLine fdl : formDeductionLines){
			if(fdl.getDate() == null && fdl.getAmount() != null && !fdl.getAmount().equals(0.0)){
				hasNoDate = true;
			}
		}
		return hasNoDate;
	}

	private Double computeTotalAmount(List<FormDeductionLine> formDeductionLines){
		double totalAmount = 0.0;
		for(FormDeductionLine fLines : formDeductionLines){
			totalAmount += fLines.getAmount();
		}
		return NumberFormatUtil.roundOffTo2DecPlaces(totalAmount);
	}

	private boolean hasDuplicateDate (List<FormDeductionLine> formDeductionLines) {
		if (formDeductionLines != null && !formDeductionLines.isEmpty()) {
			Map<Date, FormDeductionLine> itemHM = new HashMap<Date, FormDeductionLine>();
			for (FormDeductionLine i : formDeductionLines) {
				if (itemHM.containsKey(i.getDate()) && i.getDate() != null) {
					return true;
				} else {
					itemHM.put(i.getDate(), i);
				}
			}
		}
		return false;
	}

	/**
	 * Get the list of FormDeduction by criteria and by type.
	 * @param searchCriteria The search criteria.
	 * @param The type id of the FormDeduction.
	 * @param user The current logged user.
	 * @return The list of FomrDeductions.
	 */
	public List<FormSearchResult> search(String searchCriteria, Integer typeId, User user){
		List<FormDeduction> formDeductions =
				formDeductionDao.searchFormDeductions(searchCriteria, typeId, user);
		List<FormSearchResult> results = new ArrayList<>();
		List<ResultProperty> properties = null;
		for(FormDeduction fDeduction : formDeductions){
			StringBuilder title = new StringBuilder();
			if(typeId == FormDeductionType.TYPE_AUTHORITY_TO_DEDUCT){
				title.append("Authority to Deduct ");
			} else {
				title.append("Cash Bond Contract ");
			}
			title.append("No. " + fDeduction.getSequenceNumber());
			properties = new ArrayList<>();
			properties.add(ResultProperty.getInstance("Branch", fDeduction.getCompany().getNumberAndName()));
			properties.add(ResultProperty.getInstance("Deduction Type", fDeduction.getDeductionType().getName()));
			properties.add(ResultProperty.getInstance("Date", DateUtil.formatDate(fDeduction.getFormDate())));
			if(fDeduction.getRemarks() != null) {
				properties.add(ResultProperty.getInstance("Remarks", fDeduction.getRemarks()));
			}
			String status = fDeduction.getFormWorkflow().getCurrentFormStatus().getDescription();
			properties.add(ResultProperty.getInstance("Status", StringFormatUtil.formatToLowerCase(status)));
			results.add(FormSearchResult.getInstanceOf(fDeduction.getId(), title.toString(), properties));
		}
		return results;
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";

		FormDeduction formDeduction = formDeductionDao.getByEbObjectId(ebObjectId);
		Integer typeId = formDeduction.getFormDeductionTypeId();
		Integer pId = formDeduction.getId();
		FormProperty property = workflowHandler.getProperty(formDeduction.getWorkflowName(), user);
		String popupLink = "/formDeduction/"+typeId+"/form?pId=" +  pId;
		String printOutLink = "/" + property.getPrint() + "?pId=" + pId;
		String latestStatus = formDeduction.getFormWorkflow().getCurrentFormStatus().getDescription();

		String formName = typeId == FormDeductionType.TYPE_AUTHORITY_TO_DEDUCT ? "Authority to Deduct" : "Cash Bond Contract";
		String title = formName + " - " + formDeduction.getSequenceNumber();
		shortDescription = new StringBuffer(title)
				.append(" " + formDeduction.getEmployeeFullName())
				.append(" " + formDeduction.getEmployeePosition())
				.append(" " + DateUtil.formatDate(formDeduction.getFormDate()))
				.append("<b> START DATE : </b>" + DateUtil.formatDate(formDeduction.getStartDate()))
				.append(" " + formDeduction.getTotalDeductionAmount());

		return ObjectInfo.getInstance(ebObjectId, title, latestStatus,
				shortDescription.toString(), fullDescription, popupLink, printOutLink);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		int ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
		case FormDeduction.OBJECT_TYPE_ID:
			return formDeductionDao.getByEbObjectId(ebObjectId);
		case FormDeductionLine.OBJECT_TYPE_ID:
			return formDeductionLineDao.getByEbObjectId(ebObjectId);
		case ReferenceDocument.OBJECT_TYPE_ID:
			return referenceDocumentDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}
}
