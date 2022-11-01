package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.eb.domain.hibernate.RequisitionType;

/**
 * Data access object for {@link RequisitionType}

 */
public interface RequisitionTypeDao extends Dao<RequisitionType> {

	/**
	 * Get list of requisition type
	 * @return the list of requisition type
	 */
	List<RequisitionType> getRequisitionType();
}
