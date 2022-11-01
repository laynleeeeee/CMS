package eulap.eb.dao;

import java.util.List;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.SalesPersonnel;

/**
 * Data Access Object of {@link SalesPersonnel}

 *
 */

public interface SalesPersonnelDao extends Dao<SalesPersonnel>{
	/**
	 * Determine if the sales personnel name is unique.
	 * @param salesPersonnel The {@link SalesPersonnel}.
	 * @return True if unique, otherwise false.
	 */
	boolean isUnique(SalesPersonnel salesPersonnel);

	/**
	 * Get the list of {@link SalesPersonnel} in {@link Page} format.
	 * @param companyId The company id.
	 * @param name The sales personnel name.
	 * @param status The sales personnel status.
	 * @param pageNumber The pagenumber.
	 * @return The list of {@link SalesPersonnel} in {@link Page} format.
	 */
	Page<SalesPersonnel> searchSalesPersonnel(Integer companyId, String name,
			SearchStatus status, PageSetting pageSetting);

	/**
	 * Get the list of {@link SalesPersonnel} by name.
	 * @param companyId The company id.
	 * @param name The sales personnel name.
	 * @param isExact True if exact name, otherwise false.
	 * @return The list of {@link SalesPersonnel}.
	 */
	List<SalesPersonnel> getSalesPersonnelByName(Integer companyId, String name, Boolean isExact);
}
