package eulap.eb.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.domain.Domain;
import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.AccountCombinationDao;
import eulap.eb.dao.CompanyDao;
import eulap.eb.dao.DivisionDao;
import eulap.eb.dao.FormWorkflowLogDao;
import eulap.eb.dao.GeneralLedgerDao;
import eulap.eb.dao.GlEntryDao;
import eulap.eb.dao.GlEntrySourceDao;
import eulap.eb.dao.GlStatusDao;
import eulap.eb.dao.TimePeriodDao;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.GeneralLedger;
import eulap.eb.domain.hibernate.GlEntry;
import eulap.eb.domain.hibernate.GlEntrySource;
import eulap.eb.domain.hibernate.GlStatus;
import eulap.eb.domain.hibernate.TimePeriod;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.GeneralLedgerDto;
import eulap.eb.web.dto.ResultProperty;
import net.sf.json.JSONObject;

/**
 * Service for General Ledger.

 *
 */
@Service
public class GeneralLedgerService extends BaseWorkflowService{
	@Autowired
	private GeneralLedgerDao generalLedgerDao;
	@Autowired
	private GlStatusDao glStatusDao;
	@Autowired
	private GlEntrySourceDao glEntrySourceDao;
	@Autowired
	private TimePeriodDao timePeriodDao;
	@Autowired
	private CompanyDao companyDao;
	@Autowired
	private DivisionDao divisionDao;
	@Autowired
	private AccountService accountService;
	@Autowired
	private AccountCombinationService combinationService;
	@Autowired
	private GlEntryService glEntryService;
	@Autowired
	private GlEntryDao glEntryDao;
	@Autowired
	private AccountCombinationDao accountCombinationDao;
	@Autowired
	private FormWorkflowLogDao formWorkflowLogDao;
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private ReferenceDocumentService refDocumentService;
	private final Logger logger = Logger.getLogger(GeneralLedgerService.class);

	/**
	 * Generate sequence number.
	 * @return Generated sequence number.
	 */
	public int generateSequenceNo (int divisionId) {
		return generalLedgerDao.generateSequenceNo(divisionId);
	}
	
	/**
	 * Get the general ledger object by id.
	 * @param generalLedgerId The general ledger id.
	 * @return The general ledger domain object.
	 */
	public GeneralLedger getGeneralLedger (int generalLedgerId) throws IOException {
		GeneralLedger gl = generalLedgerDao.getGL(generalLedgerId);
		gl.setReferenceDocuments(refDocumentService.processReferenceDocs(gl.getEbObjectId()));
		return gl;
	}

	/**
	 * Get the general ledger object by id.
	 * @param generalLedgerId The general ledger id.
	 * @return The general ledger domain object.
	 */
	public GeneralLedger getGLPdf (int generalLedgerId) {
		GeneralLedger generalLedger = generalLedgerDao.getGL(generalLedgerId);
		if (generalLedger != null) {
			FormWorkflow formWorkflow = generalLedger.getFormWorkflow();
			if (formWorkflow != null) {
				FormWorkflowLog log = formWorkflowLogDao.getFormWorkflowLog(formWorkflow.getId(), FormStatus.CREATED_ID);
				generalLedger.setPreparedPosition(log.getCreated().getPosition());
				if (formWorkflow.getCurrentStatusId() == FormStatus.POSTED_ID) {
					log = formWorkflowLogDao.getFormWorkflowLog(formWorkflow.getId(), FormStatus.POSTED_ID);
					generalLedger.setApprovedBy(log.getCreated());
					generalLedger.setApprovedDate(log.getCreatedDate());
					generalLedger.setApprovedPosition(log.getCreated().getPosition());
				}
			}
		}
		Double rate = generalLedger.getCurrencyRateValue();
		Collection<GlEntry> glEntries = processGlLines((List<GlEntry>) generalLedger.getGlEntries(),
				rate != null ? rate : 1.0);
		generalLedger.setGlEntries(glEntries);
		return generalLedger;
	}

	/**
	 * Process GL entry lines
	 * @param glEntries The GL entry lines
	 * @param rate The currency rate
	 * @return The processed list of GL entry lines
	 */
	public Collection<GlEntry> processGlLines(List<GlEntry> glEntries, double rate) {
		for (GlEntry glEntry : glEntries) {
			AccountCombination accountCombination = accountCombinationDao.get(glEntry.getAccountCombinationId());
			Division division = accountCombination.getDivision();
			Account account = accountCombination.getAccount();
			glEntry.setAccountNo(account.getNumber());
			glEntry.setAccountName(division.getName() + "-" + account.getAccountName());
			glEntryService.setAmount(glEntry, rate); // debit/credit amount
			glEntry.setAmount(CurrencyUtil.convertMonetaryValues(glEntry.getAmount(), rate));
		}
		return glEntries;
	}

	/**
	 * Convert the json object to general ledger domain object.
	 * @param jsonGeneralLedger The json object.
	 * @return The converted general ledger domain object.
	 */
	public GeneralLedger convertToGeneralLedger (JSONObject jsonGeneralLedger) {
		GeneralLedger generalLedger = new GeneralLedger();
		if (jsonGeneralLedger.containsKey("sequenceNo"))
			generalLedger.setSequenceNo(Integer.valueOf(jsonGeneralLedger.get("sequenceNo").toString()));
		if (jsonGeneralLedger.containsKey("glStatusId"))
			generalLedger.setGlStatusId(Integer.valueOf(jsonGeneralLedger.get("glStatusId").toString()));
		if (jsonGeneralLedger.containsKey("glEntrySourceId"))
			generalLedger.setGlEntrySourceId(Integer.valueOf(jsonGeneralLedger.get("glEntrySourceId").toString()));
		if (jsonGeneralLedger.containsKey("glDate"))
			generalLedger.setGlDate(DateUtil.parseDate(jsonGeneralLedger.get("glDate").toString()));
		if (jsonGeneralLedger.containsKey("comment"))
			generalLedger.setComment(jsonGeneralLedger.get("comment").toString());
		return generalLedger;
	}

	/**
	 * Get the gl status domain based on the id.
	 * @param glStatusId The gl status id.
	 * @return The gl status id.
	 */
	public GlStatus getGlStatus (int glStatusId) {
		return glStatusDao.getGlStatus(glStatusId);
	}

	/**
	 * Get the gl entry source domain based on the id
	 * @param glEntrySourceId The gl entry source id.
	 * @return The gl entry source id.
	 */
	public GlEntrySource getGlEntrySource (int glEntrySourceId) {
		return glEntrySourceDao.getGlEntrySource(glEntrySourceId);
	}

	/**
	 * Get the list of all open time periods
	 * @return Open time periods.
	 */
	public Collection<TimePeriod> getOpenTimePeriods () {
		return timePeriodDao.getOpenTimePeriods();
	}

	/**
	 * Validates the account combinations Company, Division and Account.
	 * @param accountCombinationId The unique id of account combination.
	 * @param glLineField 1 = Company, 2 = Division, 3 = Account
	 * @return True if active, otherwise false.
	 */
	public boolean isActiveAcctCombination(int accountCombinationId, int glLineField) {
		AccountCombination ac = combinationService.getAccountCombination(accountCombinationId);
		boolean isActiveField = true;
		if(ac != null){
			if(glLineField == 1) {
				Company company = companyDao.get(ac.getCompanyId());
				if(company.isActive())
					return true;
			}
			if(glLineField == 2) {
				Division division = divisionDao.get(ac.getDivisionId());
				if(division.isActive())
					return true;
			}
			if(glLineField == 3) {
				Account account = accountService.getAccount(ac.getAccountId());
				if(account.isActive())
					return true;
			}
			isActiveField = false;
		}
		return isActiveField;
	}

	/**
	 * Validate account combination if it is active or not.
	 * @param combinationId The unique id of account combination.
	 * @return True if active otherwise false.
	 */
	public boolean isActiveGlLineCombination(int combinationId) {
		boolean isActive = true;
		if(combinationId !=0){
			AccountCombination ac = combinationService.getAccountCombination(combinationId);
			if(ac.isActive())
				return isActive;
			isActive = false;
		}
		return isActive;
	}

	public List<GeneralLedger> getAllGLs () {
		return (List<GeneralLedger>) generalLedgerDao.getAll();
	}

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return generalLedgerDao.get(id).getFormWorkflow();
	}

	private void saveGeneralLedger (User user, GeneralLedger generalLedger) {
		Date currDate = new Date();
		boolean isNew = generalLedger.getId() == 0;
		if (isNew) {
			generalLedger.setSequenceNo(generalLedgerDao.generateSequenceNo(generalLedger.getDivisionId()));
		}
		AuditUtil.addAudit(generalLedger, new Audit(user.getId(), isNew, currDate));
		// Set currency value
		boolean isValidRate = generalLedger.getCurrencyRateValue() != null && generalLedger.getCurrencyRateValue() != 0;
		double currencyRateValue = isValidRate ? generalLedger.getCurrencyRateValue() : 1.0;
		generalLedger.setCurrencyRateValue(currencyRateValue);
		generalLedgerDao.saveOrUpdate(generalLedger);
		if (!isNew) {
			// Data from database
			List<Integer> toBeDeleted = new ArrayList<Integer>();
			List<GlEntry> dbGLEntry = glEntryDao.getGLEntries(generalLedger.getId());
			// Data from user
			for (GlEntry gl : dbGLEntry) {
				boolean stillExisting = false;
				for (GlEntry entry : generalLedger.getGlEntries()) {
					if (gl.getId() == entry.getId()) {
						stillExisting = true;
						break;
					}
				}
				if (!stillExisting)
					toBeDeleted.add(gl.getId());
			}
			glEntryDao.delete(toBeDeleted);
		}
		for (GlEntry gl : generalLedger.getGlEntries()) {
			if(gl.getAccountCombinationId() != 0) {
				gl.setGeneralLedgerId(generalLedger.getId());
				Double rate = generalLedger.getCurrencyRateValue();
				gl.setAmount(CurrencyUtil.convertAmountToPhpRate(gl.getAmount(),
						rate != null ? rate : 1.0));
				glEntryDao.saveOrUpdate(gl);
			}
		}
		refDocumentService.saveReferenceDocuments(user, isNew, generalLedger.getEbObjectId(),
				generalLedger.getReferenceDocuments(), true);
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		GeneralLedger generalLedger = (GeneralLedger) form;
		saveGeneralLedger(user, generalLedger);
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog,
			BindingResult bindingResult) {
		GeneralLedger generalLedger = generalLedgerDao.getGLByWorkflow(currentWorkflowLog.getFormWorkflowId());
		if (generalLedger != null) {
			// Checks if GL date is open time period.
			Date glDate = generalLedger.getGlDate();
			String message = "";
			if (!timePeriodService.isOpenDate(glDate)) {
				message = "GL date should be in an open time period.";
				bindingResult.reject("workflowMessage", message);
				currentWorkflowLog.setWorkflowMessage(message);
			}
		}
	}
	
	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return generalLedgerDao.getByWorkflowId(workflowId);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		switch (ebObject.getObjectTypeId()) {
		case GeneralLedger.GL_OBJECT_TYPE_ID:
			return generalLedgerDao.getByEbObjectId(ebObject.getId());
		case GlEntry.OBJECT_TYPE_ID:
			return glEntryDao.getByEbObjectId(ebObject.getId());
		}
		return null;
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";

		GeneralLedger gl = generalLedgerDao.getByEbObjectId(ebObjectId);
		Integer pId = gl.getId();
		FormProperty property = workflowHandler.getProperty(gl.getWorkflowName(), user);
		String popupLink = "/" + property.getEdit()  + "?pId=" + pId;
		String printOutLink = "/" + property.getPrint() + "?pId=" + pId;

		String latestStatus = gl.getFormWorkflow().getCurrentFormStatus().getDescription();
		String title = "General Journal - " + gl.getSequenceNo();
		shortDescription = new StringBuffer(title)
				.append("<b> GL DATE : </b>" + DateUtil.formatDate(gl.getGlDate()))
				.append(" " + gl.getComment());

		return ObjectInfo.getInstance(ebObjectId, title, latestStatus,
				shortDescription.toString(), fullDescription, popupLink, printOutLink);
	}

	/**
	 * Process the object before saving
	 * @param dto
	 */
	public void processGL(GeneralLedgerDto dto) {
		GeneralLedger gl = dto.getGeneralLedger();
		Collection<GlEntry> glEntries = dto.getGlEntries();
		Collection<GlEntry> processed = new ArrayList<GlEntry>();
		for (Iterator<GlEntry> iterator = glEntries.iterator(); iterator.hasNext();) {
			GlEntry gle = (GlEntry) iterator.next();
			if(gle.getDebitAmount() > 0) {
				gle.setDebit(Boolean.TRUE);
				gle.setAmount(gle.getDebitAmount());
			} else {
				gle.setDebit(Boolean.FALSE);
				gle.setAmount(gle.getCreditAmount());
			}
			logger.info("Processed GL Entry :: "+gle.toString());
			processed.add(gle);
		}
		gl.setGlEntries(processed);
		gl.setCompanyId(dto.getCompanyId());
		gl.setDivisionId(dto.getDivisionId());
		gl.setReferenceDocuments(dto.getReferenceDocuments());
		dto.setGeneralLedger(gl);
	}

	/**
	 * General Search - General Journals
	 * @param user
	 * @param searchCriteria
	 * @param divisionId
	 * @return
	 */
	public List<FormSearchResult> search(User user, String searchCriteria, Integer divisionId) {
		Page<GeneralLedger> generalLedgers = generalLedgerDao.searchGeneralLedgers(divisionId, searchCriteria,
				new PageSetting(1));
		List<FormSearchResult> result = new ArrayList<FormSearchResult>();
		for (GeneralLedger gl : generalLedgers.getData()) {
			String title = ("JV No. " + String.valueOf(gl.getSequenceNo()));
			List<ResultProperty> properties = new ArrayList<ResultProperty>();
			properties.add(ResultProperty.getInstance("Company", gl.getCompany().getName()));
			properties.add(ResultProperty.getInstance("Source", gl.getGlEntrySource().getDescription()));
			properties.add(ResultProperty.getInstance("GL Date", DateUtil.formatDate(gl.getGlDate())));
			properties.add(ResultProperty.getInstance("Description", gl.getComment()));
			properties.add(
					ResultProperty.getInstance("Status", gl.getFormWorkflow().getCurrentFormStatus().getDescription()));
			String amount = NumberFormatUtil.format(glEntryService.getTotalAmt(gl.getGlEntries(), Boolean.TRUE));
			properties.add(ResultProperty.getInstance("Amount", amount));
			result.add(FormSearchResult.getInstanceOf(gl.getId(), title, properties));
		}
		return result;
	}
}
