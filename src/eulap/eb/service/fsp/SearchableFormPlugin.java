package eulap.eb.service.fsp;

import java.util.List;

import eulap.eb.domain.hibernate.User;
import eulap.eb.web.dto.FormSearchResult;

/**
 * Interface that defines the searchable form plugins

 *
 */
public interface SearchableFormPlugin {

	/**
	 * Search the different form form the database.
	 * @param user The current logged in user.
	 * @param searchCriteria The search criteria.
	 * @return The form search result
	 */
	List<FormSearchResult> search (User user, String searchCriteria);
}
