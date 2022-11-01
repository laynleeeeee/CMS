package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.DirectPaymentLineDao;
import eulap.eb.domain.hibernate.DirectPaymentLine;

/**
 * DAO Implementation class of {@link DirectPaymentLineDao}

 *
 */
public class DirectPaymentLineDaoImpl extends BaseDao<DirectPaymentLine> implements DirectPaymentLineDao{

	@Override
	protected Class<DirectPaymentLine> getDomainClass() {
		return DirectPaymentLine.class;
	}

	@Override
	public List<DirectPaymentLine> getDirectPaymentLinesByDirectPaymentId(Integer directPaymentId, boolean isActiveOnly) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(DirectPaymentLine.FIELD.directPaymentId.name(), directPaymentId));
		if (isActiveOnly) {
			dc.add(Restrictions.eq(DirectPaymentLine.FIELD.active.name(), true));
		}
		return getAll(dc);
	}

}
