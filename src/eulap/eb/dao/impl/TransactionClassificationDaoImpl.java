package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.TransactionClassificationDao;
import eulap.eb.domain.hibernate.TransactionClassification;
import eulap.eb.domain.hibernate.User;

/**
 * Implementing class of {@link TransactionClassification}

 *
 */
public class TransactionClassificationDaoImpl extends BaseDao<TransactionClassification> implements
TransactionClassificationDao {

	@Override
	protected Class<TransactionClassification> getDomainClass() {
		return TransactionClassification.class;
	}

	@Override
	public List<TransactionClassification> getAllTransactionClassifications(User user) {
		DetachedCriteria dc = getDCWithSLkeyIdAndActive(user.getServiceLeaseKeyId());
		dc.add(Restrictions.eq(TransactionClassification.FIELD.active.name(), true));
		return getAll(dc);
	}
}
