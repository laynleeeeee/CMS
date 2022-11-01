package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.CivilStatusDao;
import eulap.eb.domain.hibernate.CivilStatus;


/**
 * DAO Implementation of {@link CivilStatusDao}

 *
 */
public class CivilStatusDaoImpl extends BaseDao<CivilStatus> implements CivilStatusDao {

	@Override
	protected Class<CivilStatus> getDomainClass() {
		return CivilStatus.class;
	}

	@Override
	public List<CivilStatus> getAllWithInactive(Integer civilStatusId) {
		DetachedCriteria dc = getDetachedCriteria();
		Criterion criterion = Restrictions.eq(CivilStatus.FIELD.active.name(), true);
		if(civilStatusId != null) {
			criterion = Restrictions.or(criterion,
					Restrictions.and(Restrictions.eq(CivilStatus.FIELD.id.name(), civilStatusId),
							Restrictions.eq(CivilStatus.FIELD.active.name(), false)));
		}
		dc.add(criterion);
		dc.addOrder(Order.asc(CivilStatus.FIELD.name.name()));
		return getAll(dc);
	}

	@Override
	public List<CivilStatus> getActiveCivilStatuses() {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(CivilStatus.FIELD.active.name(), true));
		dc.addOrder(Order.asc(CivilStatus.FIELD.name.name()));
		return getAll(dc);
	}

}
