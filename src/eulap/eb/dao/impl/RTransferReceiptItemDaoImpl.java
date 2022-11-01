package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.RTransferReceiptItemDao;
import eulap.eb.domain.hibernate.RTransferReceiptItem;

/**
 * DAO Implementation of {@link RTransferReceiptItemDao}

 *
 */
public class RTransferReceiptItemDaoImpl extends BaseDao<RTransferReceiptItem> implements RTransferReceiptItemDao{

	@Override
	protected Class<RTransferReceiptItem> getDomainClass() {
		return RTransferReceiptItem.class;
	}

	@Override
	public List<RTransferReceiptItem> getRTrItems(int rTansferReceiptId, Integer itemId) {
		DetachedCriteria trItemsCriteria = getDetachedCriteria();
		trItemsCriteria.add(Restrictions.eq(RTransferReceiptItem.FIELD.rTransferReceiptId.name(),
				rTansferReceiptId));
		if(itemId != null) {
			trItemsCriteria.add(Restrictions.eq(RTransferReceiptItem.FIELD.itemId.name(), itemId));
		}
		return getAll(trItemsCriteria);
	}
}