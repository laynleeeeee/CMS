package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.Ratio;
import eulap.eb.domain.hibernate.User;

/**
 * Data access object for {@link Ratio}

 */
public interface RatioDao extends Dao<Ratio> {

	/**
	 * Check if ratio already exists.
	 * @param ratio The ratio object.
	 * @return True if the ratio already exists, otherwise,  false.
	 */
	boolean isDuplicate(Ratio ratio);

	/**
	 * Search Ratio by the specified criteria.
	 * @param name The name of the Ratio.
	 * @param status The status of the Ratio.
	 * @param pageSetting The page setting.
	 * @return Paged list of Ratios.
	 */
	Page<Ratio> searchRatio(User user, String name, SearchStatus status, PageSetting pageSetting);

	/**
	 * Get the list of Ratios.
	 * @param The Ratio id.
	 * @return The list of Ratios.
	 */
	List<Ratio> getRatios(Integer ratioId);
}