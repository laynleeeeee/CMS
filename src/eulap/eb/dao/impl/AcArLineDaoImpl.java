package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.AcArLineDao;
import eulap.eb.domain.hibernate.AcArLine;

/**
 * DAO Implementation class of {@link AcArLineDao}

 *
 */
public class AcArLineDaoImpl extends BaseDao<AcArLine> implements AcArLineDao{

	@Override
	protected Class<AcArLine> getDomainClass() {
		return AcArLine.class;
	}

	@Override
	public List<AcArLine> getArReceipt(int arReceiptId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(AcArLine.FIELD.arReceiptId.name(), arReceiptId));
		return getAll(dc);
	}
}
