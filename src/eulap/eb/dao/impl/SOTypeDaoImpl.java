package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.SOTypeDao;
import eulap.eb.domain.hibernate.SOType;

/**
 * DAO Implementation of {@link SOType}

 *
 */
public class SOTypeDaoImpl extends BaseDao<SOType> implements
		SOTypeDao {

	@Override
	protected Class<SOType> getDomainClass() {
		return SOType.class;
	}

	@Override
	public List<SOType> getActiveSOTypes(Integer soTypeId) {
		DetachedCriteria dc = getDetachedCriteria();
		Criterion criterion = Restrictions.eq(SOType.FIELD.active.name(), true);
		if(soTypeId != null) {
			criterion = Restrictions.or(criterion, Restrictions.and(Restrictions.eq(SOType.FIELD.id.name(), soTypeId),
					Restrictions.eq(SOType.FIELD.active.name(), false)));
		}
		dc.add(criterion);
		return getAll(dc);
	}
}
