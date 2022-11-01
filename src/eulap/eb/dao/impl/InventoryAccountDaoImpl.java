package eulap.eb.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.dao.InventoryAccountDao;
import eulap.eb.domain.hibernate.InventoryAccount;

/**
 * DAO Implementation of {@link InventoryAccountDao}

 */
public class InventoryAccountDaoImpl extends BaseDao<InventoryAccount> implements InventoryAccountDao{

	@Override
	protected Class<InventoryAccount> getDomainClass() {
		return InventoryAccount.class;
	}

	@Override
	public Page<InventoryAccount> searchInventoryAccts(int companyId, int statusId, PageSetting pageSetting) {
		DetachedCriteria invAcctCriteria = getDetachedCriteria();
		if(companyId != -1)
			invAcctCriteria.add(Restrictions.eq(InventoryAccount.FIELD.companyId.name(), companyId));
		if(statusId != -1) {
			boolean isActive = statusId == 1 ? true : false;
			invAcctCriteria.add(Restrictions.eq(InventoryAccount.FIELD.active.name(), isActive));
		}
		invAcctCriteria.createAlias("company", "c");
		invAcctCriteria.addOrder(Order.asc("c.name"));
		return getAll(invAcctCriteria, pageSetting);
	}

	@Override
	public InventoryAccount getInvAcctByCompanyId(int companyId, int inventoryAcctId) {
		DetachedCriteria invAcctCriteria = getDetachedCriteria();
		invAcctCriteria.add(Restrictions.eq(InventoryAccount.FIELD.companyId.name(), companyId));
		invAcctCriteria.add(Restrictions.eq(InventoryAccount.FIELD.active.name(), true));
		if(inventoryAcctId != 0) {
			invAcctCriteria.add(Restrictions.ne(InventoryAccount.FIELD.id.name(), inventoryAcctId));
		}
		return get(invAcctCriteria);
	}
}
