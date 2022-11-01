package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.SalesQuotationEquipmentLineDao;
import eulap.eb.domain.hibernate.SalesQuotationEquipmentLine;

/**
 * DAO implementation class for {@link SalesQuotationEquipmentLineDao}

 */

public class SalesQuotationEquipmentLineDaoImpl extends BaseDao<SalesQuotationEquipmentLine> implements SalesQuotationEquipmentLineDao {

	@Override
	protected Class<SalesQuotationEquipmentLine> getDomainClass() {
		return SalesQuotationEquipmentLine.class;
	}

}
