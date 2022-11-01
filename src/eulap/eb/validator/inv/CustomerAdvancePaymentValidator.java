package eulap.eb.validator.inv;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.eb.dao.CustomerAdvancePaymentDao;
import eulap.eb.domain.hibernate.CustomerAdvancePayment;
import eulap.eb.domain.hibernate.CustomerAdvancePaymentLine;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.service.CompanyService;
import eulap.eb.service.CustomerAdvancePaymentService;
import eulap.eb.service.ReferenceDocumentService;
import eulap.eb.service.TimePeriodService;
import eulap.eb.service.ValidationUtil;
import eulap.eb.service.fap.FormStatusService;
import eulap.eb.validator.ValidatorMessages;
import eulap.eb.validator.ValidatorUtil;

/**
 * Validator class for {@link CustomerAdvancePayment}

 */

@Service
public class CustomerAdvancePaymentValidator implements Validator {
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private CustomerAdvancePaymentService capService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private FormStatusService formStatusService;
	@Autowired
	private ReferenceDocumentService refDocumentService;
	@Autowired
	private CustomerAdvancePaymentDao capDao;

	@Override
	public boolean supports(Class<?> clazz) {
		return CustomerAdvancePayment.class.equals(clazz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		CustomerAdvancePayment cap = (CustomerAdvancePayment) obj;

		ValidatorUtil.validateCompany(cap.getCompanyId(), companyService,
				errors, "companyId");

		if(cap.getSalesOrderId() == null) {
			errors.rejectValue("salesOrderId", null, null, ValidatorMessages.getString("CustomerAdvancePaymentValidator.25"));
		}
		if (cap.getRefNumber() == null || cap.getRefNumber().trim().isEmpty()) {
			errors.rejectValue("refNumber", null, null, ValidatorMessages.getString("CustomerAdvancePaymentValidator.26"));
		} else if (cap.getRefNumber().trim().length() > CustomerAdvancePayment.MAX_REF_NUMBER) {
			errors.rejectValue("refNumber", null, null, String.format(ValidatorMessages.getString("CustomerAdvancePaymentValidator.27"), 
					CustomerAdvancePayment.MAX_REF_NUMBER));
		}

		if (cap.getArCustomerId() == null) {
			errors.rejectValue("arCustomerId", null, null, ValidatorMessages.getString("CustomerAdvancePaymentValidator.4"));
		}

		if (cap.getArCustomerAccountId() == null) {
			errors.rejectValue("arCustomerAccountId", null, null, ValidatorMessages.getString("CustomerAdvancePaymentValidator.5"));
		}

		if (cap.getSalesInvoiceNo() != null && !cap.getSalesInvoiceNo().trim().isEmpty()) {
			if (cap.getSalesInvoiceNo().trim().length() > CustomerAdvancePayment.MAX_SALE_INVOICE_NO) {
				errors.rejectValue("salesInvoiceNo", null, null, ValidatorMessages.getString("CustomerAdvancePaymentValidator.6") + 
						CustomerAdvancePayment.MAX_SALE_INVOICE_NO + ValidatorMessages.getString("CustomerAdvancePaymentValidator.7"));
			}
		}

		if (cap.getReceiptDate() == null) {
			errors.rejectValue("receiptDate", null, null, ValidatorMessages.getString("CustomerAdvancePaymentValidator.8"));
		} else if (!timePeriodService.isOpenDate(cap.getReceiptDate())) {
				errors.rejectValue("receiptDate", null, null, ValidatorMessages.getString("CustomerAdvancePaymentValidator.9"));
		}

		if (cap.getMaturityDate() == null) {
			errors.rejectValue("maturityDate", null, null, ValidatorMessages.getString("CustomerAdvancePaymentValidator.10"));
		} else if (!timePeriodService.isOpenDate(cap.getMaturityDate())) {
				errors.rejectValue("maturityDate", null, null, ValidatorMessages.getString("CustomerAdvancePaymentValidator.11"));
		}
		//Amount
		List<CustomerAdvancePaymentLine> capLines = cap.getCapLines();
		double totalLineAmt = 0;
		if(capLines != null) {
			for(CustomerAdvancePaymentLine capLine : capLines ) {
				totalLineAmt += capLine.getAmount();
			}
		}
		if(cap.getAmount() == null || cap.getAmount() == 0) {
			errors.rejectValue("amount", null, null, ValidatorMessages.getString("CustomerAdvancePaymentValidator.22"));
		} else if(cap.getAmount() < 0) {
			errors.rejectValue("amount", null, null, ValidatorMessages.getString("CustomerAdvancePaymentValidator.23"));
		} else if(cap.getAmount() > totalLineAmt) {
			errors.rejectValue("amount", null, null, ValidatorMessages.getString("CustomerAdvancePaymentValidator.24"));
		}

		List<CustomerAdvancePayment> caps = capDao.getCapsByCapId(cap.getId());
		if(caps != null && !caps.isEmpty()) {
			String capNumbers = "";
			int index = 0;
			for(CustomerAdvancePayment advPamynet : caps) {
				capNumbers += index == 0 ? "CAP-" + advPamynet.getCapNumber() : ", CAP-" + advPamynet.getCapNumber();
				index++;
			}
			errors.rejectValue("amount", null, null, String.format(ValidatorMessages.getString("CustomerAdvancePaymentValidator.28"), capNumbers));
		}

		//Validate form status
		FormWorkflow workflow = cap.getId() != 0 ? capService.getFormWorkflow(cap.getId()) : null;
		String workflowError = ValidationUtil.validateFormStatus(formStatusService, workflow);
		if (workflowError != null ) {
			errors.rejectValue("formWorkflowId", null, null, workflowError);
		}
		refDocumentService.validateReferences(cap.getReferenceDocuments(), errors);
	}
}
