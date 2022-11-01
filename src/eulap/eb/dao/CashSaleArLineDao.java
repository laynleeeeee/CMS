package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.CashSaleArLine;

/**
 * Data Access Object for {@link CashSaleArLine}

 *
 */
public interface CashSaleArLineDao extends Dao<CashSaleArLine>{

	/**
	 * Get the list of Cash Sales AR Lines.
	 * @param cashSaleId The id of the cash sale.
	 * @return The list of Cash Sale AR Lines.
	 */
	List<CashSaleArLine> getCsArLines(int cashSaleId);

	/**
	 * Get the total cash sale amount of all ar lines.
	 * @param date The cash sale receipt date.
	 * @return The cash sale amount.
	 */
	double getTotalAmountCSAByDate(Date date);
}
