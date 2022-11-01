package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.TypeOfLeave;
import eulap.eb.domain.hibernate.User;

/**
 * Data access object of {@link TypeOfLeave}

 *
 */
public interface TypeOfLeaveDao extends Dao<TypeOfLeave>{
	/**
	 * Search types of leave.
	 * @param user The user currently logged in.
	 * @param name The leave name.
	 * @param status The search status.
	 * @param pageSetting The page setting.
	 * @return The paged list of type of leaves.
	 */
	Page<TypeOfLeave> searchLeaves(User user, String name, SearchStatus status, PageSetting pageSetting);

	/**
	 * Check if the leave name is a duplicate.
	 * @param typeOfLeave The leave object.
	 * @return True if the leave name already exists, otherwise false.
	 */
	boolean isDuplicate(TypeOfLeave typeOfLeave);

	/**
	 * Get the list of type of leave by Id.
	 * @return The list of type of leaves.
	 */
	List<TypeOfLeave> getTypeOfLeaves(Integer leaveTypeId);
}
