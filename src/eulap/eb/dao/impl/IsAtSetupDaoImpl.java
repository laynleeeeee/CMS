package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.eb.dao.IsAtSetupDao;
import eulap.eb.domain.hibernate.Account;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.AccountType;
import eulap.eb.domain.hibernate.IsAtSetup;

/**
 * Implementing class of {@link IsAtSetupDao}

 *
 */
public class IsAtSetupDaoImpl extends BaseDao<IsAtSetup> implements IsAtSetupDao {

	@Override
	protected Class<IsAtSetup> getDomainClass() {
		return IsAtSetup.class;
	}

	@Override
	public List<IsAtSetup> getIsAtSetups(Integer isClassSetupId, Integer companyId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(IsAtSetup.FIELD.isClassSetupId.name(), isClassSetupId));
		
		// Account combination criteria subquery
		DetachedCriteria acctCombiCriteria = DetachedCriteria.forClass(AccountCombination.class);
		acctCombiCriteria.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		acctCombiCriteria.setProjection(Projections.property(AccountCombination.FIELD.accountId.name()));
		
		// Account criteria subquery
		DetachedCriteria accountCriteria = DetachedCriteria.forClass(Account.class);
		accountCriteria.setProjection(Projections.property(Account.FIELD.accountTypeId.name()));
		accountCriteria.add(Subqueries.propertyIn(Account.FIELD.id.name(), acctCombiCriteria));
		
		// Account type criteria subquery
		DetachedCriteria atCriteria = DetachedCriteria.forClass(AccountType.class);
		atCriteria.setProjection(Projections.property(AccountType.FIELD.id.name()));
		atCriteria.add(Subqueries.propertyIn(AccountType.FIELD.id.name(), accountCriteria));
		
		dc.add(Subqueries.propertyIn(IsAtSetup.FIELD.accountTypeId.name(), atCriteria));
		dc.addOrder(Order.desc(IsAtSetup.FIELD.sequenceOrder.name()));

		return getAll(dc);
	}

}
