package eulap.eb.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.AccountCombinationDao;
import eulap.eb.dao.AccountSaleItemDao;
import eulap.eb.dao.ArCustomerAcctDao;
import eulap.eb.dao.ArCustomerDao;
import eulap.eb.dao.ArLineDao;
import eulap.eb.dao.ArReceiptTransactionDao;
import eulap.eb.dao.ArServiceLineDao;
import eulap.eb.dao.ArTransactionDao;
import eulap.eb.dao.ArTransactionTypeDao;
import eulap.eb.dao.FormStatusDao;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.AccountSaleItem;
import eulap.eb.domain.hibernate.AccountSales;
import eulap.eb.domain.hibernate.ArCustomer;
import eulap.eb.domain.hibernate.ArLine;
import eulap.eb.domain.hibernate.ArReceipt;
import eulap.eb.domain.hibernate.ArReceiptTransaction;
import eulap.eb.domain.hibernate.ArServiceLine;
import eulap.eb.domain.hibernate.ArTransaction;
import eulap.eb.domain.hibernate.ArTransactionType;
import eulap.eb.domain.hibernate.BaseFormWorkflow;
import eulap.eb.domain.hibernate.EBObject;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.FormWorkflowLog;
import eulap.eb.domain.hibernate.ServiceSetting;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.oo.ObjectInfo;
import eulap.eb.service.workflow.BaseWorkflowService;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.service.workflow.WorkflowServiceHandler;
import eulap.eb.web.dto.ArTransactionRegisterDto;
import eulap.eb.web.dto.FormSearchResult;
import eulap.eb.web.dto.ResultProperty;

/**
 * Class that handles the business logic for ArTransaction
 * 

 */
@Service
public class ArTransactionService extends BaseWorkflowService {
	private final Logger logger = Logger.getLogger(ArTransactionService.class);
	@Autowired
	private AccountCombinationDao combinationDao;
	@Autowired
	private ArLineDao arLineDao;
	@Autowired
	private ArTransactionDao arTransactionDao;
	@Autowired
	private ArCustomerAcctDao arCustomerAcctDao;
	@Autowired
	private ArReceiptTransactionDao arReceiptTransactionDao;
	@Autowired
	private TimePeriodService timePeriodService;
	@Autowired
	private AccountSaleItemDao accountSaleItemDao;
	@Autowired
	private ArCustomerDao arCustomerDao;
	@Autowired
	private ArReceiptService arReceiptService;
	@Autowired
	private ItemService itemService;
	@Autowired
	private ItemDiscountService itemDiscountService;
	@Autowired
	private ArTransactionTypeDao transactionTypeDao;
	@Autowired
	private WorkflowServiceHandler workflowHandler;
	@Autowired
	private AccountSalePoService accountSalePoService;
	@Autowired
	private FormStatusDao formStatusDao;
	@Autowired
	private ReferenceDocumentService refDocumentService;
	@Autowired
	private UnitMeasurementService unitMeasurementService;
	@Autowired
	private ServiceSettingService serviceSettingService;
	@Autowired
	private ArServiceLineDao arServiceLineDao;

	/**
	 * Fix the Sequence numbers of AR Transaction forms.
	 * 
	 * @param user The logged user.
	 */
	public String updateSeqNumber(User user) {
		logger.info("Fixing the sequence number of ar transactions.");
		List<ArTransactionType> transactionTypes = transactionTypeDao.getTransactionTypes(user);
		int nextAvailSeqNo = 0;
		int counter = 0;
		for (ArTransactionType type : transactionTypes) {
			int typeId = type.getId();
			// get next available sequence number of the transaction type.
			nextAvailSeqNo = arTransactionDao.genSequenceNo(null, type.getId(), null);
			ArTransaction transaction = arTransactionDao.getTransactionBySeqNoAndType(nextAvailSeqNo - 1, typeId);
			List<ArTransaction> transactionsByType = arTransactionDao.getTransactions(transaction.getId(), typeId);
			logger.info("Retrieved " + transactionsByType.size() + " AR Transactions to be updated for "
					+ "transaction type: " + type.getName());
			for (ArTransaction art : transactionsByType) {
				logger.debug("Updating sequence number for ar transaction: " + art.getId() + " sequence number: "
						+ nextAvailSeqNo);
				art.setSequenceNumber(nextAvailSeqNo);
				arTransactionDao.update(art);

				// increment counters
				nextAvailSeqNo++;
				counter++;
			}
		}

		String message = "Successfully updated the sequence numbers of ar transaction. " + counter
				+ " AR Transactions updated.";
		logger.info(message);
		return message;
	}

	/**
	 * Get the reference no of the Sales Return.
	 * 
	 * @param acctSaleItems The list of account sales items.
	 * @return The form name and the sequence no of the reference (Ex. AS No. 1)
	 */
	public String getReferenceNo(List<AccountSaleItem> acctSaleItems) {
		for (AccountSaleItem asi : acctSaleItems) {
			// Get the reference of the sale return to get the id of the Account Sales
			AccountSaleItem refAsi = accountSaleItemDao.get(asi.getRefAccountSaleItemId());
			ArTransaction refAs = getARTransaction(refAsi.getArTransactionId());
			return getTransactionPrefix(refAs.getTransactionTypeId()) + " " + refAs.getSequenceNumber();
		}
		return "";
	}

	/**
	 * Converts the amount to words.
	 * 
	 * @param amount The amount to be converted.
	 * @return example: "Total : Fifteen Thousand Pesos only."
	 */
	public String convertAmtToWords(Double amount) {
		String amountStr = amount.toString();
		String decStr = "";
		if (!amountStr.contains(".0")) {
			decStr = " and " + amountStr.substring((amountStr.length()) - 2, (amountStr.length())) + "/100";
		}
		return "Total: " + NumberFormatUtil.numbersToWords(amount) + decStr + " Only.";
	}

	/**
	 * Get the account combination using the customer account.
	 * 
	 * @param customerAccountId The id of the customer account.
	 * @return The account combination.
	 */
	public AccountCombination getAccountCombinationId(int customerAccountId) {
		return combinationDao.getAcctCombiByCustomerAcctId(customerAccountId);
	}

	/**
	 * Get the AR Transaction.
	 * 
	 * @param transactionId the unique id of transaction.
	 */
	public ArTransaction getARTransaction(int transactionId) {
		ArTransaction arTransaction = arTransactionDao.get(transactionId);
		// Checks if the transaction type is account sale.
		if (arTransaction != null) {
			AccountSales accountSales = accountSalePoService.getASPOByASOEBObject(arTransaction.getEbObjectId());
			if(accountSales != null) {
				arTransaction.setReferenceObjectId(accountSales.getEbObjectId());
				arTransaction.setReferenceNo(accountSales.getPoNumber().toString());
			}
			ArCustomer arCustomer = arCustomerDao.get(arTransaction.getCustomerId());
			if (arCustomer != null) {
				if (arCustomer.getMaxAmount() != null && arCustomer.getMaxAmount() != 0) {
					Integer arCustomerId = arCustomer.getId();
					arTransaction.setAvailableBalance(computeAvailableBalance(arCustomerId, 0));
				} else
					arTransaction.setAvailableBalance(0.0);
			}
			arTransaction.setWtAmount(CurrencyUtil.convertMonetaryValues(arTransaction.getWtAmount(), arTransaction.getCurrencyRateValue()));
			arTransaction.setWtVatAmount(CurrencyUtil.convertMonetaryValues(arTransaction.getWtVatAmount(), arTransaction.getCurrencyRateValue()));
			arTransaction.setAmount(CurrencyUtil.convertMonetaryValues(arTransaction.getAmount(), arTransaction.getCurrencyRateValue()));
		}
		return arTransaction;
	}

	/**
	 * Get the list of AR Line/s of the AR Transaction.
	 * 
	 * @param arTransaction The AR Transaction/s.
	 * @return The List of AR Line object.
	 */
	public List<ArLine> getARLine(ArTransaction arTransaction) {
		List<ArLine> arLines = arLineDao.getArLines(arTransaction.getId());
		double rate = arTransaction.getCurrencyRateValue();
		if (arLines != null && !arLines.isEmpty()) {
			for (ArLine arLine : arLines) {
				ServiceSetting service = serviceSettingService.getServiceSetting(arLine.getArLineSetup().getId());
				arLine.setArLineSetupName(service.getName());
				if(arLine.getUnitOfMeasurementId() != null) {
					arLine.setUnitMeasurementName(unitMeasurementService.getUnitMeasurement(arLine.getUnitOfMeasurementId()).getName());
				}
				arLine.setUpAmount(CurrencyUtil.convertMonetaryValues(arLine.getUpAmount(), rate));
				arLine.setVatAmount(CurrencyUtil.convertMonetaryValues(arLine.getVatAmount(), rate));
				arLine.setAmount(CurrencyUtil.convertMonetaryValues(arLine.getAmount(), rate));
				service = null;
			}
		}
		return arLines;
	}

	/**
	 * Get the list of AR Line/s of the AR Transaction.
	 * 
	 * @param arTransaction The AR Transaction/s.
	 * @return The List of AR Line object.
	 */
	public List<ArServiceLine> getArServiceLine(ArTransaction arTransaction) {
		List<ArServiceLine> arServiceLines = arServiceLineDao.getArServiceLines(arTransaction.getId());
		double rate = arTransaction.getCurrencyRateValue();
		if (arServiceLines != null && !arServiceLines.isEmpty()) {
			for (ArServiceLine arServiceLine : arServiceLines) {
				ServiceSetting service = serviceSettingService.getServiceSetting(arServiceLine.getServiceSettingId());
				arServiceLine.setServiceSetting(service);
				arServiceLine.setServiceSettingName(service.getName());
				if(arServiceLine.getUnitOfMeasurementId() != null) {
					arServiceLine.setUnitMeasurementName(unitMeasurementService.getUnitMeasurement(arServiceLine.getUnitOfMeasurementId()).getName());
				}
				arServiceLine.setUpAmount(CurrencyUtil.convertMonetaryValues(arServiceLine.getUpAmount(), rate));
				arServiceLine.setVatAmount(CurrencyUtil.convertMonetaryValues(arServiceLine.getVatAmount(), rate));
				arServiceLine.setAmount(CurrencyUtil.convertMonetaryValues(arServiceLine.getAmount(), rate));
				service = null;
			}
		}
		return arServiceLines;
	}

	/**
	 * Get total amount
	 * @param arTransaction The ar transaction
	 * @return The total amount
	 */
	public Double getTotalAmount(ArTransaction arTransaction) {
		List<ArServiceLine> arServiceLines = arServiceLineDao.getArServiceLines(arTransaction.getId());
		double grandTotal = 0;
		if (arServiceLines != null && !arServiceLines.isEmpty()) {
			for (ArServiceLine arServiceLine : arServiceLines) {
				grandTotal += arServiceLine.getAmount();
			}
		}
		return grandTotal;
	}

	/**
	 * Get the list of account sale items.
	 * 
	 * @param arTransactionId The unique id of the AR Transaction.
	 * @return The list of account sale items.
	 */
	public List<AccountSaleItem> getAcctSaleItems(Integer arTransactionId) {
		return accountSaleItemDao.getAccountSaleItems(arTransactionId, null, null);
	}

	/**
	 * Get the list of AR Transactions.
	 * 
	 * @param serviceLeaseKeyId   The service lease key id of the logged user.
	 * @param arCustomerAccountId The unique id of the AR customer account.
	 * @return The list of AR Transactions.
	 */
	public List<ArTransaction> getARTransactions(int serviceLeaseKeyId, Integer arCustomerAccountId) {
		return arTransactionDao.getARTransactions(serviceLeaseKeyId, arCustomerAccountId);
	}

	/**
	 * Generate the sequence number of the AR Transaction.
	 * 
	 * @param companyId         The id of the company.
	 * @param transactionTypeId The id of the AR Transaction Type.
	 * @return The generated sequence number.
	 */
	public Integer generateSeqNo(Integer companyId, Integer transactionTypeId) {
		return arTransactionDao.genSequenceNo(companyId, transactionTypeId, null);
	}

	/**
	 * Generate the sequence number of the AR Transaction.
	 * 
	 * @param companyId         The id of the company.
	 * @param transactionTypeId The id of the AR Transaction Type.
	 * @return The generated sequence number.
	 */
	public Integer generateSeqNo(Integer companyId, Integer transactionTypeId, Integer divisionId) {
		return arTransactionDao.genSequenceNo(companyId, transactionTypeId, divisionId);
	}

	/**
	 * Save AR Transaction Object to database .
	 * 
	 * @param user          The logged user.
	 * @param arTransaction The AR Transaction object to be saved.
	 * @throws CloneNotSupportedException
	 */
	public ArTransaction saveArTransaction(User user, ArTransaction arTransaction) throws CloneNotSupportedException {
		boolean isNew = arTransaction.getId() == 0;
		Date currentDate = new Date();
		AuditUtil.addAudit(arTransaction, new Audit(user.getId(), isNew, currentDate));
		if (!isNew) {
			ArTransaction savedArTransaction = getARTransaction(arTransaction.getId());
			DateUtil.setCreatedDate(arTransaction, savedArTransaction.getCreatedDate());
		}
		arTransaction.setServiceLeaseKeyId(user.getServiceLeaseKeyId());
		String trNumber = arTransaction.getTransactionNumber();
		if (trNumber != null && !trNumber.trim().isEmpty()) {
			arTransaction.setTransactionNumber(StringFormatUtil.removeExtraWhiteSpaces(trNumber));
		}
		String desc = arTransaction.getDescription();
		if (desc != null && !desc.trim().isEmpty()) {
			arTransaction.setDescription(StringFormatUtil.removeExtraWhiteSpaces(desc));
		}

		// Save AR Transaction
		boolean isValidRate = arTransaction.getCurrencyRateValue() != null && arTransaction.getCurrencyRateValue() != 0;
		double currencyRate = isValidRate ? arTransaction.getCurrencyRateValue() : 1.0;
		arTransaction.setCurrencyRateValue(currencyRate);
		arTransaction.setWtAmount(CurrencyUtil.convertAmountToPhpRate(arTransaction.getWtAmount(), currencyRate, true));
		arTransaction.setWtVatAmount(CurrencyUtil.convertAmountToPhpRate(arTransaction.getWtVatAmount(), currencyRate, true));
		arTransactionDao.saveOrUpdate(arTransaction);
		processArServiceLines(isNew, arTransaction.getId(), arTransaction.getArServiceLines(), currencyRate);
		recomputeAmount(arTransaction);
		logger.info("Successfully saved the AR Transaction with transaction number: "+arTransaction.getTransactionNumber());
		return arTransaction;
	}

	private void recomputeAmount(ArTransaction arTransaction) {
		double totalAmount = 0;
		List<ArServiceLine> arLines = arTransaction.getArServiceLines();
		for (ArServiceLine arl : arLines) {
			totalAmount += arl.getAmount();
			totalAmount += arl.getVatAmount() != null ? arl.getVatAmount() : 0;
		}
		arTransaction.setAmount(NumberFormatUtil.roundOffTo2DecPlaces(totalAmount - arTransaction.getWtAmount() - arTransaction.getWtVatAmount()));
		arTransactionDao.update(arTransaction);
	}

	/**
	 * Set transaction type by division id
	 * @param divisionId
	 * @return
	 */
	public Integer setTransactionType(Integer divisionId) {
		switch (divisionId) {
		case 1:
			return ArTransactionType.TYPE_AR_TRANSACTION_CENTRAL;
		case 2:
			return ArTransactionType.TYPE_AR_TRANSACTION_NSB3;
		case 3:
			return ArTransactionType.TYPE_AR_TRANSACTION_NSB4;
		case 4:
			return ArTransactionType.TYPE_AR_TRANSACTION_NSB5;
		case 5:
			return ArTransactionType.TYPE_AR_TRANSACTION_NSB8;
		case 6:
			return ArTransactionType.TYPE_AR_TRANSACTION_NSB8A;
		default:
			return 0;
		}
	}

	@Override
	public void preFormSaving(BaseFormWorkflow form, String workflowName, User user) {
		ArTransaction arTransaction = (ArTransaction) form;
		boolean isNew = arTransaction.getId() == 0;
		if (isNew) {
			arTransaction.setSequenceNumber(generateSeqNo(arTransaction.getCompanyId(),
					arTransaction.getTransactionTypeId(), arTransaction.getDivisionId()));
		}
	}

	@Override
	public void saveForm(BaseFormWorkflow form, String workflowName, User user) {
		ArTransaction arTransaction = (ArTransaction) form;
		boolean isNew = arTransaction.getId() == 0;
		Integer parentObjectId = arTransaction.getEbObjectId();
		try {
			saveArTransaction(user, arTransaction);
			refDocumentService.saveReferenceDocuments(user, isNew, parentObjectId,
					arTransaction.getReferenceDocuments(), true);
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	protected double getTotalCharges(List<ArLine> otherCharges) {
		double totalCharges = 0;
		for (ArLine oc : otherCharges) {
			totalCharges += oc.getAmount();
		}
		return totalCharges;
	}

	private void processArServiceLines(boolean isNew, Integer arTransactionId, List<ArServiceLine> arServiceLines, double currencyRateValue) {
		logger.debug("Processing AR Lines.");
		List<Integer> toBeDeleted = new ArrayList<Integer>();
		if (!isNew) {
			ArTransaction oldART = arTransactionDao.get(arTransactionId);
			List<ArServiceLine> oldArServiceLines = oldART.getArServiceLines();
			for (ArServiceLine arServiceLine : oldArServiceLines)
				toBeDeleted.add(arServiceLine.getId());
			arServiceLineDao.delete(toBeDeleted);
			toBeDeleted = null;
		}
		if (arServiceLines != null && !arServiceLines.isEmpty()) {
			CurrencyUtil.convertServiceLineCostsToPhp(arServiceLines, currencyRateValue);
			for (ArServiceLine arServiceLine : arServiceLines) {
				arServiceLine.setId(0);
				arServiceLine.setaRTransactionId(arTransactionId);
				arServiceLineDao.saveOrUpdate(arServiceLine);
			}
		}
		logger.info("Successfully processed and saved ar lines.");
	}

	@Override
	public FormWorkflow getFormWorkflow(int id) {
		return arTransactionDao.get(id).getFormWorkflow();
	}

	/**
	 * Checks if The AR Transaction amount is equal to the total AR Line amount.
	 * 
	 * @param arTransaction The AR Transaction object.
	 * @return True if AR Transaction amount is equal to the total AR Line amount,
	 *         otherwise false.
	 */
	public boolean isARTAmtEqTotalARLineAmt(ArTransaction arTransaction) {
		List<ArLine> arLines = arTransaction.getArLines();
		if (arTransaction.getAmount() != null && arLines != null) {
			double totalArLineAmt = 0;
			for (ArLine arLine : arLines) {
				if (arLine.getAmount() != null)
					totalArLineAmt += arLine.getAmount();
			}
			if (NumberFormatUtil.roundOffTo2DecPlaces(arTransaction.getAmount()) != NumberFormatUtil
					.roundOffTo2DecPlaces(totalArLineAmt))
				return false;
		}
		return true;
	}

	/**
	 * Checks if the transaction number is unique.
	 * 
	 * @param arTransaction The AR Transaction object.
	 * @return True the transaction number is unique, otherwise false.
	 */
	public boolean isUniqueTransactionNo(ArTransaction arTransaction) {
		if (arTransaction.getCustomerAcctId() == null)
			return true;
		else {
			Integer companyId = arCustomerAcctDao.get(arTransaction.getCustomerAcctId()).getCompanyId();
			return arTransactionDao.isUniqueTransactionNo(arTransaction, companyId);
		}
	}

	/**
	 * Get the list of AR Transactions based on the transaction number and logged
	 * user.
	 * 
	 * @param criteria     The transaction number.
	 * @param arCustAcctId The unique id of Ar Customer Account.
	 * @param id           The unique id of Ar Transaction.
	 * @param noLimit      True if display all ar transactions.
	 * @param isExact      True if name should be exact.
	 * @param user         The logged user.
	 * @return The list of AR Transactions.
	 */
	public List<ArTransaction> getArTransactions(String criteria, Integer arCustAcctId, Integer id, String tNumbers,
			Boolean noLimit, Boolean isReference, Boolean isExact, User user) {
		List<String> transactionNums = new ArrayList<String>();
		if (tNumbers != null && !tNumbers.isEmpty()) {
			String tmpNumbers[] = tNumbers.split(";");
			for (String tmpNum : tmpNumbers)
				transactionNums.add(tmpNum);
		}
		List<ArTransaction> arTransactions = arTransactionDao.getARTransactions(criteria, arCustAcctId, transactionNums,
				id, noLimit, isExact, user.getServiceLeaseKeyId());
		List<ArTransaction> newArTransactions = new ArrayList<ArTransaction>();

		if (arTransactions == null || arTransactions.isEmpty())
			return Collections.emptyList();
		else if (isReference == null) {
			for (ArTransaction arTransaction : arTransactions) {
				arTransaction.setAmount(NumberFormatUtil.roundOffTo2DecPlaces(arTransaction.getAmount()));
				List<ArReceiptTransaction> arRTs = arReceiptTransactionDao.getArReceiptTransactions(null,
						arTransaction.getId());
				if (arRTs != null && !arRTs.isEmpty()) {
					Double totalAmount = getTransactionAmount(arTransaction.getId());
					if (Math.abs(totalAmount) < Math.abs(arTransaction.getAmount())) {
						// Partially paid
						double amount = arTransaction.getAmount() - totalAmount;
						arTransaction.setAmount(NumberFormatUtil.roundOffTo2DecPlaces(amount));
						if (arTransaction.getAmount() != 0.0) {
							newArTransactions.add(arTransaction);
						}
					}
				} else {
					// Unpaid
					if (arTransaction.getAmount() != 0.0) {
						newArTransactions.add(arTransaction);
					}
				}
			}
		} else
			return arTransactions;
		return newArTransactions;
	}

	@Override
	public void doBeforeSaving(FormWorkflowLog currentWorkflowLog, BindingResult bindingResult) {
		ArTransaction arTransaction = arTransactionDao.getArTransactionByWorkflow(currentWorkflowLog.getFormWorkflowId());
		if (arTransaction != null) {
			// Checks if GL date is open time period.
			Date glDate = arTransaction.getGlDate();
			if (glDate != null) {
				if (!timePeriodService.isOpenDate(glDate)) {
					String message = "GL date should be in an open time period.";
					setWorkflowMsg(bindingResult, currentWorkflowLog, message);
				}
			}
			if (currentWorkflowLog.getFormStatusId() == FormStatus.CANCELLED_ID) {
				int trId = arTransaction.getId();
				int trTypeId = arTransaction.getTransactionTypeId();
				StringBuffer errorMessage = null;
				boolean isTransaction = arTransaction.getTransactionTypeId() < (ArTransactionType.TYPE_ACCOUNT_SALE);
				String prefix = isTransaction ? "Transaction " : arTransaction.getArTransactionType().getName();
				logger.debug("Checking " + prefix + "if it is in use in Account Collection form.");
				List<ArReceipt> transactionPayments = arReceiptService.getArReceiptsByTrId(trId);
				if (!transactionPayments.isEmpty()) {
					errorMessage = new StringBuffer(prefix + " cannot be cancelled because it has associated payment/s: ");
					for (ArReceipt arr : transactionPayments) {
						errorMessage.append("<br> AC No. : " + arr.getSequenceNo() + ", " + "Receipt No. : "
								+ arr.getReceiptNumber());
					}
					setWorkflowMsg(bindingResult, currentWorkflowLog, errorMessage.toString());
					logger.warn("Cannot cancel AR Transaction because it is "
							+ "already paid in Account Collection form/s.");
				}

				if (trTypeId == ArTransactionType.TYPE_ACCOUNT_SALE
						|| trTypeId == ArTransactionType.TYPE_SALE_RETURN
						|| trTypeId == ArTransactionType.TYPE_ACCOUNT_SALES_IS) {
					if (arTransactionDao.isExistingInAccountSaleReturn(trId)) {
						List<ArTransaction> arTransactions = getTransactionUsedInReturns(trId);
						errorMessage = new StringBuffer(prefix + " was used as reference in: ");
						for (ArTransaction arT : arTransactions) {
							errorMessage.append("<br> " + getTransactionPrefix(arT.getTransactionTypeId())
									+ arT.getSequenceNumber());
						}
						setWorkflowMsg(bindingResult, currentWorkflowLog, errorMessage.toString());
					}
				} else if ((trTypeId == ArTransactionType.TYPE_SALE_RETURN)
						&& arTransaction.getFormWorkflow().isComplete()) {
					List<AccountSaleItem> saleReturnItems = accountSaleItemDao.getAllByRefId("arTransactionId", trId);
					List<AccountSaleItem> returnedItems = SaleItemUtil.filterSaleReturnItems(saleReturnItems, true);
					for (AccountSaleItem asi : returnedItems) {
						double totalQty = SaleItemUtil.getTotalQtyByItemAndWH(asi.getItemId(), asi.getWarehouseId(),
								returnedItems);
						// Check existing stocks of the item/s if form will be cancelled.
						String errorMsg = ValidationUtil.validateToBeCancelledItem(itemService, asi.getItemId(),
								asi.getWarehouseId(), arTransaction.getTransactionDate(), Math.abs(totalQty));
						setWorkflowMsg(bindingResult, currentWorkflowLog, errorMsg);
						break;
					}
				} else if (trTypeId == ArTransactionType.TYPE_ACCOUNT_SALES_RETURN_IS) {
					List<AccountSaleItem> saleReturnItems = accountSaleItemDao.getAllByRefId("arTransactionId", trId);
					List<AccountSaleItem> returnedItems = SaleItemUtil.filterSaleReturnItems(saleReturnItems, true);
					// Check returned items if used as reference
					String errorMsg = ValidationUtil.validateToBeCancelledRefForm(itemService, returnedItems);
					if (errorMsg != null) {
						bindingResult.reject("workflowMessage", errorMsg);
						currentWorkflowLog.setWorkflowMessage(errorMsg);
					}
				}
			}
		}
	}

	@Override
	public void doAfterSaving(FormWorkflowLog currentWorkflowLog) {
		// Do nothing
	}

	private List<ArTransaction> getTransactionUsedInReturns(Integer arTransactionId) {
		return arTransactionDao.getTransactionUsedInReturns(arTransactionId);
	}

	private void setWorkflowMsg(BindingResult bindingResult, FormWorkflowLog currentWorkflowLog, String errorMessage) {
		if (errorMessage != null) {
			bindingResult.reject("workflowMessage", errorMessage);
			currentWorkflowLog.setWorkflowMessage(errorMessage);
		}
	}

	/**
	 * Get the total transaction amount per customer account.
	 * 
	 * @param arCustomerAcctId The AR customer account id.
	 * @return The total transaction amount.
	 */
	public Double getTotalTransactionAmount(Integer arCustomerAcctId) {
		Double totalTransactionAmount = 0.0;
		List<ArTransaction> arTransactions = arTransactionDao.getTransactionsByCustAcct(arCustomerAcctId);
		if (arTransactions != null && !arTransactions.isEmpty()) {
			for (ArTransaction transaction : arTransactions)
				totalTransactionAmount += transaction.getAmount();
		}
		return NumberFormatUtil.roundOffTo2DecPlaces(totalTransactionAmount);
	}

	/**
	 * Get Ar Transaction by number.
	 * 
	 * @param criteria The criteria either sequence number or transaction number.
	 * @return The Ar Transaction object.
	 */
	public ArTransaction getArTransactionByNumber(String criteria, Integer arCustomerAcctId) {
		ArTransaction arTransaction = getTransaction(arCustomerAcctId, criteria);
		if (arTransaction != null) {
			arTransaction.setAmount(arTransaction.getAmount() - getTransactionAmount(arTransaction.getId()));
		}
		return arTransaction;
	}

	/**
	 * Get the AR Transaction using customer account and number.
	 * 
	 * @param arCustomerAcctId The unique id of the customer account.
	 * @param criteria         The criteria either sequence number or transaction
	 *                         number.
	 * @return The AR Transaction.
	 */
	public ArTransaction getTransaction(Integer arCustomerAcctId, String criteria) {
		ArTransaction arTransaction = arTransactionDao.getCompletedTransaction(arCustomerAcctId, criteria);
		if (arTransaction != null) {
			return arTransaction;
		}
		return null;
	}

	private Double getTransactionAmount(Integer arTransactionId) {
		Double transactionAmount = 0.0;
		ArTransaction arTransaction = arTransactionDao.get(arTransactionId);
		if (arTransaction != null) {
			List<ArReceiptTransaction> arRTs = arReceiptTransactionDao.getArReceiptTransactions(null, arTransactionId);
			if (arRTs != null && !arRTs.isEmpty()) {
				for (ArReceiptTransaction art : arRTs)
					transactionAmount += art.getAmount();
			}
		}
		return NumberFormatUtil.roundOffTo2DecPlaces(transactionAmount);
	}

	/**
	 * Computes the available balance of the customer.
	 * 
	 * @param arCustomerId    The id of the customer.
	 * @param arTransactionId The id of the transaction.
	 * @return The available balance of the customer. TODO : Remove this function
	 *         later because the arTransactionId parameter is not needed. The reason
	 *         its still here is for old projects that are still using this
	 *         function.
	 */
	public double computeAvailableBalance(Integer arCustomerId, int arTransactionId) {
		logger.info("Computing the available balance of the customer id: " + arCustomerId);
		ArCustomer arCustomer = arCustomerDao.get(arCustomerId);
		double creditLimit = arCustomer.getMaxAmount() != null ? arCustomer.getMaxAmount() : 0;
		if (creditLimit == 0) {
			return 0;
		}
		double totalTransaction = arTransactionDao.getTotalTransactionAmtOfCustomer(arCustomerId, arTransactionId);
		double totalPaid = arReceiptService.getCustomerTotalReceipt(arCustomerId);
		double availableBalance = NumberFormatUtil.roundOffTo2DecPlaces((creditLimit - totalTransaction) + totalPaid);
		if (creditLimit < availableBalance) {
			// Available balance should not be greater than the credit limit.
			return NumberFormatUtil.roundOffTo2DecPlaces(creditLimit);
		} else if (availableBalance < 0) {
			return 0;
		}
		logger.info("The availale balance of the customer id: " + arCustomerId + " is "
				+ NumberFormatUtil.format(availableBalance));
		return NumberFormatUtil.roundOffTo2DecPlaces(availableBalance);
	}

	/**
	 * Computes the available balance of the customer.
	 * 
	 * @param arCustomerId The id of the customer.
	 * @return The available balance of the customer.
	 */
	public double computeAvailableBalance(Integer arCustomerId) {
		double availableBalance = arTransactionDao.computeAvailableBalance(arCustomerId);
		return NumberFormatUtil.roundOffTo2DecPlaces(availableBalance < 0 ? 0 : availableBalance);
	}

	/**
	 * Get the available balance by customer and date.
	 * 
	 * @param arCustomerId The customer id.
	 * @return The available balance.
	 */
	public double getTotalTransactionAmt(Integer arCustomerId) {
		return NumberFormatUtil
				.roundOffTo2DecPlaces(arTransactionDao.getTotalTransactionAmtOfCustomer(arCustomerId, 0));
	}

	/**
	 * Checks if the available balance of the user is less than the total amount.
	 * 
	 * @param totalAmount      The total amount.
	 * @param creditLimit      The max amount allowed for the customer.
	 * @param availableBalance The available balance of the customer.
	 * @return True if available balance is less total amount, otherwise false.
	 */
	public boolean isOverAvailableBalance(double totalAmount, double availableBalance) {
		return availableBalance < totalAmount;
	}

	/**
	 * Get the paged list of account sales for CS Reference.
	 * 
	 * @param companyId           The company id.
	 * @param arCustomerId        The customer id.
	 * @param arCustomerAccountId The customer account id.
	 * @param sequenceNo          The sequence number.
	 * @param dateFrom            The start of date range.
	 * @param dateTo              The end of date range.
	 * @param status              The status of the cash sale.
	 * @param transactionTypeId   The transaction type id.
	 * @param pageNumber          The page number.
	 * @param user                The current user.
	 * @return The paged list of account sales.
	 */
	public Page<ArTransaction> getAccountSales(Integer companyId, Integer arCustomerId, Integer arCustomerAccountId,
			Integer sequenceNo, Date dateFrom, Date dateTo, Integer status, Integer transactionTypeId, int pageNumber,
			User user) {
		return arTransactionDao.getAccountSales(companyId, arCustomerId, arCustomerAccountId, sequenceNo, dateFrom,
				dateTo, status, transactionTypeId, new PageSetting(pageNumber), user);
	}

	/**
	 * Get the transaction based on the selected date.
	 * 
	 * @param itemId      The item id.
	 * @param warehouseId the warehouse id.
	 * @param date        the transaction date.
	 * @return the list of all transactions.
	 */
	public Page<ArTransaction> getTransactionsByDate(int itemId, int warehouseId, Date date) {
		PageSetting ps = new PageSetting(1, PageSetting.NO_PAGE_CONSTRAINT);
		return arTransactionDao.getTransactions(itemId, warehouseId, date, ps);
	}

	/**
	 * Fix the computation of discount and amount of account sale items.
	 */
	public void fixDiscAndAmount() {
		int maxPerPage = 100;
		int asSize = (arTransactionDao.getASSize() / maxPerPage) + 1;
		SaleItemUtil<AccountSaleItem> saleItemUtil = new SaleItemUtil<AccountSaleItem>();
		for (int i = 1; i <= asSize; i++) {
			Page<ArTransaction> accountSales = arTransactionDao.getAccountSales(new PageSetting(i, maxPerPage));
			if (accountSales != null && !accountSales.getData().isEmpty()) {
				for (ArTransaction as : accountSales.getData()) {
					logger.info("Processing Account Sale : " + as.getFormattedASNumber());
					logger.debug("Getting the cash sale items.");
					List<AccountSaleItem> asItems = getAcctSaleItems(as.getId());
					logger.debug("Processing the discount and amount of account sale items.");
					asItems = saleItemUtil.processDiscountAndAmount(asItems, itemDiscountService);
					if (asItems != null && !asItems.isEmpty()) {
						for (AccountSaleItem asItem : asItems) {
							logger.debug("Saving the account sale item : " + asItem.getId());
							accountSaleItemDao.saveOrUpdate(asItem);
						}
					}
					logger.debug("Freeing the account sale items.");
					asItems = null;
				}
			}
			logger.debug("Freeing the account sales.");
			accountSales = null;
		}
	}

	/**
	 * Get the Transaction Type.
	 * 
	 * @param user The user log.
	 * @return The user transaction type.
	 */
	public List<ArTransactionType> getAllTransactionTypes(User user) {
		return transactionTypeDao.getAllTransactionTypes(user);
	}

	/**
	 * Get the AR transaction balances.
	 * 
	 * @param arCustAcctId        The customer account ID.
	 * @param accountCollectionId Exclude the ar transactions paid in the account
	 *                            collection.
	 * @param tNumbers            The transaction number.
	 * @param criteria            The transaction number.
	 * @param isShow              Check if it is for showing.
	 * @return The transactions customer balance
	 */
	public Page<ArTransactionRegisterDto> getArTransactions(Integer arCustAcctId, Integer accountCollectionId,
			String tNumbers, String criteria, Boolean isShow, Boolean isExact) {
		return arTransactionDao.getArTransactions(arCustAcctId, accountCollectionId, tNumbers, criteria, isShow,
				isExact, (new PageSetting(1, 10)));
	}

	/**
	 * Get the prefix based from the AR Transaction type.
	 */
	public String getTransactionPrefix(int typeId) {
		return getTransactionPrefix(typeId, null);
	}

	/**
	 * Get the prefix based from the AR Transaction type.
	 */
	public String getTransactionPrefix(int typeId, ArTransaction transaction) {
		String prefix = "";
		switch (typeId) {
		case ArTransactionType.TYPE_ACCOUNT_SALE:
			prefix += "AS ";
			break;
		case ArTransactionType.TYPE_SALE_RETURN:
			prefix += "ASR ";
			break;
		case ArTransactionType.TYPE_ACCOUNT_SALES_IS:
			prefix += "AS - IS ";
			break;
		case ArTransactionType.TYPE_ACCOUNT_SALES_RETURN_IS:
			prefix += "ASR - IS ";
			break;
		default:
			prefix += "Transaction ";
			break;
		}

		prefix += "No. ";
		if (transaction == null) {
			return prefix;
		}
		return prefix + " : " + transaction.getSequenceNumber();
	}

	@Override
	public BaseFormWorkflow getFormByWorkflow(Integer workflowId) {
		return arTransactionDao.getByWorkflowId(workflowId);
	}

	@Override
	public BaseFormWorkflow getForm(Integer ebObjectId) {
		return arTransactionDao.getByEbObjectId(ebObjectId);
	}

	@Override
	public ObjectInfo getObjectInfo(int ebObjectId, User user) {
		StringBuffer shortDescription = null;
		String fullDescription = "";

		ArTransaction arTransaction = arTransactionDao.getByEbObjectId(ebObjectId);
		Integer pId = arTransaction.getId();

		FormProperty property = workflowHandler.getProperty(arTransaction.getWorkflowName(), user);
		String popupLink = "/" + property.getEdit() + "?pId=" + pId;
		String printOutLink = "/" + property.getPrint() + "?pId=" + pId;

		String latestStatus = arTransaction.getFormWorkflow().getCurrentFormStatus().getDescription();
		String title = "AR Transaction - " + arTransaction.getDivision().getName() +" - "+ arTransaction.getSequenceNumber();
		shortDescription = new StringBuffer(title).append(" " + arTransaction.getArCustomer().getName())
				.append(" " + arTransaction.getArCustomerAccount().getName())
				.append(" " + DateUtil.formatDate(arTransaction.getTransactionDate()));
		shortDescription.append(" " + arTransaction.getAmount());

		return ObjectInfo.getInstance(ebObjectId, title, latestStatus, shortDescription.toString(), fullDescription,
				popupLink, printOutLink);
	}

	@Override
	public Domain getDomain(EBObject ebObject) {
		Integer ebObjectId = ebObject.getId();
		switch (ebObject.getObjectTypeId()) {
		case ArTransaction.AR_TRANSACTION_OBJECT_TYPE_ID:
			return arTransactionDao.getByEbObjectId(ebObjectId);
		case ArLine.OBJECT_TYPE_ID:
			return arLineDao.getByEbObjectId(ebObjectId);
		}
		return null;
	}

	/*
	 * Disable the computation of weighted average
	 * 
	 * @Override public List<? extends Domain> getItems(BaseFormWorkflow form) {
	 * Integer formId = form.getId(); List<? extends Domain> ret =
	 * getAcctSaleItems(formId); return ret; }
	 * 
	 * @Override public InventoryItem getItemTransaction(Integer objectId) { return
	 * accountSaleItemDao.getByEbObjectId(objectId); }
	 * 
	 * @Override public List<Integer> getWarehouses(BaseFormWorkflow form, Domain
	 * itemLine) { ArTransaction transaction = (ArTransaction) form; for
	 * (AccountSaleItem asi : getAcctSaleItems(transaction.getId())) { if
	 * (asi.getId() == itemLine.getId()) { List<Integer> ret = new ArrayList<>();
	 * ret.add(asi.getWarehouseId()); return ret; } } throw new
	 * RuntimeException("Unable to find warehouse."); }
	 * 
	 * @Override public List<Integer> getItems(BaseFormWorkflow form, Domain
	 * itemLine) { ArTransaction transaction = (ArTransaction) form; for
	 * (AccountSaleItem asi : getAcctSaleItems(transaction.getId())) { if
	 * (asi.getId() == itemLine.getId()) { List<Integer> ret = new ArrayList<>();
	 * ret.add(asi.getItemId()); return ret; } } throw new
	 * RuntimeException("Unable to find item."); }
	 */

	/**
	 * Get the total VAT amount
	 * 
	 * @param arServiceLines The total AR lines
	 * @return The total VAT amount
	 */
	public double getTotalVatAmount(List<ArServiceLine> arServiceLines) {
		double totalVat = 0;
		for (ArServiceLine arServiceLine : arServiceLines) {
			totalVat += arServiceLine.getVatAmount() != null ? arServiceLine.getVatAmount() : 0.0;
		}
		return NumberFormatUtil.roundOffTo2DecPlaces(totalVat);
	}

	/**
	 * Get the total net amount
	 * 
	 * @param arServiceLines The total AR lines
	 * @return The total net amount
	 */
	public double getTotalNetAmount(List<ArServiceLine> arServiceLines) {
		double totalAmount = 0;
		for (ArServiceLine arServiceLine : arServiceLines) {
			totalAmount += arServiceLine.getAmount() != null ? arServiceLine.getAmount() : 0.0;
		}
		return NumberFormatUtil.roundOffTo2DecPlaces(totalAmount);
	}

	public List<FormSearchResult> search(User user, String searchCriteria, Integer divisionId) {
		// Replace comma in searchCriteria with ""
		String searchCriteriaFinal = searchCriteria.replace(",", "");

		Page<ArTransaction>   arTransactions =
				arTransactionDao.searchARTransactions(null, divisionId, searchCriteriaFinal, new PageSetting(1));
		List<FormSearchResult> result = new ArrayList<FormSearchResult>();

		for (ArTransaction transaction : arTransactions.getData()) {
			List<ResultProperty> properties = new ArrayList<ResultProperty>();
			String title = "Transaction#" + transaction.getTransactionNumber();
			properties.add(ResultProperty.getInstance("Sequence No", transaction.getSequenceNumber().toString()));
			properties.add(ResultProperty.getInstance("Type", transaction.getArTransactionType().getName()));
			properties.add(ResultProperty.getInstance("Customer", transaction.getArCustomer().getName()));
			properties.add(ResultProperty.getInstance("Customer Account",
					transaction.getArCustomerAccount().getName()));
			properties.add(ResultProperty.getInstance("Customer", transaction.getArCustomer().getName()));
			properties.add(ResultProperty.getInstance("Term", transaction.getTerm().getName()));
			properties.add(ResultProperty.getInstance("Date", DateUtil.formatDate(transaction.getTransactionDate())));
			if (transaction.getGlDate() != null) {
				properties.add(ResultProperty.getInstance("GL Date", DateUtil.formatDate(transaction.getGlDate())));
			}
			if (transaction.getDueDate() != null) {
				properties.add(ResultProperty.getInstance("Due Date",
						DateUtil.formatDate(transaction.getDueDate())));
			}
			properties.add(ResultProperty.getInstance("Amount",
					NumberFormatUtil.format(transaction.getAmount())));
			String status = formStatusDao.get(transaction.getFormWorkflow().getCurrentStatusId()).getDescription();
			properties.add(ResultProperty.getInstance("Status", status));
			result.add(FormSearchResult.getInstanceOf(transaction.getId(), title, properties));
		}
		return result;
	}
}
