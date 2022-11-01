package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.GenderDao;
import eulap.eb.domain.hibernate.Gender;


/**
 * DAO Implementation of {@link GenderDaoImpl}

 *
 */
public class GenderDaoImpl extends BaseDao<Gender> implements GenderDao {

	@Override
	protected Class<Gender> getDomainClass() {
		return Gender.class;
	}

	@Override
	public List<Gender> getAllWithInactive(Integer genderId) {
		DetachedCriteria dc = getDetachedCriteria();
		Criterion criterion = Restrictions.eq(Gender.FIELD.active.name(), true);
		if(genderId != null) {
			criterion = Restrictions.or(criterion,
					Restrictions.and(Restrictions.eq(Gender.FIELD.id.name(), genderId),
							Restrictions.eq(Gender.FIELD.active.name(), false)));
		}
		dc.add(criterion);
		dc.addOrder(Order.asc(Gender.FIELD.name.name()));
		return getAll(dc);
	}

}
