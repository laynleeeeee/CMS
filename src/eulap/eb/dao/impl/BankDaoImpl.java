package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.BankDao;
import eulap.eb.domain.hibernate.Bank;

/**
 * DAO implementation class for {@link BankDao}

 */

public class BankDaoImpl extends BaseDao<Bank> implements BankDao {

	@Override
	protected Class<Bank> getDomainClass() {
		return Bank.class;
	}

	@Override
	public List<Bank> getBanks(Integer bankId) {
		DetachedCriteria dc = getDetachedCriteria();
		Criterion criterion = Restrictions.eq(Bank.FIELD.active.name(), true);
		if (bankId != null) {
			criterion = Restrictions.or(criterion,
					Restrictions.and(Restrictions.eq(Bank.FIELD.id.name(), bankId),
							Restrictions.eq(Bank.FIELD.active.name(), false)));
		}
		dc.add(criterion);
		return getAll(dc);
	}
}
