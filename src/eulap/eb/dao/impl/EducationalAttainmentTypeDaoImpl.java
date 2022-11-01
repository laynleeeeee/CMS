package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.EducationalAttainmentTypeDao;
import eulap.eb.domain.hibernate.EducationalAttainmentType;

/**
 * Implementation class of {@link EducationalAttainmentTypeDao}.

 *
 */
public class EducationalAttainmentTypeDaoImpl extends BaseDao<EducationalAttainmentType> implements EducationalAttainmentTypeDao {

	@Override
	protected Class<EducationalAttainmentType> getDomainClass() {
		return EducationalAttainmentType.class;
	}

	@Override
	public List<EducationalAttainmentType> getAllWithInactive(Integer educAttnmntTypeId) {
		DetachedCriteria dc = getDetachedCriteria();
		Criterion criterion = Restrictions.eq(EducationalAttainmentType.FIELD.active.name(), true);
		if(educAttnmntTypeId != null) {
			criterion = Restrictions.or(criterion,
					Restrictions.and(Restrictions.eq(EducationalAttainmentType.FIELD.id.name(), educAttnmntTypeId),
							Restrictions.eq(EducationalAttainmentType.FIELD.active.name(), false)));
		}
		dc.add(criterion);
		dc.addOrder(Order.asc(EducationalAttainmentType.FIELD.name.name()));
		return getAll(dc);
	}

}
