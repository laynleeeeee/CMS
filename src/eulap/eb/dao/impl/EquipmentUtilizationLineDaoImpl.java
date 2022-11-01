package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.EquipmentUtilizationLineDao;
import eulap.eb.domain.hibernate.EquipmentUtilizationLine;

/**
 * Implementation class for {@link EquipmentUtilizationLineDao}

 *
 */
public class EquipmentUtilizationLineDaoImpl extends BaseDao<EquipmentUtilizationLine> implements EquipmentUtilizationLineDao  {

	@Override
	protected Class<EquipmentUtilizationLine> getDomainClass() {
		return EquipmentUtilizationLine.class;
	}

}
