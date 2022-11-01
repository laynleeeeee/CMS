package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.CashSaleReturnArLine;

/**
 * Data Access Object for {@link CashSaleReturnArLine}

 *
 */
public interface CashSaleReturnArLineDao extends Dao<CashSaleReturnArLine>{

	/**
	 * Get the list of Cash Sale Return Ar Lines.
	 * @param cashSaleReturnId The cash sales return id.
	 * @return The list of cash sale return ar lines.
	 */
	List<CashSaleReturnArLine> getCsrArLine(int cashSaleReturnId);
}
