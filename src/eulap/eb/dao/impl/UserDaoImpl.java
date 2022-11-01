package eulap.eb.dao.impl;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.hibernate.criterion.Subqueries;
import org.springframework.orm.hibernate3.HibernateCallback;

import eulap.common.dao.BaseDao;
import eulap.common.dao.DaoUtil;
import eulap.common.dto.LoginCredential;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.common.util.SimpleCryptoUtil;
import eulap.common.util.StringFormatUtil;
import eulap.eb.dao.UserDao;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.User;
import eulap.eb.domain.hibernate.UserCompany;
import eulap.eb.domain.hibernate.UserLoginStatus;
import eulap.eb.service.LabelName2FieldName;
import eulap.eb.webservice.WebServiceCredential;

/**
 * Implements the data access layer of the {@link UserDao}

 *
 */
public class UserDaoImpl extends BaseDao<User> implements UserDao {
	@Override
	protected Class<User> getDomainClass() {
		return User.class;
	}

	@Override
	public Page<User> getUsers() {
		PageSetting pageSetting = new PageSetting(0);
		return getAll(pageSetting, null);
	}
	
	/**
	 * shows all user users regardless if active or not
	 */
	@Override
	public Page<User> getUsers(PageSetting pageSetting,
			String userSearchText, String searchCategory) {
		return getAll(pageSetting, Order.asc(UserField.username.name()),
				Restrictions.like(UserField.username.name(), "%"+ userSearchText +"%"));
	}

	@Override
	public boolean isUniqueUserName(String userName) {
		DetachedCriteria criteria = getDetachedCriteria();
		criteria.add(Restrictions.like(UserField.username.name(), userName));
		return  getAll(criteria).size() == 0;
	}
	
	@Override
	public boolean isValidUserPEncrypted(WebServiceCredential credential) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(UserField.username.name(), credential.getUserName()));
		dc.add(Restrictions.eq(UserField.password.name(), credential.getPassword()));
		dc.add(Restrictions.eq(UserField.active.name(), true));
		return getAll(dc).size() > 0;
	}

	@Override
	public boolean validateUser(LoginCredential loginCredential) {			
		DetachedCriteria dc = getDetachedCriteria();
		try {
			dc.add(Restrictions.eq(UserField.username.name(), loginCredential.getUserName()));
			dc.add(Restrictions.eq(UserField.password.name(), SimpleCryptoUtil.convertToSHA1(loginCredential.getPassword())));
			dc.add(Restrictions.eq(UserField.active.name(), true));
			return getAll(dc).size() > 0;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public User getUser(String userName, String password){
		DetachedCriteria dc = getDetachedCriteria();
		SimpleExpression userNameRestriction = Restrictions.eq(UserField.username.name(), userName);
		SimpleExpression passwordRestriction = Restrictions.eq(UserField.password.name(), password);
		dc.add(Restrictions.and(userNameRestriction, passwordRestriction));
		return get(dc);
	}
	
	@SuppressWarnings("incomplete-switch")
	@Override
	public Page<User> searchUsers(LabelName2FieldName ln2Fn,
			SearchStatus ss, String searchCriteria, PageSetting pageSetting) {
		
		Criterion activeCriteria = null;
		boolean isActive = false;
		switch (ss) {
			case Active:
				isActive = true; //fall through
			case Inactive:
				activeCriteria = Restrictions.eq(UserField.active.name(), isActive);
				break;
		}

		Criterion lnName2fieldCriteria = null;
		if (searchCriteria.equals("*")){
			
		} else {
			String[] splittedCriteria = searchCriteria.split(" ");
			for (UserField field : ln2Fn.getFields()) {
				Criterion c = Restrictions.like(field.name(), searchCriteria+"%");
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
			return getAll(pageSetting, Order.asc(UserField.username.name()));
		return getAll(pageSetting, Order.asc(UserField.username.name()), finalCriterion);
	}

	@Override
	public int getUserIdbyUserName(String userName) {
		String sql = "{call getUserIdByUserName(?)}";
		Session session = null;
		int userId = 0;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, userName);
			List<?> list = query.list();
			userId = list.get(0) != null ? (Integer) list.get(0) : 0;  
		} finally {
			if (session != null)
				session.close();
		}
		return userId;
	}

	@Override
	public Page<User> searchUser(String userName, String firstName,
			String lastName, Integer userGroupId, Integer positionId,
			Integer loginStatus, Integer status, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		boolean result = status == 1;
		boolean blockedStatus = loginStatus == 1;
		if(!userName.isEmpty())
			dc.add(Restrictions.like(User.FIELD.username.name(), "%" +userName+ "%"));
		if(!firstName.isEmpty())
			dc.add(Restrictions.like(User.FIELD.firstName.name(), "%" +firstName+ "%"));
		if(!lastName.isEmpty())
			dc.add(Restrictions.like(User.FIELD.lastName.name(), "%" +lastName+ "%"));
		if(userGroupId != -1)
			dc.add(Restrictions.eq(User.FIELD.userGroupId.name(), userGroupId));
		if(positionId != -1)
			dc.add(Restrictions.eq(User.FIELD.positionId.name(), positionId));
		if(status != -1)
			dc.add(Restrictions.eq(User.FIELD.active.name(), result));
		
		// User Login Status Criteria
		DetachedCriteria loginStatusCriteria = DetachedCriteria.forClass(UserLoginStatus.class);
		loginStatusCriteria.setProjection(Projections.property(UserLoginStatus.FIELD.userId.name()));
		if(loginStatus != -1)
			loginStatusCriteria.add(Restrictions.eq(UserLoginStatus.FIELD.blockUser.name(), blockedStatus));
		dc.add(Subqueries.propertyIn(User.FIELD.id.name(), loginStatusCriteria));
		dc.addOrder(Order.asc(User.FIELD.username.name()));
		return getAll(dc,pageSetting);
	}

	@Override
	public List<User> getAllUsers() {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(User.FIELD.active.name(), true));
		dc.addOrder(Order.asc(User.FIELD.firstName.name()));
		return getAll(dc);
	}

	@Override
	public List<User> getUsersByPosition(int positionId, Integer companyId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(User.FIELD.positionId.name(), positionId));
		dc.add(Restrictions.eq(User.FIELD.active.name(), true));
		if(companyId != null) {
			DetachedCriteria userCompanyDc = DetachedCriteria.forClass(UserCompany.class);
			userCompanyDc.setProjection(Projections.property(UserCompany.FIELD.userId.name()));
			userCompanyDc.add(Restrictions.eq(UserCompany.FIELD.companyId.name(), companyId));
			dc.add(Subqueries.propertyIn(User.FIELD.id.name(), userCompanyDc));
		}
		return getAll(dc);
	}

	@Override
	public List<User> getUsersByName(String name) {
		DetachedCriteria dc = getDetachedCriteria();
		// Add filtering criteria for user by name if name is not empty.
		if (name != null && !name.trim().isEmpty()) {
			// Split the name whether the space between is more than 1.
			String tmpName[] = name.split("\\s{1,}");
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
			dc.add(criterion);
		}
		dc.add(Restrictions.eq(User.FIELD.active.name(), true));
		dc.getExecutableCriteria(getSession()).setMaxResults(10);
		return getAll(dc);
	}

	@Override
	public Page<User> getUsersWithCompanies(String userName,
			String companyName, SearchStatus searchStatus,
			PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.like(User.FIELD.username.name(), "%" + userName.trim() + "%"));

		// User Company subquery.
		DetachedCriteria uCDc = DetachedCriteria.forClass(UserCompany.class);
		uCDc = DaoUtil.setSearchStatus(uCDc, UserCompany.FIELD.active.name(), searchStatus);
		uCDc.setProjection(Projections.property(UserCompany.FIELD.userId.name()));
		if (companyName != null && !companyName.trim().isEmpty()) {
			uCDc.createAlias("company", "c").add(Restrictions.like("c.name", "%" + companyName.trim() + "%"));
		}
		dc.add(Subqueries.propertyIn(User.FIELD.id.name(), uCDc));
		dc.addOrder(Order.asc(User.FIELD.username.name()));
		return getAll(dc, pageSetting );
	}

	@Override
	public List<User> getUsersByUsername(final String userName,
			final boolean isActiveOnly,
			final Integer limit, final Integer userId) {

		//DetachedCriteria dc = getDetachedCriteria();
		HibernateCallback<List<User>> userHCB = new HibernateCallback<List<User>>() {
			@Override
			public List<User> doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria userCriteria = session.createCriteria(User.class);
				if (userName != null && !userName.trim().isEmpty()) {
					userCriteria.add(Restrictions.like(User.FIELD.username.name(), "%" + userName.trim() + "%"));

				}
				if (isActiveOnly) {
					userCriteria.add(Restrictions.eq(User.FIELD.active.name(), true));
				}
				DetachedCriteria userCompanyDC = DetachedCriteria.forClass(UserCompany.class);
				userCompanyDC.setProjection(Projections.property(UserCompany.FIELD.userId.name()));
				if(userId != null && userId != 0){
					userCompanyDC.add(Restrictions.ne(UserCompany.FIELD.userId.name(), userId));
				}
				userCriteria.add(Subqueries.propertyNotIn(User.FIELD.id.name(), userCompanyDC));
				if (limit != null) {
					userCriteria.setMaxResults(limit);
				}
				List<User> users = getAllByCriteria(userCriteria);
				for (User user : users) {
					getHibernateTemplate().initialize(user.getUserCompanies());
				}
				return users;
			}
		};
		return getHibernateTemplate().execute(userHCB);
	}

	@Override
	public User getUserByUserCompany(int userCompanyId) {
		DetachedCriteria dc = getDetachedCriteria();
		DetachedCriteria userCompanyDC = DetachedCriteria.forClass(UserCompany.class);
		userCompanyDC.setProjection(Projections.property(UserCompany.FIELD.userId.name()));
		userCompanyDC.add(Restrictions.eq(UserCompany.FIELD.id.name(), userCompanyId));
		dc.add(Subqueries.propertyIn(User.FIELD.id.name(), userCompanyDC));
		return get(dc);
	}

	@Override
	public User getUserByName(String name) {
		DetachedCriteria dc = getDetachedCriteria();
		if(name != null) {
			dc.add(Restrictions.sqlRestriction("CONCAT(FIRST_NAME, ' ' , LAST_NAME) = ?",
					name, Hibernate.STRING));
		}
		return get(dc);
	}

	@Override
	public List<User> getUsersByEbObjectId(Integer ebObjectId) {
		DetachedCriteria dc = getDetachedCriteria();

		DetachedCriteria obj2ObjDc = DetachedCriteria.forClass(ObjectToObject.class);
		obj2ObjDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		obj2ObjDc.add(Restrictions.eq(ObjectToObject.FIELDS.fromObjectId.name(), ebObjectId));

		dc.add(Subqueries.propertyIn(User.FIELD.ebObjectId.name(), obj2ObjDc));
		dc.addOrder(Order.asc(User.FIELD.firstName.name()));
		return getAll(dc);
	}

	@Override
	public List<User> getUsersByNameAndId(String userName, boolean isActiveOnly, Integer limit, Integer userId) {
		HibernateCallback<List<User>> userHCB = new HibernateCallback<List<User>>() {
			@Override
			public List<User> doInHibernate(Session session)
					throws HibernateException, SQLException {
				Criteria userCriteria = session.createCriteria(User.class);
				if (userName != null && !userName.trim().isEmpty()) {
					String tmpName[] = userName.split("\\s{1,}");
					Criterion criterion = null;
					for (String tmp : tmpName) {
						Criterion fAndMNameCriterion = Restrictions.or(Restrictions.like(
							User.FIELD.firstName.name(), "%" + tmp + "%"), Restrictions.like(
							User.FIELD.middleName.name(), "%" + tmp + "%"));
						Criterion nameCriterion = Restrictions.or(fAndMNameCriterion, 
								Restrictions.like(User.FIELD.lastName.name(), "%" + tmp + "%"));
						criterion = (criterion != null) ? Restrictions.and(criterion, nameCriterion)
								: nameCriterion;
					}
					userCriteria.add(criterion);
				}
				if (isActiveOnly) {
					userCriteria.add(Restrictions.eq(User.FIELD.active.name(), true));
				}
				DetachedCriteria userCompanyDC = DetachedCriteria.forClass(UserCompany.class);
				userCompanyDC.setProjection(Projections.property(UserCompany.FIELD.userId.name()));
				if(userId != null && userId != 0){
					userCompanyDC.add(Restrictions.ne(UserCompany.FIELD.userId.name(), userId));
				}
				userCriteria.add(Subqueries.propertyNotIn(User.FIELD.id.name(), userCompanyDC));
				if (limit != null) {
					userCriteria.setMaxResults(limit);
				}
				List<User> users = getAllByCriteria(userCriteria);
				for (User user : users) {
					getHibernateTemplate().initialize(user.getUserCompanies());
				}
				return users;
			}
		};
		return getHibernateTemplate().execute(userHCB);
	}

	@Override
	public boolean isUniqueName(String firstName, String lastName, Integer userId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(User.FIELD.firstName.name(), StringFormatUtil.removeExtraWhiteSpaces(firstName)));
		dc.add(Restrictions.eq(User.FIELD.lastName.name(), StringFormatUtil.removeExtraWhiteSpaces(lastName)));
		if (userId != null && userId > 0) {
			dc.add(Restrictions.ne(User.FIELD.id.name(), userId));
		}
		return getAll(dc).size() == 0;
	}
}