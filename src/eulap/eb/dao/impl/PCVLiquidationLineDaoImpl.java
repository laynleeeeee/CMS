package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.PCVLiquidationLineDao;
import eulap.eb.domain.hibernate.PettyCashVoucherLiquidationLine;

/**
 * Implementing class of {@link PettyCashVoucherLiquidationLine}

 */

public class PCVLiquidationLineDaoImpl extends BaseDao<PettyCashVoucherLiquidationLine> implements PCVLiquidationLineDao {

	@Override
	protected Class<PettyCashVoucherLiquidationLine> getDomainClass() {
		return PettyCashVoucherLiquidationLine.class;
	}
}
