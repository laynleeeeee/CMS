package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.common.dao.DaoUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.ItemCategoryAcctSetupDao;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.ItemCategoryAccountSetup;

/**
 * DAO Implementation of {@link ItemCategoryAcctSetupDao}

 */
public class ItemCategoryAcctSetupDaoImpl extends BaseDao<ItemCategoryAccountSetup> implements ItemCategoryAcctSetupDao {

	@Override
	protected Class<ItemCategoryAccountSetup> getDomainClass() {
		return ItemCategoryAccountSetup.class;
	}

	@Override
	public Page<ItemCategoryAccountSetup> getItemCategory(int companyId,
			int itemCatId, SearchStatus searchStatus, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();

		if(companyId!=-1){
			dc.add(Restrictions.eq(ItemCategoryAccountSetup.FIELD.companyId.name(), companyId));
		}

		if(itemCatId!=-1){
			dc.add(Restrictions.eq(ItemCategoryAccountSetup.FIELD.itemCategoryId.name(), itemCatId));
		}
		dc = DaoUtil.setSearchStatus(dc, ItemCategoryAccountSetup.FIELD.active.name(), searchStatus);
		dc.addOrder(Order.asc(ItemCategoryAccountSetup.FIELD.itemCategoryId.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public boolean isUniqueItemCatAcctSetup(
			ItemCategoryAccountSetup accountSetup) {
		DetachedCriteria dc = getDetachedCriteria();
		SimpleExpression id = Restrictions.ne(ItemCategoryAccountSetup.FIELD.id.name(), accountSetup.getId());
		SimpleExpression itemCatId = Restrictions.eq(ItemCategoryAccountSetup.FIELD.itemCategoryId.name(), accountSetup.getItemCategoryId());
		SimpleExpression companyId = Restrictions.eq(ItemCategoryAccountSetup.FIELD.companyId.name(), accountSetup.getCompanyId());
		
		LogicalExpression accountCombi = Restrictions.and(itemCatId, companyId);

		if(accountSetup.getId() == 0){
			dc.add(accountCombi);
		}
		else {
			dc.add(Restrictions.and(id, accountCombi));
		}
		return getAll(dc).isEmpty();
	}

	@Override
	public List<ItemCategoryAccountSetup> getItemcatAcctSetups(Integer itemCategoryId, Integer companyId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ItemCategoryAccountSetup.FIELD.itemCategoryId.name(), itemCategoryId));
		if(companyId != null){
			dc.add(Restrictions.eq(ItemCategoryAccountSetup.FIELD.companyId.name(), companyId));
		}
		return getAll(dc);
	}

	@Override
	public ItemCategoryAccountSetup getAcctCombinations(Integer companyId, Integer divisionId) {
		String sql = "SELECT AC.ACCOUNT_COMBINATION_ID, A.ACCOUNT_NAME, A.ACCOUNT_ID "
				+ "FROM ACCOUNT_COMBINATION AC "
				+ "INNER JOIN ACCOUNT A ON A.ACCOUNT_ID = AC.ACCOUNT_ID "
				+ "WHERE A.ACCOUNT_ID IN (343, 347, 348, 341, 349) "
				+ "AND DIVISION_ID = ? "
				+ "AND COMPANY_ID = ? ";
		Session session = null;
		ItemCategoryAccountSetup categorySetup = new ItemCategoryAccountSetup();
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, divisionId);
			query.setParameter(1, companyId);
			List<Object[]> list = query.list();
			if(list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					Integer acctCombiId = (Integer) row[0];
					String accountName = (String) row[1];
					Integer acctId = (Integer) row[2];
					if(acctId == ItemCategoryAccountSetup.COST_OF_SALES) {
						categorySetup.setCostAccount(acctCombiId);
						categorySetup.setCostAccountName(accountName);
					} else if(acctId == ItemCategoryAccountSetup.MERCHANDISE_INVENTORY) {
						categorySetup.setInventoryAccount(acctCombiId);
						categorySetup.setInventoryAccountName(accountName);
					} else if(acctId == ItemCategoryAccountSetup.SALES) {
						categorySetup.setSalesAccount(acctCombiId);
						categorySetup.setSalesAccountName(accountName);
					} else if(acctId == ItemCategoryAccountSetup.SALES_DISCOUNT) {
						categorySetup.setSalesDiscountAccount(acctCombiId);
						categorySetup.setSalesDiscountAccountName(accountName);
					} else if(acctId == ItemCategoryAccountSetup.SALES_RETURN) {
						categorySetup.setSalesReturnAccount(acctCombiId);
						categorySetup.setSalesReturnAccountName(accountName);
					}
				}
				categorySetup.setCompanyId(companyId);
				categorySetup.setDivisionId(divisionId);
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return categorySetup;
	}

	@Override
	public ItemCategoryAccountSetup getItemCategoryAcctByCompanyDiv(Integer itemCategoryId, Integer companyId, Integer divisionId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ItemCategoryAccountSetup.FIELD.itemCategoryId.name(), itemCategoryId));
		DetachedCriteria acDc = DetachedCriteria.forClass(AccountCombination.class);
		acDc.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
		acDc.add(Restrictions.eq(AccountCombination.FIELD.companyId.name(), companyId));
		acDc.add(Restrictions.eq(AccountCombination.FIELD.divisionId.name(), divisionId));
		acDc.add(Subqueries.propertyIn(ItemCategoryAccountSetup.FIELD.inventoryAccount.name(), dc));
		return get(dc);
	}
}
