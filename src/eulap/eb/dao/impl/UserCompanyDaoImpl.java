package eulap.eb.dao.impl;

import java.util.List;

import org.hibernate.criterion.Criterion;
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
import eulap.eb.dao.UserCompanyDao;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.UserCompany;

/**
 * Implements the data access layer of the {@link UserCompanyDao}

 *
 */
public class UserCompanyDaoImpl extends BaseDao<UserCompany> implements UserCompanyDao{
	@Override
	protected Class<UserCompany> getDomainClass() {
		return UserCompany.class;
	}

	@Override
	public List<UserCompany> getUserCompanies(int userId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(UserCompany.FIELD.userId.name(), userId));
		dc.createAlias("company", "c");
		dc.addOrder(Order.asc("c.name"));
		return getAll(dc);
	}

	@Override
	public Page<UserCompany> getAllUserCompanies(String userName,
			String companyName, SearchStatus searchStatus, PageSetting pageNumber) {
		DetachedCriteria userCompanyDc = getDetachedCriteria();

		// Add filtering criteria for teacher by name if name is not empty.
		if(userName != null && !userName.trim().isEmpty()){
			// Teacher (User) sub query.
			DetachedCriteria userDC = DetachedCriteria.forClass(User.class);
			userDC.setProjection(Projections.property(User.FIELD.id.name()));
			// Split the name whether the space between is more than 1.
			String tmpName[] = userName.split("\\s{1,}");
			Criterion criterion = null;
			for (String tmp : tmpName) {
				// Create a logical expression that will compare the first name and middle name.
				Criterion fAndMNameCriterion = Restrictions.or(Restrictions.like(
					User.FIELD.firstName.name(), "%" + tmp + "%"), Restrictions.like(
					User.FIELD.middleName.name(), "%" + tmp + "%"));
				// Create a logical expression that will compare first and middle name expression 
				// with last name.
				Criterion nameCriterion = Restrictions.or(fAndMNameCriterion, 
						Restrictions.like(User.FIELD.lastName.name(), "%" + tmp + "%"));
				// If criterion is not null, add name expression.
				criterion = (criterion != null) ? Restrictions.and(criterion, nameCriterion)
						: nameCriterion;
			}
			userDC.add(criterion);
			userCompanyDc.add(Subqueries.propertyIn(UserCompany.FIELD.userId.name(), userDC));
		}
		userCompanyDc = DaoUtil.setSearchStatus(userCompanyDc, UserCompany.FIELD.active.name(), searchStatus);
		userCompanyDc.createAlias("user", "u");
		userCompanyDc.addOrder(Order.asc("u.firstName"));
		userCompanyDc.addOrder(Order.asc("u.middleName"));
		userCompanyDc.addOrder(Order.asc("u.lastName"));
		return getAll(userCompanyDc, pageNumber);
	}

	@Override
	public List<UserCompany> getUCByUserAndCompany(int userId,
			String companyName) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(UserCompany.FIELD.userId.name(), userId));
		if (companyName != null && !companyName.trim().isEmpty()) {
			dc.createAlias("company", "c").add(Restrictions.like("c.name", "%" + companyName.trim() + "%"));			
		}
		return getAll(dc);
	}

}
