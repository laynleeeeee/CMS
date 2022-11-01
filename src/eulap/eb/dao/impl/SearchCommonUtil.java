package eulap.eb.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import eulap.eb.domain.hibernate.Company;
import eulap.eb.web.dto.ApprovalSearchParam;

/**
 * Utility to search for the common search parameters: Company number, amount and date range.

 *
 */
public class SearchCommonUtil {

	/**
	 * Common utility to be used to search for the common parameters: Company number, amount and date range.
	 * @param queryCriteria The main query criteria.
	 * @param subqueryCriteria DetachedCriteria subquery for the main criteria.
	 * @param subqueryProperty The property of the subquery.
	 * @param dateProperty1 The first date property.
	 * @param dateProperty2 The second date property.
	 * @param dateProperty3 The third date property.
	 * @param companyIds The list of company ids.
	 * @param searchParam The container that holds the search parameters. {@link ApprovalSearchParam}
	 */
	public static void searchCommonParams(Criteria queryCriteria, DetachedCriteria subqueryCriteria, final String subqueryProperty,
			final String dateProperty1, final String dateProperty2, final String dateProperty3, final List<Integer> companyIds, final ApprovalSearchParam searchParam) {

		DetachedCriteria companyCriteria  = DetachedCriteria.forClass(Company.class);
		companyCriteria.setProjection(Projections.property(Company.Field.id.name()));
		if(companyIds != null && !companyIds.isEmpty()){
			companyCriteria.add(Restrictions.in(Company.Field.id.name(), companyIds));
		}
		//Search for company number
		if(searchParam.getCompanyNo() != null) {
			companyCriteria.add(Restrictions.like(Company.Field.companyNumber.name(), "%"+searchParam.getCompanyNo().trim()+"%"));
		}
		if(subqueryCriteria != null) {
			subqueryCriteria.setProjection(Projections.property("id"));
			subqueryCriteria.add(Subqueries.propertyIn("companyId", companyCriteria));
		} else {
			subqueryCriteria = companyCriteria;
		}
		queryCriteria.add(Subqueries.propertyIn(subqueryProperty, subqueryCriteria));

		//Search for amount
		if(searchParam.getAmount() != null)
			queryCriteria.add(Restrictions.eq("amount", searchParam.getAmount()));

		//Search for date/date range
		Date dateFrom = searchParam.getDateFrom();
		Date dateTo = searchParam.getDateTo();
		if(dateFrom != null) {
			if(dateTo != null) {
				//Criterion for date range
				Criterion firstDateRangeCriterion  = Restrictions.between(dateProperty1, dateFrom, dateTo);
				if(dateProperty2 != null) {
					Criterion secondDateRangeCriterion = Restrictions.between(dateProperty2, dateFrom, dateTo);
					LogicalExpression dateRangeExp = Restrictions.or(firstDateRangeCriterion, secondDateRangeCriterion);

					//Add date range criteria for dateProperty3
					if(dateProperty3 != null) {
						dateRangeExp = Restrictions.or(dateRangeExp,
								Restrictions.between(dateProperty3, dateFrom, dateTo));
					}
					queryCriteria.add(dateRangeExp);
				} else {
					queryCriteria.add(firstDateRangeCriterion);
				}
			} else {
				//Criterion for date
				Criterion firstDateCriterion = Restrictions.eq(dateProperty1, dateFrom);
				if(dateProperty2 != null) {
					Criterion secondDateCriterion = Restrictions.eq(dateProperty2, dateFrom);
					LogicalExpression dateExpression = Restrictions.or(firstDateCriterion, secondDateCriterion);

					//Add criteria for dateProperty3
					if(dateProperty3 != null) {
						dateExpression = Restrictions.or(dateExpression,
								Restrictions.eq(dateProperty3, dateFrom));
					}
					queryCriteria.add(dateExpression);
				} else {
					queryCriteria.add(firstDateCriterion);
				}
			}
		}
	}

	/**
	 * Common utility to be used to search for the common parameters: Company number, amount and date range.
	 * @param queryCriteria The main query detached criteria.
	 * @param subqueryCriteria DetachedCriteria subquery for the main criteria.
	 * @param subqueryProperty The property of the subquery.
	 * @param dateProperty1 The first date property.
	 * @param dateProperty2 The second date property.
	 * @param dateProperty3 The third date property.
	 * @param companyIds The list of company ids.
	 * @param searchParam The container that holds the search parameters. {@link ApprovalSearchParam}
	 */
	public static void searchDCCommonParams(DetachedCriteria queryCriteria, DetachedCriteria subqueryCriteria, final String subqueryProperty,
			final String dateProperty1, final String dateProperty2, final String dateProperty3, final List<Integer> companyIds, final ApprovalSearchParam searchParam) {

		DetachedCriteria companyCriteria  = DetachedCriteria.forClass(Company.class);
		companyCriteria.setProjection(Projections.property(Company.Field.id.name()));
		if(companyIds != null && !companyIds.isEmpty()){
			companyCriteria.add(Restrictions.in(Company.Field.id.name(), companyIds));
		}
		//Search for company number
		if(searchParam.getCompanyNo() != null) {
			companyCriteria.add(Restrictions.like(Company.Field.companyNumber.name(), "%"+searchParam.getCompanyNo().trim()+"%"));
		}
		if(subqueryCriteria != null) {
			subqueryCriteria.setProjection(Projections.property("id"));
			subqueryCriteria.add(Subqueries.propertyIn("companyId", companyCriteria));
		} else {
			subqueryCriteria = companyCriteria;
		}
		queryCriteria.add(Subqueries.propertyIn(subqueryProperty, subqueryCriteria));

		//Search for amount
		if(searchParam.getAmount() != null)
			queryCriteria.add(Restrictions.eq("amount", searchParam.getAmount()));

		//Search for date/date range
		Date dateFrom = searchParam.getDateFrom();
		Date dateTo = searchParam.getDateTo();
		if(dateFrom != null) {
			if(dateTo != null) {
				//Criterion for date range
				Criterion firstDateRangeCriterion  = Restrictions.between(dateProperty1, dateFrom, dateTo);
				if(dateProperty2 != null) {
					Criterion secondDateRangeCriterion = Restrictions.between(dateProperty2, dateFrom, dateTo);
					LogicalExpression dateRangeExp = Restrictions.or(firstDateRangeCriterion, secondDateRangeCriterion);

					//Add date range criteria for dateProperty3
					if(dateProperty3 != null) {
						dateRangeExp = Restrictions.or(dateRangeExp,
								Restrictions.between(dateProperty3, dateFrom, dateTo));
					}
					queryCriteria.add(dateRangeExp);
				} else {
					queryCriteria.add(firstDateRangeCriterion);
				}
			} else {
				//Criterion for date
				Criterion firstDateCriterion = Restrictions.eq(dateProperty1, dateFrom);
				if(dateProperty2 != null) {
					Criterion secondDateCriterion = Restrictions.eq(dateProperty2, dateFrom);
					LogicalExpression dateExpression = Restrictions.or(firstDateCriterion, secondDateCriterion);

					//Add criteria for dateProperty3
					if(dateProperty3 != null) {
						dateExpression = Restrictions.or(dateExpression,
								Restrictions.eq(dateProperty3, dateFrom));
					}
					queryCriteria.add(dateExpression);
				} else {
					queryCriteria.add(firstDateCriterion);
				}
			}
		}
	}
}
