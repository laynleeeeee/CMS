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
import eulap.eb.dao.ItemCategoryDao;
import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.Item;
import eulap.eb.domain.hibernate.ItemCategory;
import eulap.eb.domain.hibernate.ItemCategoryAccountSetup;
import eulap.eb.domain.hibernate.ItemSrp;
import eulap.eb.domain.hibernate.User;

/**
 * The implementation class of {@link ItemCategoryDao}

 *
 */
public class ItemCategoryDaoImpl extends BaseDao<ItemCategory> implements ItemCategoryDao{
	@Override
	protected Class<ItemCategory> getDomainClass() {
		return ItemCategory.class;
	}
	
	@Override
	public List<ItemCategory> getItemCategories() {
		DetachedCriteria dc = getDetachedCriteria();
		return getAll(dc);
	}

	@Override
	public ItemCategory getItemCategory(int itemCategoryId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ItemCategory.FIELD.id.name(), itemCategoryId));
		return get(dc);
	}

	@Override
	public boolean isUniqueItemCategory(ItemCategory itemCategory) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ItemCategory.FIELD.name.name(), itemCategory.getName().trim()));
		if (itemCategory.getId() != 0)
			dc.add(Restrictions.not(Restrictions.eq(ItemCategory.FIELD.id.name(), itemCategory.getId())));
		return getAll(dc).size() < 1;
	}

	@Override
	public Page<ItemCategory> getAllItemCategories(PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.addOrder(Order.asc(ItemCategory.FIELD.name.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public Page<ItemCategory> searchItemCategories(String name, SearchStatus status, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.like(ItemCategory.FIELD.name.name(), "%"+name+"%"));
		dc = DaoUtil.setSearchStatus(dc, ItemCategory.FIELD.active.name(), status);
		dc.addOrder(Order.asc(ItemCategory.FIELD.name.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public List<ItemCategory> getActiveItemCategories() {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ItemCategory.FIELD.active.name(), true));
		dc.addOrder(Order.asc(ItemCategory.FIELD.name.name()));
		return getAll(dc);
	}

	@Override
	public List<ItemCategory> getItemCategoriesByCompany(String name, int companyId, Integer divisionId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ItemCategory.FIELD.active.name(), true));
		if (name != null && !name.trim().isEmpty()) {
			dc.add(Restrictions.like(ItemCategory.FIELD.name.name(), "%" + name.trim() + "%"));
		}

		DetachedCriteria icasDc = DetachedCriteria.forClass(ItemCategoryAccountSetup.class);
		icasDc.setProjection(Projections.property(ItemCategoryAccountSetup.FIELD.itemCategoryId.name()));
		if(companyId != -1) {
			icasDc.add(Restrictions.eq(ItemCategoryAccountSetup.FIELD.companyId.name(), companyId));
		}

		if(divisionId != null && divisionId != -1) {
			DetachedCriteria accountCombination = DetachedCriteria.forClass(AccountCombination.class);
			accountCombination.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
			accountCombination.add(Restrictions.eq(AccountCombination.FIELD.divisionId.name(), divisionId));
			icasDc.add(Subqueries.propertyIn(ItemCategoryAccountSetup.FIELD.costAccount.name(), accountCombination));
		}

		dc.add(Subqueries.propertyIn(ItemCategory.FIELD.id.name(), icasDc));
		dc.getExecutableCriteria(getSession()).setMaxResults(10);
		dc.addOrder(Order.asc(ItemCategory.FIELD.name.name()));
		return getAll(dc);
	}

	@Override
	public List<ItemCategory> getItemCategoriesByCompany(String name, User user) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ItemCategory.FIELD.active.name(), true));
		if (name != null && !name.trim().isEmpty()) {
			dc.add(Restrictions.like(ItemCategory.FIELD.name.name(), "%" + name.trim() + "%"));
		}
		if(user != null) {
			// Item SRP subquery
			DetachedCriteria iCDc = DetachedCriteria.forClass(ItemSrp.class);
			iCDc.setProjection(Projections.property(ItemSrp.FIELD.itemId.name()));
			addUserCompany(iCDc, user);

			// Item subquery
			DetachedCriteria itemDc = DetachedCriteria.forClass(Item.class);
			itemDc.setProjection(Projections.property(Item.FIELD.itemCategoryId.name()));
			itemDc.add(Subqueries.propertyIn(Item.FIELD.id.name(), iCDc));
			dc.add(Subqueries.propertyIn(ItemCategory.FIELD.id.name(), itemDc));
		}

		dc.getExecutableCriteria(getSession()).setMaxResults(10);
		dc.addOrder(Order.asc(ItemCategory.FIELD.name.name()));
		return getAll(dc);
	}

	@Override
	public ItemCategory getItemCategoryByName(String name, Integer companyId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ItemCategory.FIELD.active.name(), true));
		if (name != null && !name.trim().isEmpty()) {
			dc.add(Restrictions.eq(ItemCategory.FIELD.name.name(), name.trim()));
		}
		if (companyId != null && companyId > 0) {
			// Item SRP subquery
			DetachedCriteria iCDc = DetachedCriteria.forClass(ItemSrp.class);
			iCDc.setProjection(Projections.property(ItemSrp.FIELD.itemId.name()));
			iCDc.add(Restrictions.eq(ItemSrp.FIELD.companyId.name(), companyId));

			// Item subquery
			DetachedCriteria itemDc = DetachedCriteria.forClass(Item.class);
			itemDc.setProjection(Projections.property(Item.FIELD.itemCategoryId.name()));
			itemDc.add(Subqueries.propertyIn(Item.FIELD.id.name(), iCDc));
			dc.add(Subqueries.propertyIn(ItemCategory.FIELD.id.name(), itemDc));
		}
		dc.addOrder(Order.asc(ItemCategory.FIELD.name.name()));
		return get(dc);
	}

	@Override
	public ItemCategory getItemCategoryByName(String name, Integer companyId, Integer divisionId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(ItemCategory.FIELD.active.name(), true));
		if (name != null && !name.trim().isEmpty()) {
			dc.add(Restrictions.eq(ItemCategory.FIELD.name.name(), name.trim()));
		}

		DetachedCriteria icasDc = DetachedCriteria.forClass(ItemCategoryAccountSetup.class);
		icasDc.setProjection(Projections.property(ItemCategoryAccountSetup.FIELD.itemCategoryId.name()));
		if(companyId != -1) {
			icasDc.add(Restrictions.eq(ItemCategoryAccountSetup.FIELD.companyId.name(), companyId));
		}

		if(divisionId != null && divisionId != -1) {
			DetachedCriteria accountCombination = DetachedCriteria.forClass(AccountCombination.class);
			accountCombination.setProjection(Projections.property(AccountCombination.FIELD.id.name()));
			accountCombination.add(Restrictions.eq(AccountCombination.FIELD.divisionId.name(), divisionId));
			icasDc.add(Subqueries.propertyIn(ItemCategoryAccountSetup.FIELD.costAccount.name(), accountCombination));
		}
		dc.add(Subqueries.propertyIn(ItemCategory.FIELD.id.name(), icasDc));
		return get(dc);
	}
}
