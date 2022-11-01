package eulap.eb.validator;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import eulap.common.util.NumberFormatUtil;
import eulap.eb.dao.APInvoiceDao;
import eulap.eb.dao.ApPaymentDao;
import eulap.eb.dao.ApPaymentLineDao;
import eulap.eb.dao.CurrencyDao;
import eulap.eb.dao.SupplierAdvPaymentDao;
import eulap.eb.domain.hibernate.APInvoice;
import eulap.eb.domain.hibernate.ApPayment;
import eulap.eb.domain.hibernate.ApPaymentLine;
import eulap.eb.domain.hibernate.ApPaymentLineType;
import eulap.eb.domain.hibernate.SupplierAdvancePayment;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.APInvoiceService;
import eulap.eb.service.ApPaymentService;
import eulap.eb.service.CurrencyUtil;
import eulap.eb.service.ReferenceDocumentService;
import eulap.eb.web.dto.ApPaymentLineDto;

/**
 * Validation class for {@link ApPayment}

 */
@Service
public class ApPaymentValidator implements Validator{
	private static Logger logger =  Logger.getLogger(ApPaymentValidator.class);
	@Autowired
	private ApPaymentService paymentService;
	@Autowired
	private APInvoiceDao apInvoiceDao;
	@Autowired
	private SupplierAdvPaymentDao sapDao;
	@Autowired
	private CurrencyDao currencyDao;
	@Autowired
	private ReferenceDocumentService refDocumentService;
	@Autowired
	private ApPaymentLineDao apPaymentLineDao;
	@Autowired
	private APInvoiceService invoiceService;
	@Autowired
	private ApPaymentDao paymentDao;

	@Override
	public boolean supports(Class<?> clazz) {
		return ApPayment.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		//Do nothing
	}

	public void validate(Object target, Errors errors, User user) {
		validate(target, errors, user, "");
	}

	public void validate (Object target, Errors errors, User user, String fieldPrepend) {
		logger.info("Validating payments");
		ApPayment payment = (ApPayment) target;
		//Amount
		if (payment.getAmount() > 10000000000.00 || payment.getAmount() < 0) {
			errors.rejectValue(fieldPrepend+"amount", null, null, ValidatorMessages.getString("ApPaymentValidator.0"));
		}

		//Bank Account
		boolean hasBankAccount = true;
		if (payment.getBankAccountId() == null || payment.getBankAccountId() == 0) {
			errors.rejectValue(fieldPrepend+"bankAccountId", null, null, ValidatorMessages.getString("ApPaymentValidator.1"));
			hasBankAccount = false;
		}

		//Checkbook
		boolean hasCheckbook = true;
		if (payment.getCheckbookId() == null || payment.getCheckbookId() == 0) {
			errors.rejectValue(fieldPrepend+"checkbookId", null, null, ValidatorMessages.getString("ApPaymentValidator.2"));
			hasCheckbook = false;
		}

		//Supplier
		if (payment.getSupplierId() == null || payment.getSupplierId() == 0) {
			errors.rejectValue(fieldPrepend+"supplierId", null, null, ValidatorMessages.getString("ApPaymentValidator.3"));
		}

		//Supplier Account
		if (payment.getSupplierAccountId() == null || payment.getSupplierAccountId() == 0) {
			errors.rejectValue(fieldPrepend+"supplierAccountId", null, null, ValidatorMessages.getString("ApPaymentValidator.4"));
		}

		//Check number
		if (payment.getCheckNumber() == null || payment.getCheckNumber().equals(BigDecimal.ZERO)) {
			errors.rejectValue(fieldPrepend+"checkNumber", null, null, ValidatorMessages.getString("ApPaymentValidator.5"));
		} else if (String.valueOf(payment.getCheckNumber()).length() > 20) {
			errors.rejectValue(fieldPrepend+"checkNumber", null, null, ValidatorMessages.getString("ApPaymentValidator.6"));
		} else if (hasCheckbook && !paymentService.isCheckNumberInRange(payment.getCheckbookId(), payment.getCheckNumber())) {
			errors.rejectValue(fieldPrepend+"checkNumber", null, null, ValidatorMessages.getString("ApPaymentValidator.7"));
		} else if (hasBankAccount && !paymentService.isUniqueCheckNumber(payment.getBankAccountId(), payment)) {
			errors.rejectValue(fieldPrepend+"checkNumber", null, null, ValidatorMessages.getString("ApPaymentValidator.8"));
		}

		//Payment Date
		if (payment.getPaymentDate() == null) {
			errors.rejectValue(fieldPrepend+"paymentDate", null, null, ValidatorMessages.getString("ApPaymentValidator.9"));
		} else if (!paymentService.isValidPaymentDate(payment.getPaymentDate())) {
			errors.rejectValue(fieldPrepend+"paymentDate", null, null, ValidatorMessages.getString("ApPaymentValidator.10"));
		}

		//Check Date
		if (payment.getCheckDate() == null) {
			errors.rejectValue(fieldPrepend+"checkDate", null, null, ValidatorMessages.getString("ApPaymentValidator.11"));
		} else if (!paymentService.isValidPaymentDate(payment.getCheckDate())) {
			errors.rejectValue(fieldPrepend+"checkDate", null, null, ValidatorMessages.getString("ApPaymentValidator.12"));
		}

		if (payment.isSpecifyPayee()) {
			if (payment.getPayee() == null){
				errors.rejectValue(fieldPrepend+"payee", null, null, ValidatorMessages.getString("ApPaymentValidator.13"));
			} else if (payment.getPayee().trim().isEmpty()) {
				errors.rejectValue(fieldPrepend+"payee", null, null, ValidatorMessages.getString("ApPaymentValidator.14"));
			} else {
				if (payment.getPayee().trim().length() > ApPayment.MAX_PAYEE) {
					errors.rejectValue(fieldPrepend+"payee", null, null, ValidatorMessages.getString("ApPaymentValidator.15")
							+ ApPayment.MAX_PAYEE + ValidatorMessages.getString("ApPaymentValidator.17"));
				}
			}
		}
		if(payment.getCurrencyId() == null) {
			//Currency is required.
			errors.rejectValue(fieldPrepend+"currencyId", null, null, ValidatorMessages.getString("ApPaymentValidator.31"));
		} else if(!currencyDao.get(payment.getCurrencyId()).isActive()) {
			//Currency is inactive
			errors.rejectValue(fieldPrepend+"currencyId", null, null, ValidatorMessages.getString("ApPaymentValidator.32"));
		}

		int rowNumber = 0;
		boolean isValidTotalAmt = true;
		double totalAmount = 0;
		List<ApPaymentLineDto> apPaymentLineDtos = payment.getApPaymentLineDtos();
		APInvoice invoice = null;
		for (ApPaymentLineDto dto : apPaymentLineDtos) {
			rowNumber++;
			if (dto.getReferenceNumber().trim().isEmpty() && dto.getAmount() != 0.0) {
				errors.rejectValue(fieldPrepend+"apInvoiceMessage", null, null, ValidatorMessages.getString("ApPaymentValidator.18"));
			}
			Integer refObjectId = dto.getRefenceObjectId();
			Integer typeId = dto.getApPaymentLineTypeId();
			boolean isAdvPayment = typeId.equals(ApPaymentLineType.SUPLIER_ADVANCE_PAYMENT);
			double refTransRate = getReferenceCurrencyRate(refObjectId, isAdvPayment);

			Double savedAmount = CurrencyUtil.convertMonetaryValues(getParentAmount(refObjectId, typeId), refTransRate);
			Double totalPaidAmt = CurrencyUtil.convertMonetaryValues(paymentService.getTotalApLineAmt(refObjectId, payment.getId()), refTransRate);
			Double remainingBalance = NumberFormatUtil.roundOffRuleOfFive(savedAmount - Math.abs(totalPaidAmt));

			double lineAmount = NumberFormatUtil.roundOffRuleOfFive(dto.getAmount() != null ? dto.getAmount() : 0);
			double toBeEvalAmount = lineAmount;
			if (!isAdvPayment) {
				toBeEvalAmount = Math.abs(lineAmount);
			}
			if (lineAmount == 0) {
				errors.rejectValue(fieldPrepend+"apInvoiceMessage", null, null, ValidatorMessages.getString("ApPaymentValidator.22"));
				isValidTotalAmt = false;
			} else if (!isAdvPayment && remainingBalance < Math.abs(toBeEvalAmount)) {
				errors.rejectValue(fieldPrepend+"apInvoiceMessage", null, null,
						String.format(ValidatorMessages.getString("ApPaymentValidator.29"), rowNumber));
			} else if (toBeEvalAmount < 0) { // Exclude negative amounts (SAP transactions)
				Double totalSapPaidNegativeAmt = getSapTotalNegativePaidAmount(refObjectId, payment.getId());
				Double remainingSapNegativeAmt = NumberFormatUtil.roundOffRuleOfFive(savedAmount - Math.abs(totalSapPaidNegativeAmt));
				if(Math.abs(remainingSapNegativeAmt) < Math.abs(lineAmount)) {
					errors.rejectValue(fieldPrepend+"apInvoiceMessage", null, null,
							String.format(ValidatorMessages.getString("ApPaymentValidator.29"), rowNumber));
				}
			}

			invoice = getInvByObjectId(refObjectId);
			if (invoice != null && invoiceService.isLoanInvoiceType(invoice.getInvoiceTypeId())) {
				double loanAmount = NumberFormatUtil.roundOffRuleOfFive(CurrencyUtil.convertMonetaryValues(invoice.getAmount(),
						invoice.getCurrencyRateValue()));
				if (loanAmount > lineAmount) {
					//AP Loan transactions should be paid in full
					errors.rejectValue(fieldPrepend+"apInvoiceMessage", null, null, ValidatorMessages.getString("ApPaymentValidator.33"));
				}
			}
			totalAmount += lineAmount;
		}

		if (rowNumber == 0) {
			errors.rejectValue(fieldPrepend+"apInvoiceMessage", null, null, ValidatorMessages.getString("ApPaymentValidator.27"));
		}

		double headerAmount = NumberFormatUtil.roundOffNumber(payment.getAmount(), 6);
		double newTotalAmount = NumberFormatUtil.roundOffNumber(totalAmount, 6);
		if (isValidTotalAmt && (newTotalAmount != headerAmount)) {
			errors.rejectValue(fieldPrepend+"apInvoiceMessage", null, null, ValidatorMessages.getString("ApPaymentValidator.28"));
		}

		refDocumentService.validateReferences(payment.getReferenceDocuments(), errors);
	}

	private Double getSapTotalNegativePaidAmount(Integer parentObjectId, Integer apPaymentId) {
		List<ApPaymentLine> apPaymentLines = apPaymentLineDao.getApPaymentLines(parentObjectId, apPaymentId);
		Double total = 0.00;
		for(ApPaymentLine apl : apPaymentLines) {
			if(apl.getPaidAmount() < 0) {
				total += apl.getPaidAmount();
			}
		}
		return total;
	}

	private APInvoice getInvByObjectId(Integer objectId) {
		return apInvoiceDao.getByEbObjectId(objectId);
	}

	private Double getParentAmount(Integer objectId, Integer apInvoiceLineTypeId) {
		if (ApPaymentLineType.INVOICE == apInvoiceLineTypeId) {
			return Math.abs(getInvByObjectId(objectId).getAmount()); // get absolute value for RTS/Debit memo amount
		} else {
			return sapDao.getByEbObjectId(objectId).getAmount();
		}
	}

	/**
	 * Validate ap payment clearing details.
	 * @param apPayment The {@link ApPayment}.
	 * @param errors The {@link Error}
	 */
	public void validateStatusLogs(ApPayment apPayment, Errors errors) {
		ApPayment savedPayment = paymentDao.get(apPayment.getId());
		if(apPayment.getDateCleared() == null) {
			errors.rejectValue("dateCleared", null, null, ValidatorMessages.getString("ApPaymentValidator.30"));
		} else if (savedPayment.getCheckDate().after(apPayment.getDateCleared())) {
			errors.rejectValue("dateCleared", null, null, ValidatorMessages.getString("ApPaymentValidator.34"));
		}
	}

	private double getReferenceCurrencyRate(Integer refObjId, boolean isSap) {
		if (isSap) {
			SupplierAdvancePayment sap = sapDao.getByEbObjectId(refObjId);
			if (sap == null) {
				return 0;
			}
			return sap.getCurrencyRateValue();
		}
		APInvoice apInvoice = getInvByObjectId(refObjId);
		if (apInvoice == null) {
			return 0;
		}
		return apInvoice.getCurrencyRateValue();
	}
}
