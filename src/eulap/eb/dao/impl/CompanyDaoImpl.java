package eulap.eb.dao.impl;

import java.util.Collection;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.dao.BaseDao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.CompanyDao;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.User;
import eulap.eb.service.LabelName2FieldNameCompany;

/**
 * Implements the data access layer of the {@link CompanyDao}


 */
public class CompanyDaoImpl extends BaseDao<Company> implements CompanyDao{
	private enum FIELDS {id, name, address, contactNumber, emailAddress, active};

	@Override
	protected Class<Company> getDomainClass() {
		return Company.class;
	}
	
	@Override
	public Page<Company> getCompanies(PageSetting pageSetting,
			String companySearchText, String searchCategory, boolean activeOnly) {
		
		String searchField = "";
		
		if (searchCategory.trim().equals("name")) searchField = "name";
		if (searchCategory.trim().equals("emailAddress")) searchField = "emailAddress";
				
		return getAll(pageSetting, Order.asc(FIELDS.name.name()),
				Restrictions.like(searchField, "%"+ companySearchText +"%"),
				Restrictions.eq(FIELDS.active.name(), activeOnly));
	}

	@Override
	public Page<Company> getCompanies() {
		DetachedCriteria dc = getDetachedCriteria();
		dc.addOrder(Order.asc(Company.Field.name.name()));
		return getAll(dc, new PageSetting(1));
	}
	
	/**
	 * shows all companies regardless if active or not
	 */
	@Override
	public Page<Company> getCompanies(PageSetting pageSetting,
			String companySearchText, String searchCategory) {
		
		String searchField = "";
		
		if (searchCategory.trim().equals("name")) searchField = "name";
		if (searchCategory.trim().equals("emailAddress")) searchField = "emailAddress";
		
		return getAll(pageSetting, Order.asc(FIELDS.name.name()),
				Restrictions.like(searchField, "%"+companySearchText+"%"));
	}
	
	@Override
	public boolean isUniqueCompanyName(String companyName) {
		DetachedCriteria criteria = getDetachedCriteria();
		criteria.add(Restrictions.like(FIELDS.name.name(), companyName));
		return  getAll(criteria).size() == 0;
	}

	@Override
	public Collection<Company> getCompanies(Collection<Integer> toBeExcluded) {
		DetachedCriteria criteria = getDetachedCriteria ();
		for (Integer companyId : toBeExcluded)
			criteria.add(Restrictions.not(Restrictions.eq(FIELDS.id.name(), companyId)));
		criteria.addOrder(Order.asc(FIELDS.name.name()));
		return getAll(criteria);
	}
	
	@SuppressWarnings("incomplete-switch")
	@Override
	public Page<Company> searchCompanies(LabelName2FieldNameCompany ln2FnC,
			SearchStatus ss, String searchCriteria, PageSetting pageSetting) {
		
		Criterion activeCriteria = null;
		boolean isActive = false;
		switch (ss) {
			case Active:
				isActive = true; //fall through
			case Inactive:
				activeCriteria = Restrictions.eq(CompanyField.active.name(), isActive);
				break;
		}

		Criterion lnName2fieldCriteria = null;
		if (searchCriteria.equals("*")){
			
		} else {
			String[] splittedCriteria = searchCriteria.split(" ");
			for (CompanyField field : ln2FnC.getFields()) {
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
			return getAll(pageSetting, Order.asc(CompanyField.name.name()));
		return getAll(pageSetting, Order.asc(CompanyField.name.name()), finalCriterion);
	}

	@Override
	public int getCompanyIdbyName(String name) {
		String sql = "SELECT COMPANY_ID FROM COMPANY WHERE NAME = ?";
		Session session = null;
		int companyId = 0;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, name);
			List<?> list = query.list();
			companyId = list.size() > 0 && list.get(0) != null ? (Integer) list.get(0) : 0; 
		} finally {
			if (session != null)
				session.close();
		}
		return companyId;
	}
	
	@Override
	public String getCompanyName(int companyId) {
		String sql = "SELECT NAME FROM COMPANY WHERE COMPANY_ID = ? " ;
		Session session = null;
		String companyName = "";
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(sql);
			query.setParameter(0, companyId);
			List<?> list = query.list();
			companyName = !list.get(0).equals("") ? (String) list.get(0) : "";
		} finally {
			if (session != null)
				session.close();
		}
		return companyName;
	}

	@Override
	public Page<Company> getCompanyList(String companyNumber, String name,
			String tin, User user, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		if (!companyNumber.isEmpty())
			dc.add(Restrictions.like(Company.Field.companyNumber.name(), "%" + companyNumber + "%"));
		if (!name.isEmpty())
			dc.add(Restrictions.like(Company.Field.name.name(), "%" + name + "%"));
		if (!tin.isEmpty())
			dc.add(Restrictions.like(Company.Field.tin.name(), "%" + tin + "%"));
		dc.add(Restrictions.eq(Company.Field.serviceLeaseKeyId.name(), user.getServiceLeaseKeyId()));
		dc.addOrder(Order.asc(Company.Field.name.name()));
		
		return getAll(dc, pageSetting);
	}

	/**
	 * The Implementation when checking the company if unique.
	 */
	@Override
	public boolean isUnique(Company company, int companyField, User user) {
		DetachedCriteria dc = getDetachedCriteria();
		switch (companyField){
			case 1 : dc.add(Restrictions.eq(Company.Field.companyNumber.name(), company.getCompanyNumber().trim()));
					break;
			case 2 : dc.add(Restrictions.eq(Company.Field.name.name(), company.getName().trim()));
					break;
			case 3 : dc.add(Restrictions.eq(Company.Field.tin.name(), company.getTin().trim()));
					break;
			case 4 : dc.add(Restrictions.eq(Company.Field.companyCode.name(), company.getCompanyCode().trim()));
					break;
		}
		if(user != null) {
			dc.add(Restrictions.eq(Company.Field.serviceLeaseKeyId.name(), user.getServiceLeaseKeyId()));
		}
		return getAll(dc).size() < 1;
	}

	public Collection<Company> getActiveCompanies(User user, String companyName, 
			List<String> companyNames, List<String> filterCNames){
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Company.Field.active.name(), true));
		filterCompaniesfromUserCompany(dc, user);
		if (companyName != null && !companyName.trim().isEmpty()) {
			dc.add(Restrictions.or(Restrictions.like(Company.Field.name.name(), "%" + companyName.trim() + "%"),
					Restrictions.like(Company.Field.companyNumber.name(), "%" + companyName.trim() + "%")));
		}
		if (companyNames != null && !companyNames.isEmpty()) {
			for (String cName : companyNames) 
				dc.add(Restrictions.ne(Company.Field.name.name(), cName.trim()));
		}
		if (filterCNames != null && !filterCNames.isEmpty()) {
			int size = filterCNames.size();
			if (size == 1)
				dc.add(Restrictions.eq(Company.Field.name.name(), filterCNames.iterator().next().trim()));
			else if (size >= 2) {
				LogicalExpression le = Restrictions.or(Restrictions.eq(Company.Field.name.name(), filterCNames.get(0).trim()), 
						Restrictions.eq(Company.Field.name.name(), filterCNames.get(1).trim()));
				if (size > 2) {
					for (int i=2; i<size; i++) 
						le = Restrictions.or(le, Restrictions.eq(Company.Field.name.name(), filterCNames.get(i).trim()));
				}
				dc.add(le);
			}
		}
		dc.addOrder(Order.asc(Company.Field.name.name()));
		return getAll(dc);
	}

	private void filterCompaniesfromUserCompany(DetachedCriteria dc, User user) {
		dc.add(Restrictions.eq(Company.Field.serviceLeaseKeyId.name(), user.getServiceLeaseKeyId()));
		List<Integer> userCompanyIds = user.getCompanyIds();
		if (!userCompanyIds.isEmpty()){
			dc.add(Restrictions.in(Company.Field.id.name(), userCompanyIds));
		}
	}

	@Override
	public List<Company> getCompanies(int serviceLeaseKeyId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Company.Field.serviceLeaseKeyId.name(), serviceLeaseKeyId));
		dc.addOrder(Order.asc(Company.Field.name.name()));
		return getAll(dc);
	}

	@Override
	public Company getCompanyByNumber(String companyNumber, int serviceLeaseKeyId) {
		DetachedCriteria dc = getDCWithSLkeyIdAndActive(serviceLeaseKeyId);
		dc.add(Restrictions.eq(Company.Field.companyNumber.name(), companyNumber));
		return get(dc);
	}

	@Override
	public Company getCompanyByName(String companyName) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Company.Field.name.name(), companyName.trim()));
		return get(dc);
	}

	@Override
	public Company getCompanyByName(User user, String companyName) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(Company.Field.name.name(), companyName.trim()));
		filterCompaniesfromUserCompany(dc, user);
		return get(dc);
	}

	@Override
	public Page<Company> getCompanyList(String companyNumber, String name,
			String tin, String code, User user, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		if (!companyNumber.isEmpty())
			dc.add(Restrictions.like(Company.Field.companyNumber.name(), "%" + companyNumber + "%"));
		if (!name.isEmpty())
			dc.add(Restrictions.like(Company.Field.name.name(), "%" + name + "%"));
		if (!tin.isEmpty())
			dc.add(Restrictions.like(Company.Field.tin.name(), "%" + tin + "%"));
		if (!code.isEmpty()) {
			dc.add(Restrictions.like(Company.Field.companyCode.name(), "%" + code + "%"));
		}
		dc.add(Restrictions.eq(Company.Field.serviceLeaseKeyId.name(), user.getServiceLeaseKeyId()));
		dc.addOrder(Order.asc(Company.Field.name.name()));
		
		return getAll(dc, pageSetting);
	}

	@Override
	public List<Company> getActiveCompaniesByName(String companyName, boolean isActiveOnly, Integer limit) {
		DetachedCriteria dc = getDetachedCriteria();
		if (companyName != null && !companyName.trim().isEmpty()) {
			dc.add(Restrictions.like(Company.Field.name.name(), "%" + companyName.trim() + "%"));			
		}
		if (isActiveOnly) {
			dc.add(Restrictions.eq(Company.Field.active.name(), true));
		}
		if (limit != null) {
			dc.getExecutableCriteria(getSession()).setMaxResults(limit);
		}
		return getAll(dc);
	}

	@Override
	public int generateCompanyNumber() {
		return generateSequenceNumber(FIELDS.id.name());
	}

	@Override
	public Company getCompanyByEbObjectId(Integer ebObjectId) {
		DetachedCriteria dc = getDetachedCriteria();

		DetachedCriteria obj2ObjDc = DetachedCriteria.forClass(ObjectToObject.class);
		obj2ObjDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));
		obj2ObjDc.add(Restrictions.eq(ObjectToObject.FIELDS.fromObjectId.name(), ebObjectId));

		dc.add(Subqueries.propertyIn(User.FIELD.ebObjectId.name(), obj2ObjDc));
		return get(dc);
	}
}