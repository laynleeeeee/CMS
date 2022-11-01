package eulap.eb.dao.impl;

import java.util.Collection;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;

import eulap.eb.domain.hibernate.AccountCombination;
import eulap.eb.domain.hibernate.Division;

/**
 * Division data access object handler utility class.

 *
 */
public class DaoDivisionHandler {
	/**
	 * Add the criteria for divisions.
	 * @param dc The detached criteria
	 * @param divisions The collection of divisions.
	 * @return The detached criteria with division criteria.
	 */
	public static DetachedCriteria setDivisionCriteria (DetachedCriteria dc, Collection<Division> divisions) {
		if (!divisions.isEmpty()) {
			SimpleExpression se = Restrictions.eq(AccountCombination.FIELD.divisionId.name(), 
					divisions.iterator().next().getId());
			if (divisions.size() > 1) {
				int cnt = 0;
				LogicalExpression le = null;
				for (Division division : divisions) {
					if (cnt == 0) {
						// 2nd expression is compared with the first expression.
						le = Restrictions.or(se, Restrictions.eq(AccountCombination.FIELD.divisionId.name(), 
							division.getId()));
						cnt++;
					} else if (cnt > 0) {
						// Previous expression is compared with the current expression.
						le = Restrictions.or(le, Restrictions.eq(AccountCombination.FIELD.divisionId.name(),
							division.getId()));
					}
				} 
				dc.add(le);
			} else {
				dc.add(se);
			}
		}
		return dc;
	}
}
