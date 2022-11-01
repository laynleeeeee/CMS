package eulap.common.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import eulap.common.dao.BaseDao.QueryResultHandler;
import eulap.common.util.DateUtil;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;

/**
 * A class that handles the basic operation of the page base dao.

 *
 * @param <T>
 */
public abstract class PageDao<T> extends HibernateDaoSupport{
	/**
	 * Get the domain class type.
	 * 
	 * @return The domain class type.
	 */
	protected abstract Class<T> getDomainClass();
	
	public DetachedCriteria getDetachedCriteria() {
		return DetachedCriteria.forClass(getDomainClass());
	}
	
	public Page<T> getAll(PageSetting pageSetting, Order order,
			Criterion... criteria) {
		DetachedCriteria dc = getDetachedCriteria();
		if (criteria != null)
			for (Criterion c : criteria)
				dc.add(c);
		if (order != null)
			dc.addOrder(order);
		// NOTE: page starts at 1 but sql start at 0.
		int firstResult = pageSetting.getStartResult();// (pageSetting.getPageNumber()
														// - 1) *
														// pageSetting.getMaxResult();
		List<?> result = getHibernateTemplate().findByCriteria(dc, firstResult,
				pageSetting.getMaxResult());
		dc = getDetachedCriteria();
		if (criteria != null)
			for (Criterion c : criteria)
				dc.add(c);
		dc.setProjection(Projections.rowCount());
		List<?> count = getHibernateTemplate().findByCriteria(dc);
		return new Page(pageSetting, result, (Integer) count.iterator().next());
	}
	
	public Page<T> getAll(Criteria c, PageSetting pageSetting) {
		c.setProjection(Projections.rowCount());
		List<?> count = c.list();
		int firstResult = pageSetting.getStartResult();
		c.setProjection(null);
		if (pageSetting.getMaxResult() != PageSetting.NO_PAGE_CONSTRAINT)
			c.setMaxResults(pageSetting.getMaxResult());
		c.setFirstResult(firstResult);
		List<Object> result = c.list();
		List<Object> finalResult = new ArrayList<Object>();
		for (Object obj : result) {
			if (obj instanceof Object[]) {
				for (Object o : (Object[]) obj) {
					if (o == null)
						continue;
					if (o.getClass().equals(getDomainClass()))
						finalResult.add(o);
				}
			} else {
				finalResult = result;
				break;
			}
		}
		return new Page(pageSetting, finalResult, (Integer) count.iterator()
				.next());
	}

	private Criterion getCompanyCriterion(int companyId) {
		return Restrictions.eq("companyId", companyId);
	}
	
	public Page<T> getAll(int companyId, PageSetting pageSetting, Order order,
			Criterion... criteria) {
		Criterion companyCriterion = getCompanyCriterion(companyId);
		if (criteria == null)
			return getAll(pageSetting, order, companyCriterion);
		Criterion[] criteriaWithCompanyId = new Criterion[criteria.length + 1];
		int index = 0;
		// Add first the company id criteria.
		criteriaWithCompanyId[index++] = companyCriterion;
		for (Criterion c : criteria) {
			criteriaWithCompanyId[index++] = c;
		}
		return getAll(pageSetting, order, criteriaWithCompanyId);
	}
	
	public Page<T> getAll(int companyId, PageSetting pageSetting) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq("companyId", companyId));
		return getAll(dc, pageSetting);
	}

	public Page<T> getAll(PageSetting pageSetting, Order order) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.addOrder(order);
		return getAll(dc, pageSetting);
	}

	public Page<T> getAll(int companyId, PageSetting pageSetting, Order order) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.addOrder(order);
		dc.add(Restrictions.eq("companyId", companyId));
		return getAll(dc, pageSetting);
	}
	
	public Page<T> getAll(DetachedCriteria dc, PageSetting pageSetting) {
		dc.setProjection(Projections.rowCount());
		List<?> count = getHibernateTemplate().findByCriteria(dc);
		int firstResult = pageSetting.getStartResult();
		dc.setProjection(null);
		dc.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		List<Object> result = getHibernateTemplate().findByCriteria(dc,
				firstResult, pageSetting.getMaxResult());
		
		// Check if the result is array of objects, this usually occurs when the
		// detached criteria has 'Aliases'.
		List<Object> finalResult = new ArrayList<Object>();
		for (Object obj : result) {
			if (obj instanceof Object[]) {
				for (Object o : (Object[]) obj) {
					if (o == null)
						continue;
					if (o.getClass().equals(getDomainClass()))
						finalResult.add(o);
				}
			} else {
				finalResult = result;
				break;
			}
		}
		return new Page(pageSetting, finalResult, (Integer) count.iterator()
				.next());
	}
	
	public <A> Page<A> getAllAsPage(String sql, PageSetting pageSetting,
			QueryResultHandler<A> handler) {
		String sqlCount = "SELECT count(*) as TOTAL_COUNT FROM (" + sql
				+ ") as tc";
		List<A> result = null;
		Session session = null;
		int totalRecords = 0;
		try {
			session = getSession();
			SQLQuery query = null;
			boolean addPaging = false;
			if (pageSetting.getMaxResult() != PageSetting.NO_PAGE_CONSTRAINT) {
				query = session.createSQLQuery(sql + " " + "LIMIT ?,?");
				addPaging = true;
			} else {
				query = session.createSQLQuery(sql);
			}
			int lastIndex = handler.setParamater(query) + 1;
			if (addPaging) {
				// Paging starts at 1
				query.setParameter(lastIndex++, pageSetting.getStartResult());
				query.setParameter(lastIndex, pageSetting.getMaxResult());
			}
			handler.setScalars(query);
			List<Object[]> queryResult = query.list();
			result = handler.convert(queryResult);
			query = session.createSQLQuery(sqlCount);
			handler.setParamater(query);
			List<?> count = query.list();
			if (count.size() > 0)
				totalRecords = Integer.valueOf(count.get(count.size() - 1)
						.toString());
		} finally {
			session.close();
		}
		return new Page<A>(pageSetting, result, totalRecords);
	}

	public Criterion getDateCriterion(Date dateFrom, Date dateTo, String propertyName) {
		if(dateFrom != null && dateTo != null) {
			return Restrictions.between(propertyName, dateFrom, dateTo);
		} else if(dateFrom != null || dateTo != null) {
			Date date = dateFrom == null ? dateTo : dateFrom;
			return Restrictions.eq(propertyName, date);
		}
		return null;
	}
}
