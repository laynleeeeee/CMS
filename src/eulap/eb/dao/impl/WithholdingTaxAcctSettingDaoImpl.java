package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.WithholdingTaxAcctSettingDao;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.WithholdingTaxAcctSetting;

/**
 * Data access object implementation class for {@link WithholdingTaxAcctSettingDao}

 *
 */

public class WithholdingTaxAcctSettingDaoImpl extends BaseDao<WithholdingTaxAcctSetting> implements WithholdingTaxAcctSettingDao {

	@Override
	protected Class<WithholdingTaxAcctSetting> getDomainClass() {
		return WithholdingTaxAcctSetting.class;
	}

	@Override
	public List<WithholdingTaxAcctSetting> getWithholdingTaxAcctSettings(Integer companyId, Integer divisionId, boolean isCreditable) {
		DetachedCriteria dc = getCriteriaByCompanyId(companyId);
		if (companyId != null) {
			dc.add(Restrictions.eq(WithholdingTaxAcctSetting.FIELD.companyId.name(), companyId));
		}
		if (divisionId != null) {
			DetachedCriteria acDc = DetachedCriteria.forClass(AccountCombination.class);
			acDc.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
			acDc.add(Restrictions.eq(AccountCombination.FIELD.divisionId.name(), divisionId));
			dc.add(Subqueries.propertyIn(WithholdingTaxAcctSetting.FIELD.acctCombinationId.name(), acDc));
		}
		dc.add(Restrictions.eq(WithholdingTaxAcctSetting.FIELD.creditable.name(), isCreditable));
		dc.add(Restrictions.eq(WithholdingTaxAcctSetting.FIELD.active.name(), true));
		dc.addOrder(Order.asc(WithholdingTaxAcctSetting.FIELD.name.name()));
		return getAll(dc);
	}
}