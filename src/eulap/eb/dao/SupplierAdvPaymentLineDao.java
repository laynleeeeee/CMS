package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.SupplierAdvancePaymentLine;

/**
 * Data access object for {@link SupplierAdvancePaymentLine}

 */

public interface SupplierAdvPaymentLineDao extends Dao<SupplierAdvancePaymentLine> {

	/**
	 * 
	 * @param advPaymentId
	 * @return
	 */
	List<SupplierAdvancePaymentLine> getAdvancePaymentLines(Integer advPaymentId);

}
