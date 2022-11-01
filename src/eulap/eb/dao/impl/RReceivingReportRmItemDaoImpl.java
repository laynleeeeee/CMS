package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.RReceivingReportRmItemDao;
import eulap.eb.domain.hibernate.RReceivingReportRmItem;

/**
 * Implementation class of {@link RReceivingReportRmItemDao}

 *
 */
public class RReceivingReportRmItemDaoImpl extends BaseDao<RReceivingReportRmItem> implements RReceivingReportRmItemDao {

	@Override
	protected Class<RReceivingReportRmItem> getDomainClass() {
		return RReceivingReportRmItem.class;
	}

}
