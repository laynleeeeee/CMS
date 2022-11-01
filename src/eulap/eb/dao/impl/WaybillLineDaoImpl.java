package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.WaybillLineDao;
import eulap.eb.domain.hibernate.WaybillLine;

/**
 * DAO Implementation class of {@link WaybillLineDao}

 *
 */
public class WaybillLineDaoImpl extends BaseDao<WaybillLine> implements WaybillLineDao{

	@Override
	protected Class<WaybillLine> getDomainClass() {
		return WaybillLine.class;
	}
}
