package eulap.eb.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.InvoiceImportationDetailsDao;
import eulap.eb.domain.hibernate.InvoiceImportationDetails;

/**
 * Implementation class of {@link InvoiceImportationDetailsDao}.

 *
 */
public class InvoiceImportationDetailsDaoImpl extends BaseDao<InvoiceImportationDetails> implements InvoiceImportationDetailsDao{

	@Override
	protected Class<InvoiceImportationDetails> getDomainClass() {
		return InvoiceImportationDetails.class;
	}

	@Override
	public InvoiceImportationDetails getIIDByInvoiceId(Integer apInvoiceId) {
		DetachedCriteria dc = getDetachedCriteria();
		if(apInvoiceId != null) {
			dc.add(Restrictions.eq(InvoiceImportationDetails.FIELD.apInvoiceId.name(), apInvoiceId));
		}
		return get(dc);
	}
}