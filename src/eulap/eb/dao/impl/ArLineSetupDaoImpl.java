package eulap.eb.dao.impl;

import java.util.Collection;
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
import eulap.eb.dao.ArLineSetupDao;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.ArCustomerAccount;
import eulap.eb.domain.hibernate.ArLineSetup;

/**
 * DAO implementation of {@link ArLineSetupDao}

 *
 */
public class ArLineSetupDaoImpl extends BaseDao<ArLineSetup> implements ArLineSetupDao{

	@Override
	protected Class<ArLineSetup> getDomainClass() {
		return ArLineSetup.class;
	}

	@Override
	public boolean hasDuplicate(ArLineSetup arLineSetup) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.ne(ArLineSetup.FIELD.id.name(), arLineSetup.getId()));
		dc.add(Restrictions.eq(ArLineSetup.FIELD.name.name(), arLineSetup.getName().trim()));

		DetachedCriteria aCDc = DetachedCriteria.forClass(AccountCombination.class);
		aCDc.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
		aCDc.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), arLineSetup.getCompanyId()));

		dc.add(Subqueries.propertyIn(ArLineSetup.FIELD.accountCombinationId.name(), aCDc));
		return getAll(dc).size() > 0;
	}

	@Override
	public Page<ArLineSetup> searchArLines(String searchCriteria, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.like(ArLineSetup.FIELD.name.name(), "%"+searchCriteria.trim()+"%"));
		dc.addOrder(Order.asc(ArLineSetup.FIELD.name.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public Collection<ArLineSetup> getArLines(int companyId, int serviceLeaseKeyId) {
		DetachedCriteria dc = getDCWithSLkeyIdAndActive(serviceLeaseKeyId);
		DetachedCriteria accountCombiCriteria = DetachedCriteria.forClass(AccountCombination.class);
		accountCombiCriteria.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
		accountCombiCriteria.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		dc.add(Subqueries.propertyIn(ArLineSetup.FIELD.accountCombinationId.name(), accountCombiCriteria));
		return getAll(dc);
	}

	@Override
	public List<ArLineSetup> getAllActiveARLineAccts() {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ArLineSetup.FIELD.active.name(), true));
		return getAll(dc);
	}

	@Override
	public ArLineSetup getArLineSetupByNumber(String arLineNumber,int serviceLeaseKeyId) {
		DetachedCriteria dc = getDCWithSLkeyIdAndActive(serviceLeaseKeyId);
		dc.add(Restrictions.eq(ArLineSetup.FIELD.arLineSetupNumber.name(), arLineNumber));
		return get(dc);
	}

	@Override
	public List<ArLineSetup> getArLineSetUps(String name, Integer arCustAcctId, Integer id, Boolean noLimit, 
			Boolean isExact, int serviceLeaseKeyId, Integer divisionId) {
		DetachedCriteria dc = getDCWithSLkeyIdAndActive(serviceLeaseKeyId);
		if (name != null && !name.trim().isEmpty() && id == null) {
			dc.add(Restrictions.like(ArLineSetup.FIELD.name.name(), "%" + name.trim() + "%"));
		} else if (name != null && !name.trim().isEmpty() && isExact) {
			dc.add(Restrictions.eq(ArLineSetup.FIELD.name.name(), name.trim()));
		} else if (id != null) {
			dc.add(Restrictions.eq(ArLineSetup.FIELD.id.name(), id));
		}
		if (arCustAcctId != null) {
			// Account Combination criteria
			DetachedCriteria accountCombinationDc = DetachedCriteria.forClass(AccountCombination.class);
			accountCombinationDc.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
			if (divisionId != null) {
				accountCombinationDc.add(Restrictions.eq(AccountCombination.FIELD.divisionId.name(), divisionId));
			}
			// AR Customer Account criteria
			DetachedCriteria arCustomerAccountDc = DetachedCriteria.forClass(ArCustomerAccount.class);
			arCustomerAccountDc.setProjection(Projections.property(ArCustomerAccount.FIELD.companyId.name()));
			arCustomerAccountDc.add(Restrictions.eq(ArCustomerAccount.FIELD.id.name(), arCustAcctId));
			accountCombinationDc.add(Subqueries.propertyIn(AccountCombination.FIELD.companyId.name(), arCustomerAccountDc));
			dc.add(Subqueries.propertyIn(ArLineSetup.FIELD.accountCombinationId.name(), accountCombinationDc));
		}
		dc.addOrder(Order.asc(ArLineSetup.FIELD.name.name()));
		if (noLimit == null)
			dc.getExecutableCriteria(getSession()).setMaxResults(10);
		return getAll(dc);
	}
 
	@Override
	public ArLineSetup getALSetupByNameAndCompany(String arLineSetupName, Integer companyId, Integer divisionId) {
		DetachedCriteria dc = getDetachedCriteria();
		if (arLineSetupName != null && !arLineSetupName.trim().isEmpty()) {
			dc.add(Restrictions.eq(ArLineSetup.FIELD.name.name(), arLineSetupName.trim()));
		}
		if (companyId != null) {
			dc.createAlias("accountCombination", "ac");
			dc.add(Restrictions.eq("ac.companyId", companyId));
			if (divisionId != null) {
				dc.add(Restrictions.eq("ac.divisionId", divisionId));
			}
		}
		return get(dc);
	}

	@Override
	public List<ArLineSetup> getArLineSetUps(Integer companyId, String name, Boolean isExact, int serviceLeaseKeyId) {
		DetachedCriteria dc = getDCWithSLkeyIdAndActive(serviceLeaseKeyId);
		//Company
		if (companyId != null && companyId.intValue() != -1){
			DetachedCriteria accountCombinationDc = DetachedCriteria.forClass(AccountCombination.class);
			accountCombinationDc.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
			accountCombinationDc.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
			dc.add(Subqueries.propertyIn(ArLineSetup.FIELD.accountCombinationId.name(), accountCombinationDc));
		}
		if (name != null && !name.trim().isEmpty())
			dc.add(Restrictions.like(ArLineSetup.FIELD.name.name(), "%" + name.trim() + "%"));
		else if (name != null && !name.trim().isEmpty() && isExact)
			dc.add(Restrictions.eq(ArLineSetup.FIELD.name.name(), name.trim()));

		dc.addOrder(Order.asc(ArLineSetup.FIELD.name.name()));
		return getAll(dc);	
	}

	@Override
	public Page<ArLineSetup> searchArLineSetup(String name, Integer companyId,
			SearchStatus status, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		if(!name.trim().isEmpty())
			dc.add(Restrictions.like(ArLineSetup.FIELD.name.name(), "%"+name.trim()+"%"));
		if(companyId != -1) {
			DetachedCriteria accountCombiCriteria = DetachedCriteria.forClass(AccountCombination.class);
			accountCombiCriteria.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
			accountCombiCriteria.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
			dc.add(Subqueries.propertyIn(ArLineSetup.FIELD.accountCombinationId.name(), accountCombiCriteria));
		}
		dc = DaoUtil.setSearchStatus(dc,ArLineSetup.FIELD.active.name(),status);
		dc.addOrder(Order.asc(ArLineSetup.FIELD.name.name()));
		return getAll(dc, pageSetting);
	}
}
