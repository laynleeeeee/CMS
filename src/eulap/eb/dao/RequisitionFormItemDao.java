package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.RequisitionFormItem;

/**
 * Data access object for {@link RequisitionFormItem}

 */
public interface RequisitionFormItemDao extends Dao<RequisitionFormItem> {

	/**
	 * Get the list of all {@code RequisitionFormItem} by the eb object of its parent.
	 * @param refObjectId The eb object id of {@code RequisitionForm}
	 * @return
	 */
	List<RequisitionFormItem> getAllByParent(Integer refObjectId);
}
