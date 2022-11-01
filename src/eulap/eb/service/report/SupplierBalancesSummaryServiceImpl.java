package eulap.eb.service.report;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.AccountDao;
import eulap.eb.dao.SupplierAccountDao;
import eulap.eb.service.jr.EBDataSource;
import eulap.eb.service.jr.EBJRServiceHandler;
import eulap.eb.web.dto.SupplierBalancesSummaryDto;
import net.sf.jasperreports.engine.JRDataSource;

/**
 * Business logic for generating report for supplier balances summary.


 */

@Service
public class SupplierBalancesSummaryServiceImpl implements SupplierBalancesSummaryService {
	private final Logger logger = Logger.getLogger(SupplierBalancesSummaryServiceImpl.class);
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private SupplierAccountDao supplierAccountDao;

	/**
	 * Get the list of Supplier Accounts for Classification.
	 * @param companyId The id of the company.
	 * @param divisionId The division id.
	 * @return The list of Supplier Accounts.
	 */
	public Collection<String> getSupplierAccountNames(int companyId, Integer divisionId) {
		return accountDao.getSupplierAccountNames(companyId, divisionId);
	}

	/**
	 * Get the supplier balance summary report data
	 * @param companyId The company id
	 * @param divisionId The division id
	 * @param asOfDate The as of date
	 * @param currencyId The currency id
	 * @param balanceOption The balance option
	 * @param classification The account classification
	 * @return The supplier balance summary report data
	 */
	@Override
	public JRDataSource getSupplierBalancesData(int companyId, int divisionId, Date asOfDate, int currencyId,
			int balanceOption, String classification) {
		logger.info("Retrieving supplier balances report data");
		int creditAccountId = !classification.equals("-1") ? accountDao.getAccountByName(classification, true).getId() : -1;
		EBJRServiceHandler<SupplierBalancesSummaryDto> handler = new SupplierBalancesHandler(this, companyId,
				divisionId, asOfDate, currencyId, balanceOption, creditAccountId);
		return new EBDataSource<SupplierBalancesSummaryDto>(handler);
	}

	private static class SupplierBalancesHandler implements EBJRServiceHandler<SupplierBalancesSummaryDto> {
		private SupplierBalancesSummaryServiceImpl srvcImpl;
		private int companyId;
		private int divisionId;
		private Date asOfDate;
		private int currencyId;
		private int balanceOption;
		private int creditAccountId;

		private SupplierBalancesHandler (SupplierBalancesSummaryServiceImpl srvcImpl, int companyId,
				int divisionId, Date asOfDate, int currencyId, int balanceOption, int creditAccountId) {
			this.srvcImpl = srvcImpl;
			this.companyId = companyId;
			this.divisionId = divisionId;
			this.asOfDate = asOfDate;
			this.currencyId = currencyId;
			this.balanceOption = balanceOption;
			this.creditAccountId = creditAccountId;
		}

		@Override
		public void close() throws IOException {
			srvcImpl = null;
		}

		@Override
		public Page<SupplierBalancesSummaryDto> nextPage(PageSetting pageSetting) {
			return srvcImpl.supplierAccountDao.getSupplierAcctBalancesData(companyId, divisionId,
					creditAccountId, currencyId, asOfDate, balanceOption, pageSetting);
		}
	}
}
