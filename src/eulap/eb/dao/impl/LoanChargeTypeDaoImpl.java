package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.LoanChargeTypeDao;
import eulap.eb.domain.hibernate.LoanChargeType;

/**
 * DAO implementation class for {@link LoanChargeTypeDao}

 */

public class LoanChargeTypeDaoImpl extends BaseDao<LoanChargeType> implements LoanChargeTypeDao {

	@Override
	protected Class<LoanChargeType> getDomainClass() {
		return LoanChargeType.class;
	}

	@Override
	public List<LoanChargeType> getAllActiveLoanChargeTypes(Integer loanChargeTypeId) {
		DetachedCriteria dc = getDetachedCriteria();
		Criterion criterion = Restrictions.eq(LoanChargeType.FIELD.active.name(), true);
		if (loanChargeTypeId != null) {
			criterion = Restrictions.or(criterion,
					Restrictions.and(Restrictions.eq(LoanChargeType.FIELD.id.name(), loanChargeTypeId),
							Restrictions.eq(LoanChargeType.FIELD.active.name(), false)));
		}
		dc.add(criterion);
		return getAll(dc);
	}
}
