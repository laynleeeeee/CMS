package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.eb.domain.hibernate.EmployeeDocument;
import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.ApprovalSearchParam;

/**
 * Data Access Object interface of {@link EmployeeDocument}

 *
 */
public interface EmployeeDocumentDao extends Dao<EmployeeDocument>{
	/**
	 * Generates Sequence Number for Clinical and Cosmetic Record.
	 * @return
	 */
	Integer generateSequenceNo();

	/** Get Employee documents.
	 * @param searchParam The search parameter.
	 * @param statuses The form status ids.
	 * @param pageSetting The page settings.
	 * @return The paged collection of {@link EmployeeDocument}
	 */
	Page<EmployeeDocument> getEmployeeDocuments(ApprovalSearchParam searchParam, List<Integer> statuses,
			PageSetting pageSetting);

	/**
	 * Get the list of {@link EmployeeDocument}
	 * @param searchCriteria The search Criteria
	 * @param user The current logged user.
	 * @return The List of {@link EmployeeDocument}
	 */
	List<EmployeeDocument> searchEmployeeDocuments(String searchCriteria, User user);
}
