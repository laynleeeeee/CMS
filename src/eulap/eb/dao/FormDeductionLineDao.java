package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.FormDeductionLine;

/**
 * Data Access Object of {@link FormDeductionLine}

 *
 */
public interface FormDeductionLineDao extends Dao<FormDeductionLine>{

	/**
	 * Get the list of FormDeductionLine By EbObjectId
	 * @param ebObjectId The ebObjectId
	 * @return The list of FormDeductionLines
	 */
	List<FormDeductionLine> getFormDeductionLineByEbObject(Integer ebObjectId);
}
