package eulap.common.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;

import eulap.common.dao.BaseDao.QueryResultHandler;
import eulap.common.domain.Domain;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.User;


/**
 * Defines the basic attribute of a DAO (Data Access Object).

 *
 */
public interface Dao <T> {
	/**
	 * Get the object 
	 * @param id The object's id.
	 * @return the object.
	 */
	T get (int id);
	
	/**
	 * Get the object. 
	 * @param id The object unique id.
	 * @param isDetached True to detached the object otherwise un detached the object in the session. 
	 */
	T get (int id, boolean isDetached);
	/**
	 * Get the object given the criteria.
	 * @param criteria The criteria in search of the object.
	 * @return the expected object. Null if not found.
	 */
	T get (DetachedCriteria criteria);

	/**
	 * Get domain by criteria.
	 * @param criteria the criteria. 
	 */
	T get(Criteria criteria);

	/**
	 * Get all of the objects
	 * @return the objects that belong to this dao.
	 */
	Collection<T> getAll ();
	
	/**
	 * Get all of the objects given the criteria.
	 * @param criteria The criteria that will be used in getting the objects.
	 * @return The objects that belongs to this dao.
	 */
	Collection<T> getAll (DetachedCriteria criteria);
	
	/**
	 * Get objects given company id;
	 * @param companyId The company id.
	 * @return The objects that belong to the company.
	 */
	Collection<T> getAllByCompanyId (int companyId);
	
	/**
	 * Save the object.
	 * @param t The object to be save. 
	 */
	void save (Domain t);
	
	/**
	 * Save the object.
	 * @param t The object to be save. All object that does not have id will be
	 * inserted otherwise is updated.
	 */
	void saveOrUpdate (Domain t);
	
	/**
	 * Delete item.
	 * @param t The obejct to be deleted. 
	 */
	void delete (Domain t);
	/**
	 * Update the object.
	 * @param t The object to be updated. All objects that have id will be updated.
	 */
	void update (Domain t);
	
	/**
	 * Delete the object.
	 * @param t The object to be deleted.
	 */
	void delete (T t);
	
	/**
	 * Delete the object
	 * @param id The object id that will be deleted.
	 */
	void delete (int id);
	
	/**
	 * Delete the object given the ids.
	 * @param ids The ids of object that will be deleted.
	 */
	void delete (Collection<Integer> ids);
	/**
	 * Persist the object.
	 * @param t the object to be persisted.
	 */
	void persist (T t);

	/**
	 * Get criteria with company id.
	 * @param companyId The company id.
	 * @return The detached criteria with company id.
	 */
	DetachedCriteria getCriteriaByCompanyId(int companyId);
	
	/**
	 * Get criteria by the companies of the logged user.
	 * @param user The current Logged user.
	 * @return The detached criteria with companies restriction.
	 */
	DetachedCriteria getCriteriaByCompanies (User user);
	
	/**
	 * Get all data controlled by page. 
	 * @param companyId company id;
	 * @param pageSetting the page setting
	 * @return The page result.
	 */
	Page<T> getAll (int companyId, PageSetting pageSetting);
	
	/**
	 * Get all data controlled by page. 
	 * @param companyId company id;
	 * @param pageSetting the page setting
	 * @param order set the order of the result
	 * @return The page result.
	 */
	Page<T> getAll (int companyId, PageSetting pageSetting, Order order);
	
	/**
	 * Get all data controlled by page
	 * @param companyId Company id
	 * @param pageSetting the page setting
	 * @param order set the order of the result
	 * @param criteria criteria
	 * @return The page result
	 */
	Page<T> getAll (int companyId, PageSetting pageSetting,
					Order order, Criterion ...criteria);
	
	/**
	 * Get all data controlled by page.
	 * @param pageSetting the page setting
	 * @param order set the order of the result.
	 * @return All of the domain data.
	 */
	Page<T> getAll (PageSetting pageSetting, Order order);
	
	/**
	 * Get all date controlled by page.
	 * @param pageSetting the page setting
	 * @param order set the order of the result.
	 * @param criteria The criteria in search the domain.
	 * @return The page<T> data.
	 */
	Page<T> getAll (PageSetting pageSetting, Order order, Criterion ...criteria);

	/**
	 * Execute the SQL script.
	 */
	void executeSQL (String sql);

	/**
	 * Call the sql statement.
	 * @param sql The sql statement
	 * @param handler query result handler
	 * @return The collection of the sql data.
	 */
	<A> Collection<A> get (String sql, QueryResultHandler<A> handler);

	/**
	 * This will perform the batch delete. This will delete 100 domains as at time.
	 * Use this function if you want to deleting more than 10 domains for faster deletion.
	 * @param t the list of domains to be deleted.
	 */
	void batchDelete(List<Domain> t);

	/**
	 * This will save the entities by batch.
	 * 
	 * @param entities the domain entities to be save.
	 */
	void batchSave (List<Domain> entities);
	
	/**
	 * Force save the entity to the database.
	 * @param entity The entity to be save
	 */
	void forceSaveOrUpdate (Domain entity);
	
	/**
	 * Save the list of entities with rollback.
	 * @param entities The list of entities.
	 */
	void batchSaveOrUpdate (List<Domain> entities);
	
	/**
	 * Execute the stored procedure.
	 * @param storedProc The stored procedure name
	 * @param param The stored procedure parameter.
	 * @return The expected return.s
	 */
	List<Object> executeSP (String storedProc, Object ...param);
	
	/**
	 * Get all by detached criteria.
	 * @param dc The detached criteria.
	 * @param pageSetting The page setting
	 * @return The paged date result. 
	 */
	Page<T> getAll (DetachedCriteria dc, PageSetting pageSetting);
	
	/**
	 * Get all the result of the criteria with result limitation.
	 * @param dc The detached criteria
	 * @param maxResult The maximum result
	 * @return The result data.
	 */
	List<T> getAll (DetachedCriteria dc, int maxResult);

	/**
	 * Generate the sequence number of any entity that uses sequence number.
	 * @param field The field of the entity.
	 * @return The generated sequence number.
	 */
	Integer generateSequenceNumber (String field);

	/**
	 * Generate the sequence number of any entity that uses sequence number.
	 * @param field The field of the entity.
	 * @param restrictionProperty Generates sequence number by this property.
	 * @param restrictionValue The value for the property name.
	 * @return The generated sequence number.
	 */
	Integer generateSequenceNumber(String field, String restrictionProperty, Integer restrictionValue);

	/**
	 * Get the object using its form workflow id.
	 * @param formWorkflowId The form workflow id.
	 * @return The object.
	 */
	T getByWorkflowId(int formWorkflowId);

	/**
	 * Get all by active.
	 * @return List of T.
	 */
	List<T> getAllActive();

	/**
	 * Get all by reference Id.
	 * @return List of T.
	 */
	List<T> getAllByRefId(String field, int refId);

	/**
	 * Get the date / date range criterion. This function handles 3 scenarios:
	 * <br> 1. Date from and to are present, will search for between.
	 * <br> 2. Either date from and to are present, will search for equal with the date.
	 * <br> 3. None are present, will return null.
	 * @param dateFrom The start date of the range.
	 * @param dateTo The end date of the range.
	 * @param propertyName The property name of the date.
	 * @return The criterion, otherwise null.
	 */
	Criterion getDateCriterion(Date dateFrom, Date dateTo, String propertyName);
	
	/**
	 * add a restriction that will not include the cancelled transaction in the result.
	 * This is only applicable to the form base domains. 
	 * @param dc the datached critiria.
	 */
	void addExcludeCancelledTransactions (DetachedCriteria dc);
	
	/**
	 * Generate the next sequence number of the form. 
	 * @param companyId The company. This can be null.
	 * @return The next sequence number.
	 */
	int generateSN(Integer companyId);

	/**
	 * Get the object 
	 * @param id The object's ebObject id.
	 * @return the object.
	 */
	T getByEbObjectId (int ebObjectId);

	/**
	 * Add the user companies in the criteria. The result will restrict
	 * to the current assigned company to the user.
	 * @param dc The criteria.
	 * @param user The current user.	 */
	void addUserCompany (DetachedCriteria dc, User user);

	/**
	 * Add restriction for {@link ObjectToObject} source object id from main object.
	 * @param dc The detached criteria.
	 * @param sourceCriterion The source criterion.
	 * @param sourceId the source object id.
	 */
	void restrictO2OReference(DetachedCriteria dc, DetachedCriteria sourceCriterion, int sourceId);
}
