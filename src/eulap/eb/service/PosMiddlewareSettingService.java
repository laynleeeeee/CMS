package eulap.eb.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bp.web.ar.AuditUtil;
import eulap.common.domain.Audit;
import eulap.common.util.Page;
import eulap.common.util.PageSetting;
import eulap.common.util.SearchStatus;
import eulap.eb.dao.PosMiddlewareSettingDao;
import eulap.eb.domain.hibernate.PosMiddlewareSetting;
import eulap.eb.domain.hibernate.User;

/**
 * Class that handles the business logic of Pos 

 *
 */
@Service
public class PosMiddlewareSettingService {
	@Autowired
	private PosMiddlewareSettingDao middlewareSettingDao;

	/**
	 * Get the middleware settings.
	 * @param companyId The company filter.
	 * @param warehouseId The warehouse filter.
	 * @param customerName The name of customer.
	 * @param status ALL, ACTIVE, or INACTIVE
	 * @param pageNumber The page number.
	 * @param user The logged user.
	 * @return The paged list of middleware settings.
	 */
	public Page<PosMiddlewareSetting> getMiddlewares (Integer companyId, 
			Integer warehouseId, String customerName, String status, int pageNumber,
			User user) {
		SearchStatus searchStatus = SearchStatus.getInstanceOf(status);
		return middlewareSettingDao.getMiddlewares(companyId, warehouseId, customerName,
				searchStatus, user, new PageSetting(pageNumber, PageSetting.MAX_ADMIN_RECORD));
	}

	/**
	 * Get the {@link PosMiddlewareSetting} object by id.
	 * @param id The id.
	 * @return The {@link PosMiddlewareSetting} object.
	 */
	public PosMiddlewareSetting getById (Integer id) {
		return middlewareSettingDao.get(id);
	}

	/**
	 * Checks if there is already an active setting for a company.
	 * @param setting The PosMiddlewareSetting object.
	 * @return True if there is already existing, otherwise false.
	 */
	public boolean hasActive (PosMiddlewareSetting setting) {
		boolean hasActive = middlewareSettingDao.hasActive(setting);
		if (hasActive && !setting.isActive()) {
			return false;
		}
		return hasActive;
	}

	/**
	 * Save the middleware setting object into the database.
	 * @param user The logged user.
	 * @param middlewareSetting The object to be saved.
	 */
	public void save(User user, PosMiddlewareSetting middlewareSetting) {
		boolean isNewRecord = middlewareSetting.getId() == 0;
		AuditUtil.addAudit(middlewareSetting, new Audit(user.getId(), isNewRecord, new Date()));
		middlewareSettingDao.saveOrUpdate(middlewareSetting);
	}

	/**
	 * Get the {@link PosMiddlewareSetting} by company id.
	 * @param companyId The company filter.
	 * @param user The current user logged in.
	 * @return The {@link PosMiddlewareSetting}.
	 */
	public PosMiddlewareSetting getByCompany(int companyId, User user) {
		return middlewareSettingDao.getByCompany(companyId, user);
	}

	/**
	 * Get the {@link PosMiddlewareSetting} by company id.
	 * @param companyId The company filter.
	 * @return The {@link PosMiddlewareSetting}.
	 */
	public PosMiddlewareSetting getByCompany(int companyId) {
		return getByCompany(companyId, null);
	}

	/**
	 * Get the {@link PosMiddlewareSetting} by company id and warehouse id.
	 * @param companyId The company filter.
	 * @param warehouseId The warehouse filter.
	 * @return The {@link PosMiddlewareSetting}.
	 */
	public PosMiddlewareSetting getByCompanyAndWarehouse(Integer companyId, Integer warehouseId) {
		return middlewareSettingDao.getByCompanyAndWarehouse(companyId, warehouseId);
	}
}
