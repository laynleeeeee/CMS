package eulap.eb.service.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.ArCustomerAcctDao;
import eulap.eb.dao.ArReceiptDao;
import eulap.eb.dao.ArTransactionDao;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.web.dto.CustomerAccountHistoryDto;
import eulap.eb.web.dto.CustomerBalancesSummaryDto;
import net.sf.jasperreports.engine.JRDataSource;
/**
 * Business logic for generating report for customer account history.

 */
@Service
public class CustomerAcctHistoryServiceImpl implements CustomerAcctHistoryService{
	private final Logger logger =  Logger.getLogger(CustomerAcctHistoryServiceImpl.class);
	@Autowired
	private ArCustomerAcctDao customerAccountDao;
	@Autowired
	private ArTransactionDao transactionDao;
	@Autowired
	private ArReceiptDao receiptDao;

	@Override
	public Double getCustomerAcctTotalTransactionAmount(Integer customerAccountId) {
		logger.debug("Get the total transaction amount of customer id " + customerAccountId);
		return transactionDao.getCustomerAcctTotalTransaction(customerAccountId);
	}

	@Override
	public Double getCustomerAcctTotalReceiptAmount(Integer customerAccountId) {
		logger.debug("Get the total receipt amount of customer id " + customerAccountId);
		return receiptDao.getCustomerAcctTotalReceipt(customerAccountId);
	}

	@Override
	public List<CustomerAccountHistoryDto> generate(
			CustomerAccountHistoryParam param) {
		logger.debug("Retreiving customer account history from the database");
		return customerAccountDao.getCustomerAccountHistory(param);
	}

	@Override
	public CustomerAccountHistoryDto getTotalTransactionsAndReceipts(int customerAcctId) {
		return customerAccountDao.getTotalTransactionsAndReceipts(customerAcctId);
	}

	/**
	 * Generate customer account history report data
	 * @param param The customer account history report parameters
	 * @return The customer account history report data
	 */
	public JRDataSource generateCustAcctHistReport(CustomerAccountHistoryParam param) {
		EBJRServiceHandler<CustomerAccountHistoryDto> handler =
				new JRCustomerCustomerHistoryHandler(param, this);
		return new EBDataSource<CustomerAccountHistoryDto> (handler);
	}

	private static class JRCustomerCustomerHistoryHandler implements EBJRServiceHandler<CustomerAccountHistoryDto> {
		private CustomerAccountHistoryParam param;
		private CustomerAcctHistoryServiceImpl historyServiceImpl;
		private double runningBalance;

		private JRCustomerCustomerHistoryHandler (CustomerAccountHistoryParam param, CustomerAcctHistoryServiceImpl historyServiceImpl){
			this.param = param;
			this.historyServiceImpl = historyServiceImpl;
		}

		@Override
		public void close() throws IOException {
			historyServiceImpl=null;
			runningBalance = 0.00;
		}

		@Override
		public Page<CustomerAccountHistoryDto> nextPage(PageSetting pageSetting) {
			Page<CustomerAccountHistoryDto> result = historyServiceImpl.customerAccountDao.getCustomerAccountHistoryReport(param, pageSetting);
			List<CustomerAccountHistoryDto> customerAcctHistoryDtos = new ArrayList<CustomerAccountHistoryDto>();
			for (CustomerAccountHistoryDto dto : result.getData()) {
				if (!dto.getDescription().equalsIgnoreCase("Beginning Balance")) {
					double transactionAmount = dto.getTransactionAmount();
					double receiptAmount = dto.getReceiptAmount();
					runningBalance += (transactionAmount - receiptAmount);
					dto.setBalance(runningBalance);
				} else {
					runningBalance = dto.getBalance();
				}
				customerAcctHistoryDtos.add(dto);
			}
			return new Page<CustomerAccountHistoryDto>(pageSetting, customerAcctHistoryDtos, result.getTotalRecords());
		}
	}

	/**
	 * Get the summary of customer transaction
	 * @param companyId The company id
	 * @param customerAcctId The customer account
	 * @param divisionId The division id
	 * @param currencyId The currency id
	 * @param dateTo The date range end
	 * @return The summary of customer transaction
	 */
	public Page<CustomerBalancesSummaryDto> getTotalSummary(Integer companyId, Integer customerAcctId,
			Integer divisionId, Integer currencyId, Date dateTo) {
		return customerAccountDao.getCustomerBalancesSummaryReport(customerAcctId, companyId, divisionId, -1,
				dateTo, currencyId, null, new PageSetting (PageSetting.START_PAGE, PageSetting.NO_PAGE_CONSTRAINT));
	}
}
