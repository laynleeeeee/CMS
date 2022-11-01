package eulap.common.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.common.domain.BaseDomain;
import eulap.common.domain.Domain;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.Company;
import eulap.eb.domain.hibernate.FormStatus;
import eulap.eb.domain.hibernate.FormWorkflow;
import eulap.eb.domain.hibernate.ObjectToObject;
import eulap.eb.domain.hibernate.User;

/**
 * Handle the basic processing of the hibernate base doa.
 * 

 * 
 * @param <T>
 *            Domain object
 */
public abstract class BaseDao<T extends BaseDomain> extends PageDao<T> implements Dao<T>{

	private static final String FORM_WORKFLOW_ID = "formWorkflowId";
	private static final String COMPANY_ID = "companyId";
	private static final String EB_OBJECT_ID = "ebObjectId";

	private static final int BATCH_SIZE = 100;
	@Override
	public void delete(T t) {
		Session session = null;
		Transaction tx = null;
		try {
			session = getSession();
			tx = session.getTransaction();
			tx.begin();
			session.delete(t);
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			throw e;
		} finally {
			if (session != null)
				session.close();
		}
	}

	@Override
	public void delete(int id) {
		T t = get(id);
		delete(t);
	}

	@Override
	public void delete(Collection<Integer> ids) {
		Collection<T> toBeDeleted = new ArrayList<T>();
		for (Integer id : ids) {
			toBeDeleted.add(get(id));
		}
		getHibernateTemplate().deleteAll(toBeDeleted);
	}

	@Override
	public T get(int id) {
		return getHibernateTemplate().get(getDomainClass(), id);
	}

	@Override
	public T get(int id, boolean isDetached) {
		T t = get (id);
		if (isDetached)
			getHibernateTemplate().evict(t);
		return t;
	}

	@Override
	public List<T> getAll() {
		DetachedCriteria criteria = DetachedCriteria.forClass(getDomainClass());
		return getAll(criteria);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getAll(DetachedCriteria criteria) {
		return getHibernateTemplate().findByCriteria(criteria);
	}

	/**
	 * Get all object based from criteria.
	 * @param criteria the criteria that will be the basis for retrieval. 
	 * @return The list of result object.
	 */
	public List<T> getAllByCriteria (Criteria criteria) {
		return criteria.list();
	}
	
	@Override
	public Collection<T> getAllByCompanyId(int companyId) {
		return getAll(getCriteriaByCompanyId(companyId));
	}

	@Override
	public DetachedCriteria getCriteriaByCompanyId(int companyId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq("companyId", companyId));
		return dc;
	}

	@Override
	public DetachedCriteria getCriteriaByCompanies(User user) {
		DetachedCriteria dc = getDetachedCriteria();
		List<Integer> ids = new ArrayList<Integer>();
		for (Company company : user.getCompanies()) {
			int companyId = company.getId();
			ids.add(companyId);
		}
		addAsOrInCritiria(dc, "companyId", ids);
		return dc;
	}
	
	@Override
	public T get(DetachedCriteria criteria) {
		Collection<T> result = getHibernateTemplate().findByCriteria(criteria,
				0, 1);
		if (result == null || result.isEmpty())
			return null;
		return result.iterator().next();
	}

	public T get(Criteria criteria) {
		criteria.setMaxResults(1);
		Collection<T> result = criteria.list();
		if (result == null || result.isEmpty())
			return null;
		return result.iterator().next();
	}

	public List<Object> getByProjection(Projection p) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.setProjection(p);
		return getHibernateTemplate().findByCriteria(dc);
	}

	/**
	 * Get the result by adding project in the criteria.
	 * 
	 * @param criteria
	 *            the criteria created.
	 * @return query result.
	 */
	public List<Object> getByProjection(DetachedCriteria criteria) {
		return getHibernateTemplate().findByCriteria(criteria);
	}

	/**
	 * Get the project with "sum" option.
	 * @param criteria The criteria to be retrieved.
	 * @return The sum projection.
	 */
	public double getBySumProjection (DetachedCriteria criteria) {
		List<Object> ret = getByProjection(criteria);
		if (ret != null && ret.size() > 0) {
			Object retObj = ret.iterator().next();
			if (retObj == null)
				return 0;
			return (Double) retObj;
		}
		return 0;
	}

	@Override
	public void save(Domain domain) {
		getHibernateTemplate().save(domain);
	}

	@Override
	public void delete(Domain t) {
		getHibernateTemplate().delete(t);
	}
	
	@Override
	public void saveOrUpdate(Domain domain) {
		getHibernateTemplate().saveOrUpdate(domain);
	}

	public void update(Domain domian) {
		getHibernateTemplate().update(domian);
	}

	@Override
	public void forceSaveOrUpdate(Domain entity) {
		Session session = null;
		try {
			session = getSession();
			Transaction tx = session.getTransaction();
			tx.begin();
			if (entity.getId() == 0)
				session.save(entity);
			else
				session.update(entity);
			tx.commit();
		} finally {
			if (session != null)
				session.close();
		}
	}

	@Override
	public void batchDelete(List<Domain> domains) {
		Session session = null;
		Transaction tx = null;
		try {
			int i = 0;
			session = getSession();
			tx = session.getTransaction();
			tx.begin();
			for (Domain entity : domains) {
				session.delete(entity);
				if (i++ % BATCH_SIZE == 0) { // 100 domains at a time.
					session.flush();
					session.clear();
				}
			}
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			throw e;
		} finally {
			if (session != null)
				session.close();
		}
	}
	@Override
	public void batchSave(List<Domain> entities) {
		Session session = null;
		try {
			int i = 0;
			session = getSession();
			Transaction tx = session.getTransaction();
			tx.begin();
			for (Domain entity : entities) {
				session.save(entity);
				if (i++ % BATCH_SIZE == 0) {
					session.flush();
					session.clear();
				}
			}
			tx.commit();
		} finally {
			if (session != null)
				session.close();
		}
	}

	@Override
	public void batchSaveOrUpdate(List<Domain> entities) {
		Session session = null;
		Transaction tx = null;
		try {
			session = getSession();
			tx = session.getTransaction();
			tx.begin();
			for (Domain entity : entities) {
				if (entity.getId() == 0)
					session.save(entity);
				else {
					session.update(entity);
				}
			}
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();
			throw e;
		} finally {
			if (session != null)
				session.close();
		}
	}

	@Override
	public void persist(T t) {
		getHibernateTemplate().persist(t);
	}

	

	@Deprecated
	public Criteria getCriteria() {
		return getSession().createCriteria(getDomainClass());
	}

	@Override
	public List<T> getAll (DetachedCriteria dc, int maxResult) {
		return getHibernateTemplate().findByCriteria(dc, 0, maxResult);
	}
	
	/**
	 * Get the first result of the criteria.
	 * 
	 * @param dc
	 *            The criteria
	 * @return The top result
	 */
	public T getFirstResult(DetachedCriteria dc) {
		List<?> result = getHibernateTemplate().findByCriteria(dc, 0, 1);
		return result.size() > 0 ? (T) result.iterator().next() : null;
	}

	public void addExcludeCancelledTransactions (DetachedCriteria dc) {
		DetachedCriteria workflowDc = DetachedCriteria.forClass(FormWorkflow.class);
		workflowDc.add(Restrictions.ne(FormWorkflow.FIELD.currentStatusId.name(), FormStatus.CANCELLED_ID));
		workflowDc.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dc.add(Subqueries.propertyIn(FORM_WORKFLOW_ID, workflowDc));
	}
	
	@Override
	public void executeSQL(String sql) {
		Session session = null;
		try {
			session = getSession();
			SQLQuery query = null;
			query = session.createSQLQuery(sql);
			query.executeUpdate();
		} finally {
			if (session != null)
				session.close();
		}
	}

	@Override
	public <A> Collection<A> get(String sql, QueryResultHandler<A> handler) {
		List<A> result = null;
		Session session = null;
		try {
			session = getSession();
			SQLQuery query = null;
			query = session.createSQLQuery(sql);
			handler.setParamater(query);
			handler.setScalars(query);
			List<Object[]> queryResult = query.list();
			result = handler.convert(queryResult);
		} finally {
			if (session != null)
				session.close();
		}
		return result;
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

	public interface QueryResultHandler<A> {
		/**
		 * Convert the necessary list of Query result the the desired object
		 */
		List<A> convert(List<Object[]> queryResult);

		/**
		 * Set the parameter of the query.
		 * 
		 * @param query
		 *            The {@link SQLQuery}
		 * @return the last index in the set parameter.
		 */
		int setParamater(SQLQuery query);

		/**
		 * Set the scalars
		 * 
		 * @param query
		 *            query The {@link SQLQuery}
		 */
		void setScalars(SQLQuery query);
	}

	@Override
	public List<Object> executeSP(String storedProc, Object... param) {
		int paramLenght = param.length;

		String formattedSP = "{call " + storedProc;
		for (int index = 0; index < paramLenght; index++) {
			if (index == 0)
				formattedSP += "(?";
			else
				formattedSP += ",?";
		}
		formattedSP += ")}";
		Session session = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(formattedSP);
			for (int index = 0; index < paramLenght; index++)
				query.setParameter(index, param[index]);
			return query.list();
		} finally {
			if (session != null)
				session.close();
		}
	}

	public <A> Page<A> executePagedSP(String storedProc, PageSetting pageSetting, QueryResultHandler<A> handler, Object... param) {
		int paramLenght = param.length;

		String formattedSP = "{call " + storedProc;
		for (int index = 0; index < paramLenght; index++) {
			if (index == 0)
				formattedSP += "(?";
			else
				formattedSP += ",?";
		}
		//Limit from and to
		formattedSP += ",?,?)}";
		Session session = null;
		List<A> result = null;
		try {
			session = getSession();
			SQLQuery query = session.createSQLQuery(formattedSP);
			int index;
			for (index = 0; index < paramLenght; index++) {
				query.setParameter(index, param[index]);
			}
			query.setParameter(index++, pageSetting.getStartResult());
			query.setParameter(index, pageSetting.getMaxResult());
			List<Object[]> queryResult = query.list();
			result = handler.convert(queryResult);
			//TODO: Better implementation of total records in paging.
			Integer totalRecords = pageSetting.getMaxResult(); //Set total records to 9,999,999 so that the last page will work.
			return new Page<A>(pageSetting, result, totalRecords);
		} finally {
			if (session != null)
				session.close();
		}
	}

	/**
	 * Add the ids as OR condition in the criteria.
	 * 
	 * @param dc
	 *            The detached criteria
	 * @param property
	 *            The property of the id
	 * @param ids
	 *            The list of id's to be added.
	 */
	protected void addAsOrInCritiria(DetachedCriteria dc, String property,
			int... ids) {
		boolean isStarted = false;
		Criterion criterion = convertToCriterion(property, ids);
		dc.add(criterion);
	}

	private Criterion convertToCriterion(String property, int... ids) {
		Criterion criterion = null;
		boolean isStarted = false;
		for (int id : ids) {
			if (!isStarted) {
				isStarted = true;
				criterion = Restrictions.eq(property, id);
				continue;
			}
			criterion = Restrictions.or(criterion,
					Restrictions.eq(property, id));
		}
		return criterion;
	}

	/**
	 * Add the ids as OR condition in the criteria.
	 * 
	 * @param dc
	 *            The detached criteria
	 * @param The
	 *            property of the id
	 * @param ids
	 *            The list of id's to be added.
	 */
	protected void addAsOrInCritiria(DetachedCriteria dc, String property,
			List<Integer> ids) {
		int[] intIds = new int[ids.size()];
		int index = 0;
		for (int id : ids) {
			intIds[index++] = id;
		}
		addAsOrInCritiria(dc, property, intIds);
	}

	protected void addNotInCriteria(DetachedCriteria dc, String property, List<Integer> ids) {
		int[] intIds = new int[ids.size()];
		int index = 0;
		for (int id : ids) {
			intIds[index++] = id;
		}
		Criterion criterion = convertDcToCriterion(property, intIds);
		dc.add(criterion);
	}

	private Criterion convertDcToCriterion(String property, int... ids) {
		Criterion criterion = null;
		boolean isStarted = false;
		for (int id : ids) {
			if (!isStarted) {
				isStarted = true;
				criterion = Restrictions.ne(property, id);
				continue;
			}
			criterion = Restrictions.and(criterion,
					Restrictions.ne(property, id));
		}
		return criterion;
	}


	protected void addAsOrInCritiria(Criteria c, String property,
			List<Integer> ids) {
		int[] intIds = new int[ids.size()];
		int index = 0;
		for (int id : ids) {
			intIds[index++] = id;
		}
		c.add(convertToCriterion(property, intIds));
	}

	/**
	 * Get the detached criteria with service lease key and active condition. 
	 * @param serviceLeaseKeyId The service key id.
	 * @return The detached criteria with restrictions. 
	 */
	protected DetachedCriteria getDCWithSLkeyIdAndActive (int serviceLeaseKeyId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq("serviceLeaseKeyId", serviceLeaseKeyId));
		dc.add(Restrictions.eq("active", true));
		return dc;
	}
	
	
	/**
	 * Generate the sequence number of any entity that uses sequence number.
	 * @param field The field of the entity.
	 * @return The generated sequence number.
	 */
	@Override
	public Integer generateSequenceNumber(String field) {
		return generateSequenceNumber(field, null, null);
	}

	/**
	 * Generate the sequence number of any entity that uses sequence number.
	 * @param field The field of the entity.
	 * @param restrictionProperty Generates sequence number by this property.
	 * @param restrictionValue The value for the property name.
	 * @return The generated sequence number.
	 */
	public Integer generateSequenceNumber(String field, String restrictionProperty, Integer restrictionValue) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.setProjection(Projections.max(field));
		if(restrictionProperty != null && restrictionValue != null)
			dc.add(Restrictions.eq(restrictionProperty, restrictionValue));
		return generateSeqNo(dc);
	}

	/**
	 * Generate the sequence number.
	 * @param dc The detached criteria.
	 * @return The generated sequence number.
	 */
	public Integer generateSeqNo(DetachedCriteria dc) {
		List<Object> result = getByProjection(dc);
		if (result == null)
			return 1;
		Object obj = result.iterator().next();
		if (obj == null)
			return 1;
		return ((Integer) obj) + 1;
	}

	/**
	 * Restrict the "form-based" criteria to retrieve the form that are only complete. 
	 * @param dc The  form based detached criteria
	 */
	public void filterByComplete (DetachedCriteria dc) {
		DetachedCriteria dcWorkflow = DetachedCriteria.forClass(FormWorkflow.class);
		dcWorkflow.add(Restrictions.eq(FormWorkflow.FIELD.complete.name(), true));
		dcWorkflow.setProjection(Projections.property(FormWorkflow.FIELD.id.name()));
		dc.add(Subqueries.propertyIn("formWorkflowId", dcWorkflow));
	}

	@Override
	public T getByWorkflowId(int formWorkflowId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq("formWorkflowId", formWorkflowId));
		return get(dc);
	}
	
	/**
	 * Restrict the criteria to get the completed transaction only. 
	 * @param dc The detached criteria.
	 */
	protected void restrictToCompletedOnly (DetachedCriteria dc) {
		dc.createAlias("formWorkflow", "fw");
		dc.add(Restrictions.eq("fw.complete", true));
	}

	@Override
	public List<T> getAllActive() {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq("active", true));
		return getAll(dc);
	}

	@Override
	public List<T> getAllByRefId(String field, int refId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(field, refId));
		return getAll(dc);
	}

	@Override
	public Criterion getDateCriterion(Date dateFrom, Date dateTo, String propertyName) {
		if(dateFrom != null && dateTo != null) {
			return Restrictions.between(propertyName, dateFrom, dateTo);
		} else if(dateFrom != null || dateTo != null) {
			Date date = dateFrom == null ? dateTo : dateFrom;
			return Restrictions.eq(propertyName, date);
		}
		return null;
	}
	
	@Override
	public int generateSN(Integer companyId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.setProjection(Projections.max("sequenceNumber"));
		if(companyId != null) {
			dc.add(Restrictions.eq(COMPANY_ID, companyId));
		}
		return generateSeqNo(dc);
	}

	@Override
	public T getByEbObjectId(int ebObjectId) {
		DetachedCriteria dc = getDetachedCriteria();
		dc.add(Restrictions.eq(EB_OBJECT_ID, ebObjectId));
		return get(dc);
	}

	@Override
	public void addUserCompany(DetachedCriteria dc, User user) {
		List<Integer> userCompanyIds = user.getCompanyIds();
		if (userCompanyIds.isEmpty())
			return; //TODO: for now, we will not restraint the result if
		// the user has now company assignment. No company assignment is assume
		// to have access to all companies
		dc.add(Restrictions.in(COMPANY_ID, userCompanyIds));
	}

	@Override
	public void restrictO2OReference(DetachedCriteria dc, DetachedCriteria sourceCriterion, int sourceId) {

		DetachedCriteria obj2ObjDc = DetachedCriteria.forClass(ObjectToObject.class);
		obj2ObjDc.setProjection(Projections.property(ObjectToObject.FIELDS.toObjectId.name()));

		sourceCriterion.setProjection(Projections.property(EB_OBJECT_ID));
		sourceCriterion.add(Restrictions.eq("id", sourceId));
		obj2ObjDc.add(Subqueries.propertyIn(ObjectToObject.FIELDS.fromObjectId.name(), sourceCriterion));

		dc.add(Subqueries.propertyIn(EB_OBJECT_ID, obj2ObjDc));
	}
}
