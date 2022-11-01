package eulap.common.dao;

import java.util.Date;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.User;
import eulap.common.util.DateUtil;

/**
 * Utility class for data access object.

 *
 */
public class DaoUtil {		
	/**
	 * Set the criteria based on the search status.
	 * @param dc The detached criteria.
	 * @param activeField The active field.
	 * @param searchStatus The status are All, Active, and Inactive.
	 * @return The detached criteria.
	 */
	public static DetachedCriteria setSearchStatus (DetachedCriteria dc, String activeField,
			SearchStatus searchStatus) {
		// Add domain object status condition
		switch (searchStatus) {
			case All : break;
			case Active : 
				dc.add(Restrictions.eq(activeField, true));
				break;
			case Inactive :
				dc.add(Restrictions.eq(activeField, false));
		}
		return dc;
	}
	
	/**
	 * Set the criteria based on the search status.
	 * @param criteria The criteria.
	 * @param activeField The active field.
	 * @param searchStatus The status are All, Active, and Inactive.
	 * @return The criteria.
	 */
	public static Criteria setCritSearchStatus (Criteria criteria, String activeField,
			SearchStatus searchStatus) {
		// Add domain object status condition
		switch (searchStatus) {
			case All : break;
			case Active : 
				criteria.add(Restrictions.eq(activeField, true));
				break;
			case Inactive :
				criteria.add(Restrictions.eq(activeField, false));
		}
		return criteria;
	}
	
	/**
	 * Adds criteria based on the date range.
	 * @param dc The detached criteria.
	 * @param dateField the date field.
	 * @param startDate The start of date range.
	 * @param endDate The end if date range.
	 * @return The detached criteria.
	 */
	public static DetachedCriteria addDateRangeCriteria (DetachedCriteria dc, String dateField, 
			Date startDate, Date endDate) {
		if (startDate != null && endDate != null) 
			dc.add(Restrictions.between(dateField, startDate, DateUtil.addDaysToDate(endDate, 1)));
		else if (startDate != null)
			dc.add(Restrictions.eq(dateField, startDate));
		else if (endDate != null)
			dc.add(Restrictions.eq(dateField, endDate));
		return dc;
	}
	
	/**
	 * Create a criterion that filters data by the given date range.
	 * @param dateField The date field. 
	 * @param startDate The start of date range.
	 * @param endDate The end of date range.
	 * @return The criterion
	 */
	public static Criterion addDateRangeCriterion (String dateField, Date startDate, Date endDate) {
		if (startDate != null && endDate != null) 
			return Restrictions.between(dateField, startDate, DateUtil.addDaysToDate(endDate, 1));
		else if (startDate != null)
			return Restrictions.eq(dateField, startDate);
		else if (endDate != null)
			return Restrictions.eq(dateField, endDate);
		return null;
	}
	
	/**
	 * Function that handles keyword/s that has special character/s that behave/s abnormally.
	 * For the list of mysql special characters refer to: {@link  MysqlSpecialCharacterField}
	 * @param keyword The keyword used for searching.
	 * @return The processed keyword.
	 */
	public static String handleMysqlSpecialCharacter (String keyword) {				
		for (MysqlSpecialCharacterField scf : MysqlSpecialCharacterField.values())
			keyword = keyword.replaceAll(scf.getValue(), "\\\\" + scf.getValue());
		return keyword;
	}

	/**
	 * Get the string formatted list of company ids that are available for the logged user based on the 
	 * user company setup.
	 * @param user The logged user.
	 * @return The string formatted list of company ids.
	 */
	public static String getCompanyIds(User user) {
		String companyIds = "";
		int cnt = 1;
		for (Integer compId : user.getCompanyIds()) {
			if(cnt == 1 && cnt == user.getCompanyIds().size()){
				companyIds += "("+compId+")";
			} else if(cnt == user.getCompanyIds().size()){
				companyIds += compId+")";
			} else {
				companyIds += (cnt == 1 ? "(" : "") + compId + ", ";
			}
			cnt++;
		}
		return companyIds;
	}
}
