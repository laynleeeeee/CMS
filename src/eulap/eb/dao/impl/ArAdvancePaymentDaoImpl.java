package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.ArAdvancePaymentDao;
import eulap.eb.domain.hibernate.ArReceiptAdvancePayment;

/**
 * DAO implementation class for {@link ArAdvancePaymentDao}

 */

public class ArAdvancePaymentDaoImpl extends BaseDao<ArReceiptAdvancePayment> implements ArAdvancePaymentDao {

	@Override
	protected Class<ArReceiptAdvancePayment> getDomainClass() {
		return ArReceiptAdvancePayment.class;
	}

	@Override
	public List<ArReceiptAdvancePayment> getArRecAdvPayments(Integer arReceiptId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ArReceiptAdvancePayment.FIELD.arReceiptId.name(), arReceiptId));
		return getAll(dc);
	}

}
