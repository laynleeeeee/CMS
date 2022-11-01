package eulap.eb.service.report;

import java.util.Date;

import net.sf.jasperreports.engine.JRDataSource;

/**
 * The business logic class of Supplier Balances Summary.

 */
public interface SupplierBalancesSummaryService {

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
	public JRDataSource getSupplierBalancesData(int companyId, int divisionId, Date asOfDate, int currencyId,
			int balanceOption, String classification);
}
