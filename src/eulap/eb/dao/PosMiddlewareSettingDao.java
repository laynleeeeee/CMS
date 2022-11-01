package eulap.eb.dao;

import eulap.common.dao.Dao;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.domain.hibernate.PosMiddlewareSetting;
import eulap.eb.domain.hibernate.User;

/**
 * Data Access Object for {@link PosMiddlewareSetting}

 *
 */
public interface PosMiddlewareSettingDao extends Dao<PosMiddlewareSetting> {

	/**
	 * Get the middleware settings.
	 * @param companyId The company filter.
	 * @param warehouseId The warehouse filter.
	 * @param customerName The name of customer.
	 * @param searchStatus The search status.
	 * @param pageSetting The page setting.
	 * @param user The logged user.
	 * @return The paged list of middleware settings.
	 */
	Page<PosMiddlewareSetting> getMiddlewares (Integer companyId, Integer warehouseId, 
			String customerName, SearchStatus searchStatus, User user, PageSetting pageSetting);

	/**
	 * Checks if there is already an active setting for a company.
	 * @param setting The PosMiddlewareSetting object.
	 * @return True if there is already existing, otherwise false.
	 */
	boolean hasActive (PosMiddlewareSetting setting);

	/**
	 * Get the {@link PosMiddlewareSetting} by company id.
	 * @param companyId The company filter.
	 * @param user The current logged in user.
	 * @return The {@link PosMiddlewareSetting}.
	 */
	PosMiddlewareSetting getByCompany(int companyId, User user);

	/**
	 * Get the {@link PosMiddlewareSetting} by company id and warehouse id.
	 * @param companyId The company filter.
	 * @param warehouseId The warehouse filter.
	 * @return The {@link PosMiddlewareSetting}.
	 */
	PosMiddlewareSetting getByCompanyAndWarehouse(Integer companyId, Integer warehouseId);
}
