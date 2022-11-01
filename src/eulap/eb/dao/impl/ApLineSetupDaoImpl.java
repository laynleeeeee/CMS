package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.common.dao.DaoUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.ApLineSetupDao;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.ApLineSetup;

/**
 * DAO implementation of {@link ApLineSetupDao}

 *
 */
public class ApLineSetupDaoImpl extends BaseDao<ApLineSetup> implements ApLineSetupDao {

	@Override
	protected Class<ApLineSetup> getDomainClass() {
		return ApLineSetup.class;
	}

	@Override
	public boolean isUniqueName(Integer apLineSetupId, String name, Integer companyId, Integer divisionId) {
		DetachedCriteria aplsCrit = DetachedCriteria.forClass(ApLineSetup.class);
		aplsCrit.add(Restrictions.eq(ApLineSetup.FIELD.name.name(), name.trim()));
		if (!apLineSetupId.equals(0)) {
			//Add condition if the AP Line Setup is edited.
			aplsCrit.add(Restrictions.ne(ApLineSetup.FIELD.id.name(), apLineSetupId));
		}
		DetachedCriteria aCDc = DetachedCriteria.forClass(AccountCombination.class);
		aCDc.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
		aCDc.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		if (divisionId != null) {
			aCDc.add(Restrictions.eq(AccountCombination.FIELD.divisionId.name(), divisionId));
		}
		aplsCrit.add(Subqueries.propertyIn(ApLineSetup.FIELD.accountCombinationId.name(), aCDc));
		return getAll(aplsCrit).size() == 0;
	}

	@Override
	public Page<ApLineSetup> searchApLineSetups(Integer companyId, String name,
			SearchStatus status, PageSetting pageSetting) {
		DetachedCriteria aplsCrit = getDetachedCriteria();
		aplsCrit = getAplsCriteria(companyId, null, name, aplsCrit);
		aplsCrit = DaoUtil.setSearchStatus(aplsCrit, ApLineSetup.FIELD.active.name(), status);
		aplsCrit.addOrder(Order.asc(ApLineSetup.FIELD.name.name()));
		aplsCrit.addOrder(Order.asc(ApLineSetup.FIELD.accountCombinationId.name()));
		return getAll(aplsCrit, pageSetting);
	}

	@Override
	public List<ApLineSetup> getApLineSetups(Integer companyId, Integer divisionId, String name) {
		DetachedCriteria aplsCrit = DetachedCriteria.forClass(ApLineSetup.class);
		aplsCrit.add(Restrictions.eq(ApLineSetup.FIELD.active.name(), true));
		aplsCrit = getAplsCriteria(companyId, divisionId, name, aplsCrit);
		return getAll(aplsCrit);
	}

	private DetachedCriteria getAplsCriteria(Integer companyId, Integer divisionId, String name, DetachedCriteria aplsCrit) {
		if (companyId > 0) {
			aplsCrit.createAlias("accountCombination", "ac");
			aplsCrit.add(Restrictions.eq("ac.companyId", companyId));
			if (divisionId != null) {
				aplsCrit.add(Restrictions.eq("ac.divisionId", divisionId));
			}
		}
		if (!name.trim().isEmpty()) {
			aplsCrit.add(Restrictions.like("name", "%"+name.trim()+"%"));
		}
		return aplsCrit;
	}

	@Override
	public ApLineSetup getApLineSetup(Integer companyId, Integer divisionId, String name) {
		DetachedCriteria dc = DetachedCriteria.forClass(ApLineSetup.class);
		dc.add(Restrictions.eq(ApLineSetup.FIELD.active.name(), true));
		if (companyId > 0) {
			dc.createAlias("accountCombination", "ac");
			dc.add(Restrictions.eq("ac.companyId", companyId));
			if (divisionId != null) {
				dc.add(Restrictions.eq("ac.divisionId", divisionId));
			}
		}
		if (!name.trim().isEmpty()) {
			dc.add(Restrictions.eq(ApLineSetup.FIELD.name.name(), name.trim()));
		}
		return get(dc);
	}
}
