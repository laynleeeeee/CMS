package eulap.eb.service.report;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.AccountDao;
import eulap.eb.dao.ArCustomerAcctDao;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.web.dto.CustomerBalancesSummaryDto;
import net.sf.jasperreports.engine.JRDataSource;
/**
 * Business logic for generating report for customer balances summary.


 */
@Service
public class CustomerBalancesSummaryServiceImpl {
	@Autowired
	private ArCustomerAcctDao customerAcctDao;
	@Autowired
	private AccountDao accountDao;

	/**
	 * Get the customer balances summary report data
	 * @param companyId The company id
	 * @param balanceOption The balance option (no zero or all)
	 * @param asOfDate The as of date
	 * @return The customer balances summary report data
	 */
	public JRDataSource generateReport(Integer companyId,Integer divisionId, Integer balanceOption, Date asOfDate,
			Integer currencyId, String classification ) {
		Integer accountId = !classification.equals("-1") ? accountDao.getAccountByName(classification, true).getId() : -1;
		EBJRServiceHandler<CustomerBalancesSummaryDto> handler =
				new JRCustomerBalancesSummaryHandler(companyId, divisionId, balanceOption, asOfDate, currencyId, accountId, this);
		return new EBDataSource<CustomerBalancesSummaryDto> (handler);
	}

	private static class JRCustomerBalancesSummaryHandler implements EBJRServiceHandler<CustomerBalancesSummaryDto> {
		private Integer companyId;
		private Integer divisionId;
		private Integer balanceOption;
		private Date asOfDate;
		private Integer currencyId;
		private Integer accountId;
		private CustomerBalancesSummaryServiceImpl cbsServiceImpl;

		private JRCustomerBalancesSummaryHandler (Integer companyId, Integer divisionId, Integer balanceOption,
				Date asOfDate, Integer currencyId, Integer accountId, CustomerBalancesSummaryServiceImpl summaryServiceImpl){
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.balanceOption = balanceOption;
			this.cbsServiceImpl = summaryServiceImpl;
			this.asOfDate = asOfDate;
			this.currencyId = currencyId;
			this.accountId = accountId;
		}

		@Override
		public void close() throws IOException {
			cbsServiceImpl = null;
		}

		@Override
		public Page<CustomerBalancesSummaryDto> nextPage(PageSetting pageSetting) {
			return cbsServiceImpl.customerAcctDao.getCustomerBalancesSummaryReport(-1, companyId, divisionId,
					balanceOption, asOfDate, currencyId, accountId, pageSetting);
		}
	}

	/**
	 * Get the customer balances summary report data within selected date range
	 * @param companyId The company id
	 * @param balanceOption The balance option
	 * @param dateFrom The start date
	 * @param dateTo The end date
	 * @return The customer account balances summary
	 */
	public JRDataSource generateCustomerBalSummryRprt(Integer companyId, Integer balanceOption,
			Date dateFrom, Date dateTo) {
		EBJRServiceHandler<CustomerBalancesSummaryDto> handler =
				new JRCustomerBalSummaryHandler(companyId, balanceOption, dateFrom, dateTo, this);
		return new EBDataSource<CustomerBalancesSummaryDto> (handler);
	}

	private static class JRCustomerBalSummaryHandler implements EBJRServiceHandler<CustomerBalancesSummaryDto> {
		private Integer companyId;
		private Integer balanceOption;
		private Date dateFrom;
		private Date dateTo;
		private CustomerBalancesSummaryServiceImpl summaryServiceImpl;

		private JRCustomerBalSummaryHandler (Integer companyId, Integer balanceOption,
				Date dateFrom, Date dateTo, CustomerBalancesSummaryServiceImpl summaryServiceImpl){
			this.companyId = companyId;
			this.balanceOption = balanceOption;
			this.dateFrom = dateFrom;
			this.dateTo = dateTo;
			this.summaryServiceImpl = summaryServiceImpl;
		}

		@Override
		public void close() throws IOException {
			summaryServiceImpl = null;
		}

		@Override
		public Page<CustomerBalancesSummaryDto> nextPage(PageSetting pageSetting) {
			return summaryServiceImpl.customerAcctDao.getCustomerBalSummaryFromDateRange(companyId,
					balanceOption, dateFrom, dateTo, pageSetting);
		}
	}

	/**
	 * Get the list of AR Accounts for Classification.
	 * @param companyId The id of the company.
	 * @param divisionId The division id.
	 * @return The list of AR Accounts.
	 */
	public Collection<String> getArAccounts(int companyId, Integer divisionId) {
		return accountDao.getArAccounts(companyId, divisionId);
	}
}
