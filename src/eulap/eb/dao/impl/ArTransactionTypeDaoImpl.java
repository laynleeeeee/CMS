package eulap.eb.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.ArTransactionTypeDao;
import eulap.eb.domain.hibernate.ArTransactionType;
import eulap.eb.domain.hibernate.User;

/**
 * Dao implementation of {@link ArTransactionTypeDao}.

 */
public class ArTransactionTypeDaoImpl extends BaseDao<ArTransactionType> implements ArTransactionTypeDao{

	@Override
	protected Class<ArTransactionType> getDomainClass() {
		return ArTransactionType.class;
	}

	@Override
	public List<ArTransactionType> getTransactionTypes(User user) {
		DetachedCriteria dc = getDCWithSLkeyIdAndActive(user.getServiceLeaseKeyId());
		List<Integer> transactionTypeIds = new ArrayList<Integer>();
		transactionTypeIds = Arrays.asList(ArTransactionType.TYPE_REGULAR_TRANSACTION,
				ArTransactionType.TYPE_DEBIT_MEMO, ArTransactionType.TYPE_CREDIT_MEMO);
		addAsOrInCritiria(dc, ArTransactionType.FIELD.id.name(), transactionTypeIds);
		return getAll(dc);
	}

	@Override
	public List<ArTransactionType> getAllTransactionTypes(User user) {
		DetachedCriteria dc = getDCWithSLkeyIdAndActive(user.getServiceLeaseKeyId());
		dc.add(Restrictions.eq(ArTransactionType.FIELD.active.name(), true));
		return getAll(dc);
	}
}
