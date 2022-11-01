package eulap.eb.service.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.DateUtil;
import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.APInvoiceDao;
import eulap.eb.dao.ApPaymentLineDao;
import eulap.eb.dao.CompanyDao;
import eulap.eb.dao.InvoiceTypeDao;
import eulap.eb.dao.SupplierAccountDao;
import eulap.eb.dao.SupplierDao;
import eulap.eb.dao.TermDao;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.ApPaymentLine;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.InvoiceType;
import eulap.eb.domain.hibernate.RReceivingReport;
import eulap.eb.domain.hibernate.Supplier;
import eulap.eb.domain.hibernate.SupplierAccount;
import eulap.eb.domain.hibernate.Term;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.RReceivingReportService;
import eulap.eb.service.fap.FormStatusService;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.web.dto.InvoiceRegisterDto;
import eulap.eb.web.dto.PaymentStatus;
/**
 * Business logic for generating report for the Invoice Register.

 */
@Service
public class InvoiceRegisterServiceImpl implements InvoiceRegisterService{
	@Autowired
	private InvoiceTypeDao invoiceTypeDao;
	@Autowired
	private SupplierDao supplierDao;
	@Autowired
	private SupplierAccountDao supplierAccountDao;
	@Autowired
	private TermDao termDao;
	@Autowired
	private APInvoiceDao invoiceDao;
	@Autowired
	private RReceivingReportService reportService;
	@Autowired
	private FormStatusService formStatusService;
	@Autowired
	private CompanyDao companyDao;
	@Autowired
	private ApPaymentLineDao paymentLineDao;

	private static double CANCELLED_AMOUNT = 0.0;
	private final Logger logger = Logger.getLogger(InvoiceRegisterServiceImpl.class);

	@Override
	public Page<InvoiceRegisterDto> generateReport(User user,
			PageSetting pageSetting, InvoiceRegisterParam param) {
		logger.debug("Retrieving list of invoices from the databse");
		Page<APInvoice> invoices = invoiceDao.searchInvoices(param.getCompanyId(), param.getInvoiceTypeId(),
				param.getSupplierId(), param.getSupplierAccountId(), param.getTermId(),
				param.getInvoiceNumber(), param.getFromInvoiceDate(), param.getToInvoiceDate(),
				param.getFromGLDate(), param.getToGLDate(), param.getFromDueDate(), param.getToDueDate(),
				param.getFromAmount(), param.getToAmount(), param.getFromSeqNumber(),
				param.getToSeqNumber(), param.getInvoiceStatus(), param.getPaymentStatus(), param.getAsOfDate(), pageSetting);
		Collection<InvoiceRegisterDto> invoiceRegister = new ArrayList<InvoiceRegisterDto>();
		String deliveryReceiptNumber = "";
		for (APInvoice invoice : invoices.getData()) {
			Double balance = invoice.getAmount() - getTotalPaidAmount(invoice, param.getAsOfDate());
			setPaymentStatus(invoice, balance);
			if(invoice.getFormWorkflow().getCurrentStatusId() == FormStatus.CANCELLED_ID) {
				invoice.setAmount(CANCELLED_AMOUNT);
				balance = CANCELLED_AMOUNT;
			}
			if(param.getPaymentStatus() == invoice.getPaymentStatus() || param.getPaymentStatus() == -1) {
				Integer termId = invoice.getTermId();
				Term term = null;
				Double invoiceAmount = 0.0;
				if(termId != null)
					term = termDao.get(termId);
				InvoiceType invoiceType = invoiceTypeDao.get(invoice.getInvoiceTypeId());
				Supplier supplier = supplierDao.get(invoice.getSupplierId());
				SupplierAccount supplierAcct = supplierAccountDao.get(invoice.getSupplierAccountId());
				//Set values for Return to supplier
				if(invoice.getInvoiceTypeId().equals(InvoiceType.RTS_TYPE_ID)) {
					logger.debug("Processing return to supplier");
					term = supplierAcct.getTerm();
					invoiceAmount = NumberFormatUtil.negateAmount(invoice.getAmount());
					invoice.setAmount(invoiceAmount);
					balance = NumberFormatUtil.negateAmount(balance);
					invoice.setInvoiceNumber("RTS-" + invoice.getSequenceNumber());
					invoice.setInvoiceDate(invoice.getGlDate());
					invoice.setDueDate(DateUtil.addDaysToDate(invoice.getGlDate(), term.getDays()));
				}
				//Set values for Receiving report
				else if(invoice.getInvoiceTypeId().equals(InvoiceType.RR_TYPE_ID)) {
					logger.debug("Processing receiving reports");
					String sequenceNumber = "RR-" + invoice.getSequenceNumber();
					String supplierInvoiceNumber = invoice.getInvoiceNumber();
					RReceivingReport rrReport = reportService.getRrByInvoiceId(invoice.getId());
					if(rrReport != null)
						deliveryReceiptNumber = rrReport.getDeliveryReceiptNo();
					if(!supplierInvoiceNumber.isEmpty())
						sequenceNumber += ", " + supplierInvoiceNumber;
					if(!deliveryReceiptNumber.isEmpty())
						sequenceNumber += ", " + deliveryReceiptNumber;
					invoice.setInvoiceNumber(sequenceNumber);
				}
				String formStatus = invoice.getFormWorkflow().getCurrentStatus().getDescription();
				if(invoice.getFormWorkflow().getCurrentStatusId() == FormStatus.CREATED_ID)
					formStatus = "NEW"; //Set form status to NEW if created
				//Set the data to the Invoice Register dto
				Company company = companyDao.get(param.getCompanyId());
				invoiceRegister.add(InvoiceRegisterDto.getInstanceOf(company, invoice.getDivision().getName(), invoice, invoiceType,
						supplier, supplierAcct, term, formStatus, balance, null));
			}
		}
		return new Page<InvoiceRegisterDto>(pageSetting, invoiceRegister, invoices.getTotalRecords());
	}

	/**
	 * Compute the total paid amount of the invoice.
	 * @param apInvoice The invoice object.
	 * @return the total paid amount
	 */
	public Double getTotalPaidAmount(APInvoice apInvoice, Date asOfDate) {
		List<ApPaymentLine> payments = paymentLineDao.getPaidInvoices(apInvoice.getId(), asOfDate);
		Double totalPaidAmount = 0.0;
		Double invoiceAmount = 0.0;
		Integer invoiceTypeId = apInvoice.getInvoiceTypeId();
		for (ApPaymentLine appl : payments) {
			invoiceAmount = appl.getPaidAmount();
			if (invoiceTypeId.equals(InvoiceType.RTS_TYPE_ID)
					|| invoiceTypeId.equals(InvoiceType.RTS_CENTRAL_TYPE_ID) || invoiceTypeId.equals(InvoiceType.RTS_EB_TYPE_ID) ||
					invoiceTypeId.equals(InvoiceType.RTS_NSB3_TYPE_ID) || invoiceTypeId.equals(InvoiceType.RTS_NSB4_TYPE_ID) ||
					invoiceTypeId.equals(InvoiceType.RTS_NSB5_TYPE_ID) || invoiceTypeId.equals(InvoiceType.RTS_NSB8_TYPE_ID) ||
					invoiceTypeId.equals(InvoiceType.RTS_NSB8A_TYPE_ID)) {
				totalPaidAmount += Math.abs(invoiceAmount); // Remove negation
			} else {
				totalPaidAmount += invoiceAmount;
			}
		}
		logger.debug("The total paid amount of invoice number "+apInvoice.getInvoiceNumber()+" is: " +totalPaidAmount);
		// Free up memory
		payments = null;
		return totalPaidAmount;
	}

	/**
	 * Set the payment status of the AP Invoice.
	 * <br>FULLY PAID : balance == 0.0
	 * <br>PARTIALLY PAID : balance < invoice.getAmount()
	 * <br>UNPAID : balance == invoice.getAmount()
	 */
	public void setPaymentStatus(APInvoice invoice, Double balance) {
		if(balance == 0.0)
			invoice.setPaymentStatus(PaymentStatus.FULLY_PAID);
		else if(balance < invoice.getAmount())
			invoice.setPaymentStatus(PaymentStatus.PARTIALL_PAID);
		else if (balance.equals(invoice.getAmount()))
			invoice.setPaymentStatus(PaymentStatus.UNPAID);
	}

	/**
	 * Create the Payment Statuses.
	 * <br>The payment statuses are: Fully Paid, Partially Paid and Unpaid
	 * @return List of payment status.
	 */
	public List<PaymentStatus> paymentStatus() {
		List<PaymentStatus> payments = new ArrayList<PaymentStatus>();
		payments.add(PaymentStatus.getInstanceOf(PaymentStatus.FULLY_PAID, "Fully Paid"));
		payments.add(PaymentStatus.getInstanceOf(PaymentStatus.PARTIALL_PAID, "Partially Paid"));
		payments.add(PaymentStatus.getInstanceOf(PaymentStatus.UNPAID, "Unpaid"));
		return payments;
	}

	/**
	 * Get the statuses of ap invoice.
	 * @param user The logged user.
	 * @return The list of statuses of ap invoice
	 */
	public List<FormStatus> getInvoiceRegisterStatuses(User user) {
		return formStatusService.getFormStatuses(FormProperty.ACCT_INVOICE , user, false, true);
	}

	/**
	 * Get the invoice register report data.
	 * @param user Current User Log.
	 * @return Invoice register report data.
	 */
	public JRDataSource generateInvoiceRegister(User user, InvoiceRegisterParam param) {
		EBJRServiceHandler<InvoiceRegisterDto> handler = new JRInvoiceHandler(param, this);
		return new EBDataSource<InvoiceRegisterDto>(handler);
	}

	private static class JRInvoiceHandler implements EBJRServiceHandler<InvoiceRegisterDto> {
		private InvoiceRegisterParam param;
		private InvoiceRegisterServiceImpl registerServiceImpl;

		private JRInvoiceHandler (InvoiceRegisterParam param,
				InvoiceRegisterServiceImpl registerServiceImpl){
			this.param = param;
			this.registerServiceImpl = registerServiceImpl;
		}
		@Override
		public void close() throws IOException {
			registerServiceImpl = null;
		}
		@Override
		public Page<InvoiceRegisterDto> nextPage(PageSetting pageSetting) {
			registerServiceImpl.logger.debug("Retrieving list of invoices from the databse");
			Page<APInvoice> invoices = registerServiceImpl.invoiceDao.searchInvoices(param.getCompanyId(), param.getDivisionId(),
					param.getInvoiceTypeId(), param.getSupplierId(), param.getSupplierAccountId(), param.getTermId(),
					param.getInvoiceNumber(), param.getFromInvoiceDate(), param.getToInvoiceDate(),
					param.getFromGLDate(), param.getToGLDate(), param.getFromDueDate(), param.getToDueDate(),
					param.getFromAmount(), param.getToAmount(), param.getFromSeqNumber(),
					param.getToSeqNumber(), param.getInvoiceStatus(), param.getPaymentStatus(), param.getAsOfDate(), pageSetting);
			Collection<InvoiceRegisterDto> invoiceRegister = new ArrayList<InvoiceRegisterDto>();
			String deliveryReceiptNumber = "";
			for (APInvoice invoice : invoices.getData()) {
				InvoiceRegisterDto invoiceReg= new InvoiceRegisterDto();
				Double balance = invoice.getAmount() - registerServiceImpl.getTotalPaidAmount(invoice, param.getAsOfDate());
				registerServiceImpl.setPaymentStatus(invoice, balance);
				if(invoice.getFormWorkflow().getCurrentStatusId() == FormStatus.CANCELLED_ID) {
					invoice.setAmount(CANCELLED_AMOUNT);
					balance = CANCELLED_AMOUNT;
					for (FormWorkflowLog log : invoice.getFormWorkflow().getFormWorkflowLogs()) {
						if (log.getFormStatusId() == FormStatus.CANCELLED_ID) {
							invoiceReg.setCancellationRemarks(log.getComment());
						}
					}
				}
				if(param.getPaymentStatus() == invoice.getPaymentStatus() || param.getPaymentStatus() == -1) {
					Integer termId = invoice.getTermId();
					Term term = null;
					Double invoiceAmount = 0.0;
					if(termId != null)
						term = registerServiceImpl.termDao.get(termId);
					InvoiceType invoiceType = registerServiceImpl.invoiceTypeDao.get(invoice.getInvoiceTypeId());
					Supplier supplier = registerServiceImpl.supplierDao.get(invoice.getSupplierId());
					SupplierAccount supplierAcct = registerServiceImpl.supplierAccountDao.get(invoice.getSupplierAccountId());
					//Set values for Return to supplier
					Integer invoiceTypeId = invoice.getInvoiceTypeId();
					if(invoiceTypeId.equals(InvoiceType.RTS_TYPE_ID) ||
							invoiceTypeId.equals(InvoiceType.RTS_CENTRAL_TYPE_ID) || invoiceTypeId.equals(InvoiceType.RTS_EB_TYPE_ID) ||
							invoiceTypeId.equals(InvoiceType.RTS_NSB3_TYPE_ID) || invoiceTypeId.equals(InvoiceType.RTS_NSB4_TYPE_ID) ||
							invoiceTypeId.equals(InvoiceType.RTS_NSB5_TYPE_ID) || invoiceTypeId.equals(InvoiceType.RTS_NSB8_TYPE_ID) ||
							invoiceTypeId.equals(InvoiceType.RTS_NSB8A_TYPE_ID)) {
						registerServiceImpl.logger.debug("Processing return to supplier");
						term = supplierAcct.getTerm();
						invoiceAmount = NumberFormatUtil.negateAmount(invoice.getAmount());
						invoice.setAmount(invoiceAmount);
						balance = NumberFormatUtil.negateAmount(balance);
						invoice.setInvoiceNumber("RTS-" + invoice.getSequenceNumber());
						invoice.setInvoiceDate(invoice.getGlDate());
						invoice.setDueDate(DateUtil.addDaysToDate(invoice.getGlDate(), term.getDays()));
					} else if (invoiceTypeId.equals(InvoiceType.RR_TYPE_ID) || invoiceTypeId.equals(InvoiceType.RR_CENTRAL_TYPE_ID) || invoiceTypeId.equals(InvoiceType.RR_NSB3_TYPE_ID)
							|| invoiceTypeId.equals(InvoiceType.RR_NSB4_TYPE_ID) || invoiceTypeId.equals(InvoiceType.RR_NSB5_TYPE_ID) || invoiceTypeId.equals(InvoiceType.RR_NSB8_TYPE_ID)
							|| invoiceTypeId.equals(InvoiceType.RR_NSB8A_TYPE_ID)) { //Set values for Receiving report
						registerServiceImpl.logger.debug("Processing receiving reports");
						String sequenceNumber = "RR-" + invoice.getSequenceNumber();
						String supplierInvoiceNumber = invoice.getInvoiceNumber();
						RReceivingReport rrReport = registerServiceImpl.reportService.getRrByInvoiceId(invoice.getId());
						if(rrReport != null)
							deliveryReceiptNumber = rrReport.getDeliveryReceiptNo();
						if(!supplierInvoiceNumber.isEmpty())
							sequenceNumber += ", " + supplierInvoiceNumber;
						if(!deliveryReceiptNumber.isEmpty())
							sequenceNumber += ", " + deliveryReceiptNumber;
						invoice.setInvoiceNumber(sequenceNumber);
					}
					String formStatus = invoice.getFormWorkflow().getCurrentStatus().getDescription();
					//Set the data to the Invoice Register dto
					Company company = registerServiceImpl.companyDao.get(param.getCompanyId());
					invoiceRegister.add(InvoiceRegisterDto.getInstanceOf(company, invoice.getDivision().getName() ,invoice, invoiceType,
							supplier, supplierAcct, term, formStatus, balance, invoiceReg.getCancellationRemarks()));
				}
			}
			return new Page<InvoiceRegisterDto>(pageSetting, invoiceRegister, invoices.getTotalRecords());
		}
	}
	
}
