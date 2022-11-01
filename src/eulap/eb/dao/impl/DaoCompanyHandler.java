package eulap.eb.dao.impl;

import java.util.Collection;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;

import eulap.eb.domain.hibernate.Company;

/**
 * Company data access object handler utility class.

 *
 */
public class DaoCompanyHandler {
	/**
	 * Add the companies based on the company ids.
	 * @param dc The detached criteria.
	 * @param companyId The company id.
	 * @param companies The collection of companies under the logged user.
	 * @param companyIdField The company id field.
	 * @return The detached criteria.
	 */
	public static DetachedCriteria addCompanies (DetachedCriteria dc, int companyId, 
			Collection<Company> companies,  String companyIdField) {
		// -1 = search all companies
		if (companyId == -1) {
			int companySize = companies.size();
			// Stored the restriction in a variable for readability and reusability.
			SimpleExpression se = Restrictions.eq(companyIdField, 
				companies.iterator().next().getId());
			if (companySize > 1) {
				int cnt = 0;
				LogicalExpression le = null;
				for (Company company : companies) {
					if (cnt == 0) {
						// 2nd expression is compared with the first expression.
						le = Restrictions.or(se, Restrictions.eq(companyIdField, 
							company.getId()));
						cnt++;
					} else if (cnt > 0) {
						// Previous expression is compared with the current expression.
						le =  Restrictions.or(le, Restrictions.eq(companyIdField,
							company.getId()));
					}
				} 
				dc.add(le);
			} else {
				dc.add(se);
			}
			dc.createAlias("company", "company").addOrder(Order.asc("company.name"));
		} else {
			dc.add(Restrictions.eq(companyIdField, companyId));
		}
		return dc;
	}
}
