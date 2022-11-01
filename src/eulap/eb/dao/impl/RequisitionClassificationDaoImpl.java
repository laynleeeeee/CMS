package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.RequisitionClassificationDao;
import eulap.eb.domain.hibernate.RequisitionClassification;

/**
 * Implementation class for {@link RequisitionClassificationDao}

 */
public class RequisitionClassificationDaoImpl extends BaseDao<RequisitionClassification> implements RequisitionClassificationDao {

	@Override
	protected Class<RequisitionClassification> getDomainClass() {
		return RequisitionClassification.class;
	}

	@Override
	public List<RequisitionClassification> getAllActive(boolean isPurchaseRequest) {
		DetachedCriteria dc = getDetachedCriteria();
		if (isPurchaseRequest) {
			dc.add(Restrictions.eq(RequisitionClassification.FIELD.id.name(), RequisitionClassification.RC_PURCHASE_REQUISITION));
		} else {
			dc.add(Restrictions.ne(RequisitionClassification.FIELD.id.name(), RequisitionClassification.RC_PURCHASE_REQUISITION));
		}
		dc.add(Restrictions.eq(RequisitionClassification.FIELD.active.name(), true));
		return getAll(dc);
	}
}