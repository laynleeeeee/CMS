package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.BusinessClassificationDao;
import eulap.eb.domain.hibernate.BusinessClassification;

/**
 * Implementation class of {@link BusinessClassificationDao}

 *
 */
public class BusinessClassificationDaoImpl extends BaseDao<BusinessClassification> implements BusinessClassificationDao {

	@Override
	protected Class<BusinessClassification> getDomainClass() {
		return BusinessClassification.class;
	}

	@Override
	public List<BusinessClassification> getBusinessClassifications(Integer busClassId) {
		DetachedCriteria dc = getDetachedCriteria();
		Criterion criterion = Restrictions.eq(BusinessClassification.FIELD.active.name(), true);
		if(busClassId != null) {
			criterion = Restrictions.or(criterion,
					Restrictions.and(Restrictions.eq(BusinessClassification.FIELD.id.name(), busClassId),
							Restrictions.eq(BusinessClassification.FIELD.active.name(), false)));
		}
		dc.add(criterion);
		return getAll(dc);
	}
}
