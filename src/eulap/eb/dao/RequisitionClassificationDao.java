package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.RequisitionClassification;

/**
 * Data access object for {@link RequisitionClassification}

 */
public interface RequisitionClassificationDao extends Dao<RequisitionClassification> {

	/**
	 * Get the list of classifications but filters the list if its purchase request or requisition form.
	 * @param isPurchaseRequest True if to filter purchase request only, otherwise false.
	 */
	List<RequisitionClassification> getAllActive(boolean isPurchaseRequest); 


}