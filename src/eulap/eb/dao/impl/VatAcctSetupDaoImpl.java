package eulap.eb.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.VatAcctSetupDao;
import eulap.eb.domain.hibernate.VatAcctSetup;

/**
 * DAO implementation class for {@link VatAcctSetupDao}

 */

public class VatAcctSetupDaoImpl extends BaseDao<VatAcctSetup> implements VatAcctSetupDao {

	@Override
	protected Class<VatAcctSetup> getDomainClass() {
		return VatAcctSetup.class;
	}

	@Override
	public VatAcctSetup getVatAccountSetup(Integer companyId, Integer divisionId) {
		return get(getVatAccountSetupDc(companyId, divisionId, null));
	}

	private DetachedCriteria getVatAccountSetupDc(Integer companyId, Integer divisionId, Integer taxTypeId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(VatAcctSetup.FIELD.companyId.name(), companyId));
		if (divisionId != null) {
			dc.add(Restrictions.eq(VatAcctSetup.FIELD.divisionId.name(), divisionId));
		}
		if(taxTypeId != null) {
			dc.add(Restrictions.eq(VatAcctSetup.FIELD.taxTypeId.name(), taxTypeId));
		}
		return dc;
	}

	@Override
	public VatAcctSetup getVatAccountSetup(Integer companyId, Integer divisionId, Integer taxTypeId) {
		return get(getVatAccountSetupDc(companyId, divisionId, taxTypeId));
	}

}
