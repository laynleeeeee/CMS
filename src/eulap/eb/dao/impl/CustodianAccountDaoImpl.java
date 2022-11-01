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
import eulap.eb.dao.CustodianAccountDao;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.CustodianAccount;
import eulap.eb.domain.hibernate.UserCustodian;

/**
 * DAO Implementation of {@link CustodianAccountDao}

 *
 */
public class CustodianAccountDaoImpl extends BaseDao<CustodianAccount> implements CustodianAccountDao {

	@Override
	protected Class<CustodianAccount> getDomainClass() {
		return CustodianAccount.class;
	}

	@Override
	public boolean isUniqueCustodianName(CustodianAccount custodianAccount) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.like(CustodianAccount.FIELD.custodianName.name(), custodianAccount.getCustodianName()));
		if(custodianAccount.getId() != 0) {
			dc.add(Restrictions.ne(CustodianAccount.FIELD.id.name(), custodianAccount.getId()));
		}
		dc.add(Restrictions.eq(CustodianAccount.FIELD.companyId.name(), custodianAccount.getCompanyId()));
		return getAll(dc).size() == 0;
	}

	@Override
	public boolean isUniqueCustodianAccountName(CustodianAccount custodianAccount) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.like(CustodianAccount.FIELD.custodianAccountName.name(), custodianAccount.getCustodianAccountName()));
		if(custodianAccount.getId() != 0) {
			dc.add(Restrictions.ne(CustodianAccount.FIELD.id.name(), custodianAccount.getId()));
		}
		dc.add(Restrictions.eq(CustodianAccount.FIELD.companyId.name(), custodianAccount.getCompanyId()));
		return getAll(dc).size() == 0;
	}

	@Override
	public Page<CustodianAccount> searchCustodianAccounts(String custodianName, String custodianAccountName,
			Integer companyId, Integer termId, SearchStatus status, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		if(!custodianName.trim().isEmpty()) {
			dc.add(Restrictions.like(CustodianAccount.FIELD.custodianName.name(), "%" + custodianName.trim() + "%"));
		}
		if(!custodianAccountName.isEmpty()) {
			dc.add(Restrictions.like(CustodianAccount.FIELD.custodianAccountName.name(), "%" + custodianAccountName.trim() +"%"));
		}
		if(companyId != -1) {
			dc.add(Restrictions.eq(CustodianAccount.FIELD.companyId.name(), companyId));
		}
		if(termId != -1) {
			dc.add(Restrictions.eq(CustodianAccount.FIELD.termId.name(), termId));
		}
		dc = DaoUtil.setSearchStatus(dc, CustodianAccount.FIELD.active.name(), status);
		dc.addOrder(Order.asc(CustodianAccount.FIELD.custodianName.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public List<CustodianAccount> getCustodianAccounts(Integer companyId, String name, Boolean isExact) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(CustodianAccount.FIELD.active.name(), true));
		if ((name != null && !name.isEmpty()) && isExact == null || !isExact) {
			dc.add(Restrictions.like(CustodianAccount.FIELD.custodianName.name(), "%" + name.trim() + "%"));
		} else if (isExact != null || isExact) {
			dc.add(Restrictions.eq(CustodianAccount.FIELD.custodianName.name(), name.trim()));
		}
		if (companyId != null) {
			dc.add(Restrictions.eq(CustodianAccount.FIELD.companyId.name(), companyId));
		}
		dc.addOrder(Order.asc(CustodianAccount.FIELD.custodianName.name()));
		dc.getExecutableCriteria(getSession()).setMaxResults(10);
		return getAll(dc);
	}

	@Override
	public List<CustodianAccount> getCustodianAccounts(Integer companyId, Integer divisionId, Integer userCustodianId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(CustodianAccount.FIELD.active.name(), true));
		if (companyId != null) {
			dc.add(Restrictions.eq(CustodianAccount.FIELD.companyId.name(), companyId));
		}
		if (divisionId != null && divisionId > 0) {
			DetachedCriteria acDc = DetachedCriteria.forClass(AccountCombination.class);
			acDc.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
			acDc.add(Restrictions.eq(AccountCombination.FIELD.divisionId.name(), divisionId));
			dc.add(Subqueries.propertyIn(CustodianAccount.FIELD.fdAccountCombinationId.name(), acDc));
		}
		if (userCustodianId != null && userCustodianId > 0) {
			DetachedCriteria ucDc = DetachedCriteria.forClass(UserCustodian.class);
			ucDc.setProjection(Projections.property(UserCustodian.FIELD.custodianAccountId.name()));
			ucDc.add(Restrictions.eq(UserCustodian.FIELD.id.name(), userCustodianId));
			ucDc.add(Restrictions.eq(UserCustodian.FIELD.active.name(), true));
			dc.add(Subqueries.propertyIn(CustodianAccount.FIELD.id.name(), ucDc));
		}
		return getAll(dc);
	}
}
