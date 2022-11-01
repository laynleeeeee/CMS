package eulap.eb.service.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRDataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.NumberFormatUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.view.ARReceiptRegisterDao;
import eulap.eb.domain.hibernate.ArReceipt;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.view.hibernate.ARReceiptRegister;
import eulap.eb.service.CashSaleService;
import eulap.eb.service.fap.FormStatusService;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.service.workflow.FormProperty;
import eulap.eb.web.dto.AppliedStatus;

/**
 * Business logic for generating report for Receipt Register.


 */
@Service
public class ReceiptRegisterServiceImpl implements ReceiptRegisterService{
	private static Logger logger = Logger.getLogger(ReceiptRegisterServiceImpl.class);
	@Autowired
	private ARReceiptRegisterDao receiptRegisterDao;
	@Autowired
	private FormStatusService formStatusService;
	@Autowired
	private CashSaleService cashSaleService;

	@Override
	public Page<ARReceiptRegister> generateReport(User user, PageSetting pageSetting, ReceiptRegisterParam param) {
		logger.info("Processing the AR Receipt Register report.");
		Page<ARReceiptRegister> receiptRegisterData = receiptRegisterDao.getReceiptRegisterData(param, pageSetting);
		logger.info("Retrieved "+receiptRegisterData.getTotalRecords()+" data for the report.");
		if(param.getAppliedStatusId() == ARReceiptRegister.ALL_OPTION) {
			logger.debug("Applied status ALL. Return all.");
			return receiptRegisterData;
		}

		logger.info("Processing the data for applied status: "+param.getAppliedStatusId());
		List<ARReceiptRegister> processedData = new ArrayList<ARReceiptRegister>();
		for (ARReceiptRegister arr : receiptRegisterData.getData()) {
			if(arr.getSourceId() == ARReceiptRegister.ACCT_COLLECTION) {
				int appliedStatus = getAppliedStatus(arr.getAmount(), arr.getPaidAmount());
				logger.debug("Applied status of AC: "+appliedStatus+" "+arr.getSource()+" "+arr.getReceiptNumber());
				if(param.getAppliedStatusId() == appliedStatus) {
					//Add the Account Collection to the list.
					processedData.add(arr);
				}
			} else if(param.getAppliedStatusId() == AppliedStatus.FULLY_APPLIED) {
				logger.debug("Added: "+arr);
				processedData.add(arr);
			}
		}
		logger.debug("Processed "+processedData.size()+" for the report.");
		logger.info("Successfully generated the AR Receipt Register data.");
		return new Page<ARReceiptRegister>(pageSetting, processedData, receiptRegisterData.getTotalRecords());
	}

	/**
	 * Get the applied status of the Account Collection {@link ArReceipt}.
	 * @param amount The amount.
	 * @param paidAmount The total paid amount.
	 * @return The applied status:
	 * <br> 1 = Fully Applied (zero balance)
	 * <br> 2 = Partially Applied (amount is greater than the paid amount)
	 * <br> 3 = Unapplied (paid amount is zero)
	 */
	private int getAppliedStatus(Double amount, Double paidAmount) {
		amount = NumberFormatUtil.roundOffTo2DecPlaces(amount);
		logger.debug("Amount: "+amount);
		paidAmount = NumberFormatUtil.roundOffTo2DecPlaces(paidAmount);
		logger.debug("Paid amount: "+paidAmount);

		if(paidAmount.equals(amount)) {
			return AppliedStatus.FULLY_APPLIED;
		} else if (paidAmount < amount) {
			return AppliedStatus.PARTIALLY_APPLIED;
		}
		return AppliedStatus.UNAPPLIED;
	}

	/**
	 * Get the statuses of the forms.
	 * @param user The logged in user.
	 * @return The list of {@link FormStatus}.
	 */
	public List<FormStatus> getFormStatuses(User user) {
		return formStatusService.getFormStatuses(FormProperty.ACCT_RECEIPT, user, false, true);
	}

	/**
	 * Generate Data source of Receipt register. 
	 * @param param The Receipt register parameter.
	 * @return The data source of receipt register.
	 */
	public JRDataSource generateReceiptRegister(ReceiptRegisterParam param) {
		EBJRServiceHandler<ARReceiptRegister> handler = new JRReceiptRegisterHandler(param, this, cashSaleService);
		return new EBDataSource<ARReceiptRegister>(handler);
	}

	private static class JRReceiptRegisterHandler implements EBJRServiceHandler<ARReceiptRegister> {
		private ReceiptRegisterParam param;
		private ReceiptRegisterServiceImpl registerServiceImpl;

		private JRReceiptRegisterHandler (ReceiptRegisterParam param, ReceiptRegisterServiceImpl registerServiceImpl,
				CashSaleService cashSaleService) {
			this.param = param;
			this.registerServiceImpl = registerServiceImpl;
		}

		@Override
		public void close() throws IOException {
			registerServiceImpl = null;
		}

		@Override
		public Page<ARReceiptRegister> nextPage(PageSetting pageSetting) {
			logger.info("Processing the AR Receipt Register report.");
			Page<ARReceiptRegister> receiptRegisterData = registerServiceImpl.receiptRegisterDao.getReceiptRegisterData(param, pageSetting);
			logger.info("Retrieved "+receiptRegisterData.getTotalRecords()+" data for the report.");
			if (param.getAppliedStatusId() == ARReceiptRegister.ALL_OPTION) {
				logger.debug("Applied status ALL. Return all.");
				return receiptRegisterData;
			}
			logger.info("Processing the data for applied status: "+param.getAppliedStatusId());
			List<ARReceiptRegister> processedData = new ArrayList<ARReceiptRegister>();
			for (ARReceiptRegister arr : receiptRegisterData.getData()) {
				if(arr.getSourceId() == ARReceiptRegister.ACCT_COLLECTION) {
					int appliedStatus = registerServiceImpl.getAppliedStatus(arr.getAmount(), arr.getPaidAmount());
					logger.debug("Applied status of AC: "+appliedStatus+" "+arr.getSource()+" "+arr.getReceiptNumber());
					if(param.getAppliedStatusId() == appliedStatus) {
						//Add the Account Collection to the list.
						processedData.add(arr);
					}
				} else if(param.getAppliedStatusId() == AppliedStatus.FULLY_APPLIED) {
					logger.debug("Added: "+arr);
					processedData.add(arr);
				}
			}
			logger.debug("Processed "+processedData.size()+" for the report.");
			logger.info("Successfully generated the AR Receipt Register data.");
			return new Page<ARReceiptRegister>(pageSetting, processedData, receiptRegisterData.getTotalRecords());
		}
	}
}
