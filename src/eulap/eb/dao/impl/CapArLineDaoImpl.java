package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.CapArLineDao;
import eulap.eb.domain.hibernate.CapArLine;

/**
 * Implementing class of {@link CapArLineDao}

 *
 */
public class CapArLineDaoImpl extends BaseDao<CapArLine> implements CapArLineDao{

	@Override
	protected Class<CapArLine> getDomainClass() {
		return CapArLine.class;
	}

	@Override
	public List<CapArLine> getCapArLines(int capId) {
		DetachedCriteria capArLineDc = getDetachedCriteria();
		capArLineDc.add(Restrictions.eq(CapArLine.FIELD.customerAdvancePaymentId.name(), capId));
		return getAll(capArLineDc);
	}

	@Override
	public double getTotalCAPLineAmount(int capId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(CapArLine.FIELD.customerAdvancePaymentId.name(), capId));
		dc.setProjection(Projections.sum(CapArLine.FIELD.amount.name()));
		return getBySumProjection(dc);
	}
}
