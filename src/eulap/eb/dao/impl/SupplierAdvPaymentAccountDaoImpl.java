package eulap.eb.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.SupplierAdvPaymentAccountDao;
import eulap.eb.domain.hibernate.SupplierAdvPaymentAccount;

/**
 * DAO implementation class {@link SupplierAdvPaymentAccountDao}

 */

public class SupplierAdvPaymentAccountDaoImpl extends BaseDao<SupplierAdvPaymentAccount> implements SupplierAdvPaymentAccountDao {

	@Override
	protected Class<SupplierAdvPaymentAccount> getDomainClass() {
		return SupplierAdvPaymentAccount.class;
	}

	@Override
	public SupplierAdvPaymentAccount getSapAcctByCompany(Integer companyId) {
		return get(getSapAccount(companyId, null));
	}

	private DetachedCriteria getSapAccount(Integer companyId, Integer divisionId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(SupplierAdvPaymentAccount.FIELD.companyId.name(), companyId));
		if (divisionId != null) {
			dc.add(Restrictions.eq(SupplierAdvPaymentAccount.FIELD.divisionId.name(), divisionId));
		}
		return dc;
	}

	@Override
	public SupplierAdvPaymentAccount getSapAcctByCompanyDiv(Integer companyId, Integer divisionId) {
		return get(getSapAccount(companyId, divisionId));
	}
}