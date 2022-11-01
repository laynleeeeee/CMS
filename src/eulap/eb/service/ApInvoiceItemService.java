package eulap.eb.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import eulap.common.domain.Domain;
import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.APInvoiceDao;
import eulap.eb.dao.APLineDao;
import eulap.eb.dao.AccountDao;
import eulap.eb.dao.ApInvoiceAccountDao;
import eulap.eb.dao.ApInvoiceItemDao;
import eulap.eb.dao.ApInvoiceLineDao;
import eulap.eb.dao.ObjectToObjectDao;
import eulap.eb.dao.ReferenceDocumentDao;
import eulap.eb.dao.SupplierAccountDao;
import eulap.eb.dao.SupplierDao;
import eulap.eb.dao.VatAcctSetupDao;
import eulap.eb.dao.WithholdingTaxAcctSettingDao;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.APLine;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.ApInvoiceItem;
import eulap.eb.domain.hibernate.ApInvoiceLine;
import eulap.eb.domain.hibernate.ApLineSetup;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.InvoiceType;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.SupplierAccount;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.VatAcctSetup;
import eulap.eb.domain.hibernate.WithholdingTaxAcctSetting;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.validator.APInvoiceValidator;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.web.dto.ApInvoiceDto;
import eulap.eb.web.dto.ApInvoiceItemDto;
import eulap.eb.web.dto.COCTaxDto;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.InvoicePrintoutDto;
import eulap.eb.web.dto.ResultProperty;
import eulap.eb.web.dto.SupplierApLineDto;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * Class that handles the business logic for {@link ApInvoiceItem}

 */

@Service
public class ApInvoiceItemService extends BaseWorkflowService {
	@Autowired
	private APInvoiceDao apInvoiceDao;
	@Autowired
	private APLineDao apLineDao;
	@Autowired
	private ReferenceDocumentDao referenceDocDao;
	@Autowired
	private ApInvoiceLineDao apInvoiceLineDao;
	@Autowired
	private APInvoiceService apInvoiceService;
	@Autowired
	private ApInvoiceItemDao apInvoiceItemDao;
	@Autowired
	private ReferenceDocumentService referenceDocumentService;
	@Autowired
	private ObjectToObjectDao objectToObjectDao;
	@Autowired
	private APInvoiceValidator apInvoiceValidator;
	@Autowired
	private EBFormServiceHandler ebFormServiceHandler;
	@Autowired
	private WithholdingTaxAcctSettingDao wtAcctSettingDao;
	@Autowired
	private AccountCombinationService accountCombinationService;
	@Autowired
	private SupplierDao supplierDao;
	@Autowired
	private SupplierAccountDao supplierAccountDao;
	@Autowired
	private ApInvoiceAccountDao invoiceAcctDao;
	@Autowired
	private RrRawMaterialService rawMaterialService;
	@Autowired
	private RReceivingReportService rrService;
	@Autowired
	private WithholdingTaxAcctSettingDao wtaxAcctSettingDao;
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private VatAcctSetupDao vatAcctSetupDao;

	/**
	 * Get and set the AP invoice DTO
	 * @param apInvoiceId The AP invoice id
	 * @return The AP invoice DTO
	 * @throws IOException 
	 */
	public ApInvoiceItemDto setApInvoiceItemDto(int apInvoiceId) throws IOException {
		ApInvoiceItemDto apInvoiceDto = new ApInvoiceItemDto();
		APInvoice apInvoice = apInvoiceService.getInvoice(apInvoiceId);
		int parentObjectId = apInvoice.getEbObjectId();
		apInvoice.setReferenceDocuments(referenceDocumentService.getReferenceDocuments(parentObjectId));
		if (apInvoice.getInvoiceTypeId() == InvoiceType.INVOICE_ITEM_TYPE_ID) {
			apInvoice.setApInvoiceItems(getAndProcessApInvoiceItems(parentObjectId, true));
		} else {
			apInvoice.setaPlines(apInvoiceService.getAPLine(apInvoice));
		}

		List<ApInvoiceLine> apInvoiceLines = rrService.getApInvoiceLines(apInvoiceId);
		for (ApInvoiceLine apl : apInvoiceLines) {
			apl.setApLineSetupName(apl.getApLineSetup().getName());
			if (apl.getUnitOfMeasurementId() != null) {
				apl.setUnitMeasurementName(apl.getUnitMeasurement().getName());
			}
		}
		apInvoice.setApInvoiceLines(apInvoiceLines);
		apInvoiceDto.setApInvoice(apInvoice);
		return apInvoiceDto;
	}

	/**
	 * Get the list of AP Invoice Items by the parent object id.
	 * @param parentObjectId The parent object id.
	 * @param isViewing True if used for viewing, otherwise false.
	 * @return The list of ApInvoiceItems.
	 * @throws IOException 
	 */
	public List<ApInvoiceItem> getAndProcessApInvoiceItems(int parentObjectId, boolean isViewing) throws IOException {
		List<ObjectToObject> objectToObjects = objectToObjectDao.getAllByRefId(
				ObjectToObject.FIELDS.fromObjectId.name(), parentObjectId);
		List<ApInvoiceItem> apInvoiceItems = new ArrayList<ApInvoiceItem>();
		for (ObjectToObject objectToObject : objectToObjects) {
			apInvoiceItems.addAll(apInvoiceItemDao.getAllByRefId(
				ApInvoiceItem.FIELD.ebObjectId.name(), objectToObject.getToObjectId()));
		}

		if (isViewing) {
			APInvoice apInvoice = null;
			String invNumber = "";
			String drNumber = "";
			for (ApInvoiceItem api : apInvoiceItems) {
				apInvoice = apInvoiceService.processReceivingReportAndItems(api.getApInvoiceId());
				invNumber = !apInvoice.getInvoiceNumber().isEmpty() ? " Inv # "+apInvoice.getInvoiceNumber() : "";
				drNumber = !apInvoice.getReceivingReport().getDeliveryReceiptNo().isEmpty()
						? " DR # "+apInvoice.getReceivingReport().getDeliveryReceiptNo() : "";
				api.setInvoiceNumber("RR-"+apInvoice.getSequenceNumber()+invNumber+drNumber);
			}
		}
		
		return apInvoiceItems;
	}

	/**
	 * Get all unsettled receiving report forms
	 * @param supplierAcctId The supplier account id
	 * @param invoiceNumber The invoice number
	 * @param invoiceIds The invoice ids
	 * @return The list of all unsettled receiving report forms
	 */
	public Collection<APInvoice> getReceivingReports(Integer formId, Integer supplierAcctId, String invoiceNumber, String invoiceIds, boolean isExact) {
		Page<APInvoice> receivingReports = apInvoiceDao.getReceivingReports(formId, supplierAcctId, invoiceNumber, invoiceIds,
				isExact, new PageSetting(PageSetting.START_PAGE, PageSetting.NO_PAGE_CONSTRAINT));
		return receivingReports.getData();
	}

	/**
	 * Remove empty lines of the to be saved AP invoice items
	 * @param apInvoiceItems The AP invoice items list
	 * @return The process AP invoice items
	 */
	public List<ApInvoiceItem> processToBeSavedInvoiceItems(List<ApInvoiceItem> apInvoiceItems) {
		List<ApInvoiceItem> toBeSavedLines = new ArrayList<>();
		for (ApInvoiceItem api : apInvoiceItems) {
			if ((api.getInvoiceNumber() != null && !api.getInvoiceNumber().isEmpty())
				|| (api.getAmount() != null && api.getAmount() != 0.0)
				|| api.getApInvoiceId() != null) {
				toBeSavedLines.add(api);
			}
		}
		return toBeSavedLines;
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		APInvoice apInvoice = (APInvoice) form;
		try {
			apInvoiceService.saveInvoice(user, apInvoice, workflowName);
			List<ApInvoiceLine> apInvoiceLines = apInvoice.getApInvoiceLines();
			if (apInvoiceLines != null && !apInvoiceLines.isEmpty()) {
				// Save other charges
				rawMaterialService.saveApInvoiceLines(apInvoice);
			}
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Save the AP invoice - Item
	 * @param invoiceItemDto The AP invoice item DTO
	 * @param user The current user logged
	 * @throws IOException 
	 */
	public void saveApInvoiceItem(ApInvoiceItemDto invoiceItemDto, User user) throws ClassNotFoundException, IOException {
		APInvoice apInvoice = invoiceItemDto.getApInvoice();
		boolean isNew = apInvoice.getId() == 0;
		Integer parentObjectId = apInvoice.getEbObjectId();
		ebFormServiceHandler.saveForm(apInvoice, user);

		if (apInvoice.getInvoiceTypeId() == InvoiceType.INVOICE_ITEM_TYPE_ID) {
			if (!isNew) {
				List<ApInvoiceItem> savedInvoiceItems = getAndProcessApInvoiceItems(parentObjectId, false);
				for (ApInvoiceItem toBeInactiveInvoiceItem : savedInvoiceItems) {
					toBeInactiveInvoiceItem.setActive(false);
					apInvoiceItemDao.save(toBeInactiveInvoiceItem);
				}
			}

			// Save the AP invoice items
			List<ApInvoiceItem> apInvoiceItems = apInvoice.getApInvoiceItems();
			for (ApInvoiceItem api : apInvoiceItems) {
				api.setActive(true);
				apInvoiceItemDao.save(api);
			}
		}
	}

	@Override
	public void preFormSaving(BaseFormWorkflow form, String workflowName, User user) {
		apInvoiceService.preFormSaving(form, workflowName, user);
	}

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return apInvoiceService.getFormWorkflow(id);
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return apInvoiceService.getFormByWorkflow(workflowId);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		Integer ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
			case APInvoice.AP_INVOICE_OBJECT_TYPE_ID:
				return apInvoiceDao.getByEbObjectId(ebObjectId);
			case APLine.AP_LINE_OBJECT_TYPE:
				return apLineDao.getByEbObjectId(ebObjectId);
			case ApInvoiceLine.OBJECT_TYPE_ID:
				return apInvoiceLineDao.getByEbObjectId(ebObjectId);
			case ReferenceDocument.OBJECT_TYPE_ID:
				return referenceDocDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		ObjectInfo objectInfo = apInvoiceService.getObjectInfo(ebObjectId, user);
		APInvoice apInvoice = apInvoiceDao.getByEbObjectId(ebObjectId);
		String label = apInvoice.getInvoiceTypeId() == InvoiceType.INVOICE_ITEM_TYPE_ID
				? "AP Invoice Item " : "AP Invoice Service ";
		String title = label + apInvoice.getSequenceNumber();
		StringBuffer shortDescription = new StringBuffer(title)
				.append(" " + apInvoice.getSupplier().getName())
				.append(" " + apInvoice.getSupplierAccount().getName())
				.append(" " + DateUtil.formatDate(apInvoice.getGlDate()))
				.append(" " + apInvoice.getAmount());
		return ObjectInfo.getInstance(ebObjectId, title, objectInfo.getLatestStatus(),
				shortDescription.toString(), objectInfo.getFullDescription(),
				objectInfo.getPopupLink(), objectInfo.getPrintOutLink());
	}

	/**
	 * Validates the form
	 * @param apInvoiceItemDto The AP invoice item DTO
	 * @param user The current user logged
	 * @param errors The validation errors
	 */
	public void validateForm(ApInvoiceItemDto apInvoiceItemDto, User user, Errors errors) {
		APInvoice apInvoice = apInvoiceItemDto.getApInvoice();
		boolean isInvoiceItem = apInvoice.getInvoiceTypeId() == InvoiceType.INVOICE_ITEM_TYPE_ID;
		apInvoiceValidator.validate(apInvoice, errors, user, "apInvoice.", false, isInvoiceItem);
		if (isInvoiceItem) {
			if (apInvoice.getTermId() == null) {
				errors.rejectValue("apInvoice.termId", null, null, ValidatorMessages.getString("ApInvoiceItemService.4"));
			}

			List<ApInvoiceItem> apInvoiceItems = apInvoice.getApInvoiceItems();
			if (apInvoiceItems != null && !apInvoiceItems.isEmpty()) {
				int row = 0;
				for (ApInvoiceItem api : apInvoiceItems) {
					row++;
					if (api.getApInvoiceId() == null) {
						errors.rejectValue("apInvoice.apInvoiceItems", null, null,
								String.format(ValidatorMessages.getString("ApInvoiceItemService.0"), row));
					}
					if (api.getAmount() != null && api.getAmount() < 0) {
						errors.rejectValue("apInvoice.apInvoiceItems", null, null,
								String.format(ValidatorMessages.getString("ApInvoiceItemService.1"), row));
					}
				}
			} else {
				errors.rejectValue("apInvoice.apInvoiceItems", null, null,
						ValidatorMessages.getString("ApInvoiceItemService.2"));
			}
		}
	}

	/**
	 * Set the AP invoice item lines for form printout
	 * @param apInvoiceId The AP invoice id
	 * @return The AP invoice item DTO
	 * @throws IOException 
	 */
	public ApInvoiceItemDto setApInvoiceItemPrintDetails(int apInvoiceId) throws IOException {
		ApInvoiceItemDto apInvoiceItemDto = setApInvoiceItemDto(apInvoiceId);
		List<ApInvoiceDto> apInvoiceDtos = new ArrayList<ApInvoiceDto>();
		ApInvoiceDto dto = null;
		APInvoice apInvoice = apInvoiceItemDto.getApInvoice();
		List<ApInvoiceItem> apInvoiceItems = apInvoice.getApInvoiceItems();
		List<APLine> apLines = apInvoice.getaPlines();
		int index = 0;
		String invoiceNumbers = "";
		Account account = null;
		AccountCombination accountCombination = null;
		Integer accountCombinationId = null;
		SupplierAccount supplierAcct = apInvoice.getSupplierAccount();
		if (supplierAcct != null) {
			// AP Invoice items
			accountCombinationId = invoiceAcctDao.getInvoiceAcctByCompany(supplierAcct.getCompanyId()).getAccountCombinationId();
			accountCombination = getAccountCombination(accountCombinationId);
			account = accountCombination.getAccount();
			if (apInvoiceItems != null && !apInvoiceItems.isEmpty()) {
				for (ApInvoiceItem apii : apInvoiceItems) {
					dto = new ApInvoiceDto();
					if (index > 0) {
						invoiceNumbers += ", " + apii.getInvoiceNumber();
					} else {
						invoiceNumbers += apii.getInvoiceNumber();
					}
					dto.setAccountName(account.getAccountName());
					dto.setAccountNo(account.getNumber());
					dto.setParticular(apii.getInvoiceNumber());
					dto.setDebit(apii.getAmount());
					apInvoiceDtos.add(dto);
					index++;
				}
				account = null;
			} else if (apLines != null && !apLines.isEmpty()) {
				Integer taxTypeId = null;
				VatAcctSetup ia = null;
				for (APLine apl : apLines) {
					dto = new ApInvoiceDto();
					account = apl.getAccountCombination().getAccount();
					dto.setAccountName(account.getAccountName());
					dto.setAccountNo(account.getNumber());
					double amount = apl.getAmount();
					if (amount < 0) {
						dto.setCredit(Math.abs(amount));
					} else {
						dto.setDebit(amount);
					}
					apInvoiceDtos.add(dto);
					account = null;

					taxTypeId = apl.getTaxTypeId();
					double vatAmount = apl.getVatAmount() != null ? apl.getVatAmount() : 0.0;
					if (taxTypeId != null && vatAmount != 0.0) {
						accountCombination = getAccountCombination(apl.getAccountCombinationId());
						ia = vatAcctSetupDao.getVatAccountSetup(accountCombination.getCompanyId(), null);
						if (ia != null) {
							dto = new ApInvoiceDto();
							accountCombination = getAccountCombination(ia.getInputVatAcId());
							ia = null;
							account = getAccount(accountCombination.getAccountId());
							dto.setAccountName(account.getAccountName());
							dto.setAccountNo(account.getNumber());
							dto.setParticular("");
							if (vatAmount < 0) {
								dto.setCredit(Math.abs(vatAmount));
							} else {
								dto.setDebit(vatAmount);
							}
							account = null;
							apInvoiceDtos.add(dto);
							accountCombination = null;
						}
					}
					taxTypeId = null;
					index++;
				}
			}

			List<ApInvoiceLine> apInvoiceLines = apInvoice.getApInvoiceLines();
			if (apInvoiceLines != null && !apInvoiceLines.isEmpty()) {
				for (ApInvoiceLine apil : apInvoiceLines) {
					ApLineSetup lineSetup = apil.getApLineSetup();
					accountCombination = lineSetup.getAccountCombination();
					account = accountCombination.getAccount();
					dto = new ApInvoiceDto();
					dto.setAccountName(account.getAccountName());
					dto.setAccountNo(account.getNumber());
					dto.setParticular("");
					double lineAmt = Math.abs(apil.getAmount());
					if (apil.getAmount() > 0) {
						dto.setDebit(lineAmt);
					} else {
						dto.setCredit(lineAmt);
					}
					apInvoiceDtos.add(dto);

					accountCombination = null;
					account = null;
				}
			}

			// Supplier account
			dto = new ApInvoiceDto();
			accountCombination = supplierAcct.getDefaultCreditAC();
			account = accountCombination.getAccount();
			dto.setAccountName(account.getAccountName());
			dto.setAccountNo(account.getNumber());
			dto.setParticular(invoiceNumbers);

			Double amount = Math.abs(apInvoice.getAmount());
			if (apInvoice.getAmount() < 0) {
				dto.setDebit(amount);
			} else {
				dto.setCredit(amount);
			}
			apInvoiceDtos.add(dto);

			accountCombination = null;
			account = null;
		}

		// Withholding tax
		WithholdingTaxAcctSetting wTaxAcctSetting = null;
		Integer wtaxAcctSettingId = apInvoice.getWtAcctSettingId();
		if (wtaxAcctSettingId != null) {
			dto = new ApInvoiceDto();
			wTaxAcctSetting = wtAcctSettingDao.get(wtaxAcctSettingId);
			accountCombination = getAccountCombination(wTaxAcctSetting.getAcctCombinationId());
			account = accountCombination.getAccount();
			dto.setAccountName(account.getAccountName());
			dto.setAccountNo(account.getNumber());
			dto.setParticular("");

			Double amount = Math.abs(apInvoice.getWtAmount());
			if (apInvoice.getWtAmount() < 0) {
				dto.setDebit(amount);
			} else {
				dto.setCredit(amount);
			}
			apInvoiceDtos.add(dto);
		}

		wTaxAcctSetting = null;
		account = null;
		accountCombination = null;

		apInvoiceItemDto.setApInvoiceDtos(apInvoiceDtos);
		return apInvoiceItemDto;
	}

	private AccountCombination getAccountCombination(int acctCombinationId) {
		return accountCombinationService.getAccountCombination(acctCombinationId);
	}

	private Account getAccount(int accountId) {
		return accountDao.get(accountId);
	}

	/**
	 * Search for AP invoice items on general search
	 * @param user The current user logged
	 * @param criteria The search criteria
	 * @param typeId The invoice type id
	 * @return The AP invoice items list
	 */
	public List<FormSearchResult> searchInvoiceForms(User user, String criteria, Integer typeId) {
		Page<APInvoice> invoiceItems = apInvoiceDao.retrieveApInvoices(criteria, typeId,
				new PageSetting(PageSetting.START_PAGE));
		List<FormSearchResult> result = new ArrayList<FormSearchResult>();
		for (APInvoice inv : invoiceItems.getData()) {
			String title = (inv.getInvoiceNumber());
			List<ResultProperty> properties = new ArrayList<ResultProperty>();
			String supplier = supplierDao.get(inv.getSupplierId()).getName();
			String supplierAcct = supplierAccountDao.get(inv.getSupplierAccountId()).getName();
			String status = inv.getFormWorkflow().getCurrentFormStatus().getDescription();
			properties.add(ResultProperty.getInstance("Supplier", supplier));
			properties.add(ResultProperty.getInstance("Supplier Account", supplierAcct));
			properties.add(ResultProperty.getInstance("Invoice Date", DateUtil.formatDate(inv.getInvoiceDate())));
			properties.add(ResultProperty.getInstance("GL Date", DateUtil.formatDate(inv.getGlDate())));
			properties.add(ResultProperty.getInstance("Due Date", DateUtil.formatDate(inv.getDueDate())));
			properties.add(ResultProperty.getInstance("Amount", NumberFormatUtil.format(inv.getAmount())));
			properties.add(ResultProperty.getInstance("Status", status));
			result.add(FormSearchResult.getInstanceOf(inv.getId(), title, properties));
		}
		return result;
	}

	/**
	 * Set the account combination id of each ap lines.
	 * @param aPInvoice The Ap Invoice object.
	 */
	public void setApLineCombination(APInvoice aPInvoice){
		Collection<APLine> apLines = aPInvoice.getaPlines();
		for(APLine apl : apLines) {
			if (apl.getAccountNumber() != null || apl.getCompanyNumber() != null || apl.getDivisionNumber() != null) {
				AccountCombination ac = apInvoiceService.getAccountCombination(apl);
				if (ac != null) {
					apl.setAccountCombinationId(ac.getId());
				}
			}
		}
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog, BindingResult bindingResult) {
		apInvoiceService.doBeforeSaving(currentWorkflowLog, bindingResult);
	}

	/**
	 * Generate supplier account per AP line report data source
	 * @param companyId the company ID
	 * @param divisionId the division ID
	 * @param supplierId the supplier ID
	 * @param supplierAcctId the supplier account ID
	 * @param accountId the account ID
	 * @param dateFrom the date from
	 * @param dateTo the date to
	 * @return the supplier account per AP line report data source
	 */
	public JRDataSource generateSupplierApLine(int companyId, int divisionId, int supplierId,
			int supplierAcctId, int accountId, Date dateFrom, Date dateTo) {
		EBJRServiceHandler<SupplierApLineDto> supplierApLineHandler = new SupplierApLineJRHandler(apInvoiceDao,
				companyId, divisionId, supplierId, supplierAcctId, accountId, dateFrom, dateTo);
		return new EBDataSource<SupplierApLineDto>(supplierApLineHandler);
	}

	private static class SupplierApLineJRHandler implements EBJRServiceHandler<SupplierApLineDto> {
		private APInvoiceDao apInvoiceDao;
		private final int companyId;
		private final int divisionId;
		private final int supplierId;
		private final int supplierAcctId;
		private final int accountId;
		private final Date dateFrom;
		private final Date dateTo;

		private SupplierApLineJRHandler(APInvoiceDao gemmaApInvoiceDao, int companyId, int divisionId, int supplierId,
				int supplierAcctId, int accountId, Date dateFrom, Date dateTo) {
			this.apInvoiceDao = gemmaApInvoiceDao;
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.supplierId = supplierId;
			this.supplierAcctId = supplierAcctId;
			this.accountId = accountId;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
		}

		@Override
		public void close() throws IOException {
			apInvoiceDao = null;
		}

		@Override
		public Page<SupplierApLineDto> nextPage(PageSetting pageSetting) {
			return apInvoiceDao.getSupplierApLine(companyId, supplierId, supplierAcctId, divisionId, accountId, dateFrom, dateTo, pageSetting);
		}
	}

	/**
	 * Get AP invoice withholding tax, BIR 2307 printout data
	 * @param pId The AP invoice id
	 * @return The BIR 2307 printout data
	 * @throws IOException 
	 */
	public List<InvoicePrintoutDto> getInvoicePrintoutDtos(Integer pId) throws IOException {
		List<InvoicePrintoutDto> invoicePrintoutDtos = new ArrayList<InvoicePrintoutDto>();
		InvoicePrintoutDto invoicePrintoutDto = new InvoicePrintoutDto();

		// Main form
		List<ApInvoiceItemDto> apInvoiceItemDtos = new ArrayList<ApInvoiceItemDto>();
		ApInvoiceItemDto apInvoiceItemDto = setApInvoiceItemPrintDetails(pId);
		apInvoiceItemDtos.add(apInvoiceItemDto);
		invoicePrintoutDto.setApInvoiceItemDtos(apInvoiceItemDtos);

		// COC Tax
		List<COCTaxDto> cocTaxDtos = new ArrayList<COCTaxDto>();
		COCTaxDto cocTaxDto = new COCTaxDto();
		APInvoice apInvoice = apInvoiceItemDto.getApInvoice();
		Date glDate = apInvoice.getGlDate();
		cocTaxDto.setStartPeriod(DateUtil.getFirstDayOfMonth(glDate));
		cocTaxDto.setEndPeriod(DateUtil.getEndDayOfMonth(glDate));

		Integer typeId = apInvoice.getInvoiceTypeId();
		Integer supplierId = apInvoice.getSupplierId();
		boolean isNonVat = supplierDao.get(supplierId).getBusRegTypeId() == 1;
		Integer wtaxAcctSettingId = apInvoice.getWtAcctSettingId();
		if (wtaxAcctSettingId != null) {
			cocTaxDto.setAtc(wtaxAcctSettingDao.get(wtaxAcctSettingId).getName());
			double totalAmount = 0;
			String wtDesc = "";
			if (typeId.equals(InvoiceType.INVOICE_ITEM_TYPE_ID)) {
				wtDesc = "GOODS"; // AP invoice item description
				List<ApInvoiceItem> apInvoiceItems = getAndProcessApInvoiceItems(
						apInvoice.getEbObjectId(), false);
				for (ApInvoiceItem apii : apInvoiceItems) {
					totalAmount += apii.getAmount();
				}
				List<ApInvoiceLine> apInvoiceLines = rrService.getApInvoiceLines(pId);
				if (apInvoiceLines != null && !apInvoiceLines.isEmpty()) {
					for (ApInvoiceLine apl : apInvoiceLines) {
						totalAmount += apl.getAmount();
					}
				}
				apInvoiceItems = null;
				apInvoiceLines = null;
			} else if ((typeId.equals(InvoiceType.INVOICE_SERVICE_TYPE_ID))) {
				wtDesc = "SERVICE"; // AP invoice service description
				List<APLine> apLines = apInvoiceService.getAPLine(apInvoice);
				for (APLine apl : apLines) {
					totalAmount += apl.getAmount();
				}
				apLines = null;
			}
			cocTaxDto.setWtDesc(wtDesc);
			double netOfVat = isNonVat ? totalAmount : totalAmount / 1.12;

			// Set net amount per quarter
			Date firstQtrStart = getDate(glDate, 1, 1);
			Date firstQtrEnd = getDate(glDate, 4, 30);
			Date scndQtrStart = getDate(glDate, 5, 1);
			Date scndQtrEnd = getDate(glDate, 8, 31);
			Date thirdQtrStart = getDate(glDate, 9, 1);
			Date thirdQtrEnd = getDate(glDate, 12, 31);
			if (isBetweenDates(glDate, firstQtrStart, firstQtrEnd)) {
				cocTaxDto.setFirstQuarter(netOfVat);
			} else if (isBetweenDates(glDate, scndQtrStart, scndQtrEnd)) {
				cocTaxDto.setSecondQuarter(netOfVat);
			} else if (isBetweenDates(glDate, thirdQtrStart, thirdQtrEnd)) {
				cocTaxDto.setThirdQuarter(netOfVat);
			}
			cocTaxDto.setTaxWithheld(NumberFormatUtil.roundOffTo2DecPlaces(apInvoice.getWtAmount()));
		}
		cocTaxDtos.add(cocTaxDto);
		invoicePrintoutDto.setCocTaxDtos(cocTaxDtos);

		invoicePrintoutDtos.add(invoicePrintoutDto);
		return invoicePrintoutDtos;
	}

	private Date getDate(Date glDate, int month, int day) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(DateUtil.removeTimeFromDate(glDate));
		int year = DateUtil.getYear(cal.getTime());
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.MONTH, month-1);
		cal.set(Calendar.YEAR, year);
		return cal.getTime();
	}

	private boolean isBetweenDates(Date date, Date startDate, Date endDate) {
		return (date.equals(startDate) || date.after(startDate))
				&& (date.equals(endDate) || date.before(endDate));
	}
}
