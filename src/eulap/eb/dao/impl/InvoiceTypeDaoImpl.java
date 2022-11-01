package eulap.eb.dao.impl;

import java.util.Arrays;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.InvoiceTypeDao;
import eulap.eb.domain.hibernate.InvoiceType;
import eulap.eb.domain.hibernate.User;

public class InvoiceTypeDaoImpl extends BaseDao<InvoiceType> implements
		InvoiceTypeDao {

	@Override
	protected Class<InvoiceType> getDomainClass() {
		return InvoiceType.class;
	}

	@Override
	public List<InvoiceType> getInvoiceTypes(User user) {
		DetachedCriteria dc = getDCWithSLkeyIdAndActive(user.getServiceLeaseKeyId());
		dc.add(Restrictions.between(InvoiceType.FIELD.id.name(), InvoiceType.REGULAR_TYPE_ID, InvoiceType.CREDIT_MEMO_TYPE_ID));
		return getAll(dc);
	}

	@Override
	public List<InvoiceType> getAllInvoiceTypes(User user) {
		DetachedCriteria dc = getDCWithSLkeyIdAndActive(user.getServiceLeaseKeyId());
		dc.add(Restrictions.eq(InvoiceType.FIELD.active.name(), true));
		// List of Excluded in Invoice Type in AP Invoice Register Report
		List<Integer> invoiceTypeIds = Arrays.asList(InvoiceType.RR_TYPE_ID, InvoiceType.RR_CENTRAL_TYPE_ID, InvoiceType.RTS_TYPE_ID, InvoiceType.RR_NSB3_TYPE_ID,
				InvoiceType.RR_NSB4_TYPE_ID, InvoiceType.RR_NSB5_TYPE_ID, InvoiceType.RR_NSB8_TYPE_ID, InvoiceType.RR_NSB8A_TYPE_ID, InvoiceType.RR_RAW_MAT_TYPE_ID,
				InvoiceType.RR_RM_NET_WEIGHT_TYPE_ID, InvoiceType.DEBIT_MEMO_TYPE_ID, InvoiceType.CREDIT_MEMO_TYPE_ID, InvoiceType.REGULAR_TYPE_ID, InvoiceType.PREPAID_TYPE_ID);
		addNotInCriteria(dc, InvoiceType.FIELD.id.name(), invoiceTypeIds);
		return getAll(dc);
	}

}
