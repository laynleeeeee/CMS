package eulap.eb.dao.impl;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.ArInvoiceEquipmentLineDao;
import eulap.eb.domain.hibernate.ArInvoiceEquipmentLine;

/**
 * Implementation class for {@link ArInvoiceEquipmentLineDao}

 *
 */
public class ArInvoiceEquipmentLineDaoImpl extends BaseDao<ArInvoiceEquipmentLine> implements ArInvoiceEquipmentLineDao  {

	@Override
	protected Class<ArInvoiceEquipmentLine> getDomainClass() {
		return ArInvoiceEquipmentLine.class;
	}

}
