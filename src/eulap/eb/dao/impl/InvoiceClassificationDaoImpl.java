package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.InvoiceClassificationDao;
import eulap.eb.domain.hibernate.InvoiceClassification;
import eulap.eb.domain.hibernate.User;

public class InvoiceClassificationDaoImpl extends BaseDao<InvoiceClassification> implements
InvoiceClassificationDao {

	@Override
	protected Class<InvoiceClassification> getDomainClass() {
		return InvoiceClassification.class;
	}

	@Override
	public List<InvoiceClassification> getAllInvoiceClassifications(User user) {
		DetachedCriteria dc = getDCWithSLkeyIdAndActive(user.getServiceLeaseKeyId());
		dc.add(Restrictions.eq(InvoiceClassification.FIELD.active.name(), true));
		return getAll(dc);
	}

}
