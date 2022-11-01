package eulap.eb.dao;

import java.util.Date;
import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.PersonnelActionNotice;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.ApprovalSearchParam;

/**
 * Data access object for {@link PersonnelActionNotice}

 *
 */
public interface PersonnelActionNoticeDao extends Dao<PersonnelActionNotice> {
	/**
	 * Generates sequence number for personnel action notice.
	 * @return The sequence number of the form.
	 */
	Integer generateSequenceNo();

	/**
	 * Get the paged list of requests per type.
	 * @param param The search parameter.
	 * @return The paged list of request per type.
	 */
	Page<PersonnelActionNotice> getActionNotices(ApprovalSearchParam searchParam, List<Integer> statuses,
			PageSetting pageSetting);

	/**
	 * get the list of employee request based on the search criteria
	 * @param typeId The type id
	 * @param searchCriteria The search criteria
	 * @param user The user logged
	 * @return The list of employee request
	 */
	List<PersonnelActionNotice> searchActionNotices(String searchCriteria, User user);

	/**
	 * Get the list of the latest action notice.
	 * @param updatedDate The updated date.
	 * @return The list action notice.
	 */
	List<PersonnelActionNotice> getLatestActionNotices(Date updatedDate);
}
