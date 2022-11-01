package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.ActionNotice;

/**
 * Data Access Object interface of {@link ActionNotice}

 *
 */
public interface ActionNoticeDao extends Dao<ActionNotice>{

	/**
	 * Evaluates whether the Action Notice is existing.
	 * @param actionNotice The Action Notice Object.
	 * @return True if the Action Notice exists, else false.
	 */
	boolean isDuplicate(ActionNotice actionNotice);

	/**
	 * Search Action Notice by criteria.
	 * @param actionNoticeName The name of the Action Notice.
	 * @param status 1 if active, otherwise 0 if inactive
	 * @param pageSetting The page setting.
	 * @return Paged collection of matched Action Notices.
	 */
	Page<ActionNotice> searchActionNotice(String actionNoticeName, Integer status, PageSetting pageSetting);

	/**
	 * Get the list of action notices.
	 * @param The action notice id.
	 * @return The list of action notices.
	 */
	List<ActionNotice> getActionNotices(Integer actionNoticeId);
}
