package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import eulap.common.dao.BaseDao;
import eulap.common.domain.Domain;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.UserGroupDao;
import eulap.eb.domain.hibernate.UserGroup;
import eulap.eb.service.LabelName2FieldNameUG;

/**
 * Implements the data access layer of the {@link UserGroupDao}

 *
 */
public class UserGroupDaoImpl extends BaseDao<UserGroup> implements UserGroupDao {
	private enum FIELDS {name, description,  active};
	
	@Override
	protected Class<UserGroup> getDomainClass() {
		return UserGroup.class;
	}
	
	@Override
	public Page<UserGroup> getUserGroups(PageSetting pageSetting,
			String userGroupSearchText, String searchCategory, boolean activeOnly) {
		
		String searchField = "";
		
		if (searchCategory.trim().equals("name")) searchField = "name";
		if (searchCategory.trim().equals("description")) searchField = "description";
		
		return getAll(pageSetting, Order.asc(FIELDS.name.name()),
				Restrictions.like(searchField, "%" + userGroupSearchText + "%"),
				Restrictions.eq(FIELDS.active.name(), activeOnly));
	}
	
	@Override
	public Page<UserGroup> getUserGroups() {
		PageSetting pageSetting = new PageSetting(0);
		return getAll(pageSetting, null);
	}
	
	/**
	 * shows all user groups regardless if active or not
	 */	
	public Page<UserGroup> getUserGroups(PageSetting pageSetting,
			String userGroupSearchText, String searchCategory) {
		
		String searchField = "";
		
		if (searchCategory.trim().equals("name")) searchField = "name";
		if (searchCategory.trim().equals("description")) searchField = "description";
		
		return getAll(pageSetting, Order.asc(FIELDS.name.name()),
				Restrictions.like(searchField , "%"+ userGroupSearchText +"%"));
	}
	
	@Override
	public boolean isUniqueUserGroupName(String userGroupName) {
		DetachedCriteria criteria = getDetachedCriteria();
		criteria.add(Restrictions.like(FIELDS.name.name(), userGroupName));
		return  getAll(criteria).size() == 0;
	}
	
	@Override
	public Page<UserGroup> searchUserGroups(LabelName2FieldNameUG ln2FnC,
			SearchStatus ss, String searchCriteria, PageSetting pageSetting) {
		
		Criterion activeCriteria = null;
		boolean isActive = false;
		if (ss.equals(SearchStatus.Active))
			isActive = true; //fall through

		activeCriteria = Restrictions.eq(UserGroupField.active.name(), isActive);
		
		Criterion lnName2fieldCriteria = null;
		if (searchCriteria.equals("*")){
			
		} else {
			String[] splittedCriteria = searchCriteria.split(" ");
			for (UserGroupField field : ln2FnC.getFields()) {
				Criterion c = Restrictions.like(field.name(), "%"+searchCriteria+"%");
				if (lnName2fieldCriteria == null)
					lnName2fieldCriteria = c;
				else 
					lnName2fieldCriteria = Restrictions.or(lnName2fieldCriteria, c);
				// Support search criteria with equal value with ln2fn fields.	
				for (String str : splittedCriteria){
					Criterion cLike = Restrictions.like(field.name(), str);
					lnName2fieldCriteria = Restrictions.or(lnName2fieldCriteria, cLike);
				}
			}
		}	
		
		Criterion finalCriterion = null;
		
		if (activeCriteria != null && lnName2fieldCriteria != null)
			finalCriterion = Restrictions.and(lnName2fieldCriteria, activeCriteria);
		else if (lnName2fieldCriteria != null)
			finalCriterion = lnName2fieldCriteria;
		else if (activeCriteria != null)
			finalCriterion = activeCriteria;
		if (finalCriterion == null) // ALL and empty search criteria. 
			return getAll(pageSetting, Order.asc(UserGroupField.name.name()));
		return getAll(pageSetting, Order.asc(UserGroupField.name.name()), finalCriterion);
	}

	@Override
	public int getUserGroupIdbyName(String name) {
		String sql = "SELECT USER_GROUP_ID FROM USER_GROUP WHERE NAME = ? ";
		Session session = null;
		int userGroupId = 0;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, name);
			List<?> list = query.list();
			userGroupId = list.get(0) != null ? (Integer) list.get(0) : 0;  
		} finally {
			if (session != null)
				session.close();
		}
		return userGroupId;
	}

	@Override
	public UserGroup getUserGroupByName(String name) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(UserGroup.FIELD.name.name(), name));
		dc.add(Restrictions.eq(UserGroup.FIELD.active.name(), true));
		return get(dc);
	}

	@Override
	public List<UserGroup> getUserGroup(String name) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.like(UserGroup.FIELD.name.name(), "%" +name+ "%"));
		dc.add(Restrictions.eq(UserGroup.FIELD.active.name(), true));
		dc.addOrder(Order.asc(UserGroup.FIELD.name.name()));
		return getAll(dc);
	}@Override
	public void batchSave(List<Domain> entities) {
		// TODO Auto-generated method stub
		super.batchSave(entities);
	}

	@Override
	public Page<UserGroup> getUserGroups(String groupName, String description,
			int status, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		boolean result = status == 1 ? true : false;
		if (groupName != null && !groupName.isEmpty())
			dc.add(Restrictions.like(UserGroup.FIELD.name.name(), "%" + groupName + "%"));
		if (description != null && !description.isEmpty())
			dc.add(Restrictions.like(UserGroup.FIELD.description.name(), "%" + description + "%"));
		if(status != -1)
			dc.add(Restrictions.eq(UserGroup.FIELD.active.name(), result));
		dc.addOrder(Order.asc(UserGroup.FIELD.name.name()));
		return getAll(dc, pageSetting);
	}

	@Override
	public List<UserGroup> getActiveUserGroups() {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(UserGroup.FIELD.active.name(), true));
		dc.addOrder(Order.asc(UserGroup.FIELD.name.name()));
		return getAll(dc);
	}
}