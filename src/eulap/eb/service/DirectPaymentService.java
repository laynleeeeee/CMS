package eulap.eb.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Domain;
import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.AccountCombinationDao;
import eulap.eb.dao.ApPaymentDao;
import eulap.eb.dao.DirectPaymentDao;
import eulap.eb.dao.DirectPaymentLineDao;
import eulap.eb.dao.DirectPaymentTypeDao;
import eulap.eb.dao.ObjectToObjectDao;
import eulap.eb.dao.ReferenceDocumentDao;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.ApPayment;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.DirectPayment;
import eulap.eb.domain.hibernate.DirectPaymentLine;
import eulap.eb.domain.hibernate.DirectPaymentType;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.PaymentType;
import eulap.eb.domain.hibernate.ReferenceDocument;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.web.dto.ApInvoiceDto;
import eulap.eb.web.dto.DirectPaymentDto;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.ResultProperty;

/**
 * Service class that will handle business logic for {@link DirectPayment} of client

 */

@Service
public class DirectPaymentService extends BaseWorkflowService {
	@Autowired
	private DirectPaymentTypeDao directPaymentTypeDao;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private ApPaymentDao paymentDao;
	@Autowired
	private ApPaymentService apPaymentService;
	@Autowired
	private DirectPaymentDao directPaymentDao;
	@Autowired
	private AccountCombinationDao accountCombiDao;
	@Autowired
	private DirectPaymentLineDao directPaymentLineDao;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private TermService termService;
	@Autowired
	private EbObjectService ebObjectService;
	@Autowired
	private ReferenceDocumentDao referenceDocumentDao;
	@Autowired
	private ObjectToObjectDao objectToObjectDao;
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private ReferenceDocumentService documentService;

	private static final Logger logger = Logger.getLogger(DirectPaymentService.class);

	public List<DirectPaymentType> getPaymentTypes() {
		return (List<DirectPaymentType>) directPaymentTypeDao.getAll();
	}

	/**
	 * Save Direct Payment.
	 * @param paymentDto The Direct Payment DTO.
	 * @param user The current user.
	 */
	public void saveDirectPayment(DirectPaymentDto paymentDto, User user) {
		logger.info("Saving Direct Payment!!");
		// Saving Ap payment
		ApPayment apPayment = paymentDto.getPayment();
		DirectPayment directPayment = paymentDto.getDirectPayment();
		saveApPayment(apPayment, user, directPayment.getDirectPaymentTypeId());

		// Instance of current date.
		Date currentDate = new Date();

		// Saving Direct payment
		boolean isNew = directPayment.getId() == 0;
		directPayment.setApPaymentId(apPayment.getId());
		if (isNew) {
			directPayment.setEbObjectId(ebObjectService.saveAndGetEbObjectId(user.getId(), DirectPayment.OBJECT_TYPE_ID, currentDate));
		}
		directPaymentDao.saveOrUpdate(directPayment);

		String companyNumber = companyService.getCompany(apPayment.getCompanyId()).getCompanyNumber();
		// Saving direct payment line
		saveDirectPaymentLine(directPayment, companyNumber);

		List<ReferenceDocument> referenceDocuments = paymentDto.getReferenceDocuments();
		paymentDto.setReferenceDocuments(referenceDocuments);

		saveRefDocs(isNew, user, currentDate, paymentDto);
		logger.info("Done!");
	}

	private void saveDirectPaymentLine(DirectPayment directPayment, String companyNumber) {
		List<DirectPaymentLine> savedLines = 
				directPaymentLineDao.getDirectPaymentLinesByDirectPaymentId(directPayment.getId(), true);
		if (!savedLines.isEmpty()) {
			List<Domain> toBeInactive = new ArrayList<>();
			for (DirectPaymentLine dpl : savedLines) {
				dpl.setActive(false);
				toBeInactive.add(dpl);
				dpl = null;
			}
			directPaymentLineDao.batchSaveOrUpdate(toBeInactive);
		}
		savedLines = null;

		for (DirectPaymentLine paymentLine : directPayment.getPaymentLines()) {
			paymentLine.setCompanyNumber(companyNumber);
			paymentLine.setDirectPaymentId(directPayment.getId());
			setAccountCombi(paymentLine);
			if(paymentLine.getAccountCombinationId() != 0) {
				paymentLine.setActive(true);
				directPaymentLineDao.save(paymentLine);
				logger.info("Successfully saved Direct Payment Line amounting " + paymentLine.getAmount());
			}
		}
	}

	private void setAccountCombi(DirectPaymentLine paymentLine) {
		if(paymentLine.getCompanyNumber() != null && (paymentLine.getDivisionNumber() != null
				|| paymentLine.getAccountNumber() != null)) {
			AccountCombination ac = accountCombiDao.getAccountCombination(paymentLine.getCompanyNumber(),
					paymentLine.getDivisionNumber(), paymentLine.getAccountNumber());
			paymentLine.setAccountCombination(ac);
			paymentLine.setAccountCombinationId(ac.getId());
		}
	}

	/**
	 * Save the AP Payment.
	 * @param directPaymentTypeId The direct payment type id.s
	 */
	private void saveApPayment(ApPayment apPayment, User user, Integer directPaymentTypeId) {
		boolean isNew = apPayment.getId() == 0;
		if(isNew) {
			apPayment.setVoucherNumber(paymentDao.generateDirectPaymentVC(apPayment.getCompanyId(), directPaymentTypeId));
			logger.debug("Generated voucher number "+apPayment.getVoucherNumber()+" for AP Payment form.");
			workflowHandler.processFormWorkflow(apPayment.getWorkflowName(), user, apPayment);
		} else {
			ApPayment savedPayment = apPaymentService.getApPayment(apPayment.getId());
			DateUtil.setCreatedDate(apPayment, savedPayment.getCreatedDate());
		}
		AuditUtil.addAudit(apPayment, user, new Date());
		paymentDao.saveOrUpdate(apPayment);
		logger.info("Successfully saved Direct Payment form with voucher no. "+apPayment.getVoucherNumber()
				+", check no. "+apPayment.getCheckNumber());
	}

	/**
	 * Get the {@link DirectPaymentDto}
	 * @param directPaymentId The id of the Direct Payment.
	 * @return The direct payment object.
	 */
	public DirectPaymentDto getDirectPayment(Integer directPaymentId) {
		DirectPaymentDto directPaymentDto = new DirectPaymentDto();
		DirectPayment directPayment = null;
		ApPayment apPayment = null;
		if(directPaymentId != null) {
			directPayment = directPaymentDao.get(directPaymentId);
			directPayment.setPaymentLines(getDirectPaymentLines(directPaymentId));
			apPayment = apPaymentService.getApPayment(directPayment.getApPaymentId());
			directPaymentDto.setPaymentType(directPaymentTypeDao.get(directPayment.getDirectPaymentTypeId()).getName());
			directPaymentDto.setTermName(termService.getTerm(directPayment.getTermId()).getName());
			directPaymentDto.setDirectPayment(directPayment);
			directPaymentDto.setPayment(apPayment);
			List<ReferenceDocument> referenceDocuments = 
					referenceDocumentDao.getRDsByEbObject(directPayment.getEbObjectId());
			directPaymentDto.setReferenceDocuments(referenceDocuments);
		}
		return directPaymentDto;
	}

	/**
	 * Get the {@link DirectPaymentDto}
	 * @param directPaymentId The id of the Direct Payment.
	 * @return The direct payment object with invoices.
	 */
	public DirectPaymentDto getDirectPaymentPrintOut(Integer directPaymentId) {
		DirectPaymentDto directPaymentDto = getDirectPayment(directPaymentId);
		DirectPayment directPayment = directPaymentDto.getDirectPayment();
		List<ApInvoiceDto> apInvoiceDtos = new ArrayList<ApInvoiceDto>();
		if(directPayment != null) {
			double amount = 0;
			for (DirectPaymentLine directPaymentLine : directPayment.getPaymentLines()) {
				amount = directPaymentLine.getAmount();
				Double debit = amount >= 0.0 ? amount : null;
				Double credit = amount < 0.0 ? -amount : null;
				String invoiceNumber = directPayment.getInvoiceNo();
				String accountNumber = directPaymentLine.getAccountNumber();
				String accountName = directPaymentLine.getAccountCombination().getAccount().getAccountName();
				apInvoiceDtos.add(ApInvoiceDto.getInstanceOf(debit, credit, invoiceNumber, directPaymentLine.getDescription(),
						accountNumber, accountName));
			}
		}
		ApPayment payment = directPaymentDto.getPayment();
		Account account = null;
		AccountCombination accountCombination = accountCombiDao.get(payment.getBankAccount() != null ? payment.getBankAccount().getCashInBankAcctId()
				: payment.getSupplierAccount().getDefaultCreditACId());
		account = accountCombination.getAccount();
		apInvoiceDtos.add(ApInvoiceDto.getInstanceOf(payment.getAmount() < 0.0 ? -payment.getAmount() : null,
					payment.getAmount() >= 0.0 ? payment.getAmount() : null, null, null,
					account.getNumber(), account.getAccountName()));
		directPaymentDto.setApInvoiceDtos(apInvoiceDtos);
		return directPaymentDto;
	}

	private List<DirectPaymentLine> getDirectPaymentLines(Integer directPaymentId) {
		List<DirectPaymentLine> directPaymentLines = 
				directPaymentLineDao.getDirectPaymentLinesByDirectPaymentId(directPaymentId, true);
		AccountCombination ac = null;
		for(DirectPaymentLine directPaymentLine : directPaymentLines) {
			ac = directPaymentLine.getAccountCombination();
			directPaymentLine.setDivisionNumber(ac.getDivision().getNumber());
			directPaymentLine.setAccountNumber(ac.getAccount().getNumber());
		}
		return directPaymentLines;
	}

	private void saveRefDocs (boolean isNew, User user, Date date, DirectPaymentDto directPayment) {
		if (!isNew) {
			List<ReferenceDocument> savedRDocs = 
					referenceDocumentDao.getRDsByEbObject(directPayment.getDirectPayment().getEbObjectId());
			if (savedRDocs != null && !savedRDocs.isEmpty()) {
				List<Integer> ids = new ArrayList<>();
				for (ReferenceDocument rd : savedRDocs) {
					ids.add(rd.getId());
				}
				referenceDocumentDao.delete(ids);
			}
		}
		List<Domain> toBeSaved = new ArrayList<>();
		List<ReferenceDocument> referenceDocuments = directPayment.getReferenceDocuments();
		if (referenceDocuments != null && !referenceDocuments.isEmpty()) {
			List<Domain> oos = new ArrayList<>();
			int ebObjectId = 0;
			int userId = user.getId();
			for (ReferenceDocument rd : referenceDocuments) {
				ebObjectId = ebObjectService.saveAndGetEbObjectId(userId, ReferenceDocument.OBJECT_TYPE_ID, date);
				rd.setEbObjectId(ebObjectId);
				oos.add(ObjectToObject.getInstanceOf(directPayment.getDirectPayment().getEbObjectId(), ebObjectId, 
						ReferenceDocument.OR_TYPE_ID, user, date));
				toBeSaved.add(rd);
			}
			objectToObjectDao.batchSave(oos);
			oos = null;
			referenceDocumentDao.batchSave(toBeSaved);
			toBeSaved = null;
		}
	}

	public void validate(DirectPaymentDto directPaymentDto, Errors errors) {
		DirectPayment directPayment = directPaymentDto.getDirectPayment();
		ApPayment apPayment = directPaymentDto.getPayment();
		boolean isCheck = directPayment.getDirectPaymentTypeId() == DirectPaymentType.TYPE_CHECK;

		if(apPayment.getPaymentDate() == null) {
			errors.rejectValue("payment.paymentDate", null, null, ValidatorMessages.getString("DirectPayment.0"));
		} else if(!timePeriodService.isOpenDate(apPayment.getPaymentDate())) {
			errors.rejectValue("payment.paymentDate", null, null, ValidatorMessages.getString("DirectPayment.15"));
		}

		if(directPayment.getInvoiceNo() != null && !directPayment.getInvoiceNo().trim().isEmpty()) {
			if(StringFormatUtil.removeExtraWhiteSpaces(directPayment.getInvoiceNo()).length() > DirectPayment.INVOICE_NO_MAX_CHAR) {
				errors.rejectValue("directPayment.invoiceNo", null, null,
					String.format(ValidatorMessages.getString("DirectPayment.19"), DirectPayment.INVOICE_NO_MAX_CHAR));
			} else if(directPaymentDao.isExistingInvoiceNo(directPayment.getInvoiceNo(), directPayment.getId())) {
				errors.rejectValue("directPayment.invoiceNo", null, null, ValidatorMessages.getString("DirectPayment.20"));
			}
		}

		if(directPayment.getTermId() == null) {
			errors.rejectValue("directPayment.termId", null, null, ValidatorMessages.getString("DirectPayment.1"));
		}

		if(apPayment.getSupplierId() == null) {
			errors.rejectValue("payment.supplierId", null, null, ValidatorMessages.getString("DirectPayment.2"));
		}

		if(apPayment.getSupplierAccountId() == null) {
			errors.rejectValue("payment.supplierAccountId", null, null, ValidatorMessages.getString("DirectPayment.3"));
		}

		if(directPayment.getDescription().trim().equals("")) {
			errors.rejectValue("directPayment.description", null, null, ValidatorMessages.getString("DirectPayment.4"));
		}

		if(apPayment.getAmount() == 0.00) {
			errors.rejectValue("payment.amount", null, null, ValidatorMessages.getString("DirectPayment.5"));
		}

		if(isCheck) {
			if(apPayment.getBankAccountId() == null) {
				errors.rejectValue("payment.bankAccountId", null, null, ValidatorMessages.getString("DirectPayment.6"));
			}

			if(apPayment.getCheckbookId() == null) {
				errors.rejectValue("payment.checkbookId", null, null, ValidatorMessages.getString("DirectPayment.7"));
			}

			if(apPayment.getCheckNumber() == null) {
				errors.rejectValue("payment.checkNumber", null, null, ValidatorMessages.getString("DirectPayment.8"));
			}
			if(apPayment.getCheckDate() == null) {
				errors.rejectValue("payment.checkDate", null, null, ValidatorMessages.getString("DirectPayment.16"));
			} else if(!timePeriodService.isOpenDate(apPayment.getCheckDate())) {
				errors.rejectValue("payment.checkDate", null, null, ValidatorMessages.getString("DirectPayment.17"));
			}

		} else {
			if(apPayment.getBankAccountId() == null) {
				errors.rejectValue("payment.bankAccountId", null, null, ValidatorMessages.getString("DirectPayment.9"));
			}
		}

		List<DirectPaymentLine> directPaymentLines = directPayment.getPaymentLines();
		if(directPaymentLines == null || directPaymentLines.isEmpty()) {
			errors.rejectValue("payment.apInvoiceMessage", null, null, ValidatorMessages.getString("DirectPayment.10"));
		} else {
			double totalPayment = 0;
			boolean hasError = false;
			double amount = 0;
			int row = 0;
			for (DirectPaymentLine directPaymentLine : directPaymentLines) {
				amount = directPaymentLine.getAmount();
				if((directPaymentLine.getDivisionNumber() == null || directPaymentLine.getDivisionNumber().trim().isEmpty())
						&& (directPaymentLine.getAccountNumber() == null || directPaymentLine.getAccountNumber().trim().isEmpty())
						&& (amount == 0.00) && (directPaymentLine.getDescription() == null || directPaymentLine.getDescription().trim().isEmpty())) {
					continue;
				}
				row++;
				if(directPaymentLine.getDivisionNumber() == null || directPaymentLine.getDivisionNumber().trim().isEmpty()) {
					hasError = true;
					errors.rejectValue("payment.apInvoiceMessage", null, null, String.format(ValidatorMessages.getString("DirectPayment.12"), row));
				}
				if(directPaymentLine.getAccountNumber() == null || directPaymentLine.getAccountNumber().trim().isEmpty()) {
					hasError = true;
					errors.rejectValue("payment.apInvoiceMessage", null, null, String.format(ValidatorMessages.getString("DirectPayment.13"), row));
				}
				if(amount == 0.00) {
					hasError = true;
					errors.rejectValue("payment.apInvoiceMessage", null, null, String.format(ValidatorMessages.getString("DirectPayment.11"), row));
				} else {
					totalPayment += amount;
				}
				if(directPaymentLine.getDescription() == null || directPaymentLine.getDescription().trim().isEmpty()) {
					hasError = true;
					errors.rejectValue("payment.apInvoiceMessage", null, null, String.format(ValidatorMessages.getString("DirectPayment.18"), row));
				}

				if(hasError) {
					break;
				}
			}

			if(row == 0) {
				errors.rejectValue("payment.apInvoiceMessage", null, null, ValidatorMessages.getString("DirectPayment.10"));
			}

			if(!hasError && (totalPayment != apPayment.getAmount())) {
				errors.rejectValue("payment.apInvoiceMessage", null, null, ValidatorMessages.getString("DirectPayment.14"));
			}
		}
		documentService.validateReferences(directPaymentDto.getReferenceDocuments(), errors);
	}

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return directPaymentDao.get(id).getApPayment().getFormWorkflow();
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return null;
	}

	/**
	 * Search Direct Payment form in general search
	 * @param searchCriteria The search criteria
	 * @param user The current user logged
	 * @return The Direct Payment form result
	 */
	public List<FormSearchResult> searchDirectPayments(String searchCriteria, User user) {
		// Replace comma in searchCriteria with ""
		String searchCriteriaFinal = searchCriteria.replace(",", "");
		Page<ApPayment> payments = paymentDao.searchPayment(searchCriteriaFinal, new PageSetting(1), PaymentType.TYPE_DIRECT_PAYMENT);
		List<FormSearchResult> result = new ArrayList<FormSearchResult>();
		for (ApPayment payment : payments.getData()) {
			apPaymentService.getApPayment(payment);
			String title = ("Voucher No. " + Integer.toString(payment.getVoucherNumber()));
			List<ResultProperty> properties = new ArrayList<ResultProperty>();
			String supplier = payment.getSupplier().getName();
			String comp = payment.getCompany().getName();
			String bankAcct = payment.getBankAccount().getName();
			String supplierAcct = payment.getSupplierAccount().getName();
			String status = payment.getFormWorkflow().getCurrentFormStatus().getDescription();
			DirectPayment directPayment = directPaymentDao.getDirectPaymentByPaymentId(payment.getId());
			String paymentTypeLabel = "";
			if(directPayment != null) {
				paymentTypeLabel = directPayment.getDirectPaymentTypeId().equals(DirectPaymentType.TYPE_CASH) ? "CASH" : "CHECK";
				properties.add(ResultProperty.getInstance("Payment Type", paymentTypeLabel));
				properties.add(ResultProperty.getInstance("Company", comp));
				properties.add(ResultProperty.getInstance("Payment Date", DateUtil.formatDate(payment.getPaymentDate())));
				properties.add(ResultProperty.getInstance("Bank Account", bankAcct));
				if(payment.getCheckDate() != null) {
					properties.add(ResultProperty.getInstance("Check Date", DateUtil.formatDate(payment.getCheckDate())));
				}
				properties.add(ResultProperty.getInstance("Amount", NumberFormatUtil.format(payment.getAmount())));
				properties.add(ResultProperty.getInstance("Supplier", supplier));
				properties.add(ResultProperty.getInstance("Supplier Account", supplierAcct));
				properties.add(ResultProperty.getInstance("Status", status));

				result.add(FormSearchResult.getInstanceOf(directPayment.getId(), title, properties));
			}
		}
		return result;
	}

	/**
	 * Remove empty invoice lines
	 * @param toBeSavedLines The to be saved direct payment invoice lines
	 * @return The processed list of invoices to be saved
	 */
	public List<DirectPaymentLine> processInvoiceLines(List<DirectPaymentLine> toBeSavedLines) {
		boolean hasDivision = false;
		boolean hasAccount = false;
		boolean hasAmount = false;
		boolean hasDescription = false;
		List<DirectPaymentLine> processedInvoiceLines = new ArrayList<DirectPaymentLine>();
		if(toBeSavedLines != null && !toBeSavedLines.isEmpty()) {
			for (DirectPaymentLine dpl : toBeSavedLines) {
				hasDivision = dpl.getDivisionNumber() != null && !dpl.getDivisionNumber().trim().isEmpty();
				hasAccount = dpl.getAccountNumber() != null && !dpl.getAccountNumber().trim().isEmpty();
				hasAmount = dpl.getAmount() != 0.0;
				hasDescription = dpl.getDescription() != null && !dpl.getDescription().trim().isEmpty();
				if(hasDivision || hasAccount || hasAmount || hasDescription) {
					processedInvoiceLines.add(dpl);
				}
			}
		}
		return processedInvoiceLines;
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";

		DirectPayment directPayment = directPaymentDao.getByEbObjectId(ebObjectId);
		Integer pId = directPayment.getId();
		ApPayment apPayment = directPayment.getApPayment();

		FormProperty property = workflowHandler.getProperty("DirectPayment", user);
		String popupLink = "/" + property.getEdit() + "?pId=" + pId;
		String printOutLink = "/" + property.getPrint() + "?pId=" + pId;

		String latestStatus = apPayment.getFormWorkflow().getCurrentFormStatus().getDescription();
		String title = "Direct Payment -" + apPayment.getVoucherNumber();
		shortDescription = new StringBuffer(title)
				.append(" " + apPayment.getSupplier().getName())
				.append(" " + apPayment.getSupplierAccount().getName())
				.append(" " + DateUtil.formatDate(apPayment.getPaymentDate()));
				shortDescription.append(" " + apPayment.getAmount());

		return ObjectInfo.getInstance(ebObjectId, title, latestStatus,
				shortDescription.toString(), fullDescription, popupLink, printOutLink);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		Integer ebObjectId = ebObject.getId();
		return directPaymentDao.getByEbObjectId(ebObjectId);
	}
}
