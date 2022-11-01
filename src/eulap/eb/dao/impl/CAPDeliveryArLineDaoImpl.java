package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.CAPDeliveryArLineDao;
import eulap.eb.domain.hibernate.CAPDeliveryArLine;

/**
 * DAO Implementation layer of {@link CAPDeliveryArLineDao}

 *
 */
public class CAPDeliveryArLineDaoImpl extends BaseDao<CAPDeliveryArLine> implements CAPDeliveryArLineDao {

	@Override
	protected Class<CAPDeliveryArLine> getDomainClass() {
		return CAPDeliveryArLine.class;
	}

}
