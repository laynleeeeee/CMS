package eulap.eb.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.ApInvoiceAccountDao;
import eulap.eb.domain.hibernate.ApInvoiceAccount;

/**
 * DAO implementation class {@link ApInvoiceAccountDao}

 */

public class ApInvoiceAccountDaoImpl extends BaseDao<ApInvoiceAccount> implements ApInvoiceAccountDao {

	@Override
	protected Class<ApInvoiceAccount> getDomainClass() {
		return ApInvoiceAccount.class;
	}

	@Override
	public ApInvoiceAccount getInvoiceAcctByCompany(Integer companyId) {
		return get(getInvoiceAccount(companyId, null));
	}

	private DetachedCriteria getInvoiceAccount(Integer companyId, Integer divisionId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ApInvoiceAccount.FIELD.companyId.name(), companyId));
		if (divisionId != null) {
			dc.add(Restrictions.eq(ApInvoiceAccount.FIELD.divisionId.name(), divisionId));
		}
		return dc;
	}

	@Override
	public ApInvoiceAccount getInvoiceAcctByCompanyDiv(Integer companyId, Integer divisionId) {
		return get(getInvoiceAccount(companyId, divisionId));
	}
}