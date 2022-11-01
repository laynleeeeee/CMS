package eulap.eb.service;

import java.io.IOException;
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
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.common.util.TaxUtil;
import eulap.eb.dao.ArCustomerAcctDao;
import eulap.eb.dao.ArCustomerDao;
import eulap.eb.dao.ArInvoiceDao;
import eulap.eb.dao.ArReceiptDao;
import eulap.eb.dao.CompanyDao;
import eulap.eb.dao.CurrencyDao;
import eulap.eb.dao.DivisionDao;
import eulap.eb.dao.EBObjectDao;
import eulap.eb.dao.ProjectRetentionDao;
import eulap.eb.dao.ProjectRetentionLineDao;
import eulap.eb.dao.SalesOrderDao;
import eulap.eb.dao.WithholdingTaxAcctSettingDao;
import eulap.eb.domain.hibernate.ArCustomer;
import eulap.eb.domain.hibernate.ArCustomerAccount;
import eulap.eb.domain.hibernate.ArInvoice;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.CurrencyRate;
import eulap.eb.domain.hibernate.Division;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.ProjectRetention;
import eulap.eb.domain.hibernate.ProjectRetentionLine;
import eulap.eb.domain.hibernate.ProjectRetentionType;
import eulap.eb.domain.hibernate.SalesOrder;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.WithholdingTaxAcctSetting;
import eulap.eb.service.oo.OOLinkHelper;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.ResultProperty;

/**
 * Service class for {@link ProjectRetention}

 */

@Service
public class ProjectRetentionService extends BaseWorkflowService {
	@Autowired
	private ProjectRetentionDao projectRetentionDao;
	@Autowired
	private ProjectRetentionLineDao projectRetentionLineDao;
	@Autowired
	private SalesOrderDao salesOrderDao;
	@Autowired
	private ArCustomerService arCustomerService;
	@Autowired
	private CompanyDao companyDao;
	@Autowired
	private DivisionDao divisionDao;
	@Autowired
	private ArCustomerDao arCustomerDao;
	@Autowired
	private ArCustomerAcctDao arCustomerAcctDao;
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private CurrencyDao currencyDao;
	@Autowired
	private ReferenceDocumentService refDocumentService;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private ArInvoiceDao arInvoiceDao;
	@Autowired
	private ArReceiptDao arReceiptDao;
	@Autowired
	private OOLinkHelper ooLinkHelper;
	@Autowired 
	private EBObjectDao ebObjectDao;
	@Autowired
	private DivisionService divisionService;
	@Autowired
	private WithholdingTaxAcctSettingDao wtAcctDao;
	@Autowired
	private CurrencyRateService currencyRateService;

	private static final int CENTRAL_ID = 1;
	private static final int NSB3_ID = 2;
	private static final int NSB4_ID = 3;
	private static final int NSB5_ID = 4;
	private static final int NSB8_ID = 5;
	private static final int NSB8A_ID = 6;

	private static final int PHP_RATE = 1;

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return projectRetentionDao.get(id).getFormWorkflow();
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return projectRetentionDao.getByWorkflowId(workflowId);
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";

		ProjectRetention pr = projectRetentionDao.getByEbObjectId(ebObjectId);
		Integer pId = pr.getId();

		FormProperty property = workflowHandler.getProperty(pr.getWorkflowName(), user);
		String popupLink = "/" + property.getEdit() + "?pId="+ pId;
		String printOutLink = "/" + property.getPrint() + "?pId=" + pId;
		
		
		String latestStatus = pr.getFormWorkflow().getCurrentFormStatus().getDescription();
		String title = getFormName(pr.getDivisionId()) + " - " + pr.getSequenceNo();
		
		shortDescription = new StringBuffer(title)
				.append(" " + companyDao.get(pr.getCompanyId()).getName())
				.append("<b> DATE : </b>" + DateUtil.formatDate(pr.getDate()));
		//TODO: Add additional info about the form
		return ObjectInfo.getInstance(ebObjectId, title, latestStatus,
				shortDescription.toString(), fullDescription, popupLink, printOutLink);
	}

	private String getFormName(int divisionId) {
		return "PR - " + divisionDao.get(divisionId).getName();
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		return projectRetentionDao.getByEbObjectId(ebObject.getId());
	}

	@Override
	public void preFormSaving(BaseFormWorkflow form, String workflowName, User user) {
		ProjectRetention pr = (ProjectRetention) form;
		boolean isNew = pr.getId() == 0;
		if (isNew) {
			pr.setSequenceNo(projectRetentionDao.generateSeqNo(pr.getCompanyId(), pr.getProjectRetentionTypeId()));
			int currencyId = pr.getCurrencyId() != null ? pr.getCurrencyId() : 1;
			double defaultRate = PHP_RATE; //Set the default value for PHP.
			if (currencyId != 1) {
				CurrencyRate currencyRate = currencyRateService.getLatestRate(currencyId);
				pr.setCurrencyRateId(currencyRate.getId());
				defaultRate = currencyRate.getRate();
				currencyRate = null;
			}
			pr.setCurrencyRateValue(defaultRate);
		} else {
			List<ProjectRetentionLine> toBeDeletedLines = projectRetentionLineDao.getAllByRefId(
					ProjectRetentionLine.FIELD.projectRetentionId.name(), pr.getId());
			if (toBeDeletedLines != null) {
				for (ProjectRetentionLine prl : toBeDeletedLines) {
					projectRetentionLineDao.delete(prl);
				}
			}
			toBeDeletedLines = null;
		}
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		ProjectRetention pr = (ProjectRetention) form;
		boolean isNew = pr.getId() == 0;
		AuditUtil.addAudit(pr, new Audit(user.getId(), isNew, new Date()));
		String remarks = pr.getRemarks();
		if (remarks != null && !remarks.isEmpty()) {
			pr.setRemarks(StringFormatUtil.removeExtraWhiteSpaces(remarks));
		}
		double currencyRate = pr.getCurrencyRateValue();
		double wtAmount = pr.getWtAmount() != null ? pr.getWtAmount() : 0;
		pr.setWtAmount(CurrencyUtil.convertAmountToPhpRate(wtAmount, currencyRate, true));
		projectRetentionDao.saveOrUpdate(pr);

		// Save lines
		List<Domain> toBeSavedPrLines = new ArrayList<Domain>();
		List<ProjectRetentionLine> prLines = pr.getProjectRetentionLines();
		double totalAmount = 0;
		double totalVatAmount = 0;
		for (ProjectRetentionLine prl : prLines) {
			prl.setProjectRetentionId(pr.getId());
			double lineCurrencyRate = prl.getCurrencyRateValue();
			double upAmount = prl.getUpAmount();
			prl.setUpAmount(CurrencyUtil.convertAmountToPhpRate(upAmount, lineCurrencyRate));
			double vatAmount = 0;

			if (TaxUtil.isVatable(prl.getTaxTypeId())) {
				vatAmount = NumberFormatUtil.roundOffTo2DecPlaces(prl.getVatAmount() != null ? prl.getVatAmount() : 0);
				prl.setVatAmount(CurrencyUtil.convertAmountToPhpRate(vatAmount, lineCurrencyRate, true));
			}
			double amount = NumberFormatUtil.roundOffTo2DecPlaces(upAmount - vatAmount);
			totalAmount += amount;
			totalVatAmount += vatAmount;
			prl.setAmount(CurrencyUtil.convertAmountToPhpRate(amount, lineCurrencyRate, true));
			toBeSavedPrLines.add(prl);
		}
		projectRetentionLineDao.batchSave(toBeSavedPrLines);

		double amount = NumberFormatUtil.roundOffTo2DecPlaces((totalAmount + totalVatAmount) - wtAmount);
		pr.setAmount(CurrencyUtil.convertAmountToPhpRate(amount, currencyRate, true));
		projectRetentionDao.update(pr);

		// Save reference documents
		refDocumentService.saveReferenceDocuments(user, isNew, pr.getEbObjectId(), pr.getReferenceDocuments(), true);
	}

	/**
	 * Validate project retention form data.
	 * @param pr The {@link ProjectRetention}.
	 * @param errors The {@link Error}.
	 */
	public void validate(ProjectRetention pr, Errors errors) {
		if(pr.getCompanyId() == null) {
			//Company is required.
			errors.rejectValue("companyId", null, null, ValidatorMessages.getString("ProjectRetentionService.1"));
		} else if(!companyDao.get(pr.getCompanyId()).isActive()) {
			//Company is inactive.
			errors.rejectValue("companyId", null, null, ValidatorMessages.getString("ProjectRetentionService.2"));
		}

		if(pr.getDivisionId() == null) {
			//Division is required.
			errors.rejectValue("divisionId", null, null, ValidatorMessages.getString("ProjectRetentionService.3"));
		} else if(!divisionDao.get(pr.getDivisionId()).isActive()) {
			//Division is inactive.
			errors.rejectValue("divisionId", null, null, ValidatorMessages.getString("ProjectRetentionService.4"));
		}

		if(pr.getSalesOrderId() == null) {
			//SO Reference is required.
			errors.rejectValue("salesOrderId", null, null, ValidatorMessages.getString("ProjectRetentionService.5"));
		}

		if(pr.getArCustomerId() == null) {
			//Customer is required.
			errors.rejectValue("arCustomerId", null, null, ValidatorMessages.getString("ProjectRetentionService.6"));
		} else if(!arCustomerDao.get(pr.getArCustomerId()).isActive()) {
			//Customer is inactive.
			errors.rejectValue("arCustomerId", null, null, ValidatorMessages.getString("ProjectRetentionService.7"));
		}

		if(pr.getArCustomerAccountId() == null) {
			//Customer account is required.
			errors.rejectValue("arCustomerAccountId", null, null, ValidatorMessages.getString("ProjectRetentionService.8"));
		} else if(!arCustomerAcctDao.get(pr.getArCustomerAccountId()).isActive()) {
			//Customer account is inactive.
			errors.rejectValue("arCustomerAccountId", null, null, ValidatorMessages.getString("ProjectRetentionService.9"));
		}

		if(pr.getDate() == null) {
			//Date is required.
			errors.rejectValue("date", null, null, ValidatorMessages.getString("ProjectRetentionService.10"));
		} else if(!timePeriodService.isOpenDate(pr.getDate())) {
			//Date should be in an open time period.
			errors.rejectValue("date", null, null, ValidatorMessages.getString("ProjectRetentionService.11"));
		}

		if(pr.getDueDate() == null) {
			//Due date is required.
			errors.rejectValue("dueDate", null, null, ValidatorMessages.getString("ProjectRetentionService.12"));
		} else if(!timePeriodService.isOpenDate(pr.getDueDate())) {
			//Due date should be in an open time period.
			errors.rejectValue("dueDate", null, null, ValidatorMessages.getString("ProjectRetentionService.13"));
		}

		if(pr.getCurrencyId() == null) {
			//Currency is required.
			errors.rejectValue("currencyId", null, null, ValidatorMessages.getString("ProjectRetentionService.16"));
		} else if(!currencyDao.get(pr.getCurrencyId()).isActive()) {
			//Currency is inactive.
			errors.rejectValue("currencyId", null, null, ValidatorMessages.getString("ProjectRetentionService.17"));
		}
		double totalAmount = 0;
		List<ProjectRetentionLine> prLines = pr.getProjectRetentionLines();
		if(prLines == null || prLines.isEmpty()) {
			//Project retention details is required.
			errors.rejectValue("projectRetentionLines", null, null, ValidatorMessages.getString("ProjectRetentionService.18"));
		} else {
			int row = 0;
			for(ProjectRetentionLine prLine : prLines) {
				row++;
				if(prLine.getUpAmount() == null || prLine.getUpAmount() == 0) {
					//Gross amount is required in row %d.
					errors.rejectValue("projectRetentionLines", null, null,
							String.format(ValidatorMessages.getString("ProjectRetentionService.19"), row));
				} else if(prLine.getUpAmount() > getRemainingRetentionBal(prLine)) {
					//Gross amount should not exceed the remaining balance in row %d.
					errors.rejectValue("projectRetentionLines", null, null,
							String.format(ValidatorMessages.getString("ProjectRetentionService.20"), row));
				}
				totalAmount += prLine.getUpAmount();
			}
		}

		totalAmount -= pr.getWtAmount() != null ? pr.getWtAmount() : 0;//Subtract withholding tax amount.
		Double prAmount = NumberFormatUtil.roundOffTo2DecPlaces(pr.getAmount());
		if(prAmount == null || prAmount == 0) {
			//Amount is required.
			errors.rejectValue("amount", null, null, ValidatorMessages.getString("ProjectRetentionService.14"));
		} else if(prAmount < 0) {
			//Invalid amount
			errors.rejectValue("amount", null, null, ValidatorMessages.getString("ProjectRetentionService.15"));
		} else if(prAmount != NumberFormatUtil.roundOffTo2DecPlaces(totalAmount)) {
			//Amount must be equal.
			errors.rejectValue("amount", null, null, ValidatorMessages.getString("ProjectRetentionService.21"));
		}
		//Validate reference documents.
		refDocumentService.validateReferences(pr.getReferenceDocuments(), errors);
	}

	private double getRemainingRetentionBal(ProjectRetentionLine prLine) {
		EBObject ebObject = ebObjectDao.get(prLine.getRefenceObjectId());
		return projectRetentionLineDao.getRemainingRetBal(ebObject.getId(), prLine.getId(), isInvoice(ebObject.getObjectTypeId()));
	}

	private boolean isInvoice(int objectTypeId) {
		if(objectTypeId == ArInvoice.OBJECT_TYPE_ID) {
			return true;
		}
		return false;
	}

	public Page<SalesOrder> getProjectRetentionSalesOrders(Integer companyId, Integer divisionId, Integer arCustomerId, 
			Integer arCustomerAcctId, Integer soNumber, String poNumber, Date dateFrom, Date dateTo,
			Integer statusId, Integer pageNumber) {
		Page<SalesOrder> salesOrders = salesOrderDao.getProjectRetentionSalesOrders(companyId, divisionId, arCustomerId, 
				arCustomerAcctId, soNumber, poNumber, dateFrom, dateTo, statusId,
				new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
		for(SalesOrder so : salesOrders.getData()) {
			so.setArCustomer(arCustomerService.getCustomer(so.getArCustomerId()));
		}
		return salesOrders;
	}

	/**
	 * Convert {@link SalesOrder} reference transaction to {@link ProjectRetention}.
	 * @param salesOrderId The sales order id.
	 * @return The {@link ProjectRetention}.
	 */
	public ProjectRetention convertSoToPr(Integer salesOrderId) {
		ProjectRetention pr = new ProjectRetention();
		SalesOrder so = salesOrderDao.get(salesOrderId);
		pr.setCompanyId(so.getCompanyId());
		pr.setCompany(so.getCompany());
		pr.setDivisionId(so.getDivisionId());
		pr.setDivision(so.getDivision());
		pr.setSalesOrderId(so.getId());
		pr.setSoNumber(so.getSequenceNumber().toString());
		pr.setPoNumber(so.getPoNumber());
		pr.setArCustomerId(so.getArCustomerId());
		pr.setArCustomer(so.getArCustomer());
		pr.setArCustomerAccountId(so.getArCustomerAcctId());
		pr.setArCustomerAccount(so.getArCustomerAccount());
		pr.setCurrencyId(so.getCurrencyId());
		pr.setCurrencyRateId(so.getCurrencyRateId());
		pr.setCurrencyRateValue(so.getCurrencyRateValue());

		List<ProjectRetentionLine> projectRetentionLines = projectRetentionLineDao.getTransactionsBySoId(salesOrderId);
		convertPrlMonetaryValues(projectRetentionLines);
		pr.setProjectRetentionLines(projectRetentionLines);
		return pr;
	}

	private void convertPrlMonetaryValues(List<ProjectRetentionLine> projectRetentionLines) {
		for(ProjectRetentionLine prl : projectRetentionLines) {
			double rate = prl.getCurrencyRateValue();
			prl.setAmount(CurrencyUtil.convertMonetaryValues(prl.getAmount(), rate));
			prl.setUpAmount(CurrencyUtil.convertMonetaryValues(prl.getUpAmount(), rate));
			if(prl.getVatAmount() != null) {
				prl.setVatAmount(CurrencyUtil.convertMonetaryValues(prl.getVatAmount(), rate));
			}
		}
	}

	/**
	 * Get the {@link ProjectRetention}.
	 * @param pId The project retention id.
	 * @return The {@link ProjectRetention}.
	 */
	public ProjectRetention getProjectRetention(Integer pId) {
		ProjectRetention pr = projectRetentionDao.get(pId);
		Integer soNumber = salesOrderDao.get(pr.getSalesOrderId()).getSequenceNumber();
		pr.setSoNumber(soNumber.toString());
		double rate = pr.getCurrencyRateValue();
		pr.setAmount(CurrencyUtil.convertMonetaryValues(pr.getAmount(), rate));
		pr.setWtAmount(CurrencyUtil.convertMonetaryValues(pr.getWtAmount(), rate));
		List<ProjectRetentionLine> prLines = projectRetentionLineDao.getAllByRefId(
				ProjectRetentionLine.FIELD.projectRetentionId.name(), pr.getId());
		convertPrlMonetaryValues(prLines);
		for(ProjectRetentionLine prLine : prLines) {
			EBObject ebObject = ooLinkHelper.getReferenceObject(prLine.getEbObjectId(), ProjectRetentionLine.OR_TYPE_ID);
			prLine.setReferenceObjectId(ebObject.getId());
			processPrLineRefNo(prLine);
		}
		pr.setProjectRetentionLines(prLines);
		prLines = null;
		return pr;
	}

	private void processPrLineRefNo(ProjectRetentionLine prLine) {
		if(prLine != null) {
			EBObject ebObject = ooLinkHelper.getReferenceObject(prLine.getEbObjectId(), ProjectRetentionLine.OR_TYPE_ID);
			prLine.setReferenceNo(getPrLineRefNo(ebObject));
		}
	}

	private String getPrLineRefNo(EBObject ebObject) {
		String refNumber = "";
		Integer objectId = ebObject.getId();
		if(isInvoice(ebObject.getObjectTypeId())) {
			refNumber = "ARI - " + arInvoiceDao.getByEbObjectId(objectId).getSequenceNo();
		} else {
			refNumber = "AC - " + arReceiptDao.getByEbObjectId(objectId).getSequenceNo();
		}
		return refNumber;
	}

	/**
	 * Process reference documents.
	 * @param pr The {@link ProjectRetention}.
	 * @throws IOException
	 */
	public void processRefDoc(ProjectRetention pr) throws IOException {
		pr.setReferenceDocuments(refDocumentService.processReferenceDocs(pr.getEbObjectId()));
	}

	/**
	 * Get the {@link Division}.
	 * @param divisionId The division id.
	 * @return The  {@link Division}.
	 */
	public Division getDivision(int divisionId) {
		return divisionService.getDivision(divisionId);
	}

	/**
	 * Get {@link WithholdingTaxAcctSetting} name.
	 * @param wtAcctId The {@link WithholdingTaxAcctSetting} id.
	 * @return The {@link WithholdingTaxAcctSetting} name.
	 */
	public String getWtAcctSettingName(Integer wtAcctId) {
		String wtAcctName = null;
		if(wtAcctId != null) {
			wtAcctName = wtAcctDao.get(wtAcctId).getName();
		}
		return wtAcctName;
	}

	/**
	 * Get division id by project retention type id.
	 * @param typeId The project retention type id.
	 * @return The division id.
	 */
	public int getDivisionIdByPrTypeId(int typeId) {
		int divisionId;
		switch (typeId) {
		case ProjectRetentionType.PR_CENTRAL:
			divisionId = CENTRAL_ID;
			break;
		case ProjectRetentionType.PR_NSB3:
			divisionId = NSB3_ID;
			break;
		case ProjectRetentionType.PR_NSB4:
			divisionId = NSB4_ID;
			break;
		case ProjectRetentionType.PR_NSB5:
			divisionId = NSB5_ID;
			break;
		case ProjectRetentionType.PR_NSB8:
			divisionId = NSB8_ID;
			break;
		case ProjectRetentionType.PR_NSB8A:
			divisionId = NSB8A_ID;
			break;
		default:
			divisionId = 0;
			break;
		}
		return divisionId;
	}

	/**
	 * Get the list of project retentions.
	 * @param typeId The project retention type id.
	 * @param criteria The search criteria.
	 * @return The list of project retentions in page format.
	 */
	public List<FormSearchResult> searchProjectRetentions(Integer typeId, String criteria) {
		Page<ProjectRetention> projectRetentions = projectRetentionDao.searchProjectRetentions(typeId, criteria, new PageSetting(1));
		List<FormSearchResult> result = new ArrayList<FormSearchResult>();
		String title = null;
		String status = null;
		Company company = null;
		Division division = null;
		ArCustomer customer = null;
		ArCustomerAccount custAcct = null;
		for (ProjectRetention pr : projectRetentions.getData()) {
			company = companyDao.get(pr.getCompanyId());
			division = divisionDao.get(pr.getDivisionId());
			customer = arCustomerDao.get(pr.getArCustomerId());
			custAcct = arCustomerAcctDao.get(pr.getArCustomerAccountId());
			title = String.valueOf(pr.getSequenceNo());
			List<ResultProperty> properties = new ArrayList<ResultProperty>();
			properties.add(ResultProperty.getInstance("Company", company.getName()));
			properties.add(ResultProperty.getInstance("Division", division.getName()));
			properties.add(ResultProperty.getInstance("Customer", customer.getName()));
			properties.add(ResultProperty.getInstance("Customer Account", custAcct.getName()));
			properties.add(ResultProperty.getInstance("Date", DateUtil.formatDate(pr.getDate())));
			status = pr.getFormWorkflow().getCurrentFormStatus().getDescription();
			properties.add(ResultProperty.getInstance("Status", StringFormatUtil.formatToLowerCase(status)));
			result.add(FormSearchResult.getInstanceOf(pr.getId(), title, properties));
		}
		title = null;
		status = null;
		company = null;
		customer = null;
		custAcct = null;
		division = null;
		return result;
	}

	/**
	 * Check for incorrect data.
	 * @param projectRetention The {@link ProjectRetention}.
	 * @param errors The {@link Error}.
	 */
	public void validateStatusLogs(ProjectRetention projectRetention, Errors errors) {
		if(projectRetention.getDateReceived() == null) {
			//Received date is required.
			errors.rejectValue("dateReceived", null, null, ValidatorMessages.getString("DeliveryReceiptService.17"));
		}
		if(projectRetention.getReceiver() == null || projectRetention.getReceiver().trim().isEmpty()) {
			//Received by is required.
			errors.rejectValue("receiver", null, null, ValidatorMessages.getString("DeliveryReceiptService.18"));
		} else if(projectRetention.getReceiver().length() > ProjectRetention.MAX_RECEIVER) {
			//Received by should not exceed %d characters.
			errors.rejectValue("receiver", null, null,
					String.format(ValidatorMessages.getString("DeliveryReceiptService.19"), ProjectRetention.MAX_RECEIVER));
		}
	}

	/**
	 * Update project retention with receiving details.
	 * @param projectRetention The {@link ProjectRetention}.
	 * @param user The {@link User}.
	 */
	public void savePrReceivingDetails(ProjectRetention projectRetention, User user) {
		ProjectRetention savedPr = projectRetentionDao.get(projectRetention.getId());
		//Set receiving details
		savedPr.setDateReceived(projectRetention.getDateReceived());
		savedPr.setReceiver(StringFormatUtil.removeExtraWhiteSpaces(projectRetention.getReceiver()));
		AuditUtil.addAudit(savedPr, new Audit(user.getId(), true, new Date()));
		//Save
		projectRetentionDao.saveOrUpdate(savedPr);
	}
}
